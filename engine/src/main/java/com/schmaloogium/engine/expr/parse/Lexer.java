// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.expr.parse;

import com.schmaloogium.engine.expr.api.ExpressionDiagnosticKind;
import com.schmaloogium.engine.expr.api.SourceSpan;

import java.util.ArrayList;
import java.util.List;

/** Clean-room lexer for the §4.2 grammar: ASCII identifiers, decimal numbers with optional
 * exponent, the documented operator set, and nothing else. Whitespace is ignored between
 * tokens; hex, binary, suffixes, NaN/Infinity spellings, Unicode operators, brackets, and
 * assignment are rejected. A leading-dot number is only recognized in value position —
 * after an identifier, number, or ')' the '.' is a member dot. */
final class Lexer {

    private final String text;
    private final SourceSpan whole;
    private int pos;
    private final List<Token> tokens = new ArrayList<>();

    private Lexer(String text, SourceSpan whole) {
        this.text = text;
        this.whole = whole;
    }

    static List<Token> lex(String raw, SourceSpan whole) throws ParseException {
        Lexer lexer = new Lexer(raw, whole);
        lexer.run();
        return lexer.tokens;
    }

    private void run() throws ParseException {
        while (pos < text.length()) {
            char c = text.charAt(pos);
            if (Character.isWhitespace(c)) {
                pos++;
                continue;
            }
            if (tokens.size() >= Limits.MAX_TOKENS) {
                throw limit("token limit exceeded");
            }
            int start = pos;
            switch (c) {
                case '|' -> twoChar('|', TokenType.OR);
                case '&' -> twoChar('&', TokenType.AND);
                case '=' -> twoChar('=', TokenType.EQ);
                case '!' -> twoChar('=', TokenType.NE, TokenType.BANG);
                case '>' -> twoChar('=', TokenType.GE, TokenType.GT);
                case '<' -> twoChar('=', TokenType.LE, TokenType.LT);
                case '+' -> emit(TokenType.PLUS);
                case '-' -> emit(TokenType.MINUS);
                case '*' -> emit(TokenType.STAR);
                case '/' -> emit(TokenType.SLASH);
                case '%' -> emit(TokenType.PERCENT);
                case '(' -> emit(TokenType.LPAREN);
                case ')' -> emit(TokenType.RPAREN);
                case ',' -> emit(TokenType.COMMA);
                case '.' -> dotOrNumber();
                default -> {
                    if (isIdentStart(c)) {
                        scanIdent();
                    } else if (Character.isDigit(c)) {
                        scanNumber(false);
                    } else {
                        pos++;
                        throw new ParseException(ExpressionDiagnosticKind.LEX,
                                new SourceSpan(whole.declarationOrdinal(), start, pos),
                                "unexpected character: '" + c + "'");
                    }
                }
            }
        }
        tokens.add(new Token(TokenType.EOF, "", text.length(), text.length()));
    }

    private void twoChar(char second, TokenType pair) throws ParseException {
        if (pos + 1 < text.length() && text.charAt(pos + 1) == second) {
            tokens.add(new Token(pair, text.substring(pos, pos + 2), pos, pos + 2));
            pos += 2;
        } else {
            throw lexError();
        }
    }

    private void twoChar(char second, TokenType pair, TokenType single) throws ParseException {
        if (pos + 1 < text.length() && text.charAt(pos + 1) == second) {
            tokens.add(new Token(pair, text.substring(pos, pos + 2), pos, pos + 2));
            pos += 2;
        } else {
            tokens.add(new Token(single, text.substring(pos, pos + 1), pos, pos + 1));
            pos++;
        }
    }

    private void emit(TokenType type) {
        tokens.add(new Token(type, text.substring(pos, pos + 1), pos, pos + 1));
        pos++;
    }

    private void dotOrNumber() throws ParseException {
        if (pos + 1 < text.length() && Character.isDigit(text.charAt(pos + 1))
                && valuePosition()) {
            scanNumber(true);
        } else {
            emit(TokenType.DOT);
        }
    }

    /** Value position: start of input, or after '(', ',', or a unary/binary operator. */
    private boolean valuePosition() {
        if (tokens.isEmpty()) {
            return true;
        }
        TokenType prev = tokens.get(tokens.size() - 1).type();
        return switch (prev) {
            case LPAREN, COMMA, PLUS, MINUS, STAR, SLASH, PERCENT, BANG,
                    OR, AND, EQ, NE, GT, GE, LT, LE -> true;
            default -> false;
        };
    }

    private void scanIdent() {
        int start = pos;
        while (pos < text.length() && isIdentPart(text.charAt(pos))) {
            pos++;
        }
        tokens.add(new Token(TokenType.IDENT, text.substring(start, pos), start, pos));
    }

    private void scanNumber(boolean leadingDot) throws ParseException {
        int start = pos;
        if (leadingDot) {
            pos++; // consume '.'
        }
        while (pos < text.length() && Character.isDigit(text.charAt(pos))) {
            pos++;
        }
        boolean afterMemberDot = !leadingDot && !tokens.isEmpty()
                && tokens.get(tokens.size() - 1).type() == TokenType.DOT;
        boolean indexFollowedByIndex = afterMemberDot && pos - start == 1
                && text.charAt(start) >= '0' && text.charAt(start) <= '3'
                && pos < text.length() && text.charAt(pos) == '.'
                && pos + 1 < text.length() && Character.isDigit(text.charAt(pos + 1));
        if (indexFollowedByIndex) {
            // member index 0-3 directly followed by another member index: "m.2.1"
        } else if (pos < text.length() && text.charAt(pos) == '.'
                && pos + 1 < text.length() && Character.isDigit(text.charAt(pos + 1))) {
            pos++;
            while (pos < text.length() && Character.isDigit(text.charAt(pos))) {
                pos++;
            }
        }
        if (pos < text.length() && (text.charAt(pos) == 'e' || text.charAt(pos) == 'E')) {
            int save = pos;
            pos++;
            if (pos < text.length() && (text.charAt(pos) == '+' || text.charAt(pos) == '-')) {
                pos++;
            }
            if (pos < text.length() && Character.isDigit(text.charAt(pos))) {
                while (pos < text.length() && Character.isDigit(text.charAt(pos))) {
                    pos++;
                }
            } else {
                pos = save; // exponent without digits belongs to the next token
            }
        }
        tokens.add(new Token(TokenType.NUMBER, text.substring(start, pos), start, pos));
    }

    private ParseException lexError() throws ParseException {
        int start = pos;
        pos = Math.min(pos + 1, text.length());
        throw new ParseException(ExpressionDiagnosticKind.LEX,
                new SourceSpan(whole.declarationOrdinal(), start, pos),
                "malformed token");
    }

    private ParseException limit(String message) throws ParseException {
        throw new ParseException(ExpressionDiagnosticKind.LIMIT,
                new SourceSpan(whole.declarationOrdinal(), pos, text.length()), message);
    }

    private static boolean isIdentStart(char c) {
        return c == '_' || (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z');
    }

    private static boolean isIdentPart(char c) {
        return isIdentStart(c) || Character.isDigit(c);
    }
}
