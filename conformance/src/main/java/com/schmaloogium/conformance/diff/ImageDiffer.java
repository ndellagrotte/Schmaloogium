// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.conformance.diff;

import java.util.ArrayList;
import java.util.List;

/**
 * [D-P2-7] three levels over unmasked sRGB RGB channels:
 * <ul>
 *   <li>L1 per pixel: a pixel differs when its maximum channel delta exceeds
 *       {@code channelTolerance};</li>
 *   <li>L2 aggregate: {@code differingFraction ≤ maxDifferingFraction},
 *       {@code maxChannelDelta ≤ maxDelta}, {@code rmse ≤ maxRmse} with
 *       {@code rmse = sqrt(Σdelta² / (3·unmasked))};</li>
 *   <li>L3 cluster: {@code largestClusterArea ≤ maxClusterArea} and
 *       {@code clusterCount ≤ maxClusters} over 4-connected components.</li>
 * </ul>
 * Dimension mismatch is a hard error, not a large diff (§4.6.1); all-masked input is invalid;
 * threshold equality passes; every predicate is required. Alpha must match exactly (the
 * agreed colour model is opaque ARGB from the vanilla framebuffer).
 */
public final class ImageDiffer {

    private ImageDiffer() {
    }

    public static DiffResult compare(PngRaster expected, PngRaster actual, TolerancePolicy policy,
            IgnoreMask mask) {
        if (expected.width() != actual.width() || expected.height() != actual.height()) {
            throw new IllegalArgumentException("dimension mismatch: expected " + expected.width()
                + "x" + expected.height() + ", actual " + actual.width() + "x" + actual.height());
        }
        int w = expected.width();
        int h = expected.height();
        int[] a = expected.argb();
        int[] b = actual.argb();
        boolean[] differing = new boolean[a.length];
        long unmasked = 0;
        long differingCount = 0;
        int maxDelta = 0;
        double sumSquares = 0.0;
        int tolerance = policy.advisory() ? 0 : policy.channelTolerance();
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                if (mask.masked(x, y)) {
                    continue;
                }
                unmasked++;
                int idx = y * w + x;
                int p = a[idx];
                int q = b[idx];
                int dr = Math.abs(((p >> 16) & 0xFF) - ((q >> 16) & 0xFF));
                int dg = Math.abs(((p >> 8) & 0xFF) - ((q >> 8) & 0xFF));
                int db = Math.abs((p & 0xFF) - (q & 0xFF));
                int da = Math.abs(((p >>> 24) & 0xFF) - ((q >>> 24) & 0xFF));
                int pixelMax = Math.max(Math.max(dr, dg), Math.max(db, da));
                sumSquares += (double) dr * dr + (double) dg * dg + (double) db * db;
                if (pixelMax > maxDelta) {
                    maxDelta = pixelMax;
                }
                if (pixelMax > tolerance) {
                    differing[idx] = true;
                    differingCount++;
                }
            }
        }
        if (unmasked == 0) {
            throw new IllegalArgumentException("all pixels are masked; the comparison is invalid");
        }
        double fraction = (double) differingCount / unmasked;
        double rmse = Math.sqrt(sumSquares / (3.0 * unmasked));
        ClusterAnalysis.Result clusters = ClusterAnalysis.analyse(differing, w, h);
        double maskedFraction = 1.0 - (double) unmasked / ((long) w * h);
        List<String> failed = new ArrayList<>();
        if (!policy.advisory()) {
            if (fraction > policy.maxDifferingFraction()) {
                failed.add("L2 differingFraction " + fraction + " > " + policy.maxDifferingFraction());
            }
            if (maxDelta > policy.maxDelta()) {
                failed.add("L2 maxChannelDelta " + maxDelta + " > " + policy.maxDelta());
            }
            if (rmse > policy.maxRmse()) {
                failed.add("L2 rmse " + rmse + " > " + policy.maxRmse());
            }
            if (clusters.largestClusterArea() > policy.maxClusterArea()) {
                failed.add("L3 largestClusterArea " + clusters.largestClusterArea() + " > "
                    + policy.maxClusterArea());
            }
            if (clusters.clusterCount() > policy.maxClusters()) {
                failed.add("L3 clusterCount " + clusters.clusterCount() + " > " + policy.maxClusters());
            }
        }
        return new DiffResult(policy.name(), policy.advisory(), w, h, unmasked, differingCount,
            fraction, maxDelta, rmse, clusters.clusterCount(), clusters.largestClusterArea(),
            maskedFraction, failed);
    }
}
