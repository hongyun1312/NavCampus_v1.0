package com.hongyun.navcampus.campus.mapper;

import com.hongyun.navcampus.campus.entity.Reservation;
import com.hongyun.navcampus.system.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Mapper
public interface ReservationMapper extends BaseMapper<Reservation> {

    default Optional<Reservation> findById(Long id) {
        return Optional.ofNullable(selectById(id));
    }

    default Reservation save(Reservation entity) {
        if (entity.getUser() != null) {
            entity.setUserId(entity.getUser().getId());
        }
        if (entity.getSeat() != null) {
            entity.setSeatId(entity.getSeat().getId());
        }
        if (entity.getId() != null) {
            updateById(entity);
        } else {
            insert(entity);
        }
        return entity;
    }

    default List<Reservation> findAll() {
        return selectList(null);
    }

    default long count() {
        return selectCount(null);
    }

    default void deleteAll(List<Reservation> entities) {
        entities.forEach(e -> deleteById(e.getId()));
    }

    default void delete(Reservation entity) {
        deleteById(entity.getId());
    }

    default List<Reservation> findByUserOrderByStartTimeDesc(User user) {
        return selectList(new LambdaQueryWrapper<Reservation>()
                .eq(Reservation::getUserId, user.getId())
                .orderByDesc(Reservation::getStartTime));
    }

    @Select("SELECT * FROM reservations WHERE seat_id = #{seatId} " +
            "AND start_time < #{end} AND end_time > #{start} " +
            "AND status NOT IN ('CANCELLED', 'MISSED', 'COMPLETED')")
    List<Reservation> findOverlappingReservations(@Param("seatId") Long seatId,
                                                  @Param("start") LocalDateTime start,
                                                  @Param("end") LocalDateTime end);

    @Select("SELECT * FROM reservations " +
            "WHERE start_time < #{end} AND end_time > #{start} " +
            "AND status IN ('CONFIRMED', 'CHECKED_IN')")
    List<Reservation> findAllOverlappingReservations(@Param("start") LocalDateTime start,
                                                     @Param("end") LocalDateTime end);

    default List<Reservation> findByStatus(String status) {
        return selectList(new LambdaQueryWrapper<Reservation>()
                .eq(Reservation::getStatus, status));
    }
}
