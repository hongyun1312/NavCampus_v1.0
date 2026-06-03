package com.hongyun.accounting.service;

import com.hongyun.accounting.entity.Notification;
import com.hongyun.accounting.entity.Reservation;
import com.hongyun.accounting.entity.Seat;
import com.hongyun.accounting.entity.User;
import com.hongyun.accounting.repository.NotificationRepository;
import com.hongyun.accounting.repository.ReservationRepository;
import com.hongyun.accounting.repository.SeatRepository;
import com.hongyun.accounting.repository.UserRepository;
import com.hongyun.accounting.entity.Feedback;
import com.hongyun.accounting.repository.FeedbackRepository;
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
    private SeatRepository seatRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private SystemLogService systemLogService;

    @Autowired
    private FeedbackRepository feedbackRepository;

    public List<Seat> getAllSeats() {
        // Init some seats if empty
        if (seatRepository.count() == 0) {
            initSeats();
        }
        return seatRepository.findAll();
    }

    private void initSeats() {
        // Create 20 seats
        for (int i = 1; i <= 20; i++) {
            Seat s = new Seat();
            s.setName("A-" + String.format("%03d", i));
            s.setSection("General Area");
            s.setType(i % 5 == 0 ? "POWER" : "NORMAL");
            seatRepository.save(s);
        }
    }

    public List<Reservation> getUserReservations(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return reservationRepository.findByUserOrderByStartTimeDesc(user);
    }

    @Transactional
    public Reservation createReservation(String username, Long seatId, LocalDateTime start, LocalDateTime end) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        if (user.isBlacklisted()) {
            systemLogService.log(user, "RESERVATION_BLOCKED", "Blacklisted user tried to reserve seat " + seatId);
            throw new RuntimeException("You are blacklisted and cannot make reservations.");
        }

        Seat seat = seatRepository.findById(seatId)
                .orElseThrow(() -> new RuntimeException("Seat not found"));

        if (!"AVAILABLE".equals(seat.getStatus())) {
            // Check if it's maintenance, etc.
             if ("MAINTENANCE".equals(seat.getStatus())) {
                 throw new RuntimeException("Seat is under maintenance");
             }
        }

        // Check overlapping
        List<Reservation> conflicts = reservationRepository.findOverlappingReservations(seatId, start, end);
        if (!conflicts.isEmpty()) {
            throw new RuntimeException("Time slot is already booked");
        }

        Reservation res = new Reservation();
        res.setUser(user);
        res.setSeat(seat);
        res.setStartTime(start);
        res.setEndTime(end);
        res.setStatus("CONFIRMED"); // Auto confirm for now
        
        Reservation saved = reservationRepository.save(res);
        systemLogService.log(user, "RESERVE_SEAT", "Reserved seat " + seat.getName() + " from " + start + " to " + end);
        return saved;
    }

    @Transactional
    public void cancelReservation(String username, Long reservationId) {
        Reservation res = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Reservation not found"));
        
        if (!res.getUser().getUsername().equals(username)) {
            throw new RuntimeException("Not authorized");
        }
        
        if ("COMPLETED".equals(res.getStatus()) || "CANCELLED".equals(res.getStatus())) {
             throw new RuntimeException("Cannot cancel finished/cancelled reservation");
        }

        res.setStatus("CANCELLED");
        reservationRepository.save(res);
        systemLogService.log(res.getUser(), "CANCEL_RESERVATION", "Cancelled reservation " + reservationId);
    }

    @Transactional
    public void checkIn(String username, Long reservationId) {
        Reservation res = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Reservation not found"));

        if (!res.getUser().getUsername().equals(username)) {
            throw new RuntimeException("Not authorized");
        }
        
        // Allow check-in 30 mins before start
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(res.getStartTime().minusMinutes(30))) {
            throw new RuntimeException("Too early to check in. Please wait until 30 minutes before start.");
        }
        
        res.setStatus("CHECKED_IN");
        res.setCheckInTime(now);
        reservationRepository.save(res);
        systemLogService.log(res.getUser(), "CHECK_IN", "Checked in for reservation " + reservationId);
    }

    public Seat updateSeatStatus(Long seatId, String status) {
        Seat seat = seatRepository.findById(seatId).orElseThrow(() -> new RuntimeException("Seat not found"));
        seat.setStatus(status);
        return seatRepository.save(seat);
    }

    @Transactional
    public void submitFeedback(String username, Integer rate, String content) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Feedback feedback = new Feedback();
        feedback.setUser(user);
        feedback.setRate(rate);
        feedback.setContent(content);
        
        feedbackRepository.save(feedback);
        systemLogService.log(user, "SUBMIT_FEEDBACK", "Submitted feedback with rate " + rate);
    }

    public Map<String, Object> getStats() {
        long total = seatRepository.count();
        if (total == 0) {
            initSeats();
            total = seatRepository.count();
        }
        
        // Count currently occupied seats (Reservations that are active NOW)
        LocalDateTime now = LocalDateTime.now();
        
        long maintenanceCount = seatRepository.findAll().stream()
                .filter(s -> "MAINTENANCE".equals(s.getStatus()))
                .count();
                
        // Find all reservations active right now
        List<Reservation> activeReservations = reservationRepository.findAll().stream()
                .filter(r -> (r.getStatus().equals("CONFIRMED") || r.getStatus().equals("CHECKED_IN")) &&
                             now.isAfter(r.getStartTime()) && now.isBefore(r.getEndTime()))
                .toList();
                
        // We need to count unique seats occupied by reservations
        long activeResCount = activeReservations.stream().map(r -> r.getSeat().getId()).distinct().count();
        
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
        List<Reservation> conflicts = reservationRepository.findAllOverlappingReservations(start, end);
        return conflicts.stream().map(r -> r.getSeat().getId()).distinct().toList();
    }

    @Scheduled(fixedRate = 60000)
    @Transactional
    public void sendReservationReminders() {
        LocalDateTime now = LocalDateTime.now();
        List<Reservation> upcoming = reservationRepository.findByStatus("CONFIRMED");
        
        for (Reservation res : upcoming) {
            // If starts within 30 mins and not reminded
            if (!res.isReminderSent() && 
                res.getStartTime().isAfter(now) && 
                res.getStartTime().isBefore(now.plusMinutes(30))) {
                
                Notification n = new Notification();
                n.setTitle("Reservation Reminder");
                n.setContent("Your reservation for seat " + res.getSeat().getName() + " starts soon (" + res.getStartTime() + "). Please check in on time.");
                n.setType(Notification.NotifyType.SYSTEM);
                n.setUser(res.getUser());
                notificationRepository.save(n);
                
                res.setReminderSent(true);
                reservationRepository.save(res);
            }
        }
    }

    // Every minute, check for missed reservations (didn't check in 15 mins after start)
    @Scheduled(fixedRate = 60000)
    @Transactional
    public void checkMissedReservations() {
        LocalDateTime now = LocalDateTime.now();
        List<Reservation> confirmed = reservationRepository.findByStatus("CONFIRMED");
        
        for (Reservation res : confirmed) {
            if (now.isAfter(res.getStartTime().plusMinutes(15))) {
                res.setStatus("MISSED");
                reservationRepository.save(res);
                systemLogService.log(res.getUser(), "RESERVATION_MISSED", "Auto-cancelled reservation " + res.getId() + " due to no check-in");
            }
        }
        
        // Auto complete finished reservations
        List<Reservation> active = reservationRepository.findByStatus("CHECKED_IN");
        for (Reservation res : active) {
            if (now.isAfter(res.getEndTime())) {
                res.setStatus("COMPLETED");
                reservationRepository.save(res);
            }
        }
    }
}
