package com.hongyun.navcampus.campus.controller;

import com.hongyun.navcampus.common.core.R;
import com.hongyun.navcampus.campus.converter.VoConverter;
import com.hongyun.navcampus.campus.entity.Teacher;
import com.hongyun.navcampus.campus.mapper.TeacherMapper;
import com.hongyun.navcampus.campus.vo.TeacherVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import java.util.List;

@RestController
@RequestMapping("/api/teachers")
@Tag(name = "教师信息", description = "教师信息查询")
public class TeacherController {

    @Autowired
    private TeacherMapper teacherMapper;

    @GetMapping
    @Operation(summary = "查询全部教师")
    public R<List<TeacherVO>> list() {
        return R.ok(VoConverter.toTeacherVOList(teacherMapper.findAll()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "根据ID查询教师")
    public R<TeacherVO> getById(@PathVariable Long id) {
        Teacher t = teacherMapper.selectById(id);
        if (t == null) return R.fail(404, "教师不存在");
        return R.ok(VoConverter.toTeacherVO(t));
    }

    @GetMapping("/search")
    @Operation(summary = "按名称搜索教师")
    public R<List<TeacherVO>> search(@RequestParam String name) {
        return R.ok(VoConverter.toTeacherVOList(teacherMapper.findByNameContaining(name)));
    }
}
