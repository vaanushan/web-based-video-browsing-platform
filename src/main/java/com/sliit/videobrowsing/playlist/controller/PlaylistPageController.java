package com.sliit.videobrowsing.playlist.controller;

import com.sliit.videobrowsing.playlist.entity.Playlist;
import com.sliit.videobrowsing.playlist.entity.PlaylistVideo;
import com.sliit.videobrowsing.playlist.service.PlaylistService;
import com.sliit.videobrowsing.user.entity.User;
import com.sliit.videobrowsing.user.service.UserService;
import com.sliit.videobrowsing.video.entity.Video;
import com.sliit.videobrowsing.video.service.VideoService;
import com.sliit.videobrowsing.video.util.VideoUrlUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.*;

/**
 * Owner: Sandeepani S.K - Playlist Management module.
 *
 * Renders the playlist pages with real database data.
 */
@Controller
@RequiredArgsConstructor
public class PlaylistPageController {

    private final PlaylistService playlistService;
    private final VideoService videoService;
    private final UserService userService;

    @GetMapping("/playlists")
    public String playlists(Principal principal, Model model) {
        if (principal == null) {
            return "redirect:/login";
        }
        User user = userService.getByEmail(principal.getName());
        List<Map<String, Object>> playlists = new ArrayList<>();
        for (Playlist p : playlistService.getByOwner(user.getId())) {
            playlists.add(mapPlaylist(p));
        }
        model.addAttribute("playlists", playlists);
        return "playlist/playlists";
    }

    @PostMapping("/playlists")
    public String create(@RequestParam String name, Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }
        User user = userService.getByEmail(principal.getName());
        playlistService.create(name, user.getId());
        return "redirect:/playlists";
    }

    @GetMapping("/playlists/{id}")
    public String detail(@PathVariable Long id, Principal principal, Model model) {
        Playlist playlist = playlistService.getById(id);
        model.addAttribute("playlist", mapPlaylist(playlist));
        boolean isOwner = false;
        if (principal != null) {
            try {
                isOwner = playlist.getOwnerId().equals(userService.getByEmail(principal.getName()).getId());
            } catch (Exception e) {}
        }
        model.addAttribute("isOwner", isOwner);

        List<Map<String, Object>> items = new ArrayList<>();
        if (playlist.getItems() != null) {
            for (PlaylistVideo pv : playlist.getItems()) {
                Map<String, Object> itemMap = new HashMap<>();
                itemMap.put("id", pv.getId());
                itemMap.put("videoId", pv.getVideoId());
                itemMap.put("title", "Unknown Video");
                itemMap.put("duration", "5:24");
                itemMap.put("thumbnailUrl", "");
                try {
                    Video video = videoService.getById(pv.getVideoId());
                    itemMap.put("title", video.getTitle());
                    itemMap.put("thumbnailUrl", resolveThumbnail(video));
                } catch (Exception e) {}
                items.add(itemMap);
            }
        }
        model.addAttribute("items", items);
        return "playlist/playlist-detail";
    }

    @PostMapping("/playlists/{id}/add")
    public String addVideo(@PathVariable Long id,
                           @RequestParam Long videoId,
                           Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }
        User user = userService.getByEmail(principal.getName());
        Playlist playlist = playlistService.getById(id);
        if (!playlist.getOwnerId().equals(user.getId())) {
            return "redirect:/videos/" + videoId;
        }
        int position = playlist.getItems() != null ? playlist.getItems().size() + 1 : 1;
        playlistService.addVideo(id, videoId, position);
        return "redirect:/videos/" + videoId;
    }

    @PostMapping("/playlists/{id}/delete")
    public String deletePlaylist(@PathVariable Long id, Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }
        User user = userService.getByEmail(principal.getName());
        Playlist playlist = playlistService.getById(id);
        if (!playlist.getOwnerId().equals(user.getId())) {
            return "redirect:/playlists";
        }
        playlistService.delete(id);
        return "redirect:/playlists";
    }

    @PostMapping("/playlists/{playlistId}/items/{itemId}/remove")
    public String removeItem(@PathVariable Long playlistId,
                             @PathVariable Long itemId,
                             Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }
        User user = userService.getByEmail(principal.getName());
        Playlist playlist = playlistService.getById(playlistId);
        if (!playlist.getOwnerId().equals(user.getId())) {
            return "redirect:/playlists";
        }
        playlistService.removeVideo(itemId);
        return "redirect:/playlists/" + playlistId;
    }

    private Map<String, Object> mapPlaylist(Playlist p) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", p.getId());
        map.put("name", p.getName());
        map.put("videoCount", p.getItems() != null ? p.getItems().size() : 0);
        map.put("updatedAgo", "Recently");
        map.put("thumbnailUrl", "");
        if (p.getItems() != null && !p.getItems().isEmpty()) {
            try {
                Video first = videoService.getById(p.getItems().get(0).getVideoId());
                map.put("thumbnailUrl", resolveThumbnail(first));
            } catch (Exception e) {}
        }
        return map;
    }

    private String resolveThumbnail(Video video) {
        return VideoUrlUtil.resolveThumbnail(video.getS3Url(), video.getThumbnailUrl());
    }
}
