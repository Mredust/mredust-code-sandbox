package com.mredust.codesandbox.core.template;

import cn.hutool.core.io.FileUtil;
import com.mredust.codesandbox.model.dto.ExecuteResponse;
import com.mredust.codesandbox.model.enums.ExecuteResponseEnum;
import com.mredust.codesandbox.utils.ProcessUtils;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.mredust.codesandbox.constant.CodeSandboxConstant.INIT_VALUE;
import static com.mredust.codesandbox.constant.CodeSandboxConstant.WORK_DIR;
import static com.mredust.codesandbox.constant.PythonConstant.PYTHON_SUFFIX;

/**
 * @author <a href="https://github.com/Mredust">Mredust</a>
 */
public class TempTemplate {
    String PROBLEM_CLASS_NAME = "Solution";
    String run_cmd = "python";
    
    public ExecuteResponse executeCode(String code, List<String[]> testCaseList) {
        String parentPath = String.format("%s%s%s", System.getProperty("user.dir"), File.separator, WORK_DIR);
        File tempFile = preprocessFile(parentPath, code);
        
        String templateCode = generateTemplateCode(tempFile);
        String executeCode = mergeCode(templateCode, code);
        File file = saveFile(executeCode, parentPath, PROBLEM_CLASS_NAME);
        // 运行文件
        Long[] time = {0L};
        Long[] memory = {0L};
        List<String> runMessageList = runCode(file, testCaseList, INIT_VALUE, INIT_VALUE);
        
        // 处理结果
        return getExecuteResponse(ExecuteResponseEnum.RUN_SUCCESS, false, "", time, memory, runMessageList);
    }
    
    protected File preprocessFile(String parentPath, String code) {
        String filePath = String.format("%s%s%s%s%s", parentPath, File.separator, UUID.randomUUID(), File.separator, (PROBLEM_CLASS_NAME + PYTHON_SUFFIX));
        return FileUtil.writeUtf8String(code, filePath);
    }
    
    
    protected File saveFile(String code, String parentPath, String fileName) {
        String filePath = String.format("%s%s%s%s%s", parentPath, File.separator, UUID.randomUUID(), File.separator, (PROBLEM_CLASS_NAME + PYTHON_SUFFIX));
        return FileUtil.writeUtf8String(code, filePath);
    }
    
    protected String mergeCode(String templateCode, String code) {
        return "from typing import *\n" + code + templateCode;
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
    
    protected String generateTemplateCode(File file) {
        StringBuilder sb = new StringBuilder();
        String rootPath = System.getProperty("user.dir");
        Path pythonFilePath = Paths.get(rootPath, "src", "main", "resources", "method_info_extractor.py");
        String cmd = String.format("python %s %s", pythonFilePath, file.getAbsolutePath());
        String s = ProcessUtils.processHandler(cmd, new Long[]{0L}, new Long[]{0L});
        String[] pyJson = s.split(" ");
        String name = pyJson[0].split(":")[1].trim();
        String[] args = {};
        String[] types = pyJson[1].split(":");
        if (types.length > 1) {
            args = pyJson[1].split(":")[1].split(",");
        }
        sb.append("\n\ndef type_conversion(arg, arg_type):\n")
                .append("\tif arg_type == \"int\":\n")
                .append("\t\treturn int(arg)\n")
                .append("\telif arg_type == \"float\":\n")
                .append("\t\treturn float(arg)\n")
                .append("\telif arg_type == \"str\":\n")
                .append("\t\treturn str(arg)\n")
                .append("\telif arg_type.startswith(\"List[\") and arg_type.endswith(\"]\"):\n")
                .append("\t\treturn [type_conversion(item.strip(), arg_type[5:-1]) for item in arg.split(',')]\n")
                .append("\telse:\n")
                .append("\t\traise ValueError(f\"不支持该类型的参数: {arg_type}\")\n\n");
        sb.append("if __name__ == '__main__':\n")
                .append("\timport sys\n");
        sb.append("\tprint(Solution().").append(name).append("(");
        for (int i = 0; i < args.length; i++) {
            sb.append("type_conversion(sys.argv[").append(i + 1).append("], '").append(args[i]).append("')");
            if (i < args.length - 1) {
                sb.append(", ");
            }
        }
        sb.append("))\n");
        return sb.toString();
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
