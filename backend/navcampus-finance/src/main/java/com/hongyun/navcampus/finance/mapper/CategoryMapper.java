package com.hongyun.navcampus.finance.mapper;

import com.hongyun.navcampus.finance.entity.Category;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import java.util.List;
import java.util.Optional;

@Mapper
public interface CategoryMapper extends BaseMapper<Category> {

    default Optional<Category> findById(Long id) {
        return Optional.ofNullable(selectById(id));
    }

    default Category save(Category entity) {
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

    default List<Category> findAll() {
        return selectList(null);
    }

    default long count() {
        return selectCount(null);
    }

    default void deleteAll(List<Category> entities) {
        entities.forEach(e -> deleteById(e.getId()));
    }

    default void delete(Category entity) {
        deleteById(entity.getId());
    }

    default List<Category> findByUserId(Long userId) {
        return selectList(new LambdaQueryWrapper<Category>()
                .eq(Category::getUserId, userId));
    }

    default List<Category> findByUserIdAndType(Long userId, Category.CategoryType type) {
        return selectList(new LambdaQueryWrapper<Category>()
                .eq(Category::getUserId, userId)
                .eq(Category::getType, type));
    }
}
