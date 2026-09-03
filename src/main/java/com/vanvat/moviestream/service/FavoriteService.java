package com.vanvat.moviestream.service;

import com.vanvat.moviestream.exception.NotFoundException;
import com.vanvat.moviestream.model.Movie;
import com.vanvat.moviestream.model.UserMovieLink;
import com.vanvat.moviestream.repository.UserMovieLinkRepository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Favorites are structurally the same as the watchlist (a UserMovieLink)
 * but deliberately kept undo/redo-free — the requirement only asks for
 * Undo/Redo on watchlist modifications — and additionally keeps
 * Movie.favoriteCount in sync since that feeds the ranking algorithm.
 */
public class FavoriteService {

    private final UserMovieLinkRepository repository;
    private final MovieService movieService;

    public FavoriteService(UserMovieLinkRepository repository, MovieService movieService) {
        this.repository = repository;
        this.movieService = movieService;
    }

    public void add(String userId, String movieId) throws NotFoundException {
        if (repository.exists(userId, movieId)) {
            return;
        }
        Movie movie = movieService.getById(movieId); // validates movie exists
        repository.save(new UserMovieLink(userId, movieId, LocalDateTime.now()));
        movie.incrementFavoriteCount();
        movieService.getRepository().save(movie);
    }

    public void remove(String userId, String movieId) throws NotFoundException {
        if (!repository.exists(userId, movieId)) {
            return;
        }
        Movie movie = movieService.getById(movieId);
        repository.removeLink(userId, movieId);
        movie.decrementFavoriteCount();
        movieService.getRepository().save(movie);
    }

    public List<UserMovieLink> getFavorites(String userId) {
        return repository.findByUserId(userId);
    }
}
