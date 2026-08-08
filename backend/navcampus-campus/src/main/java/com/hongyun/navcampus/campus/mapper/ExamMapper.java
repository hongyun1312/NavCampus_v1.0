package com.hongyun.navcampus.campus.mapper;

import com.hongyun.navcampus.campus.entity.Exam;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import java.util.List;
import java.util.Optional;

@Mapper
public interface ExamMapper extends BaseMapper<Exam> {

    default Optional<Exam> findById(Long id) {
        return Optional.ofNullable(selectById(id));
    }

    default Exam save(Exam entity) {
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

    default List<Exam> findAll() {
        return selectList(null);
    }

    default long count() {
        return selectCount(null);
    }

    default void deleteAll(List<Exam> entities) {
        entities.forEach(e -> deleteById(e.getId()));
    }

    default void delete(Exam entity) {
        deleteById(entity.getId());
    }

    default List<Exam> findByUserIdOrderByDateAsc(Long userId) {
        return selectList(new LambdaQueryWrapper<Exam>()
                .eq(Exam::getUserId, userId)
                .orderByAsc(Exam::getDate));
    }
}
