package com.huakang.common.core;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 响应码枚举
 *
 * @author huakang
 */
@Getter
@AllArgsConstructor
public enum ResultCode {

    /**
     * 成功
     */
    SUCCESS(200, "操作成功"),

    /**
     * 失败
     */
    ERROR(500, "操作失败"),

    /**
     * 参数错误
     */
    PARAM_ERROR(400, "参数错误"),

    /**
     * 未授权
     */
    UNAUTHORIZED(401, "未授权，请先登录"),

    /**
     * 无权限
     */
    FORBIDDEN(403, "无权限访问"),

    /**
     * 资源不存在
     */
    NOT_FOUND(404, "资源不存在"),

    /**
     * 业务异常
     */
    BUSINESS_ERROR(1000, "业务异常"),

    /**
     * 登录失败
     */
    LOGIN_ERROR(1001, "用户名或密码错误"),

    /**
     * Token过期
     */
    TOKEN_EXPIRED(1002, "Token已过期"),

    /**
     * Token无效
     */
    TOKEN_INVALID(1003, "Token无效"),

    /**
     * 账号被锁定
     */
    ACCOUNT_LOCKED(1004, "账号已被锁定"),

    /**
     * 积分不足
     */
    POINTS_INSUFFICIENT(2001, "积分不足"),

    /**
     * 积分操作失败
     */
    POINTS_OPERATION_ERROR(2002, "积分操作失败");

    /**
     * 响应码
     */
    private final Integer code;

    /**
     * 响应消息
     */
    private final String message;
}
