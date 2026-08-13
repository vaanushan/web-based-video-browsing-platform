package com.sliit.videobrowsing.video.service;

import com.sliit.videobrowsing.common.exception.ResourceNotFoundException;
import com.sliit.videobrowsing.video.entity.Tag;
import com.sliit.videobrowsing.video.entity.Video;
import com.sliit.videobrowsing.video.entity.VideoStatus;
import com.sliit.videobrowsing.video.repository.TagRepository;
import com.sliit.videobrowsing.video.repository.VideoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Owner: Asven - Video Management module.
 * NOTE: actual file upload to AWS S3 should be added here (see VideoController for the
 * multipart endpoint stub) - wire up an S3Client bean using the aws.s3.* properties.
 */
@Service
@RequiredArgsConstructor
public class VideoService {

    private final VideoRepository videoRepository;
    private final TagRepository tagRepository;

    public Video upload(String title, String description, Long uploaderId,
                         String s3Url, String thumbnailUrl, List<String> tagNames) {
        Set<Tag> tags = new HashSet<>();
        for (String tagName : tagNames) {
            Tag tag = tagRepository.findByName(tagName)
                    .orElseGet(() -> tagRepository.save(Tag.builder().name(tagName).build()));
            tags.add(tag);
        }

        String resolvedThumb = com.sliit.videobrowsing.video.util.VideoUrlUtil.resolveThumbnail(s3Url, thumbnailUrl);

        Video video = Video.builder()
                .title(title)
                .description(description)
                .uploaderId(uploaderId)
                .s3Url(s3Url)
                .thumbnailUrl(resolvedThumb)
                .viewCount(0)
                .status(VideoStatus.PUBLISHED)
                .tags(tags)
                .build();

        return videoRepository.save(video);
    }

    public Video getById(Long id) {
        return videoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Video not found: " + id));
    }

    public List<Video> getAllPublished() {
        return videoRepository.findByStatus(VideoStatus.PUBLISHED);
    }

    public List<Video> getByUploader(Long uploaderId) {
        return videoRepository.findByUploaderId(uploaderId);
    }

    public List<Video> searchByTitle(String keyword) {
        return videoRepository.findByTitleContainingIgnoreCase(keyword);
    }

    public Video updateMetadata(Long id, String title, String description) {
        Video video = getById(id);
        video.setTitle(title);
        video.setDescription(description);
        return videoRepository.save(video);
    }

    public void delete(Long id) {
        videoRepository.deleteById(id);
    }

    public void remove(Long id) {
        Video video = getById(id);
        video.setStatus(VideoStatus.REMOVED);
        videoRepository.save(video);
    }

    public long incrementViewCount(Long id) {
        Video video = getById(id);
        video.setViewCount(video.getViewCount() + 1);
        return videoRepository.save(video).getViewCount();
    }

    public long getViewCount(Long id) {
        return getById(id).getViewCount();
    }
}
