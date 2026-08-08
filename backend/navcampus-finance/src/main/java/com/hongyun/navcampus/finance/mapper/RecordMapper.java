package com.hongyun.navcampus.finance.mapper;

import com.hongyun.navcampus.finance.entity.Record;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Mapper
public interface RecordMapper extends BaseMapper<Record> {

    default Optional<Record> findById(Long id) {
        return Optional.ofNullable(selectById(id));
    }

    default Record save(Record entity) {
        if (entity.getUser() != null) {
            entity.setUserId(entity.getUser().getId());
        }
        if (entity.getCategory() != null) {
            entity.setCategoryId(entity.getCategory().getId());
        }
        if (entity.getAccount() != null) {
            entity.setAccountId(entity.getAccount().getId());
        }
        if (entity.getTargetAccount() != null) {
            entity.setTargetAccountId(entity.getTargetAccount().getId());
        }
        if (entity.getId() != null) {
            updateById(entity);
        } else {
            insert(entity);
        }
        return entity;
    }

    default List<Record> findAll() {
        return selectList(null);
    }

    default long count() {
        return selectCount(null);
    }

    default void deleteAll(List<Record> entities) {
        entities.forEach(e -> deleteById(e.getId()));
    }

    default void delete(Record entity) {
        deleteById(entity.getId());
    }

    default List<Record> findByUserId(Long userId) {
        return selectList(new LambdaQueryWrapper<Record>()
                .eq(Record::getUserId, userId));
    }

    @Select("SELECT * FROM records WHERE user_id = #{userId} AND time BETWEEN #{startDate} AND #{endDate}")
    List<Record> findByUserIdAndDateRange(@Param("userId") Long userId,
                                          @Param("startDate") LocalDateTime startDate,
                                          @Param("endDate") LocalDateTime endDate);

    default List<Record> findByUserIdAndAccountId(Long userId, Long accountId) {
        return selectList(new LambdaQueryWrapper<Record>()
                .eq(Record::getUserId, userId)
                .eq(Record::getAccountId, accountId));
    }
}
