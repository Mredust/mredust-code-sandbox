package com.mredust.codesandbox.core.template;

import cn.hutool.core.io.FileUtil;
import com.mredust.codesandbox.model.dto.ExecuteResponse;
import com.mredust.codesandbox.model.enums.ExecuteResponseEnum;
import com.mredust.codesandbox.utils.ProcessUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static com.mredust.codesandbox.constant.CodeSandboxConstant.*;
import static com.mredust.codesandbox.constant.JavaConstant.JAVA_RUN_CMD;
import static com.mredust.codesandbox.constant.PythonConstant.PYTHON_SUFFIX;

/**
 * @author <a href="https://github.com/Mredust">Mredust</a>
 */
public class TempTemplate {
    String PROBLEM_CLASS_NAME = "Solution";
    String run_cmd = "python";
    
    public ExecuteResponse executeCode(String code, List<String[]> testCaseList) {
        
        // 保存文件
        String parentPath = String.format("%s%s%s", System.getProperty("user.dir"), File.separator, WORK_DIR);
        String filePath = String.format("%s%s%s%s%s", parentPath, File.separator, UUID.randomUUID(), File.separator, (PROBLEM_CLASS_NAME + PYTHON_SUFFIX));
        File file = FileUtil.writeUtf8String(code, filePath);
        
        // 运行文件
        List<String> runMessageList = runCode(file, testCaseList, INIT_VALUE, INIT_VALUE);
     
        
        
        // 处理结果
        return getExecuteResponse(ExecuteResponseEnum.RUN_SUCCESS, false, "", new Long[]{0L}, new Long[]{0L}, runMessageList);
    }
    protected List<String> runCode(File file, List<String[]> testCaseList, Long[] time, Long[] memory) {
        List<String> list = new ArrayList<>();
        int size = testCaseList.size();
        int totalCombinations = 1;
        for (String[] testCase : testCaseList) {
            totalCombinations = Math.max(testCase.length, totalCombinations);
        }
        for (int i = 0; i < totalCombinations; i++) {
            List<String> params = new ArrayList<>();
            params.add(file.getAbsolutePath());
            for (int j = 0; j < size; j++) {
                params.add(testCaseList.get(j)[i]);
            }
            String paramList = String.join(" ", params);
            String cmd = run_cmd + " " + paramList;
            String msg = ProcessUtils.processHandler(cmd, time, memory);
            list.add(msg);
        }
        return list;
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
    
}
