// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.expr.parse;

import com.schmaloogium.engine.expr.api.ExpressionDiagnosticKind;
import com.schmaloogium.engine.expr.api.SourceSpan;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

/** Recursive-descent parser for the normative §4.2 EBNF. Precedence tightest to loosest:
 * member/call, unary, multiplicative, additive, relational, equality, {@code &&},
 * {@code ||}; binary operators associate left. The whole raw expression must be consumed.
 * Nesting is enforced as AST height with an explicit walk so adversarial deep/wide inputs
 * degrade to a LIMIT load error instead of a stack overflow. */
public final class Parser {

    private final SourceSpan whole;
    private final List<Token> tokens;
    private int pos;
    private int nodes;

    private Parser(SourceSpan whole, List<Token> tokens) {
        this.whole = whole;
        this.tokens = tokens;
    }

    public static Ast parse(int declarationOrdinal, String rawExpression) throws ParseException {
        SourceSpan whole = SourceSpan.whole(declarationOrdinal, rawExpression);
        if (rawExpression.getBytes(java.nio.charset.StandardCharsets.UTF_8).length > Limits.MAX_TEXT_BYTES) {
            throw new ParseException(ExpressionDiagnosticKind.LIMIT, whole,
                    "declaration exceeds the 16 KiB text limit");
        }
        Parser parser = new Parser(whole, Lexer.lex(rawExpression, whole));
        Ast root = parser.expression();
        if (parser.peek().type() != TokenType.EOF) {
            throw parser.error("unexpected trailing input", parser.peek());
        }
        int height = astHeight(root);
        if (height > Limits.MAX_DEPTH) {
            throw parser.limit(root.span(), "nesting limit exceeded");
        }
        return root;
    }

    // grammar -----------------------------------------------------------------

    private Ast expression() throws ParseException {
        return logicalOr();
    }

    private Ast logicalOr() throws ParseException {
        Ast left = logicalAnd();
        while (peek().type() == TokenType.OR) {
            Token op = next();
            left = addNode(new Ast.Bin(Ast.BinaryOp.OR, left, logicalAnd(), span(op)));
        }
        return left;
    }

    private Ast logicalAnd() throws ParseException {
        Ast left = equality();
        while (peek().type() == TokenType.AND) {
            Token op = next();
            left = addNode(new Ast.Bin(Ast.BinaryOp.AND, left, equality(), span(op)));
        }
        return left;
    }

    private Ast equality() throws ParseException {
        Ast left = relation();
        while (peek().type() == TokenType.EQ || peek().type() == TokenType.NE) {
            Token op = next();
            Ast.BinaryOp bin = op.type() == TokenType.EQ ? Ast.BinaryOp.EQ : Ast.BinaryOp.NE;
            left = addNode(new Ast.Bin(bin, left, relation(), span(op)));
        }
        return left;
    }

    private Ast relation() throws ParseException {
        Ast left = additive();
        while (peek().type() == TokenType.GT || peek().type() == TokenType.GE
                || peek().type() == TokenType.LT || peek().type() == TokenType.LE) {
            Token op = next();
            Ast.BinaryOp bin = switch (op.type()) {
                case GT -> Ast.BinaryOp.GT;
                case GE -> Ast.BinaryOp.GE;
                case LT -> Ast.BinaryOp.LT;
                default -> Ast.BinaryOp.LE;
            };
            left = addNode(new Ast.Bin(bin, left, additive(), span(op)));
        }
        return left;
    }

    private Ast additive() throws ParseException {
        Ast left = multiply();
        while (peek().type() == TokenType.PLUS || peek().type() == TokenType.MINUS) {
            Token op = next();
            Ast.BinaryOp bin = op.type() == TokenType.PLUS ? Ast.BinaryOp.ADD : Ast.BinaryOp.SUB;
            left = addNode(new Ast.Bin(bin, left, multiply(), span(op)));
        }
        return left;
    }

    private Ast multiply() throws ParseException {
        Ast left = unary();
        while (peek().type() == TokenType.STAR || peek().type() == TokenType.SLASH
                || peek().type() == TokenType.PERCENT) {
            Token op = next();
            Ast.BinaryOp bin = switch (op.type()) {
                case STAR -> Ast.BinaryOp.MUL;
                case SLASH -> Ast.BinaryOp.DIV;
                default -> Ast.BinaryOp.MOD;
            };
            left = addNode(new Ast.Bin(bin, left, unary(), span(op)));
        }
        return left;
    }

    private Ast unary() throws ParseException {
        TokenType type = peek().type();
        if (type == TokenType.PLUS || type == TokenType.MINUS || type == TokenType.BANG) {
            Token op = next();
            Ast.UnaryOp unary = switch (type) {
                case PLUS -> Ast.UnaryOp.POS;
                case MINUS -> Ast.UnaryOp.NEG;
                default -> Ast.UnaryOp.NOT;
            };
            return addNode(new Ast.Unary(unary, unary(), span(op)));
        }
        return postfix();
    }

