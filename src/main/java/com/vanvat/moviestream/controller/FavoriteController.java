package com.vanvat.moviestream.controller;

import com.vanvat.moviestream.exception.NotFoundException;
import com.vanvat.moviestream.model.UserMovieLink;
import com.vanvat.moviestream.service.FavoriteService;

import java.util.List;

public class FavoriteController {

    private final FavoriteService favoriteService;

    public FavoriteController(FavoriteService favoriteService) {
        this.favoriteService = favoriteService;
    }

    public void add(String userId, String movieId) throws NotFoundException {
        favoriteService.add(userId, movieId);
    }

    public void remove(String userId, String movieId) throws NotFoundException {
        favoriteService.remove(userId, movieId);
    }

    public List<UserMovieLink> list(String userId) {
        return favoriteService.getFavorites(userId);
    }
}
