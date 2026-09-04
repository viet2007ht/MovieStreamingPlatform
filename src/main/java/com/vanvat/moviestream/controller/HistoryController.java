package com.vanvat.moviestream.controller;

import com.vanvat.moviestream.exception.NotFoundException;
import com.vanvat.moviestream.model.WatchHistoryEntry;
import com.vanvat.moviestream.service.ReportService;
import com.vanvat.moviestream.service.WatchHistoryService;

import java.util.List;
import java.util.Map;

public class HistoryController {

    private final WatchHistoryService historyService;
    private final ReportService reportService;

    public HistoryController(WatchHistoryService historyService, ReportService reportService) {
        this.historyService = historyService;
        this.reportService = reportService;
    }

    public void recordWatch(String userId, String movieId, int positionSeconds) throws NotFoundException {
        historyService.recordWatch(userId, movieId, positionSeconds);
    }

    public List<WatchHistoryEntry> getHistory(String userId) {
        return historyService.getHistory(userId);
    }

    public List<WatchHistoryEntry> getRecentlyWatched(String userId, int limit) {
        return historyService.getRecentlyWatched(userId, limit);
    }

    public List<WatchHistoryEntry> getContinueWatching(String userId) {
        return historyService.getContinueWatching(userId);
    }

    public List<Map.Entry<String, Long>> getTrendingCategories() {
        return historyService.getTrendingCategories();
    }

    public String generateReport(String userId) {
        return reportService.generateUserReport(userId);
    }
}
