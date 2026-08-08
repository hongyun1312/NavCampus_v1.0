package com.hongyun.navcampus.system.dto;

import lombok.Data;

/**
 * 登录/刷新后的 JWT 响应。
 * 包含令牌、用户基本信息。
 */
@Data
public class JwtResponse {
    private String token;
    private String type = "Bearer";
    private Long id;
    private String username;
    private String email;
    private String role;

    /**
     * 构造函数。
     */
    public JwtResponse(String accessToken, Long id, String username, String email, String role) {
        this.token = accessToken;
        this.id = id;
        this.username = username;
        this.email = email;
        this.role = role;
    }
}
