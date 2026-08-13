package com.sliit.videobrowsing.video.entity;

import com.sliit.videobrowsing.common.entity.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

/** Owner: Asven - Video Management module. */
@Entity
@Table(name = "videos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Video extends BaseEntity {

    @NotBlank
    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private Long uploaderId;   // references User.id (owned by user module)

    private String s3Url;

    private String thumbnailUrl;

    @Builder.Default
    private long viewCount = 0;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private VideoStatus status = VideoStatus.PROCESSING;

    @ManyToMany
    @JoinTable(
        name = "video_tags",
        joinColumns = @JoinColumn(name = "video_id"),
        inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    @Builder.Default
    private Set<Tag> tags = new HashSet<>();
}
