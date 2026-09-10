// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.expr.plan;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

/** Definition-graph analysis (§4.3/§4.5): exact SCC cycle detection over the reader
 * graph, transitive invalidity propagation, and Kahn prerequisite-first ordering with
 * source-ordinal tie breaks. All algorithms are iterative. */
final class DependencyAnalyzer {

    private DependencyAnalyzer() {}

    /** Tarjan SCC, iterative. Returns each slot's component id; components of size 1
     * without a self-edge are acyclic. */
    static int[] stronglyConnected(int[][] deps) {
        int n = deps.length;
        int[] index = new int[n];
        int[] low = new int[n];
        int[] component = new int[n];
        java.util.Arrays.fill(index, -1);
        java.util.Arrays.fill(component, -1);
        int counter = 0;
        int componentCounter = 0;
        Deque<int[]> frames = new ArrayDeque<>();
        Deque<Integer> stack = new ArrayDeque<>();
        boolean[] onStack = new boolean[n];
        for (int root = 0; root < n; root++) {
            if (index[root] != -1) {
                continue;
            }
            frames.push(new int[] {root, 0});
            while (!frames.isEmpty()) {
                int[] frame = frames.peek();
                int node = frame[0];
                if (frame[1] == 0) {
                    index[node] = low[node] = counter++;
                    stack.push(node);
                    onStack[node] = true;
                }
                if (frame[1] < deps[node].length) {
                    int next = deps[node][frame[1]++];
                    if (index[next] == -1) {
                        frames.push(new int[] {next, 0});
                    } else if (onStack[next]) {
                        low[node] = Math.min(low[node], index[next]);
                    }
                } else {
                    frames.pop();
                    if (low[node] == index[node]) {
                        int member;
                        do {
                            member = stack.pop();
                            onStack[member] = false;
                            component[member] = componentCounter;
                        } while (member != node);
                        componentCounter++;
                    }
                    if (!frames.isEmpty()) {
                        int parent = frames.peek()[0];
                        low[parent] = Math.min(low[parent], low[node]);
                    }
                }
            }
        }
        return component;
    }

    /** True when the slot sits on a cycle: a multi-member component or a self-edge. */
    static boolean onCycle(int slot, int[] component, int[][] deps) {
        int own = component[slot];
        for (int dep : deps[slot]) {
            if (component[dep] == own) {
                return true;
            }
        }
        int members = 0;
        for (int other = 0; other < component.length; other++) {
            if (component[other] == own) {
                members++;
                if (members > 1) {
                    return true;
                }
            }
        }
        return false;
    }

    /** Marks every valid slot that transitively reaches an invalid slot. Returns the
     * newly-invalid slots in deterministic (ascending) order. */
    static List<Integer> propagateInvalidity(boolean[] valid, int[][] deps) {
        int n = valid.length;
        List<List<Integer>> readers = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            readers.add(new ArrayList<>());
        }
        for (int slot = 0; slot < n; slot++) {
            for (int dep : deps[slot]) {
                readers.get(dep).add(slot);
            }
        }
        Deque<Integer> queue = new ArrayDeque<>();
        boolean[] invalid = new boolean[n];
        for (int slot = 0; slot < n; slot++) {
            if (!valid[slot]) {
                invalid[slot] = true;
                queue.push(slot);
            }
        }
        while (!queue.isEmpty()) {
            int current = queue.pop();
            for (int reader : readers.get(current)) {
                if (valid[reader] && !invalid[reader]) {
                    invalid[reader] = true;
                    queue.push(reader);
                }
            }
        }
        List<Integer> newlyInvalid = new ArrayList<>();
        for (int slot = 0; slot < n; slot++) {
            if (valid[slot] && invalid[slot]) {
                newlyInvalid.add(slot);
            }
        }
        return newlyInvalid;
    }

    /** Kahn ordering over valid slots; ready sets drain lowest-ordinal-first so equal
     * prerequisites keep source order (§4.5). */
    static int[] orderValid(boolean[] valid, int[][] deps) {
        int n = valid.length;
        int[] pending = new int[n];
        List<List<Integer>> readers = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            readers.add(new ArrayList<>());
        }
        for (int slot = 0; slot < n; slot++) {
            if (!valid[slot]) {
                continue;
            }
            for (int dep : deps[slot]) {
                if (valid[dep]) {
                    pending[slot]++;
                    readers.get(dep).add(slot);
                }
            }
        }
        java.util.PriorityQueue<Integer> ready = new java.util.PriorityQueue<>();
        for (int slot = 0; slot < n; slot++) {
            if (valid[slot] && pending[slot] == 0) {
                ready.add(slot);
            }
        }
        int[] order = new int[countValid(valid)];
        int cursor = 0;
        while (!ready.isEmpty()) {
            int slot = ready.poll();
            order[cursor++] = slot;
            for (int reader : readers.get(slot)) {
                if (--pending[reader] == 0) {
                    ready.add(reader);
                }
            }
        }
        if (cursor != order.length) {
            throw new IllegalStateException("valid subgraph retains a cycle");
        }
        return order;
    }

    /** Transitive reader closure within valid slots, ascending, per slot: every valid
     * slot whose definition chain reaches {@code slot}. */
    static int[][] readerClosures(boolean[] valid, int[][] deps) {
        int n = valid.length;
        int[][] closures = new int[n][];
        for (int slot = 0; slot < n; slot++) {
            boolean[] reached = new boolean[n];
            Deque<Integer> queue = new ArrayDeque<>();
            for (int reader = 0; reader < n; reader++) {
                if (!valid[reader] || reached[reader]) {
                    continue;
                }
                for (int dep : deps[reader]) {
                    if (dep == slot) {
                        reached[reader] = true;
                        queue.push(reader);
                        break;
                    }
                }
            }
            List<Integer> closure = new ArrayList<>();
            while (!queue.isEmpty()) {
                int current = queue.pop();
                closure.add(current);
                for (int reader = 0; reader < n; reader++) {
                    if (!valid[reader] || reached[reader]) {
                        continue;
                    }
                    for (int dep : deps[reader]) {
                        if (dep == current) {
                            reached[reader] = true;
                            queue.push(reader);
                            break;
                        }
                    }
                }
            }
            closure.sort(Integer::compareTo);
            closures[slot] = closure.stream().mapToInt(Integer::intValue).toArray();
        }
        return closures;
    }

    private static int countValid(boolean[] valid) {
        int count = 0;
        for (boolean b : valid) {
            count += b ? 1 : 0;
        }
        return count;
    }
}
