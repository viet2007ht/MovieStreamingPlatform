package com.vanvat.moviestream.repository;

import com.vanvat.moviestream.model.Movie;

import java.util.List;

public class MovieRepository extends AbstractFileRepository<Movie, String> {

    public MovieRepository(String filePath) {
        super(filePath, Movie::toFileLine, Movie::fromFileLine, Movie::getId);
    }

    @Override
    public List<Movie> findAll() {
        return super.findAll().stream()
                .filter(m -> !m.isDeleted())
                .toList();
    }

    public List<Movie> findAllIncludingDeleted() {
        return super.findAll();
    }
}
