package com.vanvat.moviestream.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Core Movie entity.
 *
 * viewCount / favoriteCount are denormalized counters maintained by the
 * service layer (incremented when a user watches / favorites the movie)
 * so ranking/statistics don't need to re-scan history files every time.
 */
public class Movie {

    private String id;
    private String title;
    private String director;
    private List<String> actors = new ArrayList<>();
    private String categoryId;
    private int releaseYear;
    private double rating;          // 0.0 - 10.0
    private int durationMinutes;
    private String description;
    private int viewCount;
    private int favoriteCount;

    public Movie() {
    }

    public Movie(String id, String title, String director, List<String> actors,
                 String categoryId, int releaseYear, double rating,
                 int durationMinutes, String description) {
        this.id = id;
        this.title = title;
        this.director = director;
        this.actors = actors != null ? actors : new ArrayList<>();
        this.categoryId = categoryId;
        this.releaseYear = releaseYear;
        this.rating = rating;
        this.durationMinutes = durationMinutes;
        this.description = description;
        this.viewCount = 0;
        this.favoriteCount = 0;
    }

    // --- getters/setters ---

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDirector() { return director; }
    public void setDirector(String director) { this.director = director; }

    public List<String> getActors() { return actors; }
    public void setActors(List<String> actors) { this.actors = actors; }

    public String getCategoryId() { return categoryId; }
    public void setCategoryId(String categoryId) { this.categoryId = categoryId; }

    public int getReleaseYear() { return releaseYear; }
    public void setReleaseYear(int releaseYear) { this.releaseYear = releaseYear; }

    public double getRating() { return rating; }
    public void setRating(double rating) { this.rating = rating; }

    public int getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(int durationMinutes) { this.durationMinutes = durationMinutes; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public int getViewCount() { return viewCount; }
    public void setViewCount(int viewCount) { this.viewCount = viewCount; }
    public void incrementViewCount() { this.viewCount++; }

    public int getFavoriteCount() { return favoriteCount; }
    public void setFavoriteCount(int favoriteCount) { this.favoriteCount = favoriteCount; }
    public void incrementFavoriteCount() { this.favoriteCount++; }
    public void decrementFavoriteCount() { if (this.favoriteCount > 0) this.favoriteCount--; }

    /**
     * Popularity is a simple derived metric used for the "sort by
     * popularity" requirement, distinct from the weighted ranking score
     * used for "Automatically generate movie ranking" (see RankingService).
     */
    public int getPopularity() {
        return viewCount + favoriteCount;
    }

    // --- file (de)serialization ---
    // Format: id|title|director|actor1,actor2,...|categoryId|year|rating|duration|viewCount|favoriteCount|description

    public String toFileLine() {
        return String.join("|",
                id,
                escape(title),
                escape(director),
                escape(String.join(",", actors)),
                categoryId,
                String.valueOf(releaseYear),
                String.valueOf(rating),
                String.valueOf(durationMinutes),
                String.valueOf(viewCount),
                String.valueOf(favoriteCount),
                escape(description == null ? "" : description));
    }

    public static Movie fromFileLine(String line) {
        String[] p = line.split("\\|", -1);
        Movie m = new Movie();
        m.id = p[0];
        m.title = unescape(p[1]);
        m.director = unescape(p[2]);
        String actorsField = unescape(p[3]);
        m.actors = actorsField.isEmpty() ? new ArrayList<>() : new ArrayList<>(Arrays.asList(actorsField.split(",")));
        m.categoryId = p[4];
        m.releaseYear = Integer.parseInt(p[5]);
        m.rating = Double.parseDouble(p[6]);
        m.durationMinutes = Integer.parseInt(p[7]);
        m.viewCount = Integer.parseInt(p[8]);
        m.favoriteCount = Integer.parseInt(p[9]);
        m.description = p.length > 10 ? unescape(p[10]) : "";
        return m;
    }

    private static String escape(String value) {
        if (value == null) return "";
        return value.replace("\\", "\\\\").replace("|", "\\p").replace("\n", "\\n");
    }

    private static String unescape(String value) {
        return value.replace("\\n", "\n").replace("\\p", "|").replace("\\\\", "\\");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Movie)) return false;
        Movie movie = (Movie) o;
        return Objects.equals(id, movie.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("[%s] %s (%d) - %.1f*", id, title, releaseYear, rating);
    }
}
