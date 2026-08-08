package com.hongyun.navcampus.finance.mapper;

import com.hongyun.navcampus.finance.entity.Account;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import java.util.List;
import java.util.Optional;

@Mapper
public interface AccountMapper extends BaseMapper<Account> {

    default Optional<Account> findById(Long id) {
        return Optional.ofNullable(selectById(id));
    }

    default Account save(Account entity) {
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

    default List<Account> findAll() {
        return selectList(null);
    }

    default long count() {
        return selectCount(null);
    }

    default void deleteAll(List<Account> entities) {
        entities.forEach(e -> deleteById(e.getId()));
    }

    default void delete(Account entity) {
        deleteById(entity.getId());
    }

    default List<Account> findByUserId(Long userId) {
        return selectList(new LambdaQueryWrapper<Account>()
                .eq(Account::getUserId, userId));
    }
}
