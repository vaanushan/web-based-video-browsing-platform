package com.sliit.videobrowsing.playlist.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Join entity: which video sits at which position inside a playlist. Owner: Sandeepani. */
@Entity
@Table(name = "playlist_videos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlaylistVideo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "playlist_id", nullable = false)
    private Playlist playlist;

    @Column(nullable = false)
    private Long videoId;   // references Video.id (owned by video module)

    @Column(nullable = false)
    private Integer position;
}
