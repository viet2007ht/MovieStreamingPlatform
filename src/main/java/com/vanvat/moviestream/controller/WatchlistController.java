package com.vanvat.moviestream.controller;

import com.vanvat.moviestream.model.UserMovieLink;
import com.vanvat.moviestream.service.WatchlistService;

import java.util.List;

public class WatchlistController {

    private final WatchlistService watchlistService;

    public WatchlistController(WatchlistService watchlistService) {
        this.watchlistService = watchlistService;
    }

    public void add(String userId, String movieId) {
        watchlistService.add(userId, movieId);
    }

    public void remove(String userId, String movieId) {
        watchlistService.remove(userId, movieId);
    }

    public List<UserMovieLink> list(String userId) {
        return watchlistService.getWatchlist(userId);
    }

    public String undo() {
        return watchlistService.undo();
    }

    public String redo() {
        return watchlistService.redo();
    }
}
