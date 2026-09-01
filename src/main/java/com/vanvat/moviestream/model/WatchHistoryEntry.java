package com.vanvat.moviestream.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * One record of a user watching (part of) a movie.
 * Powers: Watching history, Continue watching, Recently watched,
 * Viewing statistics, Trending categories, Viewing reports.
 */
public class WatchHistoryEntry {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    private String id;
    private String userId;
    private String movieId;
    private LocalDateTime watchedAt;
    private int positionSeconds;   // how far the user got
    private int totalSeconds;      // movie duration in seconds, for completion %

    public WatchHistoryEntry() {
    }

    public WatchHistoryEntry(String id, String userId, String movieId, LocalDateTime watchedAt,
                              int positionSeconds, int totalSeconds) {
        this.id = id;
        this.userId = userId;
        this.movieId = movieId;
        this.watchedAt = watchedAt;
        this.positionSeconds = positionSeconds;
        this.totalSeconds = totalSeconds;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getMovieId() { return movieId; }
    public void setMovieId(String movieId) { this.movieId = movieId; }

    public LocalDateTime getWatchedAt() { return watchedAt; }
    public void setWatchedAt(LocalDateTime watchedAt) { this.watchedAt = watchedAt; }

    public int getPositionSeconds() { return positionSeconds; }
    public void setPositionSeconds(int positionSeconds) { this.positionSeconds = positionSeconds; }

    public int getTotalSeconds() { return totalSeconds; }
    public void setTotalSeconds(int totalSeconds) { this.totalSeconds = totalSeconds; }

    /** True if the user stopped before finishing -> eligible for "Continue watching". */
    public boolean isInProgress() {
        return totalSeconds > 0 && positionSeconds < totalSeconds;
    }

    public String toFileLine() {
        return String.join("|",
                id, userId, movieId, watchedAt.format(FMT),
                String.valueOf(positionSeconds), String.valueOf(totalSeconds));
    }

    public static WatchHistoryEntry fromFileLine(String line) {
        String[] p = line.split("\\|", -1);
        WatchHistoryEntry e = new WatchHistoryEntry();
        e.id = p[0];
        e.userId = p[1];
        e.movieId = p[2];
        e.watchedAt = LocalDateTime.parse(p[3], FMT);
        e.positionSeconds = Integer.parseInt(p[4]);
        e.totalSeconds = Integer.parseInt(p[5]);
        return e;
    }
}
