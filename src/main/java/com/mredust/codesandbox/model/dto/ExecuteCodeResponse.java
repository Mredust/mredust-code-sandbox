package com.mredust.codesandbox.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 代码执行响应
 *
 * @author <a href="https://github.com/Mredust">Mredust</a>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExecuteCodeResponse {
    /**
     * 执行状态
     */
    private Integer status;
    
    /**
     * 接口信息
     */
    private String message;
    
    /**
     * 接口信息
     */
    private String errorMessage;
    
    /**
     * 运行时间
     */
    private Long time;
    
    /**
     * 执行时间
     */
    private Long memory;
    
    /**
     * 执行结果列表
     */
    private List<String> outputList;
    
}
