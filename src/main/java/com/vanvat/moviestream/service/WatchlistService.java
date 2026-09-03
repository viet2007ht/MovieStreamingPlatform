package com.vanvat.moviestream.service;

import com.vanvat.moviestream.command.AddToWatchlistCommand;
import com.vanvat.moviestream.command.CommandManager;
import com.vanvat.moviestream.command.RemoveFromWatchlistCommand;
import com.vanvat.moviestream.model.UserMovieLink;
import com.vanvat.moviestream.repository.UserMovieLinkRepository;

import java.util.List;

public class WatchlistService {

    private final UserMovieLinkRepository repository;
    private final CommandManager commandManager = new CommandManager();

    public WatchlistService(UserMovieLinkRepository repository) {
        this.repository = repository;
    }

    public void add(String userId, String movieId) {
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
