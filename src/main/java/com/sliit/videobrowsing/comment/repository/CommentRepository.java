package com.sliit.videobrowsing.comment.repository;

import com.sliit.videobrowsing.comment.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByVideoIdOrderByCreatedAtDesc(Long videoId);
    List<Comment> findByUserId(Long userId);
}
