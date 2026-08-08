package com.hongyun.navcampus.campus.mapper;

import com.hongyun.navcampus.campus.entity.Course;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import java.util.List;
import java.util.Optional;

@Mapper
public interface CourseMapper extends BaseMapper<Course> {

    default Optional<Course> findById(Long id) {
        return Optional.ofNullable(selectById(id));
    }

    default Course save(Course entity) {
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

    default List<Course> findAll() {
        return selectList(null);
    }

    default long count() {
        return selectCount(null);
    }

    default void deleteAll(List<Course> entities) {
        entities.forEach(e -> deleteById(e.getId()));
    }

    default void delete(Course entity) {
        deleteById(entity.getId());
    }

    default List<Course> findByUserIdAndWeekdayOrderByTimeAsc(Long userId, int weekday) {
        return selectList(new LambdaQueryWrapper<Course>()
                .eq(Course::getUserId, userId)
                .eq(Course::getWeekday, weekday)
                .orderByAsc(Course::getTime));
    }

    default List<Course> findByUserIdOrderByWeekdayAscTimeAsc(Long userId) {
        return selectList(new LambdaQueryWrapper<Course>()
                .eq(Course::getUserId, userId)
                .orderByAsc(Course::getWeekday)
                .orderByAsc(Course::getTime));
    }
}
