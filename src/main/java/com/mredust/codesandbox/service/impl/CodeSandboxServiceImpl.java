package com.mredust.codesandbox.service.impl;

import com.mredust.codesandbox.common.ResponseCode;
import com.mredust.codesandbox.exception.BusinessException;
import com.mredust.codesandbox.model.dto.ExecuteCodeRequest;
import com.mredust.codesandbox.model.dto.ExecuteCodeResponse;
import com.mredust.codesandbox.model.enums.LanguageEnum;
import com.mredust.codesandbox.service.CodeSandboxService;
import com.mredust.codesandbox.core.template.CodeSandboxTemplate;
import com.mredust.codesandbox.core.codesandbox.CodeSandboxFactory;
import com.mredust.codesandbox.core.codesandbox.JavaCodeSandbox;
import com.mredust.codesandbox.core.codesandbox.PythonCodeSandbox;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author <a href="https://github.com/Mredust">Mredust</a>
 */
@Service
public class CodeSandboxServiceImpl implements CodeSandboxService {
    
    @Resource
    private JavaCodeSandbox javaCodeSandbox;
    @Resource
    private PythonCodeSandbox pythonCodeSandbox;
    
    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {
        List<String> inputList = executeCodeRequest.getInputList();
        String code = executeCodeRequest.getCode();
        String language = executeCodeRequest.getLanguage();
        if (StringUtils.isBlank(language)) {
            throw new BusinessException(ResponseCode.PARAMS_NULL, "语言不能为空");
        }
        CodeSandboxTemplate codeSandboxTemplate = CodeSandboxFactory.getCodeSandboxTemplate(LanguageEnum.getLanguageEnum(language));
        return codeSandboxTemplate.executeCode(inputList, code);
    }
}
