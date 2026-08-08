package com.hongyun.navcampus.system.service;

import com.hongyun.navcampus.system.entity.Notification;
import com.hongyun.navcampus.system.entity.User;
import com.hongyun.navcampus.system.mapper.NotificationMapper;
import com.hongyun.navcampus.system.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationService {

    @Autowired
    private NotificationMapper notificationMapper;

    @Autowired
    private UserMapper userMapper;

    public List<Notification> getAllNotifications() {
        return notificationMapper.findAllByOrderByCreatedAtDesc();
    }

    public List<Notification> getNotificationsForUser(String username) {
        User user = userMapper.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return notificationMapper.findByUserIdOrUserIsNullOrderByCreatedAtDesc(user.getId());
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
        return notificationMapper.save(n);
    }
    
    public void notifySite(User user, String title, String content) {
        Notification n = new Notification();
        n.setTitle(title);
        n.setContent(content);
        n.setType(Notification.NotifyType.SYSTEM);
        n.setUser(user);
        notificationMapper.save(n);
    }
    
    public void deleteNotification(Long id) {
        notificationMapper.deleteById(id);
    }
}
