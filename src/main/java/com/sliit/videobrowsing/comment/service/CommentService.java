package com.sliit.videobrowsing.comment.service;

import com.sliit.videobrowsing.comment.entity.Comment;
import com.sliit.videobrowsing.comment.repository.CommentRepository;
import com.sliit.videobrowsing.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.OptionalDouble;

/** Owner: Pathirana - Comments & Reviews Management module. */
@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;

    public Comment add(Long videoId, Long userId, String content, Integer rating) {
        Comment comment = Comment.builder()
                .videoId(videoId)
                .userId(userId)
                .content(content)
                .rating(rating)
                .build();
        return commentRepository.save(comment);
    }

    public List<Comment> getForVideo(Long videoId) {
        return commentRepository.findByVideoIdOrderByCreatedAtDesc(videoId);
    }

    public double getAverageRating(Long videoId) {
        OptionalDouble avg = commentRepository.findByVideoIdOrderByCreatedAtDesc(videoId).stream()
                .filter(c -> c.getRating() != null)
                .mapToInt(Comment::getRating)
                .average();
        return avg.orElse(0.0);
    }

    public Comment update(Long id, String content) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found: " + id));
        comment.setContent(content);
        return commentRepository.save(comment);
    }

    public void delete(Long id) {
        commentRepository.deleteById(id);
    }
}
