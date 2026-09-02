package com.vanvat.moviestream.command;

import com.vanvat.moviestream.model.UserMovieLink;
import com.vanvat.moviestream.repository.UserMovieLinkRepository;

import java.time.LocalDateTime;

public class AddToWatchlistCommand implements Command {

    private final UserMovieLinkRepository watchlistRepository;
    private final String userId;
    private final String movieId;

    public AddToWatchlistCommand(UserMovieLinkRepository watchlistRepository,
                                  String userId, String movieId) {
        this.watchlistRepository = watchlistRepository;
        this.userId = userId;
        this.movieId = movieId;
    }

    @Override
    public void execute() {
        watchlistRepository.save(new UserMovieLink(userId, movieId, LocalDateTime.now()));
    }

    @Override
    public void undo() {
        watchlistRepository.removeLink(userId, movieId);
    }

    @Override
    public String describe() {
        return "Add movie " + movieId + " to watchlist";
    }
}
