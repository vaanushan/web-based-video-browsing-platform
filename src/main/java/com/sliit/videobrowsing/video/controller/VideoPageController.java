package com.sliit.videobrowsing.video.controller;

import com.sliit.videobrowsing.comment.entity.Comment;
import com.sliit.videobrowsing.comment.service.CommentService;
import com.sliit.videobrowsing.playlist.entity.Playlist;
import com.sliit.videobrowsing.playlist.service.PlaylistService;
import com.sliit.videobrowsing.user.entity.Role;
import com.sliit.videobrowsing.user.entity.User;
import com.sliit.videobrowsing.user.service.UserService;
import com.sliit.videobrowsing.video.entity.Tag;
import com.sliit.videobrowsing.video.entity.Video;
import com.sliit.videobrowsing.video.service.VideoLikeService;
import com.sliit.videobrowsing.video.service.VideoService;
import com.sliit.videobrowsing.video.util.VideoUrlUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.security.Principal;
import java.util.*;

/**
 * Owner: Asven S.I - Video Management module.
 *
 * Renders the Thymeleaf pages using database services.
 */
@Controller
@RequiredArgsConstructor
public class VideoPageController {

    private final VideoService videoService;
    private final CommentService commentService;
    private final UserService userService;
    private final VideoLikeService videoLikeService;
    private final PlaylistService playlistService;

    @GetMapping("/")
    public String browse(Model model) {
        List<Video> allVideos = videoService.getAllPublished();
        List<Map<String, Object>> mappedVideos = new ArrayList<>();
        Map<String, Object> featured = null;

        for (Video v : allVideos) {
            Map<String, Object> mapped = mapVideo(v);
            mappedVideos.add(mapped);
        }

        if (!mappedVideos.isEmpty()) {
            featured = mappedVideos.get(0);
            mappedVideos.remove(0); // remove so it doesn't duplicate in Recently Added
        } else {
            featured = sampleVideo(1L, "Getting Started with Java — Full Tutorial",
                    "A complete beginner-friendly walkthrough covering setup, syntax, and your first project.",
                    "Tech", "12:45", 125000);
        }

        model.addAttribute("featured", featured);
        model.addAttribute("videos", mappedVideos);
        return "video/browse";
    }

    @GetMapping("/videos/{id}")
    public String detail(@PathVariable Long id, Principal principal, Model model) {
        Video video = videoService.getById(id);
        model.addAttribute("video", mapVideo(video));

        List<Comment> rawComments = commentService.getForVideo(id);
        List<Map<String, Object>> comments = new ArrayList<>();
        for (Comment c : rawComments) {
            Map<String, Object> cmap = new HashMap<>();
            String name = "User";
            try {
                name = userService.getById(c.getUserId()).getName();
            } catch (Exception e) {}
            cmap.put("userName", name);
            cmap.put("content", c.getContent());
            cmap.put("rating", c.getRating() != null ? c.getRating() : 5);
            cmap.put("postedAgo", "Recently");
            comments.add(cmap);
        }
        model.addAttribute("comments", comments);

        model.addAttribute("likeCount", videoLikeService.getCount(id));
        boolean userLiked = false;
        if (principal != null) {
            try {
                Long userId = userService.getByEmail(principal.getName()).getId();
                userLiked = videoLikeService.hasLiked(id, userId);
            } catch (Exception e) {}
        }
        model.addAttribute("userLiked", userLiked);

        boolean canDelete = false;
        List<Map<String, Object>> userPlaylists = new ArrayList<>();
        if (principal != null) {
            try {
                User user = userService.getByEmail(principal.getName());
                canDelete = video.getUploaderId().equals(user.getId()) || user.getRole() == Role.ADMIN;
                for (Playlist pl : playlistService.getByOwner(user.getId())) {
                    Map<String, Object> plMap = new HashMap<>();
                    plMap.put("id", pl.getId());
                    plMap.put("name", pl.getName());
                    userPlaylists.add(plMap);
                }
            } catch (Exception e) {}
        }
        model.addAttribute("canDelete", canDelete);
        model.addAttribute("userPlaylists", userPlaylists);
        model.addAttribute("uploaderId", video.getUploaderId());

        // Fetch up next (related) videos
        List<Video> allVideos = videoService.getAllPublished();
        List<Map<String, Object>> related = new ArrayList<>();
        for (Video v : allVideos) {
            if (!v.getId().equals(id)) {
                related.add(mapVideo(v));
            }
        }
        model.addAttribute("related", related);

        return "video/video-detail";
    }

    @GetMapping("/upload")
    public String uploadPage() {
        return "video/upload";
    }

    @GetMapping("/channel")
    public String myChannel(Principal principal, Model model) {
        if (principal == null) {
            return "redirect:/login";
        }
        User channelUser = userService.getByEmail(principal.getName());
        List<Video> userVideos = videoService.getByUploader(channelUser.getId());
        List<Map<String, Object>> mappedVideos = new ArrayList<>();
        for (Video v : userVideos) {
            mappedVideos.add(mapVideo(v));
        }
        model.addAttribute("channelUser", channelUser);
        model.addAttribute("videos", mappedVideos);
        return "video/channel";
    }

    private Map<String, Object> mapVideo(Video v) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", v.getId());
        map.put("title", v.getTitle());
        map.put("description", v.getDescription() != null ? v.getDescription() : "");
        map.put("s3Url", v.getS3Url());
        map.put("thumbnailUrl", VideoUrlUtil.resolveThumbnail(v.getS3Url(), v.getThumbnailUrl()));
        map.put("uploaderId", v.getUploaderId());

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

        VideoUrlUtil.extractYoutubeId(v.getS3Url()).ifPresentOrElse(youtubeId -> {
            map.put("isYoutube", true);
            map.put("youtubeId", youtubeId);
            map.put("embedUrl", VideoUrlUtil.embedUrl(youtubeId));
        }, () -> map.put("isYoutube", false));

        return map;
    }

    private Map<String, Object> sampleVideo(Long id, String title, String description, String category, String duration, int views) {
        Map<String, Object> v = new HashMap<>();
        v.put("id", id);
        v.put("title", title);
        v.put("description", description);
        v.put("category", category);
        v.put("duration", duration);
        v.put("views", views);
        v.put("uploaderName", "Tech Channel");
        v.put("tags", List.of("tech", "tutorial"));
        v.put("s3Url", "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerFun.mp4");
        v.put("isYoutube", false);
        return v;
    }
}
