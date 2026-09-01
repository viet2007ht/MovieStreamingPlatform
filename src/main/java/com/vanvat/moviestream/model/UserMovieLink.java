package com.vanvat.moviestream.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 * A (user, movie, addedAt) tuple. Reused for both the Watchlist and the
 * Favorites list, since both are structurally identical
 * "user bookmarked this movie at this time" records — only the file
 * they're persisted to differs.
 */
public class UserMovieLink {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    private String userId;
    private String movieId;
    private LocalDateTime addedAt;

    public UserMovieLink() {
    }

    public UserMovieLink(String userId, String movieId, LocalDateTime addedAt) {
        this.userId = userId;
        this.movieId = movieId;
        this.addedAt = addedAt;
    }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getMovieId() { return movieId; }
    public void setMovieId(String movieId) { this.movieId = movieId; }

    public LocalDateTime getAddedAt() { return addedAt; }
    public void setAddedAt(LocalDateTime addedAt) { this.addedAt = addedAt; }

    public String toFileLine() {
        return String.join("|", userId, movieId, addedAt.format(FMT));
    }

    public static UserMovieLink fromFileLine(String line) {
        String[] p = line.split("\\|", -1);
        return new UserMovieLink(p[0], p[1], LocalDateTime.parse(p[2], FMT));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserMovieLink)) return false;
        UserMovieLink that = (UserMovieLink) o;
        return Objects.equals(userId, that.userId) && Objects.equals(movieId, that.movieId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, movieId);
    }
}
