package com.hongyun.accounting.controller;

import com.hongyun.accounting.entity.Notification;
import com.hongyun.accounting.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @GetMapping
    public List<Notification> getNotifications() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return notificationService.getNotificationsForUser(auth.getName());
    }

    @PostMapping
    public ResponseEntity<?> createNotification(@RequestBody Map<String, String> payload) {
        try {
            String title = payload.get("title");
            String content = payload.get("content");
            String type = payload.get("type");
            return ResponseEntity.ok(notificationService.createNotification(title, content, type));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteNotification(@PathVariable Long id) {
        notificationService.deleteNotification(id);
        return ResponseEntity.ok().build();
    }
}
