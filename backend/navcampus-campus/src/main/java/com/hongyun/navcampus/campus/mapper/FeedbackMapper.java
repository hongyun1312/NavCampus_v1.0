package com.hongyun.navcampus.campus.mapper;

import com.hongyun.navcampus.campus.entity.Feedback;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import java.util.List;
import java.util.Optional;

@Mapper
public interface FeedbackMapper extends BaseMapper<Feedback> {

    default Optional<Feedback> findById(Long id) {
        return Optional.ofNullable(selectById(id));
    }

    default Feedback save(Feedback entity) {
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

    default List<Feedback> findAll() {
        return selectList(null);
    }

    default long count() {
        return selectCount(null);
    }

    default void deleteAll(List<Feedback> entities) {
        entities.forEach(e -> deleteById(e.getId()));
    }

    default void delete(Feedback entity) {
        deleteById(entity.getId());
    }

    default List<Feedback> findAllByOrderByCreatedAtDesc() {
        return selectList(new LambdaQueryWrapper<Feedback>()
                .orderByDesc(Feedback::getCreatedAt));
    }
}
