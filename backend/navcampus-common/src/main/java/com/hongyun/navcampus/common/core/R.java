package com.hongyun.navcampus.common.core;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 统一响应结果包装类。
 * 所有接口返回统一格式：{ code, msg, data }
 *
 * @param <T> 响应数据类型
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "统一响应结果")
public class R<T> {

    @Schema(description = "状态码：200成功，400参数错误，401未认证，403无权限，500系统错误")
    private int code;

    @Schema(description = "提示信息")
    private String msg;

    @Schema(description = "响应数据")
    private T data;

    /**
     * 成功响应（无数据）
     */
    public static <T> R<T> ok() {
        return ok(null);
    }

    /**
     * 成功响应（带数据）
     */
    public static <T> R<T> ok(T data) {
        return ok("操作成功", data);
    }

    /**
     * 成功响应（自定义消息+数据）
     */
    public static <T> R<T> ok(String msg, T data) {
        return new R<>(200, msg, data);
    }

    /**
     * 失败响应（默认500）
     */
    public static <T> R<T> fail(String msg) {
        return fail(500, msg);
    }

    /**
     * 失败响应（自定义状态码）
     */
    public static <T> R<T> fail(int code, String msg) {
        return new R<>(code, msg, null);
    }
}