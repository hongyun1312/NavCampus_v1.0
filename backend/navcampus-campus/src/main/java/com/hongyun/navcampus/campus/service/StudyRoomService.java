package com.hongyun.navcampus.campus.service;

import com.hongyun.navcampus.system.entity.Notification;
import com.hongyun.navcampus.campus.entity.Reservation;
import com.hongyun.navcampus.campus.entity.Seat;
import com.hongyun.navcampus.system.entity.User;
import com.hongyun.navcampus.system.mapper.NotificationMapper;
import com.hongyun.navcampus.campus.mapper.ReservationMapper;
import com.hongyun.navcampus.campus.mapper.SeatMapper;
import com.hongyun.navcampus.system.mapper.UserMapper;
import com.hongyun.navcampus.campus.entity.Feedback;
import com.hongyun.navcampus.campus.mapper.FeedbackMapper;
import com.hongyun.navcampus.system.service.SystemLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class StudyRoomService {

    @Autowired
    private SeatMapper seatMapper;

    @Autowired
    private ReservationMapper reservationMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private NotificationMapper notificationMapper;

    @Autowired
    private SystemLogService systemLogService;

    @Autowired
    private FeedbackMapper feedbackMapper;

    public List<Seat> getAllSeats() {
        // Init some seats if empty
        if (seatMapper.count() == 0) {
            initSeats();
        }
        return seatMapper.findAll();
    }

    private void initSeats() {
        // Create 20 seats
        for (int i = 1; i <= 20; i++) {
            Seat s = new Seat();
            s.setName("A-" + String.format("%03d", i));
            s.setSection("General Area");
            s.setType(i % 5 == 0 ? "POWER" : "NORMAL");
            seatMapper.save(s);
        }
    }

    public List<Reservation> getUserReservations(String username) {
        User user = userMapper.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return reservationMapper.findByUserOrderByStartTimeDesc(user);
    }

    @Transactional
    public Reservation createReservation(String username, Long seatId, LocalDateTime start, LocalDateTime end) {
        User user = userMapper.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        if (user.isBlacklisted()) {
            systemLogService.log(user, "RESERVATION_BLOCKED", "Blacklisted user tried to reserve seat " + seatId);
            throw new RuntimeException("You are blacklisted and cannot make reservations.");
        }

        Seat seat = seatMapper.findById(seatId)
                .orElseThrow(() -> new RuntimeException("Seat not found"));

        if (!"AVAILABLE".equals(seat.getStatus())) {
            // Check if it's maintenance, etc.
             if ("MAINTENANCE".equals(seat.getStatus())) {
                 throw new RuntimeException("Seat is under maintenance");
             }
        }

        // Check overlapping
        List<Reservation> conflicts = reservationMapper.findOverlappingReservations(seatId, start, end);
        if (!conflicts.isEmpty()) {
            throw new RuntimeException("Time slot is already booked");
        }

        Reservation res = new Reservation();
        res.setUser(user);
        res.setSeat(seat);
        res.setStartTime(start);
        res.setEndTime(end);
        res.setStatus("CONFIRMED"); // Auto confirm for now
        
        Reservation saved = reservationMapper.save(res);
        systemLogService.log(user, "RESERVE_SEAT", "Reserved seat " + seat.getName() + " from " + start + " to " + end);
        return saved;
    }

    @Transactional
    public void cancelReservation(String username, Long reservationId) {
        Reservation res = reservationMapper.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Reservation not found"));
        
        User resUser = userMapper.selectById(res.getUserId());
        if (resUser == null || !resUser.getUsername().equals(username)) {
            throw new RuntimeException("Not authorized");
        }
        
        if ("COMPLETED".equals(res.getStatus()) || "CANCELLED".equals(res.getStatus())) {
             throw new RuntimeException("Cannot cancel finished/cancelled reservation");
        }

        res.setStatus("CANCELLED");
        reservationMapper.save(res);
        systemLogService.log(resUser, "CANCEL_RESERVATION", "Cancelled reservation " + reservationId);
    }

    @Transactional
    public void checkIn(String username, Long reservationId) {
        Reservation res = reservationMapper.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Reservation not found"));

        User resUser = userMapper.selectById(res.getUserId());
        if (resUser == null || !resUser.getUsername().equals(username)) {
            throw new RuntimeException("Not authorized");
        }
        
        // Allow check-in 30 mins before start
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(res.getStartTime().minusMinutes(30))) {
            throw new RuntimeException("Too early to check in. Please wait until 30 minutes before start.");
        }
        
        res.setStatus("CHECKED_IN");
        res.setCheckInTime(now);
        reservationMapper.save(res);
        systemLogService.log(resUser, "CHECK_IN", "Checked in for reservation " + reservationId);
    }

    public Seat updateSeatStatus(Long seatId, String status) {
        Seat seat = seatMapper.findById(seatId).orElseThrow(() -> new RuntimeException("Seat not found"));
        seat.setStatus(status);
        return seatMapper.save(seat);
    }

    @Transactional
    public void submitFeedback(String username, Integer rate, String content) {
        User user = userMapper.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Feedback feedback = new Feedback();
        feedback.setUser(user);
        feedback.setRate(rate);
        feedback.setContent(content);
        
        feedbackMapper.save(feedback);
        systemLogService.log(user, "SUBMIT_FEEDBACK", "Submitted feedback with rate " + rate);
    }

    public Map<String, Object> getStats() {
        long total = seatMapper.count();
        if (total == 0) {
            initSeats();
            total = seatMapper.count();
        }
        
        // Count currently occupied seats (Reservations that are active NOW)
        LocalDateTime now = LocalDateTime.now();
        
        long maintenanceCount = seatMapper.findAll().stream()
                .filter(s -> "MAINTENANCE".equals(s.getStatus()))
                .count();
                
        // Find all reservations active right now
        List<Reservation> activeReservations = reservationMapper.findAll().stream()
                .filter(r -> (r.getStatus().equals("CONFIRMED") || r.getStatus().equals("CHECKED_IN")) &&
                             now.isAfter(r.getStartTime()) && now.isBefore(r.getEndTime()))
                .toList();
                
        // We need to count unique seats occupied by reservations
        long activeResCount = activeReservations.stream().map(r -> r.getSeatId()).distinct().count();
        
        // Total occupied = maintenance + active reservations (excluding those on maintenance seats if any, but maintenance usually blocks)
        // Simply: count unique seats that are either maintenance or have active reservation
        
        long occupiedCount = activeResCount + maintenanceCount; // Approximation
        
        if (occupiedCount > total) occupiedCount = total;
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("total", total);
        stats.put("occupied", occupiedCount);
        stats.put("percentage", total > 0 ? (int)((double)occupiedCount / total * 100) : 0);
        return stats;
    }

    public List<Long> getOccupiedSeatIds(LocalDateTime start, LocalDateTime end) {
        List<Reservation> conflicts = reservationMapper.findAllOverlappingReservations(start, end);
        return conflicts.stream().map(r -> r.getSeatId()).distinct().toList();
    }

    @Scheduled(fixedRate = 60000)
    @Transactional
    public void sendReservationReminders() {
        LocalDateTime now = LocalDateTime.now();
        List<Reservation> upcoming = reservationMapper.findByStatus("CONFIRMED");
        
        for (Reservation res : upcoming) {
            // If starts within 30 mins and not reminded
            if (!res.isReminderSent() && 
                res.getStartTime().isAfter(now) && 
                res.getStartTime().isBefore(now.plusMinutes(30))) {
                
                Notification n = new Notification();
                n.setTitle("Reservation Reminder");
                Seat seat = seatMapper.selectById(res.getSeatId());
                n.setContent("Your reservation for seat " + (seat != null ? seat.getName() : "unknown") + " starts soon (" + res.getStartTime() + "). Please check in on time.");
                n.setType(Notification.NotifyType.SYSTEM);
                User notifUser = userMapper.selectById(res.getUserId());
                n.setUser(notifUser);
                notificationMapper.save(n);
                
                res.setReminderSent(true);
                reservationMapper.save(res);
            }
        }
    }

    // Every minute, check for missed reservations (didn't check in 15 mins after start)
    @Scheduled(fixedRate = 60000)
    @Transactional
    public void checkMissedReservations() {
        LocalDateTime now = LocalDateTime.now();
        List<Reservation> confirmed = reservationMapper.findByStatus("CONFIRMED");
        
        for (Reservation res : confirmed) {
            if (now.isAfter(res.getStartTime().plusMinutes(15))) {
                res.setStatus("MISSED");
                reservationMapper.save(res);
                User missedUser = userMapper.selectById(res.getUserId());
                systemLogService.log(missedUser, "RESERVATION_MISSED", "Auto-cancelled reservation " + res.getId() + " due to no check-in");
            }
        }
        
        // Auto complete finished reservations
        List<Reservation> active = reservationMapper.findByStatus("CHECKED_IN");
        for (Reservation res : active) {
            if (now.isAfter(res.getEndTime())) {
                res.setStatus("COMPLETED");
                reservationMapper.save(res);
            }
        }
    }
}
