// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.mod.glue.id;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.schmaloogium.engine.config.MappingKind;
import com.schmaloogium.engine.config.id.ModIdSourceSnapshot;
import com.schmaloogium.engine.config.id.ModSourceEntry;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** Bounded reads of exactly the three per-mod paths from jars and directories. */
class ModSourceReaderTest {

    @TempDir
    Path tmp;

    @Test
    void jarAndDirectoryRootsContributeOnlyTheThreeExactPaths() throws IOException {
        Path jar = tmp.resolve("alpha.jar");
        try (ZipOutputStream zip = new ZipOutputStream(Files.newOutputStream(jar))) {
            put(zip, "assets/alpha/shaders/block.properties", "block.10=alpha:ore\n");
            put(zip, "assets/alpha/shaders/layer.properties", "layer.translucent=alpha:ore\n");
            put(zip, "assets/other/shaders/item.properties", "item.5=other:thing\n");
        }
        Path dir = tmp.resolve("beta");
        Files.createDirectories(dir.resolve("assets/beta/shaders"));
        Files.writeString(dir.resolve("assets/beta/shaders/entity.properties"), "entity.3=beta:mob\n");

        List<String> warned = new ArrayList<>();
        ModIdSourceSnapshot snapshot = ModSourceReader.read(List.of(
            new ModSourceReader.ModRoot("beta", dir), new ModSourceReader.ModRoot("alpha", jar),
            new ModSourceReader.ModRoot("Bad Id", dir)), (m, r) -> warned.add(m + ":" + r));

        assertEquals(2, snapshot.sources().size(), snapshot.toString());
        ModSourceEntry first = snapshot.sources().get(0);
        assertEquals("alpha", first.modId());
        assertEquals(MappingKind.BLOCK, first.kind());
        assertEquals("block.10=alpha:ore\n", ModSourceReader.text(first));
        assertEquals("beta", snapshot.sources().get(1).modId());
        assertEquals(MappingKind.ENTITY, snapshot.sources().get(1).kind());
        assertTrue(warned.isEmpty(), warned.toString());
    }

    @Test
    void overSizeAndNonRegularSourcesRejectOnlyThatMod() throws IOException {
        Path big = tmp.resolve("gamma");
        Files.createDirectories(big.resolve("assets/gamma/shaders/block.properties"));
        // a directory where the file should be: not a regular file → gamma contributes nothing
        Path good = tmp.resolve("delta");
        Files.createDirectories(good.resolve("assets/delta/shaders"));
        Files.writeString(good.resolve("assets/delta/shaders/item.properties"), "item.1=delta:x\n");

        List<String> warned = new ArrayList<>();
        ModIdSourceSnapshot snapshot = ModSourceReader.read(List.of(
            new ModSourceReader.ModRoot("gamma", big), new ModSourceReader.ModRoot("delta", good)),
            (m, r) -> warned.add(m));
        assertEquals(List.of("gamma"), warned);
        assertEquals(1, snapshot.sources().size());
        assertEquals("delta", snapshot.sources().get(0).modId());
    }

    private static void put(ZipOutputStream zip, String name, String text) throws IOException {
        zip.putNextEntry(new ZipEntry(name));
        OutputStream out = zip;
        out.write(text.getBytes(StandardCharsets.ISO_8859_1));
        zip.closeEntry();
    }
}
