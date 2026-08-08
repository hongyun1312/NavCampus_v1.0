package com.hongyun.navcampus.finance.mapper;

import com.hongyun.navcampus.finance.entity.Budget;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import java.util.List;
import java.util.Optional;

@Mapper
public interface BudgetMapper extends BaseMapper<Budget> {

    default Optional<Budget> findById(Long id) {
        return Optional.ofNullable(selectById(id));
    }

    default Budget save(Budget entity) {
        if (entity.getUser() != null) {
            entity.setUserId(entity.getUser().getId());
        }
        if (entity.getCategory() != null) {
            entity.setCategoryId(entity.getCategory().getId());
        }
        if (entity.getId() != null) {
            updateById(entity);
        } else {
            insert(entity);
        }
        return entity;
    }

    default List<Budget> findAll() {
        return selectList(null);
    }

    default long count() {
        return selectCount(null);
    }

    default void deleteAll(List<Budget> entities) {
        entities.forEach(e -> deleteById(e.getId()));
    }

    default void delete(Budget entity) {
        deleteById(entity.getId());
    }

    default List<Budget> findByUserId(Long userId) {
        return selectList(new LambdaQueryWrapper<Budget>()
                .eq(Budget::getUserId, userId));
    }

    default List<Budget> findByUserIdAndPeriod(Long userId, String period) {
        return selectList(new LambdaQueryWrapper<Budget>()
                .eq(Budget::getUserId, userId)
                .eq(Budget::getPeriod, period));
    }

    default Optional<Budget> findByUserIdAndPeriodAndType(Long userId, String period, Budget.BudgetType type) {
        Budget budget = selectOne(new LambdaQueryWrapper<Budget>()
                .eq(Budget::getUserId, userId)
                .eq(Budget::getPeriod, period)
                .eq(Budget::getType, type));
        return Optional.ofNullable(budget);
    }
}
