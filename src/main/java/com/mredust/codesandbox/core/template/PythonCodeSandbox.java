package com.mredust.codesandbox.core.template;

import cn.hutool.core.io.FileUtil;
import com.mredust.codesandbox.constant.PythonConstant;
import com.mredust.codesandbox.model.dto.ExecuteCodeResponse;
import com.mredust.codesandbox.model.dto.ExecuteResult;
import com.mredust.codesandbox.model.enums.ExecuteCodeStatusEnum;
import com.mredust.codesandbox.utils.ProcessUtils;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @author <a href="https://github.com/Mredust">Mredust</a>
 */
@Component
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
    protected List<ExecuteResult> runCompileFile(File file, List<String> inputList) throws IOException {
        // todo 传递参数有问题
        ArrayList<ExecuteResult> executeResultList = new ArrayList<>();
        for (String inputArgs : inputList) {
            String runCmd = String.format(PythonConstant.PYTHON_RUN_CMD_WITH_ARGS, file.getAbsolutePath(), inputArgs);
            Process runProcess = Runtime.getRuntime().exec(runCmd);
            ExecuteResult executeResult = ProcessUtils.processHandler(runProcess);
            executeResultList.add(executeResult);
        }
        
        String runCmd = String.format(PythonConstant.PYTHON_RUN_CMD, file.getAbsolutePath());
        Process runProcess = Runtime.getRuntime().exec(runCmd);
        ExecuteResult executeResult = ProcessUtils.processHandler(runProcess);
        executeResultList.add(executeResult);
        return executeResultList;
    }
    
}
