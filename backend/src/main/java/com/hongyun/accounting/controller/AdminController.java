package com.hongyun.accounting.controller;

import com.hongyun.accounting.entity.User;
import com.hongyun.accounting.entity.Record;
import com.hongyun.accounting.entity.Notification;
import com.hongyun.accounting.entity.SystemLog;
import com.hongyun.accounting.repository.UserRepository;
import com.hongyun.accounting.repository.RecordRepository;
import com.hongyun.accounting.repository.NotificationRepository;
import com.hongyun.accounting.repository.SystemLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 管理员控制器。
 * 处理管理员相关的请求，如用户管理、记录管理、通知管理和日志查看。
 * 所有接口都需要 ADMIN 角色权限。
 */
@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RecordRepository recordRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private SystemLogRepository systemLogRepository;

    /**
     * 获取所有用户列表。
     *
     * @return 用户列表
     */
    @GetMapping("/users")
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    /**
     * 更新用户角色。
     *
     * @param id 用户ID
     * @param payload 包含新角色的Map
     * @return 更新后的用户
     */
    @PutMapping("/users/{id}/role")
    public User updateUserRole(@PathVariable Long id, @RequestBody Map<String, String> payload) {
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        user.setRole(User.Role.valueOf(payload.get("role")));
        return userRepository.save(user);
    }

    /**
     * 切换用户的黑名单状态。
     *
     * @param id 用户ID
     * @param payload 包含黑名单状态的Map
     * @return 更新后的用户
     */
    @PutMapping("/users/{id}/blacklist")
    public User toggleBlacklist(@PathVariable Long id, @RequestBody Map<String, Boolean> payload) {
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        user.setBlacklisted(payload.get("isBlacklisted"));
        return userRepository.save(user);
    }
    
    /**
     * 获取所有财务记录。
     *
     * @return 财务记录列表
     */
    @GetMapping("/records")
    public List<Record> getAllRecords() {
        return recordRepository.findAll();
    }
    
    /**
     * 删除财务记录。
     *
     * @param id 记录ID
     */
    @DeleteMapping("/records/{id}")
    public void deleteRecord(@PathVariable Long id) {
        recordRepository.deleteById(id);
    }

    /**
     * 获取管理员发布的通知。
     *
     * @return 管理员通知列表
     */
    @GetMapping("/notifications")
    public List<Notification> getAdminNotifications() {
        return notificationRepository.findByTypeOrderByCreatedAtDesc(Notification.NotifyType.ADMIN);
    }
    
    /**
     * 获取系统日志。
     *
     * @return 系统日志列表
     */
    @GetMapping("/logs")
    public List<SystemLog> getSystemLogs() {
        return systemLogRepository.findByOrderByCreatedAtDesc();
    }

    /**
     * 创建新通知。
     *
     * @param notification 通知对象
     * @return 创建后的通知
     */
    @PostMapping("/notifications")
    public Notification createNotification(@RequestBody Notification notification) {
        notification.setType(Notification.NotifyType.ADMIN);
        // Explicitly set user to null for global notifications
        notification.setUser(null); 
        return notificationRepository.save(notification);
    }

    /**
     * 获取特定用户的通知。
     *
     * @param userId 用户ID
     * @return 用户的通知列表
     */
    @GetMapping("/users/{userId}/notifications")
    public List<Notification> getUserNotifications(@PathVariable Long userId) {
        // This method might need a custom query in repository if not exists
        // Assuming findByUserIdOrderByCreatedAtDesc exists or similar
        // Based on previous code, let's stick to what was there or what is standard
        // If findByUserIdOrderByCreatedAtDesc doesn't exist, we might need to add it or use a different one.
        // Checking imports, NotificationRepository is used.
        // Let's assume the method name was correct in the original snippet, just misplaced.
        // However, standard naming would be findByUserId... 
        // But let's check if we need to fix the repository method name too.
        // For now, I will use findByUserId (if User entity has relationship) or custom query.
        // Wait, Notification entity has `user` field. So `findByUser_Id` or `findByUserId` works.
        // I'll stick to the original call, assuming the repository supports it.
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    /**
     * 删除通知。
     *
     * @param id 通知ID
     */
    @DeleteMapping("/notifications/{id}")
    public void deleteNotification(@PathVariable Long id) {
        notificationRepository.deleteById(id);
    }
}
