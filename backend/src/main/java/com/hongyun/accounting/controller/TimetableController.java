package com.hongyun.accounting.controller;

import com.hongyun.accounting.entity.Course;
import com.hongyun.accounting.entity.Exam;
import com.hongyun.accounting.entity.User;
import com.hongyun.accounting.repository.CourseRepository;
import com.hongyun.accounting.repository.ExamRepository;
import com.hongyun.accounting.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/timetable")
@CrossOrigin(origins = "*")
public class TimetableController {

    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private ExamRepository examRepository;
    @Autowired
    private UserRepository userRepository;

    private User currentUser() {
        UserDetails ud = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userRepository.findByUsername(ud.getUsername()).orElseThrow();
    }

    private static final String[] WEEKDAY_CN = {"一","二","三","四","五","六","日"};
    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH:mm");

    @GetMapping("/today")
    public List<Map<String, Object>> today() {
        User u = currentUser();
        int weekday = toWeekday(LocalDate.now().getDayOfWeek());
        List<Course> list = courseRepository.findByUserIdAndWeekdayOrderByTimeAsc(u.getId(), weekday);
        return list.stream().map(c -> {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("id", c.getId());
            m.put("time", c.getTime() != null ? c.getTime().format(TIME_FMT) : "");
            m.put("name", c.getName());
            m.put("room", c.getRoom());
            m.put("teacher", c.getTeacher());
            m.put("important", c.isImportant());
            return m;
        }).collect(Collectors.toList());
    }

    @GetMapping("/week")
    public List<Map<String, Object>> week() {
        User u = currentUser();
        List<Course> list = courseRepository.findByUserIdOrderByWeekdayAscTimeAsc(u.getId());
        return list.stream().map(c -> {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("id", c.getId());
            m.put("weekday", toWeekdayCn(c.getWeekday()));
            m.put("time", c.getTime() != null ? c.getTime().format(TIME_FMT) : "");
            m.put("name", c.getName());
            m.put("room", c.getRoom());
            m.put("teacher", c.getTeacher());
            m.put("important", c.isImportant());
            return m;
        }).collect(Collectors.toList());
    }

    @GetMapping("/exams")
    public List<Map<String, Object>> exams() {
        User u = currentUser();
        List<Exam> list = examRepository.findByUserIdOrderByDateAsc(u.getId());
        return list.stream().map(e -> {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("id", e.getId());
            m.put("name", e.getName());
            m.put("date", e.getDate() != null ? e.getDate().toString() : "");
            m.put("important", e.isImportant());
            return m;
        }).collect(Collectors.toList());
    }

    @PostMapping("/import")
    public void importCourses(@RequestBody List<Map<String, Object>> courses) {
        User u = currentUser();
        for (Map<String, Object> map : courses) {
            try {
                Course c = new Course();
                c.setUser(u);
                c.setName((String) map.get("name"));
                c.setRoom((String) map.get("room"));
                c.setTeacher((String) map.get("teacher"));
                c.setWeekday(Integer.parseInt(map.get("weekday").toString()));
                // time format HH:mm
                c.setTime(java.time.LocalTime.parse((String) map.get("time"), TIME_FMT));
                if (map.containsKey("important")) {
                    c.setImportant(Boolean.parseBoolean(map.get("important").toString()));
                }
                courseRepository.save(c);
            } catch (Exception e) {
                // ignore invalid rows
                e.printStackTrace();
            }
        }
    }

    @DeleteMapping
    public void clear() {
        User u = currentUser();
        List<Course> list = courseRepository.findByUserIdOrderByWeekdayAscTimeAsc(u.getId());
        courseRepository.deleteAll(list);
    }

    private int toWeekday(DayOfWeek d) {
        switch (d) {
            case MONDAY: return 1;
            case TUESDAY: return 2;
            case WEDNESDAY: return 3;
            case THURSDAY: return 4;
            case FRIDAY: return 5;
            case SATURDAY: return 6;
            case SUNDAY: return 7;
            default: return 1;
        }
    }

    private String toWeekdayCn(int weekday) {
        if (weekday >= 1 && weekday <= 7) return WEEKDAY_CN[weekday - 1];
        return "";
    }
}

