package com.vanvat.moviestream.repository;

import com.vanvat.moviestream.model.UserMovieLink;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Backs both the Watchlist and the Favorites list — instantiate one
 * per file (e.g. "data/watchlist.txt" and "data/favorites.txt").
 * The composite (userId, movieId) pair is treated as the id, joined
 * with ':' — see {@link #key}.
 */
public class UserMovieLinkRepository extends AbstractFileRepository<UserMovieLink, String> {

    public UserMovieLinkRepository(String filePath) {
        super(filePath, UserMovieLink::toFileLine, UserMovieLink::fromFileLine,
                link -> key(link.getUserId(), link.getMovieId()));
    }

    public static String key(String userId, String movieId) {
        return userId + ":" + movieId;
    }

    public List<UserMovieLink> findByUserId(String userId) {
        return findAll().stream()
                .filter(l -> l.getUserId().equals(userId))
                .collect(Collectors.toList());
    }

    public void removeLink(String userId, String movieId) {
        deleteById(key(userId, movieId));
    }

    public boolean exists(String userId, String movieId) {
        return existsById(key(userId, movieId));
    }
}
