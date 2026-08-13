package com.sliit.videobrowsing.video.repository;

import com.sliit.videobrowsing.video.entity.Video;
import com.sliit.videobrowsing.video.entity.VideoStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VideoRepository extends JpaRepository<Video, Long> {
    List<Video> findByStatus(VideoStatus status);
    List<Video> findByUploaderId(Long uploaderId);
    List<Video> findByTitleContainingIgnoreCase(String keyword);
}
