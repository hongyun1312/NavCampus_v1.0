package com.hongyun.navcampus.admin.controller;

import com.hongyun.navcampus.common.core.R;
import com.hongyun.navcampus.system.converter.VoConverter;
import com.hongyun.navcampus.system.entity.User;
import com.hongyun.navcampus.system.entity.Notification;
import com.hongyun.navcampus.system.mapper.UserMapper;
import com.hongyun.navcampus.system.mapper.NotificationMapper;
import com.hongyun.navcampus.system.mapper.SystemLogMapper;
import com.hongyun.navcampus.finance.mapper.RecordMapper;
import com.hongyun.navcampus.system.vo.UserVO;
import com.hongyun.navcampus.system.vo.NotificationVO;
import com.hongyun.navcampus.system.vo.SystemLogVO;
import com.hongyun.navcampus.finance.vo.RecordVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "管理后台", description = "管理员专属接口，需ADMIN角色")
public class AdminController {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private RecordMapper recordMapper;
    @Autowired
    private NotificationMapper notificationMapper;
    @Autowired
    private SystemLogMapper systemLogMapper;

    @GetMapping("/users")
    @Operation(summary = "获取全部用户列表")
    public R<List<UserVO>> getAllUsers() {
        return R.ok(VoConverter.toUserVOList(userMapper.findAll()));
    }

    @PutMapping("/users/{id}/role")
    @Operation(summary = "更新用户角色")
    public R<UserVO> updateUserRole(@PathVariable Long id, @RequestBody Map<String, String> payload) {
        User user = userMapper.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        user.setRole(User.Role.valueOf(payload.get("role")));
        userMapper.save(user);
        return R.ok(VoConverter.toUserVO(user));
    }

    @PutMapping("/users/{id}/blacklist")
    @Operation(summary = "切换用户黑名单状态")
    public R<UserVO> toggleBlacklist(@PathVariable Long id, @RequestBody Map<String, Boolean> payload) {
        User user = userMapper.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        user.setBlacklisted(payload.get("isBlacklisted"));
        userMapper.save(user);
        return R.ok(VoConverter.toUserVO(user));
    }

    @GetMapping("/records")
    @Operation(summary = "获取全部收支记录")
    public R<List<RecordVO>> getAllRecords() {
        return R.ok(com.hongyun.navcampus.finance.converter.VoConverter.toRecordVOList(recordMapper.findAll()));
    }

    @DeleteMapping("/records/{id}")
    @Operation(summary = "删除收支记录")
    public R<Void> deleteRecord(@PathVariable Long id) {
        recordMapper.deleteById(id);
        return R.ok();
    }

    @GetMapping("/notifications")
    @Operation(summary = "获取管理员通知")
    public R<List<NotificationVO>> getAdminNotifications() {
        return R.ok(VoConverter.toNotificationVOList(
                notificationMapper.findByTypeOrderByCreatedAtDesc(Notification.NotifyType.ADMIN)));
    }

    @GetMapping("/logs")
    @Operation(summary = "获取系统日志")
    public R<List<SystemLogVO>> getSystemLogs() {
        return R.ok(VoConverter.toSystemLogVOList(systemLogMapper.findByOrderByCreatedAtDesc()));
    }

    @PostMapping("/notifications")
    @Operation(summary = "创建通知")
    public R<NotificationVO> createNotification(@RequestBody Notification notification) {
        notification.setType(Notification.NotifyType.ADMIN);
        notification.setUser(null);
        Notification saved = notificationMapper.save(notification);
        return R.ok(VoConverter.toNotificationVO(saved));
    }

    @GetMapping("/users/{userId}/notifications")
    @Operation(summary = "获取指定用户通知")
    public R<List<NotificationVO>> getUserNotifications(@PathVariable Long userId) {
        return R.ok(VoConverter.toNotificationVOList(
                notificationMapper.findByUserIdOrderByCreatedAtDesc(userId)));
    }

    @DeleteMapping("/notifications/{id}")
    @Operation(summary = "删除通知")
    public R<Void> deleteNotification(@PathVariable Long id) {
        notificationMapper.deleteById(id);
        return R.ok();
    }
}
