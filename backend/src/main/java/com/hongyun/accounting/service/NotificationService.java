package com.hongyun.accounting.service;

import com.hongyun.accounting.entity.Notification;
import com.hongyun.accounting.entity.User;
import com.hongyun.accounting.repository.NotificationRepository;
import com.hongyun.accounting.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private UserRepository userRepository;

    public List<Notification> getAllNotifications() {
        return notificationRepository.findAllByOrderByCreatedAtDesc();
    }

    public List<Notification> getNotificationsForUser(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return notificationRepository.findByUserIdOrUserIsNullOrderByCreatedAtDesc(user.getId());
    }

    public Notification createNotification(String title, String content, String typeStr) {
        Notification n = new Notification();
        n.setTitle(title);
        n.setContent(content);
        try {
            n.setType(Notification.NotifyType.valueOf(typeStr));
        } catch (IllegalArgumentException e) {
            n.setType(Notification.NotifyType.SYSTEM);
        }
        return notificationRepository.save(n);
    }
    
    public void notifySite(com.hongyun.accounting.entity.User user, String title, String content) {
        Notification n = new Notification();
        n.setTitle(title);
        n.setContent(content);
        n.setType(Notification.NotifyType.SYSTEM);
        n.setUser(user);
        notificationRepository.save(n);
    }
    
    public void deleteNotification(Long id) {
        notificationRepository.deleteById(id);
    }
}
