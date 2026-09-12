// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.conformance.diff;

/**
 * L3 of [D-P2-7]: 4-connected components over a differing-pixel mask. Iterative flood
 * fill on an {@code int[]} stack, so a full-frame difference cannot overflow the call stack.
 */
public final class ClusterAnalysis {

    public record Result(int clusterCount, int largestClusterArea) {
    }

    private ClusterAnalysis() {
    }

    public static Result analyse(boolean[] differing, int width, int height) {
        if (differing.length != width * height) {
            throw new IllegalArgumentException("mask length mismatch");
        }
        boolean[] visited = new boolean[differing.length];
        int[] stack = new int[differing.length];
        int clusters = 0;
        int largest = 0;
        for (int start = 0; start < differing.length; start++) {
            if (!differing[start] || visited[start]) {
                continue;
            }
            clusters++;
            int area = 0;
            int sp = 0;
            stack[sp++] = start;
            visited[start] = true;
            while (sp > 0) {
                int idx = stack[--sp];
                area++;
                int x = idx % width;
                int y = idx / width;
                if (x > 0) {
                    sp = push(differing, visited, stack, sp, idx - 1);
                }
                if (x < width - 1) {
                    sp = push(differing, visited, stack, sp, idx + 1);
                }
                if (y > 0) {
                    sp = push(differing, visited, stack, sp, idx - width);
                }
                if (y < height - 1) {
                    sp = push(differing, visited, stack, sp, idx + width);
                }
            }
            largest = Math.max(largest, area);
        }
        return new Result(clusters, largest);
    }

    private static int push(boolean[] differing, boolean[] visited, int[] stack, int sp, int idx) {
        if (differing[idx] && !visited[idx]) {
            visited[idx] = true;
            stack[sp++] = idx;
        }
        return sp;
    }
}
