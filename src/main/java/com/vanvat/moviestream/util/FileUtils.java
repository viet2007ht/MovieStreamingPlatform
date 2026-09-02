package com.vanvat.moviestream.util;

import com.vanvat.moviestream.exception.DataAccessException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Low-level line-based file access shared by all repositories.
 * This is the single place that touches java.nio/java.io, so the
 * "store and load data using files only" requirement lives in one
 * auditable spot.
 */
public final class FileUtils {

    private FileUtils() {
    }

    public static List<String> readLines(String path) {
        Path p = Paths.get(path);
        try {
            ensureFileExists(p);
            List<String> lines = new ArrayList<>();
            for (String line : Files.readAllLines(p, StandardCharsets.UTF_8)) {
                if (!line.isBlank()) {
                    lines.add(line);
                }
            }
            return lines;
        } catch (IOException e) {
            throw new DataAccessException("Failed to read data file: " + path, e);
        }
    }

    public static void writeLines(String path, List<String> lines) {
        Path p = Paths.get(path);
        try {
            ensureFileExists(p);
            Files.write(p, lines, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new DataAccessException("Failed to write data file: " + path, e);
        }
    }

    public static void appendLine(String path, String line) {
        Path p = Paths.get(path);
        try {
            ensureFileExists(p);
            Files.write(p, List.of(line), StandardCharsets.UTF_8,
                    java.nio.file.StandardOpenOption.APPEND);
        } catch (IOException e) {
            throw new DataAccessException("Failed to append to data file: " + path, e);
        }
    }

    private static void ensureFileExists(Path p) throws IOException {
        if (p.getParent() != null) {
            Files.createDirectories(p.getParent());
        }
        if (!Files.exists(p)) {
            Files.createFile(p);
        }
    }
}
