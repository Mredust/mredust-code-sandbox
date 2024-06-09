package com.mredust.codesandbox.common;

import lombok.Data;

import java.io.Serializable;

/**
 * @author <a href="https://github.com/Mredust">Mredust</a>
 */
@Data
public class BaseResponse<T> implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 响应码
     */
    private int code;
    /**
     * 响应消息
     */
    private String msg;
    /**
     * 响应数据
     */
    private T data;
    
    public BaseResponse(int code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }
    
    public BaseResponse(ResponseCode responseCode) {
        this(responseCode.getCode(), responseCode.getMessage(), null);
    }
    
    public BaseResponse(ResponseCode responseCode, String msg) {
        this(responseCode.getCode(), msg, null);
    }
    
    public BaseResponse(ResponseCode responseCode, T data) {
        this(responseCode.getCode(), responseCode.getMessage(), data);
    }
    
    public BaseResponse(ResponseCode responseCode, String msg, T data) {
        this(responseCode.getCode(), msg, data);
    }
}

