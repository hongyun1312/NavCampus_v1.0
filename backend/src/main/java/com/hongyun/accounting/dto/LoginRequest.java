package com.hongyun.accounting.dto;

import lombok.Data;

/**
 * 登录请求 DTO。
 * 承载用户名与密码。
 */
@Data
public class LoginRequest {
    private String username;
    private String password;
}
