package com.vanvat.moviestream.repository;

import com.vanvat.moviestream.model.WatchHistoryEntry;

import java.util.List;
import java.util.stream.Collectors;

public class WatchHistoryRepository extends AbstractFileRepository<WatchHistoryEntry, String> {

    public WatchHistoryRepository(String filePath) {
        super(filePath, WatchHistoryEntry::toFileLine, WatchHistoryEntry::fromFileLine,
                WatchHistoryEntry::getId);
    }

    public List<WatchHistoryEntry> findByUserId(String userId) {
        return findAll().stream()
                .filter(e -> e.getUserId().equals(userId))
                .collect(Collectors.toList());
    }
}
