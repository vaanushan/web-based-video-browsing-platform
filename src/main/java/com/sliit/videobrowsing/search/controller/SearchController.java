package com.sliit.videobrowsing.search.controller;

import com.sliit.videobrowsing.search.service.SearchService;
import com.sliit.videobrowsing.video.entity.Video;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** Owner: Amsakan - Search, Browsing & Notification module. */
@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
public class SearchController {

    private final SearchService searchService;

    @GetMapping
    public ResponseEntity<List<Video>> search(@RequestParam Long userId, @RequestParam String keyword) {
        return ResponseEntity.ok(searchService.search(userId, keyword));
    }
}
