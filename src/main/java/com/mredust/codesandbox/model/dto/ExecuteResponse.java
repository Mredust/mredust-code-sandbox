package com.mredust.codesandbox.model.dto;

import lombok.Data;

import java.util.List;

/**
 * @author <a href="https://github.com/Mredust">Mredust</a>
 */
@Data
public class ExecuteResponse {
    
    /**
     * 执行状态
     */
    private Integer code;
    
    /**
     * 执行信息
     */
    private String msg;
    
    /**
     * 执行结果
     */
    private List<String> stdout;
    
    /**
     * 错误信息
     */
    private String stderr;
    
    /**
     * 运行时间(ms)
     */
    private Long runTime;
    
    /**
     * 执行时间(kb)
     */
    private Long runMemory;
}
