package com.mredust.codesandbox.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.stream.Stream;


/**
 * 执行代码状态枚举
 *
 * @author <a href="https://github.com/Mredust">Mredust</a>
 */

@Getter
@AllArgsConstructor
public enum ExecuteCodeStatusEnum {
    SUCCESS(0, "通过"),
    COMPILE_FAILED(1, "编译出错"),
    RUN_FAILED(2, "运行失败"),
    NO_AUTH(3, "无权限");
    
    private final Integer code;
    private final String msg;
    
    public static ExecuteCodeStatusEnum getExecuteCodeStatusEnumByCode(Integer code) {
        return Stream.of(ExecuteCodeStatusEnum.values()).filter(status -> status.code.equals(code)).findFirst().orElse(null);
    }
}
