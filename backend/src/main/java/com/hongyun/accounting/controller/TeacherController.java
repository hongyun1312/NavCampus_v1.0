package com.hongyun.accounting.controller;

import com.hongyun.accounting.entity.Teacher;
import com.hongyun.accounting.repository.TeacherRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/teachers")
@CrossOrigin(origins = "*")
public class TeacherController {

    @Autowired
    private TeacherRepository teacherRepository;

    @PostConstruct
    public void init() {
        if (teacherRepository.count() < 6) {
            if (teacherRepository.count() == 0) {
                createTeacher("王老师", "教授", "计算机学院", "wang@example.com", "13800138000", "人工智能", "多年从事AI教学与研究。");
                createTeacher("李老师", "副教授", "外国语学院", "li@example.com", "13900139000", "英语文学", "专注于英美文学研究。");
                createTeacher("张老师", "讲师", "数学系", "zhang@example.com", "13700137000", "应用数学", "擅长数学建模。");
            }
            // 新增示例教师
            createTeacher("陈老师", "教授", "物理学院", "chen@example.com", "13600136000", "量子力学", "量子计算领域的专家。");
            createTeacher("刘老师", "副教授", "化学系", "liu@example.com", "13500135000", "有机化学", "主要研究新型有机材料。");
            createTeacher("赵老师", "讲师", "艺术学院", "zhao@example.com", "13400134000", "视觉传达", "多次获得国际设计大奖。");
        }
    }

    private void createTeacher(String name, String title, String dept, String email, String phone, String area, String bio) {
        Teacher t = new Teacher();
        t.setName(name);
        t.setTitle(title);
        t.setDepartment(dept);
        t.setEmail(email);
        t.setPhone(phone);
        t.setResearchArea(area);
        t.setBio(bio);
        teacherRepository.save(t);
    }

    @GetMapping
    public List<Teacher> search(@RequestParam(required = false) String query) {
        if (query == null || query.trim().isEmpty()) {
            return teacherRepository.findAll();
        }
        return teacherRepository.findByNameContaining(query);
    }
}
