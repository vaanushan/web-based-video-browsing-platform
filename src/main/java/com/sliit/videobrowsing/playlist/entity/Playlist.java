package com.sliit.videobrowsing.playlist.entity;

import com.sliit.videobrowsing.common.entity.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/** Owner: Sandeepani - Playlist Management module. */
@Entity
@Table(name = "playlists")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Playlist extends BaseEntity {

    @NotBlank
    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Long ownerId;   // references User.id

    @OneToMany(mappedBy = "playlist", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<PlaylistVideo> items = new ArrayList<>();
}
