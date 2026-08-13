package com.sliit.videobrowsing.playlist.controller;

import com.sliit.videobrowsing.playlist.entity.Playlist;
import com.sliit.videobrowsing.playlist.service.PlaylistService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** Owner: Sandeepani - Playlist Management module. */
@RestController
@RequestMapping("/api/playlists")
@RequiredArgsConstructor
public class PlaylistController {

    private final PlaylistService playlistService;

    @PostMapping
    public ResponseEntity<Playlist> create(@RequestParam String name, @RequestParam Long ownerId) {
        return ResponseEntity.ok(playlistService.create(name, ownerId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Playlist> getById(@PathVariable Long id) {
        return ResponseEntity.ok(playlistService.getById(id));
    }

    @GetMapping("/owner/{ownerId}")
    public ResponseEntity<List<Playlist>> getByOwner(@PathVariable Long ownerId) {
        return ResponseEntity.ok(playlistService.getByOwner(ownerId));
    }

    @PostMapping("/{id}/videos")
    public ResponseEntity<Playlist> addVideo(@PathVariable Long id,
                                              @RequestParam Long videoId,
                                              @RequestParam int position) {
        return ResponseEntity.ok(playlistService.addVideo(id, videoId, position));
    }

    @DeleteMapping("/items/{playlistVideoId}")
    public ResponseEntity<Void> removeVideo(@PathVariable Long playlistVideoId) {
        playlistService.removeVideo(playlistVideoId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Playlist> rename(@PathVariable Long id, @RequestParam String name) {
        return ResponseEntity.ok(playlistService.rename(id, name));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        playlistService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
