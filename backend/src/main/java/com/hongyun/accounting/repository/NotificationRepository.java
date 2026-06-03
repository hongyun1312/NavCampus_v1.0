package com.hongyun.accounting.repository;

import com.hongyun.accounting.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByTypeOrderByCreatedAtDesc(Notification.NotifyType type);
    List<Notification> findByUserIdOrderByCreatedAtDesc(Long userId);
    List<Notification> findByUserIdOrUserIsNullOrderByCreatedAtDesc(Long userId);
    List<Notification> findAllByOrderByCreatedAtDesc();
}
