package com.hongyun.navcampus.system.mapper;

import com.hongyun.navcampus.system.entity.Notification;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import java.util.List;
import java.util.Optional;

@Mapper
public interface NotificationMapper extends BaseMapper<Notification> {

    default Optional<Notification> findById(Long id) {
        return Optional.ofNullable(selectById(id));
    }

    default Notification save(Notification entity) {
        if (entity.getUser() != null) {
            entity.setUserId(entity.getUser().getId());
        }
        if (entity.getId() != null) {
            updateById(entity);
        } else {
            insert(entity);
        }
        return entity;
    }

    default List<Notification> findAll() {
        return selectList(null);
    }

    default long count() {
        return selectCount(null);
    }

    default void deleteAll(List<Notification> entities) {
        entities.forEach(e -> deleteById(e.getId()));
    }

    default void delete(Notification entity) {
        deleteById(entity.getId());
    }

    default List<Notification> findByTypeOrderByCreatedAtDesc(Notification.NotifyType type) {
        return selectList(new LambdaQueryWrapper<Notification>()
                .eq(Notification::getType, type)
                .orderByDesc(Notification::getCreatedAt));
    }

    default List<Notification> findByUserIdOrderByCreatedAtDesc(Long userId) {
        return selectList(new LambdaQueryWrapper<Notification>()
                .eq(Notification::getUserId, userId)
                .orderByDesc(Notification::getCreatedAt));
    }

    default List<Notification> findByUserIdOrUserIsNullOrderByCreatedAtDesc(Long userId) {
        return selectList(new LambdaQueryWrapper<Notification>()
                .and(w -> w.eq(Notification::getUserId, userId)
                        .or().isNull(Notification::getUserId))
                .orderByDesc(Notification::getCreatedAt));
    }

    default List<Notification> findAllByOrderByCreatedAtDesc() {
        return selectList(new LambdaQueryWrapper<Notification>()
                .orderByDesc(Notification::getCreatedAt));
    }
}
