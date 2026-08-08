package com.hongyun.navcampus.system.mapper;

import com.hongyun.navcampus.system.entity.SystemLog;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import java.util.List;
import java.util.Optional;

@Mapper
public interface SystemLogMapper extends BaseMapper<SystemLog> {

    default Optional<SystemLog> findById(Long id) {
        return Optional.ofNullable(selectById(id));
    }

    default SystemLog save(SystemLog entity) {
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

    default List<SystemLog> findAll() {
        return selectList(null);
    }

    default long count() {
        return selectCount(null);
    }

    default void deleteAll(List<SystemLog> entities) {
        entities.forEach(e -> deleteById(e.getId()));
    }

    default void delete(SystemLog entity) {
        deleteById(entity.getId());
    }

    default List<SystemLog> findByOrderByCreatedAtDesc() {
        return selectList(new LambdaQueryWrapper<SystemLog>()
                .orderByDesc(SystemLog::getCreatedAt));
    }
}
