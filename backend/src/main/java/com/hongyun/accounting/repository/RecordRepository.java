package com.hongyun.accounting.repository;

import com.hongyun.accounting.entity.Record;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 记录仓库。
 * 支持按用户、时间范围与账户进行查询。
 */
public interface RecordRepository extends JpaRepository<Record, Long> {
    List<Record> findByUserId(Long userId);
    
    @Query("SELECT r FROM Record r WHERE r.user.id = :userId AND r.time BETWEEN :startDate AND :endDate")
    List<Record> findByUserIdAndDateRange(@Param("userId") Long userId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    List<Record> findByUserIdAndAccountId(Long userId, Long accountId);
}
