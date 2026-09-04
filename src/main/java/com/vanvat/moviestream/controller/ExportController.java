package com.vanvat.moviestream.controller;

import com.vanvat.moviestream.model.Movie;
import com.vanvat.moviestream.model.UserMovieLink;
import com.vanvat.moviestream.model.WatchHistoryEntry;
import com.vanvat.moviestream.service.ExportService;

import java.util.List;

public class ExportController {

    private final ExportService exportService;

    public ExportController(ExportService exportService) {
        this.exportService = exportService;
    }

    public void exportMovies(List<Movie> movies, String targetPath) {
        String csv = exportService.exportMoviesCsv(movies);
        exportService.saveToFile(csv, targetPath);
    }

    public void exportWatchlist(List<UserMovieLink> watchlist, String targetPath) {
        String csv = exportService.exportWatchlistCsv(watchlist);
        exportService.saveToFile(csv, targetPath);
    }

    public void exportHistory(List<WatchHistoryEntry> history, String targetPath) {
        String csv = exportService.exportHistoryCsv(history);
        exportService.saveToFile(csv, targetPath);
    }
}
