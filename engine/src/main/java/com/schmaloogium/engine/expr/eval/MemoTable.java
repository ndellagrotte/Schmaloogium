// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.expr.eval;

/** Per-refresh memo storage (§4.5): converted definition slots and resolved input slots,
 * keyed by epoch. One evaluation/conversion/effect commit per retained enabled definition;
 * repeated references only read slots. */
final class MemoTable {

    static final byte SLOT_EMPTY = 0;
    static final byte SLOT_VALUE = 1;
    static final byte SLOT_ERROR = 2;
    static final byte SLOT_EVALUATING = 3;

    static final byte INPUT_UNKNOWN = 0;
    static final byte INPUT_PRESENT = 1;
    static final byte INPUT_ABSENT = 2;

    final int slotCount;
    final int inputCount;

    final byte[] slotStatus;
    final int[] slotEpoch;
    /** Scalar value or vector lane storage, four lanes per slot. */
    final float[] slotF;
    /** Declared-int storage (upload boundary), four lanes per slot. */
    final int[] slotI;
    final boolean[] slotB;

    final byte[] inputStatus;
    final int[] inputEpoch;
    final float[] inputF;
    final int[] inputI;
    final boolean[] inputB;
    final float[] inputM;

    MemoTable(int slotCount, int inputCount) {
        this.slotCount = slotCount;
        this.inputCount = inputCount;
        this.slotStatus = new byte[slotCount];
        this.slotEpoch = new int[slotCount];
        this.slotF = new float[4 * slotCount];
        this.slotI = new int[4 * slotCount];
        this.slotB = new boolean[slotCount];
        this.inputStatus = new byte[inputCount];
        this.inputEpoch = new int[inputCount];
        this.inputF = new float[4 * inputCount];
        this.inputI = new int[4 * inputCount];
        this.inputB = new boolean[inputCount];
        this.inputM = new float[16 * inputCount];
    }

    void beginEpoch(int epoch) {
        java.util.Arrays.fill(slotStatus, SLOT_EMPTY);
        java.util.Arrays.fill(inputStatus, INPUT_UNKNOWN);
        java.util.Arrays.fill(slotEpoch, epoch);
        java.util.Arrays.fill(inputEpoch, epoch);
    }
}
