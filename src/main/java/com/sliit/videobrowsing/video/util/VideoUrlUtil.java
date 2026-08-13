package com.sliit.videobrowsing.video.util;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Detects YouTube links and extracts the video id for iframe embedding. */
public final class VideoUrlUtil {

    private static final Pattern YOUTUBE_ID = Pattern.compile(
            "(?:youtube\\.com/(?:[^/]+/.+/|(?:v|e(?:mbed)?)/|.*[?&]v=)|youtu\\.be/)([^\"&?/\\s]{11})"
    );

    private VideoUrlUtil() {}

    public static boolean isYoutube(String url) {
        return extractYoutubeId(url).isPresent();
    }

    public static Optional<String> extractYoutubeId(String url) {
        if (url == null || url.isBlank()) {
            return Optional.empty();
        }
        Matcher matcher = YOUTUBE_ID.matcher(url);
        return matcher.find() ? Optional.of(matcher.group(1)) : Optional.empty();
    }

    public static String embedUrl(String youtubeId) {
        return "https://www.youtube.com/embed/" + youtubeId;
    }

    public static String youtubeThumbnailUrl(String youtubeId) {
        return "https://img.youtube.com/vi/" + youtubeId + "/hqdefault.jpg";
    }

    public static String resolveThumbnail(String videoUrl, String thumbnailUrl) {
        if (thumbnailUrl != null && !thumbnailUrl.isBlank()) {
            return thumbnailUrl;
        }
        return extractYoutubeId(videoUrl)
                .map(VideoUrlUtil::youtubeThumbnailUrl)
                .orElse("");
    }
}
