package com.mredust.codesandbox.core.template;

import cn.hutool.core.io.FileUtil;
import com.mredust.codesandbox.model.dto.ExecuteResponse;
import com.mredust.codesandbox.utils.ProcessUtils;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.mredust.codesandbox.constant.CodeSandboxConstant.*;
import static com.mredust.codesandbox.constant.JavaConstant.*;

/**
 * Java代码沙箱
 *
 * @author <a href="https://github.com/Mredust">Mredust</a>
 */
@Component
public class JavaCodeSandbox extends CodeSandboxTemplate {
    private static final String[] ERROR_MESSAGE_LIST = {"Exception", "Error", "错误", "异常"};
    
    @Override
    protected File preprocessFile(String parentPath, String code) {
        String importPackage = "import java.util.*;\nimport java.lang.*;\nimport java.util.function.*;\n";
        String tempCode = importPackage + code;
        File file = saveFile(tempCode, parentPath, PROBLEM_CLASS_NAME);
         /* ExecuteResponse compileErrorResponse = handleCompilationError(tempFile);
        if (compileErrorResponse != null) {
            return compileErrorResponse;
        }*/
        return file;
    }
    
    @Override
    protected String generateTemplateCode(File file) {
        Method method;
        try (URLClassLoader classLoader = new URLClassLoader(new URL[]{file.getParentFile().toURI().toURL()})) {
            Class<?> clazz = classLoader.loadClass(PROBLEM_CLASS_NAME);
            method = clazz.getDeclaredMethods()[0];
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        
        String methodName = method.getName();
        Parameter[] parameters = method.getParameters();
        String returnType = method.getReturnType().isArray() ? method.getReturnType().getComponentType().getName() + "[]" : method.getReturnType().getName();
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
        templateCode.append("\t@SuppressWarnings(\"unchecked\")\n")
                .append("\tprivate static <T> T typeConversion(String type, String arg) {\n")
                .append("\t\tMap<String, Function<String, ?>> clazzMap = new HashMap<>(8);\n")
                .append("\t\tclazzMap.put(\"int\", Integer::parseInt);\n")
                .append("\t\tclazzMap.put(\"boolean\", Boolean::parseBoolean);\n")
                .append("\t\tclazzMap.put(\"string\", s -> s);\n")
                .append("\t\tclazzMap.put(\"int[]\", i -> Arrays.stream(i.split(\",\")).mapToInt(Integer::parseInt).toArray());\n")
                .append("\t\tif (clazzMap.containsKey(type.trim().toLowerCase())) {\n")
                .append("\t\t\treturn (T) clazzMap.get(type.toLowerCase()).apply(arg);\n")
                .append("\t\t}\n")
                .append("\t\tthrow new IllegalArgumentException(\"Unsupported type: \" + type);\n").append("\t}\n}\n");
        return templateCode.toString();
    }
    
    @Override
    protected String mergeCode(String templateCode, String code) {
        return templateCode + code;
    }
    
    @Override
    protected File saveFile(String code, String parentPath, String fileName) {
        String filePath = String.format("%s%s%s%s%s", parentPath, File.separator, UUID.randomUUID(), File.separator, (fileName + JAVA_SUFFIX));
        return FileUtil.writeUtf8String(code, filePath);
    }
    
    @Override
    protected List<String> runCode(File file, List<String[]> testCaseList, Long[] time, Long[] memory) {
        String compileCmd = String.format(JAVA_COMPILE_CMD, file.getAbsolutePath());
        String compileMessage = ProcessUtils.processHandler(compileCmd, INIT_VALUE, INIT_VALUE);
       /* if (!compileMessage.isEmpty()) {
            compileMessage = getErrorMessage(Collections.singletonList(compileMessage));
            clearFile(file);
            return getExecuteResponse(ExecuteResponseEnum.COMPILE_ERROR, compileMessage);
        }*/
        
        List<String> list = new ArrayList<>();
        int size = testCaseList.size();
        int totalCombinations = 1;
        for (String[] testCase : testCaseList) {
            totalCombinations = Math.max(testCase.length, totalCombinations);
        }
        for (int i = 0; i < totalCombinations; i++) {
            List<String> params = new ArrayList<>();
            params.add(file.getParent());
            params.add(MAIN_CLASS_NAME);
            for (int j = 0; j < size; j++) {
                params.add(testCaseList.get(j)[i]);
            }
            String paramList = String.join(" ", params);
            String cmd = JAVA_RUN_CMD + " " + paramList;
            String msg = ProcessUtils.processHandler(cmd, time, memory);
            list.add(msg);
        }
        return list;
    }
    
    @Override
    protected String getErrorMessage(List<String> runMessageList) {
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
}
