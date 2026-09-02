package com.vanvat.moviestream.command;

import com.vanvat.moviestream.model.UserMovieLink;
import com.vanvat.moviestream.repository.UserMovieLinkRepository;

import java.time.LocalDateTime;

public class RemoveFromWatchlistCommand implements Command {

    private final UserMovieLinkRepository watchlistRepository;
    private final String userId;
    private final String movieId;
    private LocalDateTime removedEntryAddedAt; // captured at execute() time, for undo

    public RemoveFromWatchlistCommand(UserMovieLinkRepository watchlistRepository,
                                       String userId, String movieId) {
        this.watchlistRepository = watchlistRepository;
        this.userId = userId;
        this.movieId = movieId;
    }

    @Override
    public void execute() {
        watchlistRepository.findById(UserMovieLinkRepository.key(userId, movieId))
                .ifPresent(link -> removedEntryAddedAt = link.getAddedAt());
        watchlistRepository.removeLink(userId, movieId);
    }

    @Override
    public void undo() {
        LocalDateTime restoredTimestamp = removedEntryAddedAt != null ? removedEntryAddedAt : LocalDateTime.now();
        watchlistRepository.save(new UserMovieLink(userId, movieId, restoredTimestamp));
    }

    @Override
    public String describe() {
        return "Remove movie " + movieId + " from watchlist";
    }
}
