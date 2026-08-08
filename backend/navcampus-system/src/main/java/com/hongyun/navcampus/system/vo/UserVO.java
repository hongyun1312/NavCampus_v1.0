package com.hongyun.navcampus.system.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 用户视图对象。
 * 用于接口返回，不包含密码等敏感字段。
 */
@Data
@Schema(description = "用户信息")
public class UserVO {
    @Schema(description = "用户ID")
    private Long id;
    @Schema(description = "用户名")
    private String username;
    @Schema(description = "邮箱")
    private String email;
    @Schema(description = "手机号")
    private String phone;
    @Schema(description = "头像")
    private String avatar;
    @Schema(description = "主题色")
    private String themeColor;
    @Schema(description = "角色")
    private String role;
    @Schema(description = "是否黑名单")
    private boolean blacklisted;
    @Schema(description = "创建时间")
    private LocalDateTime createdAt;
}
