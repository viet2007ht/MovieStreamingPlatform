package com.vanvat.moviestream.util;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Helper utility for escaping and formatting CSV fields according to RFC 4180.
 */
public final class CsvExporter {

    private CsvExporter() {
    }

    public static String escapeCsv(String field) {
        if (field == null) {
            return "";
        }
        if (field.contains(",") || field.contains("\"") || field.contains("\n") || field.contains("\r")) {
            return "\"" + field.replace("\"", "\"\"") + "\"";
        }
        return field;
    }

    public static String buildRow(Object... fields) {
        return Arrays.stream(fields)
                .map(f -> escapeCsv(f == null ? "" : f.toString()))
                .collect(Collectors.joining(","));
    }
}
