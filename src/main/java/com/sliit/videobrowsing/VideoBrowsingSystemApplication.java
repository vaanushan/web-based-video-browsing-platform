package com.sliit.videobrowsing;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * SE2030 - Software Engineering Group Project
 * Web-Based Video Browsing Platform
 *
 * Entry point for the Spring Boot application.
 * Each of the 6 major modules lives in its own package under com.sliit.videobrowsing:
 *   - user      -> Playback & User Management
 *   - video     -> Video Management
 *   - playlist  -> Playlist Management
 *   - search    -> Search, Browsing & Notification
 *   - comment   -> Comments & Reviews Management
 *   - admin     -> Admin Panel
 */
@SpringBootApplication
public class VideoBrowsingSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(VideoBrowsingSystemApplication.class, args);
    }
}
