package com.hongyun.accounting.repository;

import com.hongyun.accounting.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CourseRepository extends JpaRepository<Course, Long> {
    List<Course> findByUserIdAndWeekdayOrderByTimeAsc(Long userId, int weekday);
    List<Course> findByUserIdOrderByWeekdayAscTimeAsc(Long userId);
}
