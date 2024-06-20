package com.mredust.codesandbox.core.template;

import cn.hutool.core.io.FileUtil;
import com.mredust.codesandbox.model.dto.ExecuteResponse;
import com.mredust.codesandbox.model.enums.ExecuteResponseEnum;

import java.io.File;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;

import static com.mredust.codesandbox.constant.CodeSandboxConstant.*;

/**
 * 代码沙箱模板
 *
 * @author <a href="https://github.com/Mredust">Mredust</a>
 */
public abstract class CodeSandboxTemplate {
    
    public ExecuteResponse executeCode(String code, List<String[]> testCaseList) {
        String parentPath = String.format("%s%s%s", System.getProperty("user.dir"), File.separator, WORK_DIR);
        File tempFile = preprocessCode(parentPath, code);
        ExecuteResponse compileErrorResponse = handleCompilationError(tempFile);
        if (compileErrorResponse != null) {
            return compileErrorResponse;
        }
        Method method = preprocessFile(tempFile);
        String templateCode = generateTemplateCode(method);
        String executeCode = mergeCode(templateCode, code);
        File file = saveFile(executeCode, parentPath, MAIN_CLASS_NAME);
        compileErrorResponse = handleCompilationError(file);
        if (compileErrorResponse != null) {
            return compileErrorResponse;
        }
        Long[] time = {0L};
        Long[] memory = {0L};
        List<String> runMessageList = runCode(file, testCaseList, time, memory);
        String errorMessage = getErrorMessage(runMessageList);
        if (!errorMessage.isEmpty()) {
            clearFile(file);
            return getExecuteResponse(ExecuteResponseEnum.RUNTIME_ERROR, errorMessage);
        }
        clearFile(file);
        return getExecuteResponse(ExecuteResponseEnum.RUN_SUCCESS, false, errorMessage, time, memory, runMessageList);
    }
    
    protected abstract File saveFile(String code, String parentPath, String fileName);
    
    protected abstract File preprocessCode(String parentPath, String code);
    
    protected abstract Method preprocessFile(File file);
    
    protected abstract String generateTemplateCode(Method method);
    
    protected abstract String mergeCode(String templateCode, String code);
    
    protected abstract String compileCode(File file);
    
    protected abstract List<String> runCode(File file, List<String[]> testCaseList, Long[] time, Long[] memory);
    
    
    protected void clearFile(File file) {
        String path = file.getParentFile().getAbsolutePath();
        if (FileUtil.exist(path)) {
            FileUtil.del(path);
        }
    }
    
    protected abstract String getErrorMessage(List<String> runMessageList);
    
    
    private ExecuteResponse getExecuteResponse(ExecuteResponseEnum executeResponseEnum, String msg) {
        return getExecuteResponse(executeResponseEnum, true, msg, INIT_VALUE, INIT_VALUE, Collections.emptyList());
    }
    
    @SafeVarargs
    private final ExecuteResponse getExecuteResponse(ExecuteResponseEnum executeResponseEnum, boolean isCompileAndRun, String msg, Long[] time, Long[] memory, List<String>... dataList) {
        ExecuteResponse executeResponse = new ExecuteResponse();
        executeResponse.setCode(executeResponseEnum.getCode());
        executeResponse.setMsg(executeResponseEnum.getMsg());
        executeResponse.setRunTime(time[0]);
        memory[0] = memory[0] / (1024 * 1024);
        executeResponse.setRunMemory(memory[0]);
        if (isCompileAndRun) {
            executeResponse.setStderr(msg);
        } else {
            executeResponse.setStdout(dataList[0]);
        }
        return executeResponse;
    }
    
    private ExecuteResponse handleCompilationError(File file) {
        String compileMessage = compileCode(file);
        if (!compileMessage.isEmpty()) {
            compileMessage = getErrorMessage(Collections.singletonList(compileMessage));
            clearFile(file);
            return getExecuteResponse(ExecuteResponseEnum.COMPILE_ERROR, compileMessage);
        }
        return null;
    }
}
