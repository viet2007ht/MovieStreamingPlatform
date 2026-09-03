package com.vanvat.moviestream.service;

import com.vanvat.moviestream.model.Movie;

import java.util.Comparator;
import java.util.List;

/**
 * Generates a movie ranking from a weighted combination of rating,
 * view count, and favorite count.
 *
 * Raw counts and the 0-10 rating live on very different scales, so each
 * component is min-max normalized to [0,1] across the current movie set
 * before weighting — otherwise view counts (which can run into the
 * thousands) would completely drown out the rating.
 */
public class RankingService {

    private static final double WEIGHT_RATING = 0.5;
    private static final double WEIGHT_VIEWS = 0.3;
    private static final double WEIGHT_FAVORITES = 0.2;

    public List<Movie> rank(List<Movie> movies) {
        if (movies.isEmpty()) {
            return movies;
        }

        double maxRating = movies.stream().mapToDouble(Movie::getRating).max().orElse(1);
        int maxViews = movies.stream().mapToInt(Movie::getViewCount).max().orElse(1);
        int maxFavorites = movies.stream().mapToInt(Movie::getFavoriteCount).max().orElse(1);

        Comparator<Movie> byScoreDesc = Comparator
                .comparingDouble((Movie m) -> score(m, maxRating, maxViews, maxFavorites))
                .reversed();

        return movies.stream().sorted(byScoreDesc).toList();
    }

    public double score(Movie m, double maxRating, int maxViews, int maxFavorites) {
        double normRating = maxRating == 0 ? 0 : m.getRating() / maxRating;
        double normViews = maxViews == 0 ? 0 : (double) m.getViewCount() / maxViews;
        double normFavorites = maxFavorites == 0 ? 0 : (double) m.getFavoriteCount() / maxFavorites;
        return WEIGHT_RATING * normRating + WEIGHT_VIEWS * normViews + WEIGHT_FAVORITES * normFavorites;
    }
}
