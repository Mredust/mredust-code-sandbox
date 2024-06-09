package com.mredust.codesandbox.model.dto;

import lombok.Data;

/**
 * 执行结果
 *
 * @author <a href="https://github.com/Mredust">Mredust</a>
 */
@Data
public class ExecuteResult {
    /**
     * 执行状态
     */
    private Integer executeCode;
    
    
    /**
     * 输出信息
     */
    private String message;
    
    
    /**
     * 运行时间
     */
    private Long time;
    
    /**
     * 执行时间
     */
    private Long memory;
}
