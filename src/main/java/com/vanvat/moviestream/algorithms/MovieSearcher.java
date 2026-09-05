package com.vanvat.moviestream.algorithms;

import com.vanvat.moviestream.model.Movie;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Manual linear-search implementation for {@link Movie} lists.
 *
 * <h3>Why linear search?</h3>
 * <ul>
 *   <li>The movie list is <em>not</em> sorted by the searched field, so
 *       binary search cannot be applied without a separate index.</li>
 *   <li>Substring matching (contains) requires inspecting every record
 *       regardless, making O(n) the theoretical minimum.</li>
 *   <li>Dataset sizes in a course project are modest, so O(n) is fast
 *       enough without the overhead of maintaining a sorted index.</li>
 * </ul>
 *
 * <p>All comparisons are case-insensitive (lower-cased with ROOT locale)
 * and substring-based so partial names still match.
 */
public final class MovieSearcher {

    private MovieSearcher() {
        // utility class — no instances
    }

    /**
     * Performs a linear scan of {@code movies}, returning every movie whose
     * {@code field} contains {@code query} as a case-insensitive substring.
     *
     * @param movies source list (not modified)
     * @param field  one of {@code "title"}, {@code "actor"},
     *               {@code "director"}, {@code "genre"} / {@code "category"}
     * @param query  search term (leading/trailing whitespace is stripped)
     * @return a new list of matching movies, in original order
     * @throws IllegalArgumentException if {@code field} is not recognised
     */
    public static List<Movie> linearSearch(List<Movie> movies, String field, String query) {
        String q = query.trim().toLowerCase(Locale.ROOT);
        List<Movie> results = new ArrayList<>();

        for (Movie m : movies) {               // explicit loop — no stream/filter
            if (fieldMatches(m, field, q)) {
                results.add(m);
            }
        }

        return results;
    }

    // ---------------------------------------------------------------- internals

    /**
     * Returns {@code true} if the given field of {@code movie} contains
     * {@code q} (already lower-cased).
     */
    private static boolean fieldMatches(Movie m, String field, String q) {
        return switch (field.toLowerCase(Locale.ROOT)) {
            case "title"            -> m.getTitle().toLowerCase(Locale.ROOT).contains(q);
            case "actor"            -> anyActorContains(m, q);
            case "director"         -> m.getDirector().toLowerCase(Locale.ROOT).contains(q);
            case "genre", "category"-> m.getCategoryId().toLowerCase(Locale.ROOT).contains(q);
            default -> throw new IllegalArgumentException("Unknown search field: " + field);
        };
    }

    /**
     * Iterates the actor list with an explicit loop (no stream) and returns
     * {@code true} as soon as one actor name contains {@code q}.
     */
    private static boolean anyActorContains(Movie m, String q) {
        for (String actor : m.getActors()) {   // explicit loop — no stream
            if (actor.toLowerCase(Locale.ROOT).contains(q)) {
                return true;
            }
        }
        return false;
    }
}
