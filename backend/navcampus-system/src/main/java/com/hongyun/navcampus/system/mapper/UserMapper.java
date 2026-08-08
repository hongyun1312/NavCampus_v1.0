package com.hongyun.navcampus.system.mapper;

import com.hongyun.navcampus.system.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import java.util.List;
import java.util.Optional;

@Mapper
public interface UserMapper extends BaseMapper<User> {

    default Optional<User> findById(Long id) {
        return Optional.ofNullable(selectById(id));
    }

    default User save(User entity) {
        if (entity.getId() != null) {
            updateById(entity);
        } else {
            insert(entity);
        }
        return entity;
    }

    default List<User> findAll() {
        return selectList(null);
    }

    default long count() {
        return selectCount(null);
    }

    default void deleteAll(List<User> entities) {
        entities.forEach(e -> deleteById(e.getId()));
    }

    default void delete(User entity) {
        deleteById(entity.getId());
    }

    default Optional<User> findByUsername(String username) {
        User user = selectOne(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, username));
        return Optional.ofNullable(user);
    }

    default boolean existsByUsername(String username) {
        return selectCount(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, username)) > 0;
    }

    default boolean existsByEmail(String email) {
        return selectCount(new LambdaQueryWrapper<User>()
                .eq(User::getEmail, email)) > 0;
    }
}
