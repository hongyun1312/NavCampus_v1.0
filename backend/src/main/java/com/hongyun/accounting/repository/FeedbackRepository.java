package com.hongyun.accounting.repository;

import com.hongyun.accounting.entity.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * 反馈仓库接口
 */
public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
    List<Feedback> findAllByOrderByCreatedAtDesc();
}