    private Ast postfix() throws ParseException {
        Ast base = primary();
        while (peek().type() == TokenType.DOT) {
            Token dot = next();
            Token member = peek();
            if (member.type() == TokenType.IDENT) {
                int component = letterComponent(member.text());
                if (component < 0) {
                    throw error("unknown member: ." + member.text(), member);
                }
                next();
                base = addNode(new Ast.Member(base, component, true, span(dot)));
            } else if (member.type() == TokenType.NUMBER && isIndexToken(member.text())) {
                next();
                base = addNode(new Ast.Member(base, member.text().charAt(0) - '0', false, span(dot)));
            } else {
                throw error("expected member name or matrix index after '.'", member);
            }
        }
        return base;
    }

    private Ast primary() throws ParseException {
        Token token = peek();
        switch (token.type()) {
            case NUMBER -> {
                next();
                float value;
                try {
                    value = Float.parseFloat(token.text());
                } catch (NumberFormatException e) {
                    throw error("malformed number: " + token.text(), token);
                }
                return addNode(new Ast.Num(value, span(token)));
            }
            case IDENT -> {
                if (peek(1).type() == TokenType.LPAREN) {
                    return call();
                }
                next();
                return addNode(new Ast.Ident(token.text(), span(token)));
            }
            case LPAREN -> {
                next();
                Ast inner = expression();
                expect(TokenType.RPAREN, "expected ')'");
                return inner;
            }
            default -> throw error("expected expression", token);
        }
    }

    private Ast call() throws ParseException {
        Token name = next(); // IDENT
        next(); // LPAREN
        List<Ast> args = new ArrayList<>();
        if (peek().type() != TokenType.RPAREN) {
            args.add(expression());
            while (peek().type() == TokenType.COMMA) {
                if (args.size() >= Limits.MAX_CALL_ARGS) {
                    throw limit(span(name), "call argument limit exceeded");
                }
                next();
                args.add(expression());
            }
        }
        expect(TokenType.RPAREN, "expected ')' to close call");
        return addNode(new Ast.Call(name.text(), List.copyOf(args), span(name), span(name)));
    }

    // helpers -----------------------------------------------------------------

    private Ast addNode(Ast node) throws ParseException {
        nodes++;
        if (nodes > Limits.MAX_AST_NODES) {
            throw limit(node.span(), "AST node limit exceeded");
        }
        return node;
    }

    private static int letterComponent(String text) {
        return switch (text) {
            case "x", "r" -> 0;
            case "y", "g" -> 1;
            case "z", "b" -> 2;
            default -> -1;
        };
    }

    private static boolean isIndexToken(String text) {
        return text.length() == 1 && text.charAt(0) >= '0' && text.charAt(0) <= '3';
    }

    private static List<Ast> childrenOf(Ast node) {
        if (node instanceof Ast.Num || node instanceof Ast.Ident) {
            return List.of();
        }
        if (node instanceof Ast.Unary unary) {
            return List.of(unary.operand());
        }
        if (node instanceof Ast.Bin bin) {
            return List.of(bin.left(), bin.right());
        }
        if (node instanceof Ast.Member member) {
            return List.of(member.base());
        }
        return ((Ast.Call) node).args();
    }

    /** Iterative AST height via explicit post-order: a leaf is 1, every parent is one more
     * than its tallest child. Never recurses, so adversarial shapes cannot overflow. */
    private static int astHeight(Ast root) {
        record Frame(Ast node, boolean combine) {}
        Deque<Frame> stack = new ArrayDeque<>();
        Deque<Integer> heights = new ArrayDeque<>();
        stack.push(new Frame(root, false));
        int maxHeight = 0;
        while (!stack.isEmpty()) {
            Frame frame = stack.pop();
            if (frame.combine()) {
                int maxChild = 0;
                for (int i = 0; i < childrenOf(frame.node()).size(); i++) {
                    maxChild = Math.max(maxChild, heights.pop());
                }
                int own = 1 + maxChild;
                heights.push(own);
                maxHeight = Math.max(maxHeight, own);
            } else {
                stack.push(new Frame(frame.node(), true));
                List<Ast> children = childrenOf(frame.node());
                for (int i = children.size() - 1; i >= 0; i--) {
                    stack.push(new Frame(children.get(i), false));
                }
            }
        }
        return maxHeight;
    }

    private Token peek() {
        return tokens.get(pos);
    }

    private Token peek(int ahead) {
        return tokens.get(Math.min(pos + ahead, tokens.size() - 1));
    }

    private Token next() {
        Token token = tokens.get(pos);
        if (token.type() != TokenType.EOF) {
            pos++;
        }
        return token;
    }

    private void expect(TokenType type, String message) throws ParseException {
        if (peek().type() != type) {
            throw error(message, peek());
        }
        next();
    }

    private SourceSpan span(Token token) {
        return new SourceSpan(whole.declarationOrdinal(), token.start(), token.end());
    }

    private ParseException error(String message, Token token) throws ParseException {
        throw new ParseException(ExpressionDiagnosticKind.PARSE, span(token), message);
    }

    private ParseException limit(SourceSpan span, String message) throws ParseException {
        throw new ParseException(ExpressionDiagnosticKind.LIMIT, span, message);
    }
}
