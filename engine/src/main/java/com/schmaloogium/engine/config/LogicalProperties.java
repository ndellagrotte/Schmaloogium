// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.config;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Java-Properties logical-line decoding: comment lines, escaped continuations,
 * leading-whitespace skipping, and exact key/value separation with Properties
 * unescaping, without any trimming of value text.
 */
public final class LogicalProperties {

    /** One decoded logical occurrence with its one-based physical line number. */
    public record Entry(String key, String value, int physicalLine) {
    }

    private LogicalProperties() {
    }

    /** Decodes all logical occurrences; malformed lines are skipped. */
    public static List<Entry> decode(byte[] bytes, Charset charset) {
        String text = new String(bytes, charset);
        List<Entry> out = new ArrayList<>();
        List<String> logical = new ArrayList<>();
        List<Integer> logicalStarts = new ArrayList<>();
        boolean continuation = false;
        String[] lines = text.split("\n", -1);
        for (int i = 0; i < lines.length; i++) {
            String raw = lines[i];
            if (raw.endsWith("\r")) {
                raw = raw.substring(0, raw.length() - 1);
            }
            if (continuation) {
                logical.set(logical.size() - 1, logical.get(logical.size() - 1) + raw);
            } else {
                logical.add(raw);
                logicalStarts.add(i + 1);
            }
            continuation = countsOddTrailingBackslashes(raw);
        }
        for (int i = 0; i < logical.size(); i++) {
            String stripped = stripIndent(logical.get(i));
            if (stripped.isEmpty() || stripped.charAt(0) == '#' || stripped.charAt(0) == '!') {
                continue;
            }
            Entry entry = splitKeyValue(stripped, logicalStarts.get(i));
            if (entry != null) {
                out.add(entry);
            }
        }
        return out;
    }

    private static boolean countsOddTrailingBackslashes(String line) {
        int n = 0;
        for (int i = line.length() - 1; i >= 0 && line.charAt(i) == '\\'; i--) {
            n++;
        }
        return n % 2 == 1;
    }

    private static String stripIndent(String line) {
        int i = 0;
        while (i < line.length()) {
            char c = line.charAt(i);
            if (c == ' ' || c == '\t' || c == '\f') {
                i++;
            } else {
                break;
            }
        }
        return line.substring(i);
    }

    private static Entry splitKeyValue(String line, int physicalLine) {
        int separator = -1;
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == '\\') {
                i++;
                continue;
            }
            if (c == '=' || c == ':' || c == ' ' || c == '\t') {
                separator = i;
                break;
            }
        }
        if (separator < 0) {
            return new Entry(unescape(line), "", physicalLine);
        }
        String key = unescape(line.substring(0, separator));
        int valueStart = separator + 1;
        if (line.charAt(separator) != '=') {
            while (valueStart < line.length()) {
                char c = line.charAt(valueStart);
                if (c == ' ' || c == '\t' || c == '\f' || c == '=' || c == ':') {
                    valueStart++;
                } else {
                    break;
                }
            }
        }
        return new Entry(key, unescape(line.substring(valueStart)), physicalLine);
    }

    /** Java-Properties unescape over the exact decoded characters. */
    public static String unescape(String s) {
        StringBuilder sb = new StringBuilder(s.length());
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c != '\\' || i + 1 >= s.length()) {
                sb.append(c);
                continue;
            }
            char n = s.charAt(++i);
            switch (n) {
                case 'n' -> sb.append('\n');
                case 't' -> sb.append('\t');
                case 'r' -> sb.append('\r');
                case 'f' -> sb.append('\f');
                case 'u' -> {
                    boolean ok = false;
                    if (i + 4 < s.length()) {
                        try {
                            sb.append((char) Integer.parseInt(s.substring(i + 1, i + 5), 16));
                            i += 4;
                            ok = true;
                        } catch (NumberFormatException e) {
                            ok = false;
                        }
                    }
                    if (!ok) {
                        sb.append('u');
                    }
                }
                default -> sb.append(n);
            }
        }
        return sb.toString();
    }
}
