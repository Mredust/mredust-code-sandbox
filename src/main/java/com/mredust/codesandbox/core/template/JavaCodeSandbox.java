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
    // TODO：参数优化
    private static final String RUN_CMD_ARG = "java -Dfile.encoding=UTF-8 -cp %s %s %s";
    private static final String RUN_CMD_ARGS = "java -Dfile.encoding=UTF-8 -cp %s %s %s %s";
    private static final String CLASS_NAME = "Main";
    
    @Override
    public ExecuteResponse executeCode(String code, String[]... testCaseList) {
        return super.executeCode(code, testCaseList);
    }
    
    @Override
    protected List<String> runCode(File file, String[]... testCaseList) {
        ArrayList<String> list = new ArrayList<>();
        int size = testCaseList.length;
        String runArg = RUN_CMD_ARG;
        String[] var1List = testCaseList[0];
        String[] var2List = {};
        if (size == 2) {
            runArg = RUN_CMD_ARGS;
            var2List = testCaseList[1];
        }
        for (int i = 0; i < var1List.length; i++) {
            String rumCmd = "";
            if (size == 1) {
                rumCmd = String.format(runArg, file.getParent(), CLASS_NAME, var1List[i]);
            } else {
                rumCmd = String.format(runArg, file.getParent(), CLASS_NAME, var1List[i], var2List[i]);
            }
            String msg = ProcessUtils.processHandler(rumCmd);
            list.add(msg);
        }
        return list;
    }
    
    
}
