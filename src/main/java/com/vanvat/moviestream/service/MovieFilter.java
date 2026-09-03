package com.vanvat.moviestream.service;

import com.vanvat.moviestream.model.Movie;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Predicate;

/**
 * Builder for "Advanced movie filtering with multiple conditions".
 * Each with*() call adds one AND-ed predicate; call apply() to run them
 * all against a movie list. Because it composes java.util.function.Predicate,
 * it directly reuses the same building blocks as MovieService.search.
 */
public class MovieFilter {

    private final List<Predicate<Movie>> predicates = new ArrayList<>();

    public MovieFilter withCategory(String categoryId) {
        if (categoryId != null && !categoryId.isBlank()) {
            predicates.add(m -> m.getCategoryId().equals(categoryId));
        }
        return this;
    }

    public MovieFilter withMinRating(Double minRating) {
        if (minRating != null) {
            predicates.add(m -> m.getRating() >= minRating);
        }
        return this;
    }

    public MovieFilter withMaxRating(Double maxRating) {
        if (maxRating != null) {
            predicates.add(m -> m.getRating() <= maxRating);
        }
        return this;
    }

    public MovieFilter withYearRange(Integer fromYear, Integer toYear) {
        if (fromYear != null) {
            predicates.add(m -> m.getReleaseYear() >= fromYear);
        }
        if (toYear != null) {
            predicates.add(m -> m.getReleaseYear() <= toYear);
        }
        return this;
    }

    public MovieFilter withActor(String actorNameContains) {
        if (actorNameContains != null && !actorNameContains.isBlank()) {
            String q = actorNameContains.toLowerCase(Locale.ROOT);
            predicates.add(m -> m.getActors().stream()
                    .anyMatch(a -> a.toLowerCase(Locale.ROOT).contains(q)));
        }
        return this;
    }

    public MovieFilter withDirector(String directorContains) {
        if (directorContains != null && !directorContains.isBlank()) {
            String q = directorContains.toLowerCase(Locale.ROOT);
            predicates.add(m -> m.getDirector().toLowerCase(Locale.ROOT).contains(q));
        }
        return this;
    }

    public MovieFilter withMinDuration(Integer minMinutes) {
        if (minMinutes != null) {
            predicates.add(m -> m.getDurationMinutes() >= minMinutes);
        }
        return this;
    }

    public List<Movie> apply(List<Movie> movies) {
        return movies.stream()
                .filter(predicates.stream().reduce(Predicate::and).orElse(m -> true))
                .toList();
    }
}
