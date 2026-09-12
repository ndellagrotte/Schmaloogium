// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.conformance.fixture;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.Objects;

/**
 * The never-in-repo cache root (§4.10.3): resolved from
 * {@code -Dschmaloogium.conformance.cacheDir} → {@code $SCHMALOOGIUM_CONFORMANCE_CACHE} →
 * {@code $XDG_CACHE_HOME/schmaloogium/conformance} → {@code ~/.cache/schmaloogium/conformance}.
 * [D-P2-9]: a root whose nearest existing ancestor (or any ancestor above it) contains a
 * {@code .git} entry is refused before anything is created, the walk never follows links,
 * and a refused requested path is left absent. Subdirectories: {@code packs}, {@code worlds},
 * {@code runs}, {@code baselines}, {@code oracle}.
 */
public final class FixtureCache {

    public static final String PROPERTY = "schmaloogium.conformance.cacheDir";
    public static final String ENV = "SCHMALOOGIUM_CONFORMANCE_CACHE";

    private final Path root;

    private FixtureCache(Path root) {
        this.root = root;
    }

    public Path root() {
        return root;
    }

    public Path packs() {
        return root.resolve("packs");
    }

    public Path worlds() {
        return root.resolve("worlds");
    }

    public Path runs() {
        return root.resolve("runs");
    }

    public Path baselines() {
        return root.resolve("baselines");
    }

    public Path oracle() {
        return root.resolve("oracle");
    }

    /** Resolves the configured root against {@code workingDir} and establishes it. */
    public static FixtureCache establish(Path workingDir) throws IOException {
        String configured = System.getProperty(PROPERTY);
        if (configured == null || configured.isBlank()) {
            configured = System.getenv(ENV);
        }
        Path requested;
        if (configured != null && !configured.isBlank()) {
            requested = Path.of(configured);
        } else {
            String xdg = System.getenv("XDG_CACHE_HOME");
            Path base = (xdg != null && !xdg.isBlank()) ? Path.of(xdg)
                : Path.of(System.getProperty("user.home"), ".cache");
            requested = base.resolve("schmaloogium").resolve("conformance");
        }
        return establish(workingDir, requested);
    }

    /** Establishes an explicit root: refuses git work trees, creates missing components. */
    public static FixtureCache establish(Path workingDir, Path requested) throws IOException {
        Objects.requireNonNull(requested, "requested");
        Path absolute = requested.isAbsolute() ? requested : workingDir.resolve(requested);
        absolute = absolute.toAbsolutePath().normalize();
        Path ancestor = nearestExisting(absolute);
        refuseGitWorkTree(ancestor, absolute);
        // Create missing components one at a time, rejecting links and non-directories.
        Path cursor = ancestor;
        Path remaining = ancestor.relativize(absolute);
        for (Path part : remaining) {
            if (part.toString().isEmpty()) {
                continue;
            }
            cursor = cursor.resolve(part);
            if (Files.exists(cursor, LinkOption.NOFOLLOW_LINKS)) {
                if (Files.isSymbolicLink(cursor) || !Files.isDirectory(cursor, LinkOption.NOFOLLOW_LINKS)) {
                    throw new IOException("cache root component is a link or not a directory: " + cursor);
                }
            } else {
                Files.createDirectory(cursor);
            }
        }
        FixtureCache cache = new FixtureCache(absolute);
        for (Path sub : new Path[] {cache.packs(), cache.worlds(), cache.runs(), cache.baselines(),
            cache.oracle()}) {
            if (Files.exists(sub, LinkOption.NOFOLLOW_LINKS)) {
                if (Files.isSymbolicLink(sub) || !Files.isDirectory(sub, LinkOption.NOFOLLOW_LINKS)) {
                    throw new IOException("cache subdirectory is a link or not a directory: " + sub);
                }
            } else {
                Files.createDirectory(sub);
            }
        }
        return cache;
    }

    static Path nearestExisting(Path absolute) {
        Path cursor = absolute;
        while (cursor != null && !Files.exists(cursor, LinkOption.NOFOLLOW_LINKS)) {
            cursor = cursor.getParent();
        }
        if (cursor == null) {
            throw new IllegalStateException("no existing ancestor for " + absolute);
        }
        return cursor;
    }

    /** Walks {@code ancestor} and every ancestor above it looking for a {@code .git} entry. */
    static void refuseGitWorkTree(Path ancestor, Path requested) throws IOException {
        Path cursor = ancestor;
        while (cursor != null) {
            if (Files.isSymbolicLink(cursor)) {
                throw new IOException("cache root ancestor is a symbolic link: " + cursor);
            }
            Path git = cursor.resolve(".git");
            if (Files.exists(git, LinkOption.NOFOLLOW_LINKS)) {
                throw new IOException("[D-P2-9] refusing cache root " + requested
                    + ": it lies inside the git work tree at " + cursor
                    + " (fixtures and images are never re-hosted in the repository)");
            }
            cursor = cursor.getParent();
        }
    }
}
