package com.mredust.codesandbox.controller;

import com.mredust.codesandbox.common.BaseResponse;
import com.mredust.codesandbox.common.Result;
import com.mredust.codesandbox.model.dto.ExecuteCodeRequest;
import com.mredust.codesandbox.model.dto.ExecuteCodeResponse;
import com.mredust.codesandbox.service.CodeSandboxService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 判题接口
 *
 * @author <a href="https://github.com/Mredust">Mredust</a>
 */
@RestController
@RequestMapping("/code-sandbox")
public class CodeSandboxController {
    
    @Resource
    private CodeSandboxService codeSandboxService;
    
    @PostMapping("/execute")
    public BaseResponse<ExecuteCodeResponse> executeCode(@RequestBody ExecuteCodeRequest executeCodeRequest, HttpServletRequest request) {
        ExecuteCodeResponse executeCodeResponse = codeSandboxService.executeCode(executeCodeRequest);
        return Result.success(executeCodeResponse);
    }
}
