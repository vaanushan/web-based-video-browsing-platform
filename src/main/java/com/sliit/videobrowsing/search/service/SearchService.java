package com.sliit.videobrowsing.search.service;

import com.sliit.videobrowsing.search.entity.SearchHistory;
import com.sliit.videobrowsing.search.repository.SearchHistoryRepository;
import com.sliit.videobrowsing.video.entity.Video;
import com.sliit.videobrowsing.video.repository.VideoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Owner: Amsakan - Search, Browsing & Notification module.
 * Reuses VideoRepository (from the Video Management module) for the actual search query,
 * and separately logs what each user searched for.
 */
@Service
@RequiredArgsConstructor
public class SearchService {

    private final VideoRepository videoRepository;
    private final SearchHistoryRepository searchHistoryRepository;

    public List<Video> search(Long userId, String keyword) {
        logSearch(userId, keyword);
        return videoRepository.findByTitleContainingIgnoreCase(keyword);
    }

    public void logSearch(Long userId, String keyword) {
        SearchHistory entry = SearchHistory.builder().userId(userId).query(keyword).build();
        searchHistoryRepository.save(entry);
    }

    public List<SearchHistory> getHistory(Long userId) {
        return searchHistoryRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }
}
