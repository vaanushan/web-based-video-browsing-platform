package com.sliit.videobrowsing.comment.controller;

import com.sliit.videobrowsing.comment.entity.Comment;
import com.sliit.videobrowsing.comment.service.CommentService;
import com.sliit.videobrowsing.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;

/** Owner: Pathirana - Comments & Reviews Management module. */
@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;
    private final UserService userService;

    @PostMapping
    public ResponseEntity<Comment> add(@RequestParam Long videoId,
                                       @RequestParam String content,
                                       @RequestParam(required = false) Integer rating,
                                       Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        Long userId = userService.getByEmail(principal.getName()).getId();
        return ResponseEntity.ok(commentService.add(videoId, userId, content, rating));
    }

    @GetMapping("/video/{videoId}")
    public ResponseEntity<List<Comment>> getForVideo(@PathVariable Long videoId) {
        return ResponseEntity.ok(commentService.getForVideo(videoId));
    }

    @GetMapping("/video/{videoId}/average-rating")
    public ResponseEntity<Map<String, Double>> getAverageRating(@PathVariable Long videoId) {
        return ResponseEntity.ok(Map.of("averageRating", commentService.getAverageRating(videoId)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Comment> update(@PathVariable Long id, @RequestParam String content) {
        return ResponseEntity.ok(commentService.update(id, content));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        commentService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
