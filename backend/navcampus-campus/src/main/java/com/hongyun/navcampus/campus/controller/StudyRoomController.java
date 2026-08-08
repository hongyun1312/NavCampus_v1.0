package com.hongyun.navcampus.campus.controller;

import com.hongyun.navcampus.common.core.R;
import com.hongyun.navcampus.campus.converter.VoConverter;
import com.hongyun.navcampus.campus.dto.AvailabilityRequest;
import com.hongyun.navcampus.campus.dto.FeedbackRequest;
import com.hongyun.navcampus.campus.dto.ReserveSeatRequest;
import com.hongyun.navcampus.campus.dto.SeatStatusRequest;
import com.hongyun.navcampus.campus.entity.Reservation;
import com.hongyun.navcampus.campus.entity.Seat;
import com.hongyun.navcampus.campus.service.StudyRoomService;
import com.hongyun.navcampus.campus.vo.ReservationVO;
import com.hongyun.navcampus.campus.vo.SeatVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/study-room")
@Tag(name = "自习室管理", description = "座位查询、预约、签到、反馈")
public class StudyRoomController {

    @Autowired
    private StudyRoomService studyRoomService;

    @GetMapping("/seats")
    @Operation(summary = "查询所有座位")
    public R<List<SeatVO>> getAllSeats() {
        return R.ok(VoConverter.toSeatVOList(studyRoomService.getAllSeats()));
    }

    @PostMapping("/seats/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "更新座位状态", description = "管理员操作")
    public R<SeatVO> updateSeatStatus(@PathVariable Long id, @Valid @RequestBody SeatStatusRequest payload) {
        Seat seat = studyRoomService.updateSeatStatus(id, payload.getStatus());
        return R.ok(VoConverter.toSeatVO(seat));
    }

    @GetMapping("/reservations/my")
    @Operation(summary = "查询当前用户预约记录")
    public R<List<ReservationVO>> getMyReservations() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return R.ok(VoConverter.toReservationVOList(studyRoomService.getUserReservations(auth.getName())));
    }

    @PostMapping("/reserve")
    @Operation(summary = "预约座位")
    public R<ReservationVO> reserveSeat(@Valid @RequestBody ReserveSeatRequest payload) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        LocalDateTime start = LocalDateTime.parse(payload.getStartTime(), DateTimeFormatter.ISO_DATE_TIME);
        LocalDateTime end = LocalDateTime.parse(payload.getEndTime(), DateTimeFormatter.ISO_DATE_TIME);
        Reservation res = studyRoomService.createReservation(auth.getName(), payload.getSeatId(), start, end);
        return R.ok(VoConverter.toReservationVO(res));
    }

    @PostMapping("/cancel/{id}")
    @Operation(summary = "取消预约")
    public R<Void> cancelReservation(@PathVariable Long id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        studyRoomService.cancelReservation(auth.getName(), id);
        return R.ok();
    }

    @PostMapping("/check-in/{id}")
    @Operation(summary = "签到")
    public R<Void> checkIn(@PathVariable Long id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        studyRoomService.checkIn(auth.getName(), id);
        return R.ok();
    }

    @PostMapping("/feedback")
    @Operation(summary = "提交反馈")
    public R<Void> submitFeedback(@Valid @RequestBody FeedbackRequest payload) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        studyRoomService.submitFeedback(auth.getName(), payload.getRate(), payload.getContent());
        return R.ok();
    }

    @GetMapping("/stats")
    @Operation(summary = "查询自习室统计数据")
    public R<Map<String, Object>> getStats() {
        return R.ok(studyRoomService.getStats());
    }

    @PostMapping("/availability")
    @Operation(summary = "查询指定时间段内被占用的座位ID列表")
    public R<List<Long>> checkAvailability(@Valid @RequestBody AvailabilityRequest payload) {
        LocalDateTime start = LocalDateTime.parse(payload.getStartTime(), DateTimeFormatter.ISO_DATE_TIME);
        LocalDateTime end = LocalDateTime.parse(payload.getEndTime(), DateTimeFormatter.ISO_DATE_TIME);
        return R.ok(studyRoomService.getOccupiedSeatIds(start, end));
    }
}
