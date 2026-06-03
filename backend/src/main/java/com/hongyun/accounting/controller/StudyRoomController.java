package com.hongyun.accounting.controller;

import com.hongyun.accounting.entity.Reservation;
import com.hongyun.accounting.entity.Seat;
import com.hongyun.accounting.service.StudyRoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/study-room")
public class StudyRoomController {

    @Autowired
    private StudyRoomService studyRoomService;

    @GetMapping("/seats")
    public List<Seat> getAllSeats() {
        return studyRoomService.getAllSeats();
    }

    @PostMapping("/seats/{id}/status")
    public ResponseEntity<?> updateSeatStatus(@PathVariable Long id, @RequestBody Map<String, String> payload) {
        try {
            String status = payload.get("status");
            return ResponseEntity.ok(studyRoomService.updateSeatStatus(id, status));
        } catch (Exception e) {
             return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @GetMapping("/reservations/my")
    public List<Reservation> getMyReservations() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return studyRoomService.getUserReservations(auth.getName());
    }

    @PostMapping("/reserve")
    public ResponseEntity<?> reserveSeat(@RequestBody Map<String, Object> payload) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            Long seatId = Long.valueOf(payload.get("seatId").toString());
            
            String startStr = (String) payload.get("startTime"); // ISO format expected or specific
            String endStr = (String) payload.get("endTime");
            
            // Handle frontend ISO string usually: "2023-10-27T10:00:00.000Z" or similar
            // For simplicity, let's assume standard LocalDateTime parse-able format or ISO_DATE_TIME
            LocalDateTime start = LocalDateTime.parse(startStr, DateTimeFormatter.ISO_DATE_TIME);
            LocalDateTime end = LocalDateTime.parse(endStr, DateTimeFormatter.ISO_DATE_TIME);

            Reservation res = studyRoomService.createReservation(auth.getName(), seatId, start, end);
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @PostMapping("/cancel/{id}")
    public ResponseEntity<?> cancelReservation(@PathVariable Long id) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            studyRoomService.cancelReservation(auth.getName(), id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @PostMapping("/check-in/{id}")
    public ResponseEntity<?> checkIn(@PathVariable Long id) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            studyRoomService.checkIn(auth.getName(), id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @PostMapping("/feedback")
    public ResponseEntity<?> submitFeedback(@RequestBody Map<String, Object> payload) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            Integer rate = (Integer) payload.get("rate");
            String content = (String) payload.get("content");
            studyRoomService.submitFeedback(auth.getName(), rate, content);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @GetMapping("/stats")
    public ResponseEntity<?> getStats() {
        return ResponseEntity.ok(studyRoomService.getStats());
    }

    @PostMapping("/availability")
    public ResponseEntity<?> checkAvailability(@RequestBody Map<String, String> payload) {
        try {
            String startStr = payload.get("startTime");
            String endStr = payload.get("endTime");
            LocalDateTime start = LocalDateTime.parse(startStr, DateTimeFormatter.ISO_DATE_TIME);
            LocalDateTime end = LocalDateTime.parse(endStr, DateTimeFormatter.ISO_DATE_TIME);
            
            return ResponseEntity.ok(studyRoomService.getOccupiedSeatIds(start, end));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }
}
