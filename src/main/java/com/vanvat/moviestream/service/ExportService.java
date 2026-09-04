package com.vanvat.moviestream.service;

import com.vanvat.moviestream.exception.NotFoundException;
import com.vanvat.moviestream.model.Movie;
import com.vanvat.moviestream.model.UserMovieLink;
import com.vanvat.moviestream.model.WatchHistoryEntry;
import com.vanvat.moviestream.util.CsvExporter;
import com.vanvat.moviestream.util.FileUtils;

import java.util.List;

/**
 * Service for generating CSV exports of application entities and user activity logs.
 */
public class ExportService {

    private final MovieService movieService;
    private final CategoryService categoryService;

    public ExportService(MovieService movieService, CategoryService categoryService) {
        this.movieService = movieService;
        this.categoryService = categoryService;
    }

    public String exportMoviesCsv(List<Movie> movies) {
        StringBuilder sb = new StringBuilder();
        sb.append(CsvExporter.buildRow("ID", "Title", "Director", "Actors", "Category", "Year", "Rating", "Duration", "Views", "Favorites")).append("\n");
        for (Movie m : movies) {
            String categoryName = "";
            try {
                categoryName = categoryService.getById(m.getCategoryId()).getName();
            } catch (NotFoundException ignored) {
            }
            sb.append(CsvExporter.buildRow(
                    m.getId(),
                    m.getTitle(),
                    m.getDirector(),
                    String.join("; ", m.getActors()),
                    categoryName,
                    m.getReleaseYear(),
                    m.getRating(),
                    m.getDurationMinutes(),
                    m.getViewCount(),
                    m.getFavoriteCount()
            )).append("\n");
        }
        return sb.toString();
    }

    public String exportWatchlistCsv(List<UserMovieLink> links) {
        StringBuilder sb = new StringBuilder();
        sb.append(CsvExporter.buildRow("User ID", "Movie ID", "Movie Title")).append("\n");
        for (UserMovieLink link : links) {
            String title = "";
            try {
                title = movieService.getById(link.getMovieId()).getTitle();
            } catch (NotFoundException ignored) {
            }
            sb.append(CsvExporter.buildRow(link.getUserId(), link.getMovieId(), title)).append("\n");
        }
        return sb.toString();
    }

    public String exportHistoryCsv(List<WatchHistoryEntry> history) {
        StringBuilder sb = new StringBuilder();
        sb.append(CsvExporter.buildRow("User ID", "Movie ID", "Movie Title", "Watched At", "Position (sec)", "Total Duration (sec)")).append("\n");
        for (WatchHistoryEntry entry : history) {
            String title = "";
            try {
                title = movieService.getById(entry.getMovieId()).getTitle();
            } catch (NotFoundException ignored) {
            }
            sb.append(CsvExporter.buildRow(
                    entry.getUserId(),
                    entry.getMovieId(),
                    title,
                    entry.getWatchedAt().toString(),
                    entry.getPositionSeconds(),
                    entry.getTotalSeconds()
            )).append("\n");
        }
        return sb.toString();
    }

    public void saveToFile(String content, String filePath) {
        FileUtils.writeLines(filePath, List.of(content.split("\n")));
    }
}
