package com.mredust.codesandbox.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.stream.Stream;

/**
 * 执行代码沙箱枚举
 *
 * @author <a href="https://github.com/Mredust">Mredust</a>
 */
@Getter
@AllArgsConstructor
public enum ExecuteResponseEnum {
    COMPILE_ERROR(0, "编译错误"),
    RUNTIME_ERROR(1, "运行错误"),
    RUN_SUCCESS(2, "运行成功");
    
    private final Integer code;
    
    private final String msg;
    
    public static ExecuteResponseEnum getExecuteResponseEnumByCode(Integer code) {
        return Stream.of(ExecuteResponseEnum.values()).filter(status -> status.code.equals(code)).findFirst().orElse(null);
    }
}
