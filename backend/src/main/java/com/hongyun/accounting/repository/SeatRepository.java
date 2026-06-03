package com.hongyun.accounting.repository;

import com.hongyun.accounting.entity.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SeatRepository extends JpaRepository<Seat, Long> {
    List<Seat> findBySection(String section);
    List<Seat> findByStatus(String status);
}
