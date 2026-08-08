package com.hongyun.navcampus.campus.mapper;

import com.hongyun.navcampus.campus.entity.Seat;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import java.util.List;
import java.util.Optional;

@Mapper
public interface SeatMapper extends BaseMapper<Seat> {

    default Optional<Seat> findById(Long id) {
        return Optional.ofNullable(selectById(id));
    }

    default Seat save(Seat entity) {
        if (entity.getId() != null) {
            updateById(entity);
        } else {
            insert(entity);
        }
        return entity;
    }

    default List<Seat> findAll() {
        return selectList(null);
    }

    default long count() {
        return selectCount(null);
    }

    default void deleteAll(List<Seat> entities) {
        entities.forEach(e -> deleteById(e.getId()));
    }

    default void delete(Seat entity) {
        deleteById(entity.getId());
    }

    default List<Seat> findBySection(String section) {
        return selectList(new LambdaQueryWrapper<Seat>()
                .eq(Seat::getSection, section));
    }

    default List<Seat> findByStatus(String status) {
        return selectList(new LambdaQueryWrapper<Seat>()
                .eq(Seat::getStatus, status));
    }
}
