package com.mredust.codesandbox.service.impl;

import com.mredust.codesandbox.common.ResponseCode;
import com.mredust.codesandbox.core.SimpleCodeSandboxFactory;
import com.mredust.codesandbox.core.template.CodeSandboxTemplate;
import com.mredust.codesandbox.exception.BusinessException;
import com.mredust.codesandbox.model.dto.ExecuteRequest;
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
    public ExecuteResponse executeCode(ExecuteRequest executeRequest) {
        List<String[]> testCaseList = executeRequest.getTestCaseList();
        String code = executeRequest.getCode();
        String language = executeRequest.getLanguage();
        if (StringUtils.isBlank(language)) {
            throw new BusinessException(ResponseCode.PARAMS_NULL, "编程语言不能为空");
        }
        CodeSandboxTemplate codeSandboxTemplate = new SimpleCodeSandboxFactory().getCodeSandboxTemplate(LanguageEnum.valueOf(language.toUpperCase()));
        return codeSandboxTemplate.executeCode(code, testCaseList);
    }
}
