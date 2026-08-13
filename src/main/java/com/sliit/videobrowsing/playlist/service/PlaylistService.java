package com.sliit.videobrowsing.playlist.service;

import com.sliit.videobrowsing.common.exception.ResourceNotFoundException;
import com.sliit.videobrowsing.playlist.entity.Playlist;
import com.sliit.videobrowsing.playlist.entity.PlaylistVideo;
import com.sliit.videobrowsing.playlist.repository.PlaylistRepository;
import com.sliit.videobrowsing.playlist.repository.PlaylistVideoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/** Owner: Sandeepani - Playlist Management module. */
@Service
@RequiredArgsConstructor
public class PlaylistService {

    private final PlaylistRepository playlistRepository;
    private final PlaylistVideoRepository playlistVideoRepository;

    public Playlist create(String name, Long ownerId) {
        Playlist playlist = Playlist.builder().name(name).ownerId(ownerId).build();
        return playlistRepository.save(playlist);
    }

    public Playlist getById(Long id) {
        return playlistRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Playlist not found: " + id));
    }

    public List<Playlist> getByOwner(Long ownerId) {
        return playlistRepository.findByOwnerId(ownerId);
    }

    public Playlist addVideo(Long playlistId, Long videoId, int position) {
        Playlist playlist = getById(playlistId);
        PlaylistVideo item = PlaylistVideo.builder()
                .playlist(playlist)
                .videoId(videoId)
                .position(position)
                .build();
        playlist.getItems().add(item);
        return playlistRepository.save(playlist);
    }

    public void removeVideo(Long playlistVideoId) {
        playlistVideoRepository.deleteById(playlistVideoId);
    }

    public Playlist rename(Long id, String newName) {
        Playlist playlist = getById(id);
        playlist.setName(newName);
        return playlistRepository.save(playlist);
    }

    public void delete(Long id) {
        playlistRepository.deleteById(id);
    }
}
