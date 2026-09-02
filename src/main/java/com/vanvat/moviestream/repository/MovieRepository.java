package com.vanvat.moviestream.repository;

import com.vanvat.moviestream.model.Movie;

public class MovieRepository extends AbstractFileRepository<Movie, String> {

    public MovieRepository(String filePath) {
        super(filePath, Movie::toFileLine, Movie::fromFileLine, Movie::getId);
    }
}
