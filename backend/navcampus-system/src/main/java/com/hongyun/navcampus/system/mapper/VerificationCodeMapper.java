package com.hongyun.navcampus.system.mapper;

import com.hongyun.navcampus.system.entity.VerificationCode;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import java.util.List;
import java.util.Optional;

@Mapper
public interface VerificationCodeMapper extends BaseMapper<VerificationCode> {

    default Optional<VerificationCode> findById(Long id) {
        return Optional.ofNullable(selectById(id));
    }

    default VerificationCode save(VerificationCode entity) {
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

    default List<VerificationCode> findAll() {
        return selectList(null);
    }

    default long count() {
        return selectCount(null);
    }

    default void deleteAll(List<VerificationCode> entities) {
        entities.forEach(e -> deleteById(e.getId()));
    }

    default void delete(VerificationCode entity) {
        deleteById(entity.getId());
    }

    default Optional<VerificationCode> findTopByUserIdAndTypeAndTargetOrderByExpiresAtDesc(
            Long userId, VerificationCode.CodeType type, String target) {
        VerificationCode vc = selectOne(new LambdaQueryWrapper<VerificationCode>()
                .eq(VerificationCode::getUserId, userId)
                .eq(VerificationCode::getType, type)
                .eq(VerificationCode::getTarget, target)
                .orderByDesc(VerificationCode::getExpiresAt)
                .last("LIMIT 1"));
        return Optional.ofNullable(vc);
    }
}
