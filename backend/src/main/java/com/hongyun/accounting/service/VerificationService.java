package com.hongyun.accounting.service;

import com.hongyun.accounting.entity.User;
import com.hongyun.accounting.entity.VerificationCode;
import com.hongyun.accounting.repository.UserRepository;
import com.hongyun.accounting.repository.VerificationCodeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;

/**
 * 验证服务。
 * 提供邮箱与手机号验证码的生成、发送（站内通知展示）与校验绑定。
 */
@Service
public class VerificationService {
    @Autowired
    private VerificationCodeRepository verificationCodeRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private NotificationService notificationService;

    /**
     * 生成 6 位数字验证码。
     */
    private String genCode() {
        int n = 100000 + new Random().nextInt(900000);
        return String.valueOf(n);
    }

    /**
     * 根据用户名查询用户。
     */
    private User user(String username) {
        return userRepository.findByUsername(username).orElseThrow();
    }

    /**
     * 发送邮箱验证码（站内通知展示）。
     */
    public void sendEmailCode(String username, String email) {
        User u = user(username);
        VerificationCode vc = new VerificationCode();
        vc.setUser(u);
        vc.setType(VerificationCode.CodeType.EMAIL);
        vc.setTarget(email);
        vc.setCode(genCode());
        vc.setExpiresAt(LocalDateTime.now().plusMinutes(10));
        vc.setVerified(false);
        verificationCodeRepository.save(vc);
        notificationService.notifySite(u, "邮箱验证码", "邮箱:" + email + " 验证码:" + vc.getCode());
    }

    /**
     * 校验邮箱验证码并绑定邮箱。
     */
    public boolean verifyEmail(String username, String email, String code) {
        User u = user(username);
        return verificationCodeRepository.findTopByUserIdAndTypeAndTargetOrderByExpiresAtDesc(u.getId(), VerificationCode.CodeType.EMAIL, email)
                .filter(vc -> !vc.isVerified() && vc.getExpiresAt().isAfter(LocalDateTime.now()) && vc.getCode().equals(code))
                .map(vc -> {
                    vc.setVerified(true);
                    verificationCodeRepository.save(vc);
                    u.setEmail(email);
                    userRepository.save(u);
                    return true;
                }).orElse(false);
    }

    /**
     * 发送短信验证码（站内通知展示）。
     */
    public void sendPhoneCode(String username, String phone) {
        User u = user(username);
        VerificationCode vc = new VerificationCode();
        vc.setUser(u);
        vc.setType(VerificationCode.CodeType.PHONE);
        vc.setTarget(phone);
        vc.setCode(genCode());
        vc.setExpiresAt(LocalDateTime.now().plusMinutes(10));
        vc.setVerified(false);
        verificationCodeRepository.save(vc);
        notificationService.notifySite(u, "短信验证码", "手机号:" + phone + " 验证码:" + vc.getCode());
    }

    /**
     * 校验短信验证码并绑定手机号。
     */
    public boolean verifyPhone(String username, String phone, String code) {
        User u = user(username);
        return verificationCodeRepository.findTopByUserIdAndTypeAndTargetOrderByExpiresAtDesc(u.getId(), VerificationCode.CodeType.PHONE, phone)
                .filter(vc -> !vc.isVerified() && vc.getExpiresAt().isAfter(LocalDateTime.now()) && vc.getCode().equals(code))
                .map(vc -> {
                    vc.setVerified(true);
                    verificationCodeRepository.save(vc);
                    u.setPhone(phone);
                    userRepository.save(u);
                    return true;
                }).orElse(false);
    }
}
