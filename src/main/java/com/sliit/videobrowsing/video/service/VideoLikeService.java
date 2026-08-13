package com.sliit.videobrowsing.video.service;

import com.sliit.videobrowsing.video.entity.VideoLike;
import com.sliit.videobrowsing.video.repository.VideoLikeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class VideoLikeService {

    private final VideoLikeRepository videoLikeRepository;

    public long getCount(Long videoId) {
        return videoLikeRepository.countByVideoId(videoId);
    }

    public boolean hasLiked(Long videoId, Long userId) {
        return videoLikeRepository.existsByVideoIdAndUserId(videoId, userId);
    }

    @Transactional
    public boolean toggleLike(Long videoId, Long userId) {
        var existing = videoLikeRepository.findByVideoIdAndUserId(videoId, userId);
        if (existing.isPresent()) {
            videoLikeRepository.delete(existing.get());
            return false;
        }
        videoLikeRepository.save(VideoLike.builder().videoId(videoId).userId(userId).build());
        return true;
    }
}
