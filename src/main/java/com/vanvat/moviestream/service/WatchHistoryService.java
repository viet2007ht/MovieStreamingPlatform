package com.vanvat.moviestream.service;

import com.vanvat.moviestream.exception.NotFoundException;
import com.vanvat.moviestream.model.Movie;
import com.vanvat.moviestream.model.WatchHistoryEntry;
import com.vanvat.moviestream.repository.WatchHistoryRepository;
import com.vanvat.moviestream.util.IdGenerator;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class WatchHistoryService {

    private final WatchHistoryRepository repository;
    private final MovieService movieService;
    private final IdGenerator idGenerator;

    public WatchHistoryService(WatchHistoryRepository repository, MovieService movieService) {
        this.repository = repository;
        this.movieService = movieService;
        int maxId = repository.findAll().stream()
                .map(e -> e.getId().replace("WH-", ""))
                .mapToInt(s -> s.matches("\\d+") ? Integer.parseInt(s) : 0)
                .max().orElse(0);
        this.idGenerator = new IdGenerator("WH", maxId);
    }

    /** Records (or updates) progress on a movie and bumps its view counter on first watch. */
    public void recordWatch(String userId, String movieId, int positionSeconds) throws NotFoundException {
        Movie movie = movieService.getById(movieId);
        boolean firstWatch = repository.findByUserId(userId).stream()
                .noneMatch(e -> e.getMovieId().equals(movieId));

        WatchHistoryEntry entry = new WatchHistoryEntry(
                idGenerator.next(), userId, movieId, LocalDateTime.now(),
                positionSeconds, movie.getDurationMinutes() * 60);
        repository.save(entry);

        if (firstWatch) {
            movie.incrementViewCount();
            movieService.getRepository().save(movie);
        }
    }

    public List<WatchHistoryEntry> getHistory(String userId) {
        return repository.findByUserId(userId).stream()
                .sorted(Comparator.comparing(WatchHistoryEntry::getWatchedAt).reversed())
                .toList();
    }

    /** Most recent entry per movie, most recent movie first. */
    public List<WatchHistoryEntry> getRecentlyWatched(String userId, int limit) {
        Map<String, WatchHistoryEntry> latestPerMovie = repository.findByUserId(userId).stream()
                .collect(Collectors.toMap(
                        WatchHistoryEntry::getMovieId,
                        e -> e,
                        (a, b) -> a.getWatchedAt().isAfter(b.getWatchedAt()) ? a : b));
        return latestPerMovie.values().stream()
                .sorted(Comparator.comparing(WatchHistoryEntry::getWatchedAt).reversed())
                .limit(limit)
                .toList();
    }

    /** Movies the user started but hasn't finished, most recently touched first. */
    public List<WatchHistoryEntry> getContinueWatching(String userId) {
        return getRecentlyWatched(userId, Integer.MAX_VALUE).stream()
                .filter(WatchHistoryEntry::isInProgress)
                .toList();
    }

    /**
     * Simple aggregate stats: total watch time and per-movie watch counts
     * for a user, feeding "Viewing statistics" and "Generate viewing
     * reports".
     */
    public Map<String, Long> getWatchCountsByMovie(String userId) {
        return repository.findByUserId(userId).stream()
                .collect(Collectors.groupingBy(WatchHistoryEntry::getMovieId, Collectors.counting()));
    }

    public long getTotalWatchTimeSeconds(String userId) {
        return repository.findByUserId(userId).stream()
                .mapToLong(WatchHistoryEntry::getPositionSeconds)
                .sum();
    }

    /**
     * Trending categories: ranks categories by how many watch events
     * (across all users) their movies received. Ties are naturally broken
     * by category id order from the grouping map.
     */
    public List<Map.Entry<String, Long>> getTrendingCategories() {
        Map<String, String> movieToCategory = movieService.getAll().stream()
                .collect(Collectors.toMap(Movie::getId, Movie::getCategoryId));

        return repository.findAll().stream()
                .map(e -> movieToCategory.get(e.getMovieId()))
                .filter(java.util.Objects::nonNull)
                .collect(Collectors.groupingBy(c -> c, Collectors.counting()))
                .entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .toList();
    }
}
