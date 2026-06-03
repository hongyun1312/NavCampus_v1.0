package com.hongyun.accounting.repository;

import com.hongyun.accounting.entity.SystemLog;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SystemLogRepository extends JpaRepository<SystemLog, Long> {
    List<SystemLog> findByOrderByCreatedAtDesc();
}
