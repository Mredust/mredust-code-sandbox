package com.mredust.codesandbox.core.template;

import cn.hutool.core.io.FileUtil;
import com.mredust.codesandbox.exception.CompilationException;
import com.mredust.codesandbox.model.dto.ExecuteResponse;
import com.mredust.codesandbox.model.enums.ExecuteResponseEnum;

import java.io.File;
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
        File file;
        List<String> runMessageList;
        Long[] time = {0L};
        Long[] memory = {0L};
        try {
            file = preprocessFile(parentPath, code);
            String templateCode = generateTemplateCode(file);
            clearFile(file);
            String executeCode = mergeCode(templateCode, code);
            file = saveFile(executeCode, parentPath, MAIN_CLASS_NAME);
            runMessageList = runCode(file, testCaseList, time, memory);
        } catch (CompilationException e) {
            return getExecuteResponse(ExecuteResponseEnum.COMPILE_ERROR, e.getMessage());
        }
        String errorMessage = getErrorMessage(runMessageList);
        if (!errorMessage.isEmpty()) {
            clearFile(file);
            return getExecuteResponse(ExecuteResponseEnum.RUNTIME_ERROR, errorMessage);
        }
        clearFile(file);
        return getExecuteResponse(ExecuteResponseEnum.RUN_SUCCESS, false, errorMessage, time, memory, runMessageList);
    }
    
    protected abstract File preprocessFile(String parentPath, String code);
    
    protected abstract String generateTemplateCode(File file);
    
    protected abstract String mergeCode(String templateCode, String code);
    
    protected abstract File saveFile(String code, String parentPath, String fileName);
    
    protected abstract List<String> runCode(File file, List<String[]> testCaseList, Long[] time, Long[] memory);
    
    
    protected abstract String getErrorMessage(List<String> runMessageList);
    
    
    protected void clearFile(File file) {
        String path = file.getParentFile().getAbsolutePath();
        if (FileUtil.exist(path)) {
            FileUtil.del(path);
        }
    }
    
    private ExecuteResponse getExecuteResponse(ExecuteResponseEnum executeResponseEnum, String msg) {
        return getExecuteResponse(executeResponseEnum, true, msg, INIT_VALUE, INIT_VALUE, Collections.emptyList());
    }
    
    @SafeVarargs
    private final ExecuteResponse getExecuteResponse(ExecuteResponseEnum executeResponseEnum, boolean isCompileAndRun, String msg, Long[] time, Long[] memory, List<String>... dataList) {
        ExecuteResponse executeResponse = new ExecuteResponse();
        executeResponse.setCode(executeResponseEnum.getCode());
        executeResponse.setMsg(executeResponseEnum.getMsg());
        if (isCompileAndRun) {
            executeResponse.setStderr(msg);
            executeResponse.setRunTime(0L);
            executeResponse.setRunMemory(0L);
        } else {
            executeResponse.setStdout(dataList[0]);
            executeResponse.setRunTime(time[0]);
            memory[0] = memory[0] / (1024 * 1024);
            executeResponse.setRunMemory(memory[0]);
        }
        return executeResponse;
    }
    
    
}
