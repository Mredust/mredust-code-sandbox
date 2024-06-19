package com.mredust.codesandbox.core.template;

import com.mredust.codesandbox.model.dto.ExecuteResponse;
import com.mredust.codesandbox.utils.ProcessUtils;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="https://github.com/Mredust">Mredust</a>
 */
@Component
public class JavaCodeSandbox extends CodeSandboxTemplate {
    private static final String RUN_CMD = "java -Dfile.encoding=UTF-8 -cp";
    private static final String CLASS_NAME = "Main";
    
    @Override
    public ExecuteResponse executeCode(String code, List<String[]> testCaseList) {
        return super.executeCode(code, testCaseList);
    }
    
    @Override
    protected List<String> runCode(File file, List<String[]> testCaseList) {
        List<String> list = new ArrayList<>();
        int size = testCaseList.size();
        int totalCombinations = 1;
        for (String[] testCase : testCaseList) {
            totalCombinations = Math.max(testCase.length, totalCombinations);
        }
        for (int i = 0; i < totalCombinations; i++) {
            List<String> params = new ArrayList<>();
            params.add(file.getParent());
            params.add(CLASS_NAME);
            for (int j = 0; j < size; j++) {
                params.add(testCaseList.get(j)[i]);
            }
            String paramList = String.join(" ", params);
            String cmd = RUN_CMD + " " + paramList;
            String msg = ProcessUtils.processHandler(cmd);
            list.add(msg);
        }
        return list;
    }
}
