package com.sliit.videobrowsing.common.config;

import com.sliit.videobrowsing.admin.entity.ViolationRule;
import com.sliit.videobrowsing.admin.repository.ViolationRuleRepository;
import com.sliit.videobrowsing.comment.entity.Comment;
import com.sliit.videobrowsing.comment.repository.CommentRepository;
import com.sliit.videobrowsing.playlist.entity.Playlist;
import com.sliit.videobrowsing.playlist.entity.PlaylistVideo;
import com.sliit.videobrowsing.playlist.repository.PlaylistRepository;
import com.sliit.videobrowsing.user.entity.Role;
import com.sliit.videobrowsing.user.entity.User;
import com.sliit.videobrowsing.user.repository.UserRepository;
import com.sliit.videobrowsing.video.entity.Tag;
import com.sliit.videobrowsing.video.entity.Video;
import com.sliit.videobrowsing.video.entity.VideoStatus;
import com.sliit.videobrowsing.video.repository.TagRepository;
import com.sliit.videobrowsing.video.repository.VideoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

/**
 * Automatically seeds sample data into the database on startup if the database is empty.
 * This makes the application fully functional and interactive for testing and demonstration.
 */
@Component
@RequiredArgsConstructor
public class DatabaseSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final VideoRepository videoRepository;
    private final TagRepository tagRepository;
    private final CommentRepository commentRepository;
    private final ViolationRuleRepository violationRuleRepository;
    private final PlaylistRepository playlistRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.count() > 0) {
            return; // DB already seeded or has data
        }

        // 1. Seed Users
        User creator = User.builder()
                .name("Tech Channel")
                .email("creator@videohub.com")
                .passwordHash(passwordEncoder.encode("password123"))
                .role(Role.CONTENT_CREATOR)
                .build();
        userRepository.save(creator);

        User admin = User.builder()
                .name("Super Admin")
                .email("admin@videohub.com")
                .passwordHash(passwordEncoder.encode("password123"))
                .role(Role.ADMIN)
                .build();
        userRepository.save(admin);

        User viewer = User.builder()
                .name("Ama Perera")
                .email("user@videohub.com")
                .passwordHash(passwordEncoder.encode("password123"))
                .role(Role.END_USER)
                .build();
        userRepository.save(viewer);

        // 2. Seed Tags
        Tag tagMusic = tagRepository.save(Tag.builder().name("music").build());
        Tag tagGaming = tagRepository.save(Tag.builder().name("gaming").build());
        Tag tagTech = tagRepository.save(Tag.builder().name("tech").build());
        Tag tagEducation = tagRepository.save(Tag.builder().name("education").build());

        // 3. Seed Videos
        Video v1 = Video.builder()
                .title("Getting Started with Java — Full Tutorial")
                .description("A complete beginner-friendly walkthrough covering setup, syntax, and your first project.")
                .uploaderId(creator.getId())
                .s3Url("https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerFun.mp4")
                .thumbnailUrl("")
                .viewCount(1250)
                .status(VideoStatus.PUBLISHED)
                .tags(Set.of(tagTech, tagEducation))
                .build();
        videoRepository.save(v1);

        Video v2 = Video.builder()
                .title("Top 10 Gaming Moments — Best Plays of the Year")
                .description("The most epic clutch plays, speedruns, and highlight reels from the gaming community.")
                .uploaderId(creator.getId())
                .s3Url("https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4")
                .thumbnailUrl("")
                .viewCount(3420)
                .status(VideoStatus.PUBLISHED)
                .tags(Set.of(tagGaming))
                .build();
        videoRepository.save(v2);

        Video v3 = Video.builder()
                .title("Lo-Fi Beats to Study To — 1 Hour Mix")
                .description("Relaxing background music for studying, coding, or unwinding. No interruptions.")
                .uploaderId(creator.getId())
                .s3Url("https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4")
                .thumbnailUrl("")
                .viewCount(8900)
                .status(VideoStatus.PUBLISHED)
                .tags(Set.of(tagMusic))
                .build();
        videoRepository.save(v3);

        // 4. Seed Comments
        commentRepository.save(Comment.builder()
                .videoId(v1.getId())
                .userId(viewer.getId())
                .content("Clear explanation! Finally understood how Java classes work.")
                .rating(5)
                .build());

        commentRepository.save(Comment.builder()
                .videoId(v2.getId())
                .userId(viewer.getId())
                .content("That final boss fight clip at 4:20 was insane.")
                .rating(5)
                .build());

        commentRepository.save(Comment.builder()
                .videoId(v2.getId())
                .userId(admin.getId())
                .content("Great compilation. Would love to see more RPG highlights next time.")
                .rating(4)
                .build());

        // 5. Seed Violation Rules
        violationRuleRepository.save(ViolationRule.builder()
                .definedBy(admin.getId())
                .type("Copyright Infringement")
                .description("Uploader must own the rights to all video footage and music tracks used in the video.")
                .build());

        violationRuleRepository.save(ViolationRule.builder()
                .definedBy(admin.getId())
                .type("Privacy Violation")
                .description("Close-up footage of identifiable people requires their consent before publishing.")
                .build());

        // 6. Seed Playlist
        Playlist playlist = Playlist.builder()
                .name("Watch Later")
                .ownerId(viewer.getId())
                .build();
        
        PlaylistVideo item1 = PlaylistVideo.builder()
                .playlist(playlist)
                .videoId(v1.getId())
                .position(1)
                .build();

        PlaylistVideo item2 = PlaylistVideo.builder()
                .playlist(playlist)
                .videoId(v2.getId())
                .position(2)
                .build();

        playlist.setItems(List.of(item1, item2));
        playlistRepository.save(playlist);
    }
}
