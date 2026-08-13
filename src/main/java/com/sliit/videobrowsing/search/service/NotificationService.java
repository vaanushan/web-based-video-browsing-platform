package com.sliit.videobrowsing.search.service;

import com.sliit.videobrowsing.common.exception.ResourceNotFoundException;
import com.sliit.videobrowsing.search.entity.Notification;
import com.sliit.videobrowsing.search.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/** Owner: Amsakan - Search, Browsing & Notification module. */
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public Notification send(Long userId, String message) {
        Notification notification = Notification.builder().userId(userId).message(message).build();
        return notificationRepository.save(notification);
    }

    public List<Notification> getForUser(Long userId) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    public Notification markAsRead(Long id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found: " + id));
        notification.setRead(true);
        return notificationRepository.save(notification);
    }
}
