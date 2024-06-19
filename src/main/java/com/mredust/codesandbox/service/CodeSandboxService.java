package com.mredust.codesandbox.service;

import com.mredust.codesandbox.model.dto.ExecuteRequest;
import com.mredust.codesandbox.model.dto.ExecuteResponse;

/**
 * 执行代码接口
 * @author <a href="https://github.com/Mredust">Mredust</a>
 */
public interface CodeSandboxService {
    ExecuteResponse executeCode(ExecuteRequest executeRequest);
}
