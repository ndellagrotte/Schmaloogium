# Tolerance profiles (PHASE_2_DOC §4.6.3). Profiles whose calibratedOn is empty carry the
# UNMEASURED starting numbers and require §4.6.5 calibration before tier acceptance
# (harness calibrate --run-a DIR --run-b DIR --profile NAME --write); a non-empty
# calibratedOn names the date, GPU and driver the maxima were observed on. ADVISORY forbids
# thresholds and never yields a verdict.
[profile IDENTICAL]
channelTolerance = 0
maxDifferingFraction = 0.0
maxDelta = 0
maxRmse = 0.0
maxClusterArea = 0
maxClusters = 0
calibratedOn = ""

[profile SAME_MACHINE]
channelTolerance = 1
maxDifferingFraction = 0.0005
maxDelta = 104
maxRmse = 1.0
maxClusterArea = 64
maxClusters = 1024
calibratedOn = "2026-09-13, NVIDIA GeForce RTX 3080/PCIe/SSE2, 4.6.0 NVIDIA 610.57.04, runs RUN-T1-APPROVE-terrain-day-20260912T195520 + RUN-T1-REGRESS-terrain-day-20260912T200647 + RUN-SCENE-SELFCHECK-terrain-day-20260913T004154 + RUN-SCENE-SELFCHECK-terrain-day-20260913T005053"

[profile CROSS_DRIVER]
channelTolerance = 3
maxDifferingFraction = 0.005
maxDelta = 24
maxRmse = 3.0
maxClusterArea = 512
maxClusters = 4096
calibratedOn = ""

[profile CROSS_ENGINE]
channelTolerance = 6
maxDifferingFraction = 0.02
maxDelta = 48
maxRmse = 6.0
maxClusterArea = 4096
maxClusters = 16384
calibratedOn = ""

[profile ADVISORY]
calibratedOn = ""

