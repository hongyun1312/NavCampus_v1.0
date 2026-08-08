package com.hongyun.navcampus.campus.converter;

import com.hongyun.navcampus.campus.entity.*;
import com.hongyun.navcampus.campus.vo.*;
import java.util.List;
import java.util.stream.Collectors;

public class VoConverter {

    public static SeatVO toSeatVO(Seat seat) {
        if (seat == null) return null;
        SeatVO vo = new SeatVO();
        vo.setId(seat.getId());
        vo.setName(seat.getName());
        vo.setStatus(seat.getStatus());
        vo.setType(seat.getType());
        vo.setSection(seat.getSection());
        return vo;
    }

    public static List<SeatVO> toSeatVOList(List<Seat> list) {
        return list.stream().map(VoConverter::toSeatVO).collect(Collectors.toList());
    }

    public static ReservationVO toReservationVO(Reservation r) {
        if (r == null) return null;
        ReservationVO vo = new ReservationVO();
        vo.setId(r.getId());
        vo.setUserId(r.getUserId());
        vo.setSeatId(r.getSeatId());
        vo.setStartTime(r.getStartTime());
        vo.setEndTime(r.getEndTime());
        vo.setStatus(r.getStatus());
        vo.setCheckInTime(r.getCheckInTime());
        vo.setCreatedAt(r.getCreatedAt());
        return vo;
    }

    public static List<ReservationVO> toReservationVOList(List<Reservation> list) {
        return list.stream().map(VoConverter::toReservationVO).collect(Collectors.toList());
    }

    public static CourseVO toCourseVO(Course c) {
        if (c == null) return null;
        CourseVO vo = new CourseVO();
        vo.setId(c.getId());
        vo.setUserId(c.getUserId());
        vo.setName(c.getName());
        vo.setRoom(c.getRoom());
        vo.setTeacher(c.getTeacher());
        vo.setWeekday(c.getWeekday());
        vo.setTime(c.getTime());
        vo.setImportant(c.isImportant());
        vo.setCreatedAt(c.getCreatedAt());
        return vo;
    }

    public static List<CourseVO> toCourseVOList(List<Course> list) {
        return list.stream().map(VoConverter::toCourseVO).collect(Collectors.toList());
    }

    public static ExamVO toExamVO(Exam e) {
        if (e == null) return null;
        ExamVO vo = new ExamVO();
        vo.setId(e.getId());
        vo.setUserId(e.getUserId());
        vo.setName(e.getName());
        vo.setDate(e.getDate());
        vo.setImportant(e.isImportant());
        vo.setCreatedAt(e.getCreatedAt());
        return vo;
    }

    public static List<ExamVO> toExamVOList(List<Exam> list) {
        return list.stream().map(VoConverter::toExamVO).collect(Collectors.toList());
    }

    public static FeedbackVO toFeedbackVO(Feedback f) {
        if (f == null) return null;
        FeedbackVO vo = new FeedbackVO();
        vo.setId(f.getId());
        vo.setUserId(f.getUserId());
        vo.setRate(f.getRate());
        vo.setContent(f.getContent());
        vo.setCreatedAt(f.getCreatedAt());
        return vo;
    }

    public static TeacherVO toTeacherVO(Teacher t) {
        if (t == null) return null;
        TeacherVO vo = new TeacherVO();
        vo.setId(t.getId());
        vo.setName(t.getName());
        vo.setTitle(t.getTitle());
        vo.setDepartment(t.getDepartment());
        vo.setEmail(t.getEmail());
        vo.setPhone(t.getPhone());
        vo.setResearchArea(t.getResearchArea());
        vo.setBio(t.getBio());
        return vo;
    }

    public static List<TeacherVO> toTeacherVOList(List<Teacher> list) {
        return list.stream().map(VoConverter::toTeacherVO).collect(Collectors.toList());
    }
}
