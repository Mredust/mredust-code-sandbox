package com.mredust.codesandbox.exception;


import com.mredust.codesandbox.common.BaseResponse;
import com.mredust.codesandbox.common.ResponseCode;
import com.mredust.codesandbox.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


/**
 * @author <a href="https://github.com/Mredust">Mredust</a>
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    
    @ExceptionHandler(BusinessException.class)
    public BaseResponse businessException(BusinessException ex) {
        log.error("businessException：{}", ex.getMessage());
        return Result.fail(ResponseCode.FAIL, ex.getMessage());
    }
    
    
    @ExceptionHandler(RuntimeException.class)
    public BaseResponse runtimeException(RuntimeException ex) {
        log.error("runtimeException：{}", ex.getMessage());
        return Result.fail(ResponseCode.FAIL, "系统错误");
    }
    
    
}

