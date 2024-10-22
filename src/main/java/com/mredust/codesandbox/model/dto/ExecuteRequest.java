package com.mredust.codesandbox.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 代码执行请求
 *
 * @author <a href="https://github.com/Mredust">Mredust</a>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExecuteRequest {
    
    /**
     * 测试用例
     */
    private List<String[]> testCaseList;
    
    /**
     * 输入代码
     */
    private String code;
    
    /**
     * 语言
     */
    private String language;
}
