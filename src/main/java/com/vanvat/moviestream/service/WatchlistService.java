package com.vanvat.moviestream.service;

import com.vanvat.moviestream.command.AddToWatchlistCommand;
import com.vanvat.moviestream.command.CommandManager;
import com.vanvat.moviestream.command.RemoveFromWatchlistCommand;
import com.vanvat.moviestream.exception.NotFoundException;
import com.vanvat.moviestream.model.UserMovieLink;
import com.vanvat.moviestream.repository.UserMovieLinkRepository;

import java.util.List;

public class WatchlistService {

    private final UserMovieLinkRepository repository;
    private final MovieService movieService;
    private final CommandManager commandManager = new CommandManager();

    public WatchlistService(UserMovieLinkRepository repository, MovieService movieService) {
        this.repository = repository;
        this.movieService = movieService;
    }

    /**
     * Adds a movie to the watchlist. Validates the movie actually exists
     * first — previously this was missing, so a typo'd or already-deleted
     * movieId would sit silently in watchlist.txt with no way to surface it.
     */
    public void add(String userId, String movieId) throws NotFoundException {
        movieService.getById(movieId); // throws NotFoundException if the movie doesn't exist
        if (repository.exists(userId, movieId)) {
            return; // already on the watchlist, no-op
        }
        commandManager.run(new AddToWatchlistCommand(repository, userId, movieId));
    }

    public void remove(String userId, String movieId) {
        if (!repository.exists(userId, movieId)) {
            return;
        }
        commandManager.run(new RemoveFromWatchlistCommand(repository, userId, movieId));
    }

    public List<UserMovieLink> getWatchlist(String userId) {
        return repository.findByUserId(userId);
    }

    public String undo() {
        return commandManager.undo();
    }

    public String redo() {
        return commandManager.redo();
    }

    public boolean canUndo() { return commandManager.canUndo(); }
    public boolean canRedo() { return commandManager.canRedo(); }
}
