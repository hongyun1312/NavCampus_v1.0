package com.hongyun.accounting.dto;

import lombok.Data;

/**
 * 注册请求 DTO。
 * 承载用户名、密码、邮箱、手机号。
 */
@Data
public class SignupRequest {
    private String username;
    private String password;
}
