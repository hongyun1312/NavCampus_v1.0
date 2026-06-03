package com.hongyun.accounting.repository;

import com.hongyun.accounting.entity.Reservation;
import com.hongyun.accounting.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findByUserOrderByStartTimeDesc(User user);
    
    @Query("SELECT r FROM Reservation r WHERE r.seat.id = :seatId AND " +
           "((r.startTime < :end AND r.endTime > :start)) AND " +
           "r.status NOT IN ('CANCELLED', 'MISSED', 'COMPLETED')")
    List<Reservation> findOverlappingReservations(@Param("seatId") Long seatId, 
                                                  @Param("start") LocalDateTime start, 
                                                  @Param("end") LocalDateTime end);

    @Query("SELECT r FROM Reservation r WHERE " +
           "((r.startTime < :end AND r.endTime > :start)) AND " +
           "r.status IN ('CONFIRMED', 'CHECKED_IN')")
    List<Reservation> findAllOverlappingReservations(@Param("start") LocalDateTime start, 
                                                     @Param("end") LocalDateTime end);
                                                  
    List<Reservation> findByStatus(String status);
}
