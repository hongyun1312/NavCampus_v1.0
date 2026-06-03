package com.hongyun.accounting.repository;

import com.hongyun.accounting.entity.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TeacherRepository extends JpaRepository<Teacher, Long> {
    List<Teacher> findByNameContaining(String name);
}
