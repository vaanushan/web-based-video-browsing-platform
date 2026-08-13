package com.sliit.videobrowsing.comment.entity;

import com.sliit.videobrowsing.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Owner: Pathirana - Comments & Reviews Management module. */
@Entity
@Table(name = "comments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Comment extends BaseEntity {

    @Column(nullable = false)
    private Long videoId;   // references Video.id

    @Column(nullable = false)
    private Long userId;    // references User.id

    @NotBlank
    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Min(1)
    @Max(5)
    private Integer rating;   // 1-5 stars, nullable if it's a plain comment
}
