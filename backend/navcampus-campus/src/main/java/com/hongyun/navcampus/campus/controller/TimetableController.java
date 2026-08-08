package com.hongyun.navcampus.campus.controller;

import com.hongyun.navcampus.common.core.R;
import com.hongyun.navcampus.campus.converter.VoConverter;
import com.hongyun.navcampus.campus.entity.Course;
import com.hongyun.navcampus.campus.entity.Exam;
import com.hongyun.navcampus.campus.mapper.CourseMapper;
import com.hongyun.navcampus.campus.mapper.ExamMapper;
import com.hongyun.navcampus.campus.vo.CourseVO;
import com.hongyun.navcampus.campus.vo.ExamVO;
import com.hongyun.navcampus.system.entity.User;
import com.hongyun.navcampus.system.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/timetable")
@Tag(name = "课表管理", description = "课程与考试安排")
public class TimetableController {

    @Autowired
    private CourseMapper courseMapper;
    @Autowired
    private ExamMapper examMapper;
    @Autowired
    private UserMapper userMapper;

    private Long getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userMapper.findByUsername(auth.getName()).orElse(null);
        return user != null ? user.getId() : null;
    }

    @GetMapping("/courses")
    @Operation(summary = "查询当前用户全部课程")
    public R<List<CourseVO>> getCourses() {
        Long uid = getCurrentUserId();
        if (uid == null) return R.fail(401, "未登录");
        return R.ok(VoConverter.toCourseVOList(courseMapper.findByUserIdOrderByWeekdayAscTimeAsc(uid)));
    }


    /**
     * 查询当前用户的今日课程。
     * <p>
     * 根据服务器当前日期获取星期几（ISO 标准：1=星期一，7=星期日），
     * 返回该用户在当天所有课程列表，按上课时间升序排列。
     * 前端 Campus3D.vue 在 onMounted 时调用此接口，用于在 3D 场景中
     * 对教学楼进行课程高亮展示。
     *
     * @return 今日课程列表（CourseVO），若未登录返回 401 错误
     */
    @GetMapping("/today")
    @Operation(summary = "查询今日课程", description = "根据当前星期几返回今日课程")
    public R<List<CourseVO>> getTodayCourses() {
        Long uid = getCurrentUserId();
        if (uid == null) return R.fail(401, "未登录");
        // LocalDate.now().getDayOfWeek().getValue() 返回 1~7，
        // 其中 1=星期一、7=星期日，与数据库 courses 表的 weekday 字段编码一致
        int today = LocalDate.now().getDayOfWeek().getValue();
        return R.ok(VoConverter.toCourseVOList(courseMapper.findByUserIdAndWeekdayOrderByTimeAsc(uid, today)));
    }


    /**
     * 查询当前用户的本周全部课程。
     * <p>
     * 返回当前用户所有星期的课程列表，按星期升序、时间升序排列。
     * 前端 Timetable.vue 在 onMounted 时调用此接口，
     * 用于在课表页面展示整周课程安排。
     *
     * @return 全部课程列表（CourseVO），若未登录返回 401 错误
     */
    @GetMapping("/week")
    @Operation(summary = "查询本周全部课程", description = "返回当前用户所有星期的课程列表")
    public R<List<CourseVO>> getWeekCourses() {
        Long uid = getCurrentUserId();
        if (uid == null) return R.fail(401, "未登录");
        return R.ok(VoConverter.toCourseVOList(courseMapper.findByUserIdOrderByWeekdayAscTimeAsc(uid)));
    }

    @GetMapping("/courses/{weekday}")
    @Operation(summary = "按星期查询课程")
    public R<List<CourseVO>> getCoursesByDay(@PathVariable int weekday) {
        Long uid = getCurrentUserId();
        if (uid == null) return R.fail(401, "未登录");
        return R.ok(VoConverter.toCourseVOList(courseMapper.findByUserIdAndWeekdayOrderByTimeAsc(uid, weekday)));
    }

    @PostMapping("/courses")
    @Operation(summary = "添加课程")
    public R<CourseVO> addCourse(@Valid @RequestBody Course course) {
        Long uid = getCurrentUserId();
        if (uid == null) return R.fail(401, "未登录");
        User u = new User();
        u.setId(uid);
        course.setUser(u);
        return R.ok(VoConverter.toCourseVO(courseMapper.save(course)));
    }

    @DeleteMapping("/courses/{id}")
    @Operation(summary = "删除课程")
    public R<Void> deleteCourse(@PathVariable Long id) {
        courseMapper.deleteById(id);
        return R.ok();
    }

    @DeleteMapping("/courses")
    @Operation(summary = "清空当前用户课程")
    public R<Void> deleteAllCourses() {
        Long uid = getCurrentUserId();
        if (uid == null) return R.fail(401, "未登录");
        courseMapper.deleteAll(courseMapper.findByUserIdOrderByWeekdayAscTimeAsc(uid));
        return R.ok();
    }

    @GetMapping("/exams")
    @Operation(summary = "查询当前用户全部考试")
    public R<List<ExamVO>> getExams() {
        Long uid = getCurrentUserId();
        if (uid == null) return R.fail(401, "未登录");
        return R.ok(VoConverter.toExamVOList(examMapper.findByUserIdOrderByDateAsc(uid)));
    }

    @PostMapping("/exams")
    @Operation(summary = "添加考试")
    public R<ExamVO> addExam(@Valid @RequestBody Exam exam) {
        Long uid = getCurrentUserId();
        if (uid == null) return R.fail(401, "未登录");
        User u = new User();
        u.setId(uid);
        exam.setUser(u);
        return R.ok(VoConverter.toExamVO(examMapper.save(exam)));
    }

    @DeleteMapping("/exams/{id}")
    @Operation(summary = "删除考试")
    public R<Void> deleteExam(@PathVariable Long id) {
        examMapper.deleteById(id);
        return R.ok();
    }
}
