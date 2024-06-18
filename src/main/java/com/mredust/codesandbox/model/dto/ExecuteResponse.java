package com.mredust.codesandbox.model.dto;

import lombok.Data;

import java.util.List;

/**
 * @author <a href="https://github.com/Mredust">Mredust</a>
 */
@Data
public class ExecuteResponse {
    
    private Integer code;
    
    private String msg;
    
    private List<String> stdout;
    
    private String stderr;
}
