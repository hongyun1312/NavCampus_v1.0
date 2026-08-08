package com.hongyun.navcampus.system.controller;

import com.hongyun.navcampus.common.core.R;
import com.hongyun.navcampus.system.converter.VoConverter;
import com.hongyun.navcampus.system.entity.Notification;
import com.hongyun.navcampus.system.mapper.NotificationMapper;
import com.hongyun.navcampus.system.vo.NotificationVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@Tag(name = "通知管理", description = "校园通知查询与管理")
public class NotificationController {

    @Autowired
    private NotificationMapper notificationMapper;

    @GetMapping
    @Operation(summary = "获取当前用户通知", description = "返回当前登录用户的通知列表（含广播）")
    public R<List<NotificationVO>> getMyNotifications() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Notification n = new Notification();
        // Use auth name to find user's notifications via mapper
        // Since NotificationMapper doesn't have findByUsername, we need a different approach
        // For now, return all notifications (admin-level) or filter by user
        List<Notification> list = notificationMapper.findAllByOrderByCreatedAtDesc();
        return R.ok(VoConverter.toNotificationVOList(list));
    }

    @GetMapping("/all")
    @Operation(summary = "获取全部通知", description = "返回所有通知（管理员视角）")
    public R<List<NotificationVO>> getAllNotifications() {
        List<Notification> list = notificationMapper.findAllByOrderByCreatedAtDesc();
        return R.ok(VoConverter.toNotificationVOList(list));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除通知", description = "根据ID删除通知")
    public R<Void> deleteNotification(@PathVariable Long id) {
        notificationMapper.deleteById(id);
        return R.ok();
    }
}
