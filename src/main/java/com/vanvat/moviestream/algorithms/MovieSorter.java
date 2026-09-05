package com.vanvat.moviestream.algorithms;

import com.vanvat.moviestream.model.Movie;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Manual merge-sort implementation for {@link Movie} lists.
 *
 * <h3>Why merge sort?</h3>
 * <ul>
 *   <li>O(n log n) worst-case — suitable for arbitrary-size movie catalogs.</li>
 *   <li>Stable — equal-scored movies retain their original relative order,
 *       which matters for multi-criteria ranking (rating + views + favorites).</li>
 *   <li>Works on any {@link Comparator}, so the same algorithm drives both
 *       single-field sorting and weighted ranking.</li>
 * </ul>
 *
 * <p>The original list is never mutated; a new sorted list is returned.
 */
public final class MovieSorter {

    private MovieSorter() {
        // utility class — no instances
    }

    /**
     * Sorts {@code movies} using merge sort with the given {@code comparator}.
     *
     * @param movies     source list (not modified)
     * @param comparator ordering to apply
     * @return a new list containing the same movies in sorted order
     */
    public static List<Movie> mergeSort(List<Movie> movies, Comparator<Movie> comparator) {
        if (movies.size() <= 1) {
            return new ArrayList<>(movies);
        }
        int mid = movies.size() / 2;
        List<Movie> left  = mergeSort(movies.subList(0, mid), comparator);
        List<Movie> right = mergeSort(movies.subList(mid, movies.size()), comparator);
        return merge(left, right, comparator);
    }

    // ---------------------------------------------------------------- internals

    /**
     * Merges two already-sorted sub-lists into one sorted list.
     * Standard O(n) two-pointer merge.
     */
    private static List<Movie> merge(List<Movie> left,
                                     List<Movie> right,
                                     Comparator<Movie> comparator) {
        List<Movie> result = new ArrayList<>(left.size() + right.size());
        int i = 0, j = 0;

        while (i < left.size() && j < right.size()) {
            // ≤ 0 keeps equal elements in their original order (stability)
            if (comparator.compare(left.get(i), right.get(j)) <= 0) {
                result.add(left.get(i++));
            } else {
                result.add(right.get(j++));
            }
        }

        // Drain remaining elements from whichever half still has items
        while (i < left.size())  result.add(left.get(i++));
        while (j < right.size()) result.add(right.get(j++));

        return result;
    }
}
