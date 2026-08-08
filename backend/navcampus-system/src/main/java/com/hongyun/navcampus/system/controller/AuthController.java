package com.hongyun.navcampus.system.controller;

import com.hongyun.navcampus.common.core.R;
import com.hongyun.navcampus.system.converter.VoConverter;
import com.hongyun.navcampus.system.dto.JwtResponse;
import com.hongyun.navcampus.system.dto.LoginRequest;
import com.hongyun.navcampus.system.dto.SignupRequest;
import com.hongyun.navcampus.system.entity.User;
import com.hongyun.navcampus.system.mapper.UserMapper;
import com.hongyun.navcampus.system.vo.UserVO;
import com.hongyun.navcampus.framework.security.TokenStoreService;
import com.hongyun.navcampus.framework.security.JwtUtils;
import com.hongyun.navcampus.system.service.VerificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import jakarta.validation.Valid;

import java.time.Duration;

/**
 * 认证控制器。
 * 提供登录、注册、Token刷新、邮箱/手机验证等接口。
 */
@RestController
@RequestMapping("/api/auth")
@io.swagger.v3.oas.annotations.tags.Tag(name = "认证管理", description = "登录、注册、Token刷新")
public class AuthController {
    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    UserMapper userMapper;
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

    @PostMapping("/signin")
    @io.swagger.v3.oas.annotations.Operation(summary = "用户登录", description = "验证用户名密码并返回JWT")
    public R<JwtResponse> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);
        User userDetails = (User) authentication.getPrincipal();
        tokenStoreService.storeLoginToken(userDetails.getUsername(), jwtUtils.getJwtIdFromJwtToken(jwt), Duration.ofMillis(jwtExpirationMs));
        JwtResponse resp = new JwtResponse(jwt, userDetails.getId(), userDetails.getUsername(),
                userDetails.getEmail(), userDetails.getRole() != null ? userDetails.getRole().name() : "USER");
        return R.ok(resp);
    }

    @PostMapping("/signup")
    @io.swagger.v3.oas.annotations.Operation(summary = "用户注册", description = "注册新用户，用户名唯一校验")
    public R<Void> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        if (userMapper.existsByUsername(signUpRequest.getUsername())) {
            return R.fail(400, "用户名已被占用");
        }
        User user = new User();
        user.setUsername(signUpRequest.getUsername());
        user.setPassword(encoder.encode(signUpRequest.getPassword()));
        userMapper.save(user);
        return R.ok("注册成功", null);
    }

    @PostMapping("/refresh")
    @io.swagger.v3.oas.annotations.Operation(summary = "刷新Token", description = "使用有效Token获取新Token")
    public R<JwtResponse> refresh() {
        String authHeader = null;
        try {
            authHeader = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest().getHeader("Authorization");
        } catch (Exception ignored) {}
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            if (!jwtUtils.validateJwtToken(token)) {
                return R.fail(401, "Token无效或已过期");
            }
            String username = jwtUtils.getUserNameFromJwtToken(token);
            String jti = jwtUtils.getJwtIdFromJwtToken(token);
            if (!tokenStoreService.isTokenValid(username, jti)) {
                return R.fail(401, "Token已失效");
            }
            User u = userMapper.findByUsername(username).orElse(null);
            if (u == null) {
                return R.fail(401, "用户不存在");
            }
            String jwt = jwtUtils.generateJwtTokenFromUsername(username);
            tokenStoreService.storeLoginToken(username, jwtUtils.getJwtIdFromJwtToken(jwt), Duration.ofMillis(jwtExpirationMs));
            JwtResponse resp = new JwtResponse(jwt, u.getId(), u.getUsername(), u.getEmail(),
                    u.getRole() != null ? u.getRole().name() : "USER");
            return R.ok(resp);
        }
        return R.fail(401, "缺少Authorization头");
    }

    @GetMapping("/me")
    @io.swagger.v3.oas.annotations.Operation(summary = "获取当前用户信息", description = "返回当前登录用户的详细信息")
    public R<UserVO> getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userMapper.findByUsername(auth.getName()).orElse(null);
        if (user == null) {
            return R.fail(404, "用户不存在");
        }
        return R.ok(VoConverter.toUserVO(user));
    }

    @PostMapping("/signout")
    @io.swagger.v3.oas.annotations.Operation(summary = "用户登出", description = "使当前Token失效，前端清除本地登录状态")
    public R<Void> signout() {
        // 从 SecurityContext 获取当前登录用户名
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getName() != null) {
            // 从请求头中获取 JWT，提取 jti（Token唯一标识）
            String authHeader = null;
            try {
                authHeader = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
                        .getRequest().getHeader("Authorization");
            } catch (Exception ignored) {}
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                String jti = jwtUtils.getJwtIdFromJwtToken(token);
                // 从 TokenStore 中移除该Token，使其立即失效
                tokenStoreService.revokeToken(auth.getName(), jti);
            }
        }
        // 清除 SecurityContext
        SecurityContextHolder.clearContext();
        return R.ok("登出成功", null);
    }

}
