package com.vanvat.moviestream.service;

import com.vanvat.moviestream.exception.NotFoundException;
import com.vanvat.moviestream.model.Movie;
import com.vanvat.moviestream.model.WatchHistoryEntry;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

/**
 * Builds a human-readable "Generate viewing reports based on user history"
 * report. Kept separate from WatchHistoryService (which owns the raw
 * data/aggregation) so this class only owns presentation/formatting.
 */
public class ReportService {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final WatchHistoryService historyService;
    private final MovieService movieService;

    public ReportService(WatchHistoryService historyService, MovieService movieService) {
        this.historyService = historyService;
        this.movieService = movieService;
    }

    public String generateUserReport(String userId) {
        List<WatchHistoryEntry> history = historyService.getHistory(userId);
        Map<String, Long> countsByMovie = historyService.getWatchCountsByMovie(userId);
        long totalSeconds = historyService.getTotalWatchTimeSeconds(userId);

        StringBuilder sb = new StringBuilder();
        sb.append("=== Viewing Report for user: ").append(userId).append(" ===\n");
        sb.append(String.format("Total watch events : %d%n", history.size()));
        sb.append(String.format("Distinct movies     : %d%n", countsByMovie.size()));
        sb.append(String.format("Total watch time    : %d min%n", totalSeconds / 60));
        sb.append("\n-- Most watched movies --\n");

        countsByMovie.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(5)
                .forEach(e -> {
                    String title = safeTitleOf(e.getKey());
                    sb.append(String.format("  %-30s x%d%n", title, e.getValue()));
                });

        sb.append("\n-- Recent activity --\n");
        history.stream().limit(10).forEach(e -> sb.append(String.format(
                "  %s | %-30s | %d/%d sec%n",
                e.getWatchedAt().format(FMT), safeTitleOf(e.getMovieId()),
                e.getPositionSeconds(), e.getTotalSeconds())));

        return sb.toString();
    }

    private String safeTitleOf(String movieId) {
        try {
            Movie m = movieService.getById(movieId);
            return m.getTitle();
        } catch (NotFoundException e) {
            return "(deleted movie " + movieId + ")";
        }
    }
}
