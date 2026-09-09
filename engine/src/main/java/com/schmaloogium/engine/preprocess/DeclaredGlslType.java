// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.preprocess;

import java.util.List;
import java.util.Optional;

/** Closed structural declared-type algebra. */
public sealed interface DeclaredGlslType {
    record Scalar(ScalarKind kind) implements DeclaredGlslType {

        public Scalar {
            java.util.Objects.requireNonNull(kind, "kind");
        }
    }

    record Vector(ScalarKind component, int width) implements DeclaredGlslType {

        public Vector {
            java.util.Objects.requireNonNull(component, "component");
        }
    }

    record Matrix(ScalarKind component, int columns, int rows) implements DeclaredGlslType {

        public Matrix {
            java.util.Objects.requireNonNull(component, "component");
        }
    }

    record Sampler(SampledKind sample, TextureDimension dimension,
                   boolean arrayed, boolean shadow, boolean multisample)
        implements DeclaredGlslType {

        public Sampler {
            java.util.Objects.requireNonNull(sample, "sample");
            java.util.Objects.requireNonNull(dimension, "dimension");
        }
    }

    record Image(SampledKind sample, TextureDimension dimension,
                 boolean arrayed, boolean multisample)
        implements DeclaredGlslType {

        public Image {
            java.util.Objects.requireNonNull(sample, "sample");
            java.util.Objects.requireNonNull(dimension, "dimension");
        }
    }

    record AtomicCounter() implements DeclaredGlslType {
    }

    record Array(DeclaredGlslType element, List<ArrayExtent> extents)
        implements DeclaredGlslType {

        public Array {
            java.util.Objects.requireNonNull(element, "element");
            extents = List.copyOf(extents);
        }
    }

    record Struct(Optional<String> declaredName, List<StructField> fields,
                  DeclaredStructFingerprint shape)
        implements DeclaredGlslType {

        public Struct {
            declaredName = declaredName == null ? Optional.empty() : declaredName;
            fields = List.copyOf(fields);
            java.util.Objects.requireNonNull(shape, "shape");
        }
    }
}
