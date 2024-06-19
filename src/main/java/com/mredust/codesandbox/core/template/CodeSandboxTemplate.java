package com.mredust.codesandbox.core.template;

import cn.hutool.core.io.FileUtil;
import com.mredust.codesandbox.model.dto.ExecuteResponse;
import com.mredust.codesandbox.model.enums.ExecuteResponseEnum;
import com.mredust.codesandbox.utils.ProcessUtils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author <a href="https://github.com/Mredust">Mredust</a>
 */
public abstract class CodeSandboxTemplate {
    
    private static final String WORK_DIR = "tempcode";
    private static final String PROBLEM_CLASS_NAME = "Solution";
    
    private static final String MAIN_CLASS_NAME = "Main";
    private static final String JAVA_SUFFIX = ".java";
    private static final String COMPILE_CMD = "javac -encoding UTF-8 %s";
    private static final String[] ERROR_MESSAGE_LIST = {"Exception", "Error", "错误", "异常"};
    public static final Long[] DEFAULT_TIME = {0L};
    
    public ExecuteResponse executeCode(String code, List<String[]> testCaseList) {
        // 前置处理
        String importPackage = "import java.util.*;\nimport java.lang.*;\nimport java.util.function.*;\n";
        String tempCode = importPackage + code;
        File tempFile = saveFile(tempCode, PROBLEM_CLASS_NAME);
        String compileCmd = String.format(COMPILE_CMD, tempFile.getAbsolutePath());
        String compileMessage = ProcessUtils.processHandler(compileCmd, DEFAULT_TIME, DEFAULT_TIME);
        if (!compileMessage.isEmpty()) {
            compileMessage = getErrorMessage(Collections.singletonList(compileMessage));
            clearFile(tempFile);
            return getExecuteResponse(ExecuteResponseEnum.COMPILE_ERROR, true, compileMessage, 0L, 0L);
        }
        Method method;
        try (URLClassLoader classLoader = new URLClassLoader(new URL[]{tempFile.getParentFile().toURI().toURL()})) {
            Class<?> clazz = classLoader.loadClass(PROBLEM_CLASS_NAME);
            method = clazz.getDeclaredMethods()[0];
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        clearFile(tempFile);
        String templateCode = generateTemplateCode(
                method.getName(),
                method.getParameters(),
                method.getReturnType().isArray() ? method.getReturnType().getComponentType().getName() + "[]" : method.getReturnType().getName()
        );
        
        // 正式处理
        File file = saveFile(templateCode + code, MAIN_CLASS_NAME);
        compileCmd = String.format(COMPILE_CMD, file.getAbsolutePath());
        compileMessage = ProcessUtils.processHandler(compileCmd, DEFAULT_TIME, DEFAULT_TIME);
        if (!compileMessage.isEmpty()) {
            clearFile(file);
            return getExecuteResponse(ExecuteResponseEnum.COMPILE_ERROR, true, compileMessage, 0L, 0L);
        }
        // 运行
        Long[] time = {0L};
        Long[] memory = {0L};
        List<String> runMessageList = runCode(file, testCaseList, time, memory);
        String errorMessage = getErrorMessage(runMessageList);
        if (!errorMessage.isEmpty()) {
            clearFile(file);
            return getExecuteResponse(ExecuteResponseEnum.RUNTIME_ERROR, true, errorMessage, 0L, 0L);
        }
        clearFile(file);
        return getExecuteResponse(ExecuteResponseEnum.RUN_SUCCESS, false, errorMessage, time[0], memory[0], runMessageList);
    }
    
    private static File saveFile(String code, String fileName) {
        String parentPath = String.format("%s%s%s", System.getProperty("user.dir"), File.separator, WORK_DIR);
        String filePath = String.format("%s%s%s%s%s", parentPath, File.separator, UUID.randomUUID(), File.separator, (fileName + JAVA_SUFFIX));
        return FileUtil.writeUtf8String(code, filePath);
    }
    
    private static String generateTemplateCode(String methodName, Parameter[] parameters, String returnType) {
        StringBuilder templateCode = new StringBuilder();
        templateCode.append("import java.util.*;\n").append("import java.lang.*;\n").append("import java.util.function.*;\n").append("public class Main {\n").append("\tpublic static void main(String[] args) {\n");
        if (returnType.contains("[]")) {
            templateCode.append("\t\tSystem.out.println(Arrays.toString(new Solution().").append(methodName).append("(");
        } else {
            templateCode.append("\t\tSystem.out.print(new Solution().").append(methodName).append("(");
        }
        for (int i = 0; i < parameters.length; i++) {
            Parameter parameter = parameters[i];
            String type = parameter.getType().getSimpleName();
            if (type.equals("java.lang.String")) {
                type = "string";
            }
            templateCode.append("typeConversion(\"").append(type).append("\", args[").append(i).append("]").append(")");
            if (i < parameters.length - 1) {
                templateCode.append(", ");
            }
        }
        if (returnType.contains("[]")) {
            templateCode.append(")));\n").append("\t}\n");
        } else {
            templateCode.append("));\n").append("\t}\n");
        }
        // todo：是否单独读取
        templateCode.append("\t@SuppressWarnings(\"unchecked\")\n").append("\tprivate static <T> T typeConversion(String type, String arg) {\n").append("\t\tMap<String, Function<String, ?>> clazzMap = new HashMap<>(8);\n").append("\t\tclazzMap.put(\"int\", Integer::parseInt);\n").append("\t\tclazzMap.put(\"boolean\", Boolean::parseBoolean);\n").append("\t\tclazzMap.put(\"string\", s -> s);\n").append("\t\tclazzMap.put(\"int[]\", i -> Arrays.stream(i.split(\",\")).mapToInt(Integer::parseInt).toArray());\n").append("\t\tif (clazzMap.containsKey(type.trim().toLowerCase())) {\n").append("\t\t\treturn (T) clazzMap.get(type.toLowerCase()).apply(arg);\n").append("\t\t}\n").append("\t\tthrow new IllegalArgumentException(\"Unsupported type: \" + type);\n").append("\t}\n}\n");
        return templateCode.toString();
    }
    
    protected abstract List<String> runCode(File file, List<String[]> testCaseList, Long[] time, Long[] memory);
    
    
    private void clearFile(File file) {
        String path = file.getParentFile().getAbsolutePath();
        if (FileUtil.exist(path)) {
            FileUtil.del(path);
        }
    }
    
    private String getErrorMessage(List<String> runMessageList) {
        for (String msg : runMessageList) {
            if (Arrays.stream(ERROR_MESSAGE_LIST).anyMatch(msg::contains)) {
                StringBuilder sb = new StringBuilder();
                String[] errRegex = {"java.lang.(.*)", "错误:.*?(?=(\\\\|$|\\n))"};
                for (String regex : errRegex) {
                    Pattern pattern = Pattern.compile(regex);
                    Matcher matcher = pattern.matcher(msg);
                    if (matcher.find()) {
                        String matched = matcher.group();
                        sb.append(matched).append("\n");
                    }
                }
                return sb.toString();
            }
        }
        return "";
    }
    
    @SafeVarargs
    private final ExecuteResponse getExecuteResponse(ExecuteResponseEnum executeResponseEnum, boolean isCompileAndRun, String msg, Long time, Long memory, List<String>... dataList) {
        ExecuteResponse executeResponse = new ExecuteResponse();
        executeResponse.setCode(executeResponseEnum.getCode());
        executeResponse.setMsg(executeResponseEnum.getMsg());
        executeResponse.setRunTotalTime(time);
        memory = memory / (1024 * 1024);
        executeResponse.setRunTotalMemory(memory);
        if (isCompileAndRun) {
            executeResponse.setStderr(msg);
        } else {
            executeResponse.setStdout(dataList[0]);
        }
        return executeResponse;
    }
    
}
