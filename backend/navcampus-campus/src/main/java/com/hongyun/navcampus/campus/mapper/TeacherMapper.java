package com.hongyun.navcampus.campus.mapper;

import com.hongyun.navcampus.campus.entity.Teacher;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import java.util.List;
import java.util.Optional;

@Mapper
public interface TeacherMapper extends BaseMapper<Teacher> {

    default Optional<Teacher> findById(Long id) {
        return Optional.ofNullable(selectById(id));
    }

    default Teacher save(Teacher entity) {
        if (entity.getId() != null) {
            updateById(entity);
        } else {
            insert(entity);
        }
        return entity;
    }

    default List<Teacher> findAll() {
        return selectList(null);
    }

    default long count() {
        return selectCount(null);
    }

    default void deleteAll(List<Teacher> entities) {
        entities.forEach(e -> deleteById(e.getId()));
    }

    default void delete(Teacher entity) {
        deleteById(entity.getId());
    }

    default List<Teacher> findByNameContaining(String name) {
        return selectList(new LambdaQueryWrapper<Teacher>()
                .like(Teacher::getName, name));
    }
}
