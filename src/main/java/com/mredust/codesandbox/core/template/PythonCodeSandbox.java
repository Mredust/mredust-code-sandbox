package com.mredust.codesandbox.core.template;

import com.mredust.codesandbox.model.dto.ExecuteResponse;

import java.io.File;
import java.util.List;

/**
 * Python代码沙箱
 *
 * @author <a href="https://github.com/Mredust">Mredust</a>
 */

public class PythonCodeSandbox extends CodeSandboxTemplate{
    
    
    @Override
    public ExecuteResponse executeCode(String code, List<String[]> testCaseList) {
        return super.executeCode(code, testCaseList);
    }
    
    @Override
    protected List<String> runCode(File file, List<String[]> testCaseList, Long[] time, Long[] memory) {
        return null;
    }
}
