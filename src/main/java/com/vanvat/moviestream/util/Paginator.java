package com.vanvat.moviestream.util;

import java.util.Collections;
import java.util.List;

/**
 * Generic utility for slicing collection results into paginated views.
 *
 * @param <T> element type
 */
public final class Paginator<T> {

    public record Page<T>(
            List<T> items,
            int pageNumber,
            int pageSize,
            int totalPages,
            int totalItems
    ) {}

    private Paginator() {
    }

    public static <T> Page<T> paginate(List<T> source, int pageNumber, int pageSize) {
        if (source == null || source.isEmpty()) {
            return new Page<>(Collections.emptyList(), 1, pageSize, 0, 0);
        }
        int validPageSize = Math.max(1, pageSize);
        int totalItems = source.size();
        int totalPages = (int) Math.ceil((double) totalItems / validPageSize);
        int validPageNumber = Math.max(1, Math.min(pageNumber, totalPages));

        int fromIndex = (validPageNumber - 1) * validPageSize;
        int toIndex = Math.min(fromIndex + validPageSize, totalItems);

        List<T> pageItems = source.subList(fromIndex, toIndex);
        return new Page<>(pageItems, validPageNumber, validPageSize, totalPages, totalItems);
    }
}
