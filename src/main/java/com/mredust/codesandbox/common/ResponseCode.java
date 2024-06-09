package com.mredust.codesandbox.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author <a href="https://github.com/Mredust">Mredust</a>
 */
@Getter
@AllArgsConstructor
public enum ResponseCode {
    // http code
    SUCCESS(200, "操作成功"),
    FAIL(500, "操作失败"),
    NO_AUTH(401, "无权限"),
    FORBIDDEN(403, "禁止访问"),
    NOT_FOUND(404, "未找到"),
    METHOD_NOT_ALLOWED(405, "方法不允许"),
    SYSTEM_ERROR(500, "服务器内部错误"),
    BAD_GATEWAY(502, "错误的网关"),
    SERVICE_UNAVAILABLE(503, "服务不可用"),
    GATEWAY_TIMEOUT(504, "网关超时"),
    
    // custom code
    PARAMS_ERROR(400, "请求参数错误"),
    PARAMS_NULL(400, "请求数据为空"),
    NOT_LOGIN(401, "未登录"),
    ACCOUNT_BAN(403, "账号封禁"),
    
    // oj
    SUBMIT_ERROR(500, "提交判题失败"),
    DANGER_CODE(401, "危险代码");
    
    
    private final int code;
    private final String message;
}

