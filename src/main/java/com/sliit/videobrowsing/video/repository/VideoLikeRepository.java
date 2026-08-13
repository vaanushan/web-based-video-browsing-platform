package com.sliit.videobrowsing.video.repository;

import com.sliit.videobrowsing.video.entity.VideoLike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VideoLikeRepository extends JpaRepository<VideoLike, Long> {
    long countByVideoId(Long videoId);
    Optional<VideoLike> findByVideoIdAndUserId(Long videoId, Long userId);
    boolean existsByVideoIdAndUserId(Long videoId, Long userId);
}
