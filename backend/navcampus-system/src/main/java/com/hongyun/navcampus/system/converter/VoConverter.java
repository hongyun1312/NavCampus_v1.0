package com.hongyun.navcampus.system.converter;

import com.hongyun.navcampus.system.entity.Notification;
import com.hongyun.navcampus.system.entity.SystemLog;
import com.hongyun.navcampus.system.entity.User;
import com.hongyun.navcampus.system.vo.NotificationVO;
import com.hongyun.navcampus.system.vo.SystemLogVO;
import com.hongyun.navcampus.system.vo.UserVO;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 实体到VO的转换工具。
 */
public class VoConverter {

    public static UserVO toUserVO(User user) {
        if (user == null) return null;
        UserVO vo = new UserVO();
        vo.setId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setEmail(user.getEmail());
        vo.setPhone(user.getPhone());
        vo.setAvatar(user.getAvatar());
        vo.setThemeColor(user.getThemeColor());
        vo.setRole(user.getRole() != null ? user.getRole().name() : "USER");
        vo.setBlacklisted(user.isBlacklisted());
        vo.setCreatedAt(user.getCreatedAt());
        return vo;
    }

    public static List<UserVO> toUserVOList(List<User> users) {
        return users.stream().map(VoConverter::toUserVO).collect(Collectors.toList());
    }

    public static NotificationVO toNotificationVO(Notification n) {
        if (n == null) return null;
        NotificationVO vo = new NotificationVO();
        vo.setId(n.getId());
        vo.setTitle(n.getTitle());
        vo.setContent(n.getContent());
        vo.setType(n.getType() != null ? n.getType().name() : null);
        vo.setUserId(n.getUserId());
        vo.setCreatedAt(n.getCreatedAt());
        vo.setRead(n.isRead());
        return vo;
    }

    public static List<NotificationVO> toNotificationVOList(List<Notification> list) {
        return list.stream().map(VoConverter::toNotificationVO).collect(Collectors.toList());
    }

    public static SystemLogVO toSystemLogVO(SystemLog log) {
        if (log == null) return null;
        SystemLogVO vo = new SystemLogVO();
        vo.setId(log.getId());
        vo.setAction(log.getAction());
        vo.setUserId(log.getUserId());
        vo.setDetails(log.getDetails());
        vo.setIpAddress(log.getIpAddress());
        vo.setCreatedAt(log.getCreatedAt());
        return vo;
    }

    public static List<SystemLogVO> toSystemLogVOList(List<SystemLog> list) {
        return list.stream().map(VoConverter::toSystemLogVO).collect(Collectors.toList());
    }
}
