// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.config;

import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Predicate;

/** Small Boolean switch AST for {@code program.<prog>.enabled} and profile conditions. */
final class BooleanExpression {

    private BooleanExpression(Node root) {
        this.root = root;
    }

    interface Node {
        boolean evaluate(Predicate<String> switches);

        void collect(Set<String> out);

        String render();
    }

    record Ref(String name) implements Node {
        @Override
        public boolean evaluate(Predicate<String> switches) {
            return switches.test(name);
        }

        @Override
        public void collect(Set<String> out) {
            out.add(name);
        }

        @Override
        public String render() {
            return name;
        }
    }

    record Not(Node inner) implements Node {
        @Override
        public boolean evaluate(Predicate<String> switches) {
            return !inner.evaluate(switches);
        }

        @Override
        public void collect(Set<String> out) {
            inner.collect(out);
        }

        @Override
        public String render() {
            return "!" + inner.render();
        }
    }

    record And(Node left, Node right) implements Node {
        @Override
        public boolean evaluate(Predicate<String> switches) {
            return left.evaluate(switches) && right.evaluate(switches);
        }

        @Override
        public void collect(Set<String> out) {
            left.collect(out);
            right.collect(out);
        }

        @Override
        public String render() {
            return "(" + left.render() + " && " + right.render() + ")";
        }
    }

    record Or(Node left, Node right) implements Node {
        @Override
        public boolean evaluate(Predicate<String> switches) {
            return left.evaluate(switches) || right.evaluate(switches);
        }

        @Override
        public void collect(Set<String> out) {
            left.collect(out);
            right.collect(out);
        }

        @Override
        public String render() {
            return "(" + left.render() + " || " + right.render() + ")";
        }
    }

    private final Node root;

    Node root() {
        return root;
    }

    String render() {
        return root.render();
    }

    Set<String> switches() {
        Set<String> out = new TreeSet<>();
        root.collect(out);
        return out;
    }

    boolean evaluate(Predicate<String> switches) {
        return root.evaluate(switches);
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof BooleanExpression other && root.equals(other.root);
    }

    @Override
    public int hashCode() {
        return root.hashCode();
    }

    /**
     * Parses the small Boolean switch grammar: {@code !}, {@code &&}, {@code ||},
     * parentheses, and switch names. Returns empty on a malformed expression.
     */
    static Optional<BooleanExpression> parse(String expression) {
        Parser p = new Parser(expression);
        try {
            Node n = p.parseOr();
            if (!p.atEnd()) {
                return Optional.empty();
            }
            return Optional.of(new BooleanExpression(n));
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }

    private static final class Parser {
        private final String s;
        private int pos;

        Parser(String s) {
            this.s = java.util.Objects.requireNonNull(s, "expression");
        }

        boolean atEnd() {
            skipSpace();
            return pos >= s.length();
        }

        private void skipSpace() {
            while (pos < s.length() && Character.isWhitespace(s.charAt(pos))) {
                pos++;
            }
        }

        private char peek() {
            skipSpace();
            return pos < s.length() ? s.charAt(pos) : '\0';
        }

        private boolean eat(String token) {
            skipSpace();
            if (s.startsWith(token, pos)) {
                pos += token.length();
                return true;
            }
            return false;
        }

        Node parseOr() {
            Node left = parseAnd();
            while (peek() == '|' && eat("||")) {
                left = new Or(left, parseAnd());
            }
            return left;
        }

        Node parseAnd() {
            Node left = parseUnary();
            while (peek() == '&' && eat("&&")) {
                left = new And(left, parseUnary());
            }
            return left;
        }

        Node parseUnary() {
            if (peek() == '!' && eat("!")) {
                return new Not(parseUnary());
            }
            return parseAtom();
        }

        Node parseAtom() {
            skipSpace();
            if (peek() == '(' && eat("(")) {
                Node inner = parseOr();
                if (!eat(")")) {
                    throw new IllegalArgumentException("missing )");
                }
                return inner;
            }
            int start = pos;
            while (pos < s.length() && (Character.isLetterOrDigit(s.charAt(pos))
                    || s.charAt(pos) == '_' || s.charAt(pos) == '.' || s.charAt(pos) == '-')) {
                pos++;
            }
            if (pos == start) {
                throw new IllegalArgumentException("expected switch name at " + pos);
            }
            return new Ref(s.substring(start, pos));
        }
    }
}
