package com.hongyun.accounting.repository;

import com.hongyun.accounting.entity.Exam;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExamRepository extends JpaRepository<Exam, Long> {
    List<Exam> findByUserIdOrderByDateAsc(Long userId);
}
