package com.sliit.videobrowsing.search.controller;

import com.sliit.videobrowsing.search.entity.Notification;
import com.sliit.videobrowsing.search.service.NotificationService;
import com.sliit.videobrowsing.search.service.SearchService;
import com.sliit.videobrowsing.user.entity.User;
import com.sliit.videobrowsing.user.service.UserService;
import com.sliit.videobrowsing.video.entity.Tag;
import com.sliit.videobrowsing.video.entity.Video;
import com.sliit.videobrowsing.video.service.VideoService;
import com.sliit.videobrowsing.video.util.VideoUrlUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.*;

/**
 * Owner: Amsakan T - Search, Browsing & Notification module.
 *
 * Renders search and notification pages using database services.
 */
@Controller
@RequiredArgsConstructor
public class SearchPageController {

    private final SearchService searchService;
    private final NotificationService notificationService;
    private final VideoService videoService;
    private final UserService userService;

    @GetMapping("/search")
    public String search(@RequestParam(required = false) String q,
                          @RequestParam(required = false) String category,
                          Principal principal,
                          Model model) {
        Long userId = null;
        if (principal != null) {
            try {
                userId = userService.getByEmail(principal.getName()).getId();
            } catch (Exception e) {}
        }

        List<Video> rawResults;
        if (q != null && !q.trim().isEmpty()) {
            rawResults = searchService.search(userId, q.trim());
        } else {
            rawResults = videoService.getAllPublished();
        }

        List<Map<String, Object>> results = new ArrayList<>();
        for (Video v : rawResults) {
            results.add(mapVideo(v));
        }

        if (category != null && !category.equals("all") && !category.isEmpty()) {
            results.removeIf(v -> !((String) v.get("category")).equalsIgnoreCase(category));
        }

        model.addAttribute("query", q);
        model.addAttribute("category", category);
        model.addAttribute("results", results);
        return "search/search-results";
    }

    @GetMapping("/notifications")
    public String notifications(Principal principal, Model model) {
        if (principal == null) {
            return "redirect:/login";
        }
        User user = userService.getByEmail(principal.getName());
        List<Notification> rawNotifications = notificationService.getForUser(user.getId());
        
        List<Map<String, Object>> notifications = new ArrayList<>();
        for (Notification n : rawNotifications) {
            Map<String, Object> nmap = new HashMap<>();
            nmap.put("message", n.getMessage());
            nmap.put("postedAgo", "Recently");
            nmap.put("read", n.isRead());
            notifications.add(nmap);
        }
        
        model.addAttribute("notifications", notifications);
        return "search/notifications";
    }

    private Map<String, Object> mapVideo(Video v) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", v.getId());
        map.put("title", v.getTitle());
        map.put("description", v.getDescription() != null ? v.getDescription() : "");
        map.put("s3Url", v.getS3Url());
        map.put("thumbnailUrl", VideoUrlUtil.resolveThumbnail(v.getS3Url(), v.getThumbnailUrl()));

        String uploaderName = "VideoHub Creator";
        try {
            uploaderName = userService.getById(v.getUploaderId()).getName();
        } catch (Exception e) {}
        map.put("uploaderName", uploaderName);

        map.put("views", v.getViewCount());
        map.put("duration", "5:24");
        map.put("category", v.getTags().isEmpty() ? "General" : v.getTags().iterator().next().getName());

        List<String> tagList = v.getTags().stream().map(Tag::getName).toList();
        map.put("tags", tagList);

        return map;
    }
}
