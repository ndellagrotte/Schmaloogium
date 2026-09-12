# Tolerance profiles (PHASE_2_DOC §4.6.3). These initial numbers are UNMEASURED: every
# gate profile except IDENTICAL requires §4.6.5 calibration before tier acceptance, which
# is why calibratedOn is empty. ADVISORY forbids thresholds and never yields a verdict.
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
maxDelta = 8
maxRmse = 1.0
maxClusterArea = 64
maxClusters = 1024
calibratedOn = ""

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
