package com.vanvat.moviestream.controller;

import com.vanvat.moviestream.exception.NotFoundException;
import com.vanvat.moviestream.exception.ValidationException;
import com.vanvat.moviestream.model.Movie;
import com.vanvat.moviestream.service.MovieFilter;
import com.vanvat.moviestream.service.MovieService;
import com.vanvat.moviestream.service.RankingService;

import java.util.List;

/**
 * Mediates between the console View and MovieService/RankingService.
 * Controllers stay thin on purpose: no file I/O, no business rules here,
 * just request/response shaping — that's what keeps this MVC rather than
 * "MVC in name only".
 */
public class MovieController {

    private final MovieService movieService;
    private final RankingService rankingService;

    public MovieController(MovieService movieService, RankingService rankingService) {
        this.movieService = movieService;
        this.rankingService = rankingService;
    }

    public Movie addMovie(Movie movie) throws ValidationException {
        return movieService.create(movie);
    }

    public Movie editMovie(String id, Movie updated) throws ValidationException, NotFoundException {
        return movieService.update(id, updated);
    }

    public void deleteMovie(String id) throws NotFoundException {
        movieService.delete(id);
    }

    public Movie viewMovieDetails(String id) throws NotFoundException {
        return movieService.getById(id);
    }

    public List<Movie> listAll() {
        return movieService.getAll();
    }

    public List<Movie> search(String field, String query) {
        return movieService.search(field, query);
    }

    public List<Movie> sort(List<Movie> movies, MovieService.SortField field, boolean ascending) {
        return movieService.sort(movies, field, ascending);
    }

    public List<Movie> browseByCategory(String categoryId) {
        return movieService.browseByCategory(categoryId);
    }

    public List<Movie> filter(MovieFilter filter) {
        return filter.apply(movieService.getAll());
    }

    public List<Movie> generateRanking() {
        return rankingService.rank(movieService.getAll());
    }
}
