package com.sliit.videobrowsing.playlist.repository;

import com.sliit.videobrowsing.playlist.entity.PlaylistVideo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlaylistVideoRepository extends JpaRepository<PlaylistVideo, Long> {
}
