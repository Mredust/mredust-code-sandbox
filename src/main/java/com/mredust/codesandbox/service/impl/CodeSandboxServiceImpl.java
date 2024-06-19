package com.mredust.codesandbox.service.impl;

import com.mredust.codesandbox.common.ResponseCode;
import com.mredust.codesandbox.core.CodeSandboxFactory;
import com.mredust.codesandbox.core.template.CodeSandboxTemplate;
import com.mredust.codesandbox.exception.BusinessException;
import com.mredust.codesandbox.model.dto.ExecuteCodeRequest;
import com.mredust.codesandbox.model.dto.ExecuteCodeResponse;
import com.mredust.codesandbox.model.dto.ExecuteResponse;
import com.mredust.codesandbox.model.enums.LanguageEnum;
import com.mredust.codesandbox.service.CodeSandboxService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author <a href="https://github.com/Mredust">Mredust</a>
 */
@Service
public class CodeSandboxServiceImpl implements CodeSandboxService {
    
    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {
        List<String[]> testCaseList = executeCodeRequest.getTestCaseList();
        String code = executeCodeRequest.getCode();
        String language = executeCodeRequest.getLanguage();
        if (StringUtils.isBlank(language)) {
            throw new BusinessException(ResponseCode.PARAMS_NULL, "编程语言不能为空");
        }
        CodeSandboxTemplate codeSandboxTemplate = CodeSandboxFactory.getCodeSandboxTemplate(LanguageEnum.getLanguageEnum(language));
        ExecuteResponse executeResponse = codeSandboxTemplate.executeCode(code, testCaseList);
        Integer executeResponseCode = executeResponse.getCode();
        String msg = executeResponse.getMsg();
        List<String> stdout = executeResponse.getStdout();
        String stderr = executeResponse.getStderr();
        Long runTotalTime = executeResponse.getRunTotalTime();
        Long runTotalMemory = executeResponse.getRunTotalMemory();
        return ExecuteCodeResponse.builder()
                .statusCode(executeResponseCode)
                .message(msg)
                .outputList(stdout)
                .errorMessage(stderr)
                .time(runTotalTime)
                .memory(runTotalMemory)
                .build();
    }
}
