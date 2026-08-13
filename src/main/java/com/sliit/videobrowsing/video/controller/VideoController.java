package com.sliit.videobrowsing.video.controller;

import com.sliit.videobrowsing.user.entity.Role;
import com.sliit.videobrowsing.user.entity.User;
import com.sliit.videobrowsing.user.service.UserService;
import com.sliit.videobrowsing.video.entity.Video;
import com.sliit.videobrowsing.video.service.VideoLikeService;
import com.sliit.videobrowsing.video.service.VideoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;

/** Owner: Asven - Video Management module. */
@RestController
@RequestMapping("/api/videos")
@RequiredArgsConstructor
public class VideoController {

    private final VideoService videoService;
    private final VideoLikeService videoLikeService;
    private final UserService userService;

    @PostMapping
    public ResponseEntity<Video> upload(@RequestParam String title,
                                         @RequestParam(required = false) String description,
                                         @RequestParam Long uploaderId,
                                         @RequestParam String s3Url,
                                         @RequestParam(required = false) String thumbnailUrl,
                                         @RequestParam(required = false) List<String> tags) {
        Video video = videoService.upload(title, description, uploaderId, s3Url, thumbnailUrl,
                tags == null ? List.of() : tags);
        return ResponseEntity.ok(video);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Video> getById(@PathVariable Long id) {
        return ResponseEntity.ok(videoService.getById(id));
    }

    @GetMapping
    public ResponseEntity<List<Video>> getAllPublished() {
        return ResponseEntity.ok(videoService.getAllPublished());
    }

    @GetMapping("/uploader/{uploaderId}")
    public ResponseEntity<List<Video>> getByUploader(@PathVariable Long uploaderId) {
        return ResponseEntity.ok(videoService.getByUploader(uploaderId));
    }

    @GetMapping("/search")
    public ResponseEntity<List<Video>> search(@RequestParam String keyword) {
        return ResponseEntity.ok(videoService.searchByTitle(keyword));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Video> updateMetadata(@PathVariable Long id,
                                                 @RequestParam String title,
                                                 @RequestParam(required = false) String description) {
        return ResponseEntity.ok(videoService.updateMetadata(id, title, description));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id, Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        Video video = videoService.getById(id);
        User user = userService.getByEmail(principal.getName());
        boolean isOwner = video.getUploaderId().equals(user.getId());
        boolean isAdmin = user.getRole() == Role.ADMIN;
        if (!isOwner && !isAdmin) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        videoService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/view")
    public ResponseEntity<Map<String, Long>> recordView(@PathVariable Long id) {
        long count = videoService.incrementViewCount(id);
        return ResponseEntity.ok(Map.of("views", count));
    }

    @GetMapping("/{id}/views")
    public ResponseEntity<Map<String, Long>> getViews(@PathVariable Long id) {
        return ResponseEntity.ok(Map.of("views", videoService.getViewCount(id)));
    }

    @PostMapping("/{id}/like")
    public ResponseEntity<Map<String, Object>> toggleLike(@PathVariable Long id, Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        Long userId = userService.getByEmail(principal.getName()).getId();
        boolean liked = videoLikeService.toggleLike(id, userId);
        return ResponseEntity.ok(Map.of(
                "liked", liked,
                "count", videoLikeService.getCount(id)
        ));
    }
}
