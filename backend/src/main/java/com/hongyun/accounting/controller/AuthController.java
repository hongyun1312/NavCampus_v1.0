package com.hongyun.accounting.controller;

import com.hongyun.accounting.dto.JwtResponse;
import com.hongyun.accounting.dto.LoginRequest;
import com.hongyun.accounting.dto.SignupRequest;
import com.hongyun.accounting.entity.User;
import com.hongyun.accounting.repository.UserRepository;
import com.hongyun.accounting.service.TokenStoreService;
import com.hongyun.accounting.util.JwtUtils;
import com.hongyun.accounting.service.VerificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.Duration;

/**
 * 认证控制器。
 * 提供登录、注册、Token 刷新与邮箱/短信验证码相关接口。
 */
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtUtils jwtUtils;
    @Autowired
    VerificationService verificationService;
    @Autowired
    TokenStoreService tokenStoreService;

    @Value("${jwt.expiration}")
    private long jwtExpirationMs;

    /**
     * 用户登录，返回 JWT。
     */
    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);
        
        User userDetails = (User) authentication.getPrincipal();
        tokenStoreService.storeLoginToken(userDetails.getUsername(), jwtUtils.getJwtIdFromJwtToken(jwt), Duration.ofMillis(jwtExpirationMs));

        return ResponseEntity.ok(new JwtResponse(jwt,
                userDetails.getId(),
                userDetails.getUsername(),
                userDetails.getEmail(),
                userDetails.getRole() != null ? userDetails.getRole().name() : "USER"));
    }

    /**
     * 用户注册，进行用户名唯一性校验，密码加密存储。
     */
    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@RequestBody SignupRequest signUpRequest) {
        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            return ResponseEntity
                    .badRequest()
                    .body("Error: Username is already taken!");
        }

        // Create new user's account
        User user = new User();
        user.setUsername(signUpRequest.getUsername());
        user.setPassword(encoder.encode(signUpRequest.getPassword()));

        userRepository.save(user);

        return ResponseEntity.ok("User registered successfully!");
    }

    /**
     * 刷新 JWT Token。
     */
    @PostMapping("/refresh")
    public ResponseEntity<?> refresh() {
        String authHeader = null;
        try {
            authHeader = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest().getHeader("Authorization");
        } catch (Exception ignored) {}
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            try {
                if (!jwtUtils.validateJwtToken(token)) return ResponseEntity.status(401).body("Unauthorized");

                String username = jwtUtils.getUserNameFromJwtToken(token);
                String jti = jwtUtils.getJwtIdFromJwtToken(token);
                if (!tokenStoreService.isTokenValid(username, jti)) return ResponseEntity.status(401).body("Unauthorized");

                User u = userRepository.findByUsername(username).orElse(null);
                if (u == null) return ResponseEntity.status(401).body("Unauthorized");

                String jwt = jwtUtils.generateJwtTokenFromUsername(username);
                tokenStoreService.storeLoginToken(username, jwtUtils.getJwtIdFromJwtToken(jwt), Duration.ofMillis(jwtExpirationMs));
                return ResponseEntity.ok(new JwtResponse(jwt, u.getId(), u.getUsername(), u.getEmail(), u.getRole().name()));
            } catch (Exception ignored) {
            }
        }
        return ResponseEntity.status(401).body("Unauthorized");
    }

    @PostMapping("/signout")
    public ResponseEntity<?> signout() {
        String authHeader = null;
        try {
            authHeader = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest().getHeader("Authorization");
        } catch (Exception ignored) {}
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            try {
                var claims = jwtUtils.getClaimsAllowExpired(token);
                tokenStoreService.revokeToken(claims.getSubject(), claims.getId());
            } catch (Exception ignored) {}
        }
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok("OK");
    }

    /**
     * 发送邮箱验证码（站内通知展示，可扩展邮件网关）。
     */
    @PostMapping("/send-email-code")
    public Object sendEmailCode(@RequestParam String email) {
        UserDetails ud = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        verificationService.sendEmailCode(ud.getUsername(), email);
        return "OK";
    }

    /**
     * 验证邮箱验证码并绑定邮箱。
     */
    @PostMapping("/verify-email")
    public Object verifyEmail(@RequestParam String email, @RequestParam String code) {
        UserDetails ud = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return verificationService.verifyEmail(ud.getUsername(), email, code) ? "OK" : "FAIL";
    }

    /**
     * 发送短信验证码（站内通知展示，可扩展短信网关）。
     */
    @PostMapping("/send-sms-code")
    public Object sendSmsCode(@RequestParam String phone) {
        UserDetails ud = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        verificationService.sendPhoneCode(ud.getUsername(), phone);
        return "OK";
    }

    /**
     * 验证短信验证码并绑定手机号。
     */
    @PostMapping("/verify-phone")
    public Object verifyPhone(@RequestParam String phone, @RequestParam String code) {
        UserDetails ud = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return verificationService.verifyPhone(ud.getUsername(), phone, code) ? "OK" : "FAIL";
    }
}
