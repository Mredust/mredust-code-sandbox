package com.mredust.codesandbox.core.template;

import cn.hutool.core.io.FileUtil;
import cn.hutool.dfa.FoundWord;
import cn.hutool.dfa.WordTree;
import com.mredust.codesandbox.common.ResponseCode;
import com.mredust.codesandbox.exception.BusinessException;
import com.mredust.codesandbox.model.dto.ExecuteCodeResponse;
import com.mredust.codesandbox.model.dto.ExecuteResult;
import com.mredust.codesandbox.model.enums.ExecuteCodeStatusEnum;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 代码沙箱模板方法
 *
 * @author <a href="https://github.com/Mredust">Mredust</a>
 */
public abstract class CodeSandboxTemplate {
    private static final String GLOBAL_CODE_DIR_PATH = "tempcode";
    
    private static final WordTree WORD_TREE;
    
    static {
        WORD_TREE = new WordTree();
        WORD_TREE.addWords("Files", "exec");
    }
    
    protected ExecuteCodeResponse executeCode(List<String> inputList, String code) {
        FoundWord foundWord = WORD_TREE.matchWord(code);
        if (foundWord != null) {
            throw new BusinessException(ResponseCode.DANGER_CODE);
        }
        String path = String.format("%s%s%s", System.getProperty("user.dir"), File.separator, GLOBAL_CODE_DIR_PATH);
        File file = saveCodeToFile(path, code);
        try {
            ExecuteResult executeResult = compileCode(file);
            Integer executeCode = executeResult.getExecuteCode();
            if (ExecuteCodeStatusEnum.COMPILE_FAILED.getCode().equals(executeCode)) {
                return getErrorResponse(ExecuteCodeStatusEnum.COMPILE_FAILED);
            }
        } catch (IOException e) {
            return getErrorResponse(ExecuteCodeStatusEnum.COMPILE_FAILED);
        }
        List<ExecuteResult> executeResultList;
        try {
            executeResultList = runCompileFile(file, inputList);
        } catch (IOException e) {
            return getErrorResponse(ExecuteCodeStatusEnum.RUN_FAILED);
        }
        ExecuteCodeResponse executeCodeResponse = getExecuteCodeResponse(executeResultList);
        clearFile(file);
        return executeCodeResponse;
    }
    
    protected abstract File saveCodeToFile(String path, String code);
    
    protected abstract ExecuteResult compileCode(File file) throws IOException;
    
    
    protected abstract List<ExecuteResult> runCompileFile(File file, List<String> inputList) throws IOException;
    
    protected ExecuteCodeResponse getExecuteCodeResponse(List<ExecuteResult> executeResultList) {
        ExecuteCodeResponse executeCodeResponse = new ExecuteCodeResponse();
        ArrayList<String> outputList = new ArrayList<>();
        long totalTime = 0L;
        long totalMemory = 0L;
        boolean compileFailed = false;
        for (ExecuteResult executeResult : executeResultList) {
            String message = executeResult.getMessage();
            if (ExecuteCodeStatusEnum.COMPILE_FAILED.getCode().equals(executeResult.getExecuteCode())) {
                executeCodeResponse.setStatus(ExecuteCodeStatusEnum.COMPILE_FAILED.getCode());
                executeCodeResponse.setMessage(ExecuteCodeStatusEnum.COMPILE_FAILED.getMsg());
                executeCodeResponse.setErrorMessage(message);
                outputList.clear();
                compileFailed = true;
                break;
            }
            outputList.add(executeResult.getMessage());
            Long runTime = executeResult.getTime();
            Long runMemory = executeResult.getMemory();
            totalTime += (runTime != null) ? runTime : 0L;
            totalMemory += (runMemory != null) ? runMemory : 0L;
        }
        if (!compileFailed) {
            executeCodeResponse.setStatus(ExecuteCodeStatusEnum.SUCCESS.getCode());
            executeCodeResponse.setMessage(ExecuteCodeStatusEnum.SUCCESS.getMsg());
        }
        executeCodeResponse.setTime(totalTime);
        executeCodeResponse.setMemory(totalMemory);
        executeCodeResponse.setOutputList(outputList);
        return executeCodeResponse;
    }
    
    
    protected void clearFile(File file) {
        String path = file.getParentFile().getAbsolutePath();
        if (FileUtil.exist(path)) {
            FileUtil.del(path);
        }
    }
    
    private ExecuteCodeResponse getErrorResponse(ExecuteCodeStatusEnum executeCodeStatusEnum) {
        return ExecuteCodeResponse.builder()
                .status(executeCodeStatusEnum.getCode())
                .message(executeCodeStatusEnum.getMsg())
                .outputList(new ArrayList<>())
                .time(0L)
                .memory(0L)
                .build();
    }
    
    
}
