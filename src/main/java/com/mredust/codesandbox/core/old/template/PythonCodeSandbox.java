package com.mredust.codesandbox.core.old.template;

import cn.hutool.core.io.FileUtil;
import com.mredust.codesandbox.constant.PythonConstant;
import com.mredust.codesandbox.model.dto.ExecuteCodeResponse;
import com.mredust.codesandbox.model.dto.ExecuteResult;
import com.mredust.codesandbox.model.enums.ExecuteCodeStatusEnum;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

/**
 * @author <a href="https://github.com/Mredust">Mredust</a>
 */
@Deprecated
public class PythonCodeSandbox extends CodeSandboxTemplate {
    
    @Override
    public ExecuteCodeResponse executeCode(List<String> inputList, String code) {
        return super.executeCode(inputList, code);
    }
    
    @Override
    protected File saveCodeToFile(String path, String code) {
        String codePath = String.format("%s%s%s%s%s%s%s", path, File.separator,
                PythonConstant.PYTHON, File.separator, UUID.randomUUID(), File.separator, PythonConstant.PYTHON_DEFAULT_NAME);
        return FileUtil.writeUtf8String(code, codePath);
    }
    
    @Override
    protected ExecuteResult compileCode(File file) {
        ExecuteResult executeResult = new ExecuteResult();
        executeResult.setExecuteCode(ExecuteCodeStatusEnum.SUCCESS.getCode());
        executeResult.setMessage(ExecuteCodeStatusEnum.SUCCESS.getMsg());
        return executeResult;
    }
    
    @Override
    protected List<ExecuteResult> runCompileFile(String cmd, File file, String path, List<String> inputList) throws IOException {
        // todo（bug）：传递参数不通过
        return super.runCompileFile(PythonConstant.PYTHON_RUN_CMD, file, file.getAbsolutePath(), inputList);
    }
}
