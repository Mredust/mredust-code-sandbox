package com.mredust.codesandbox.core.template;

import com.mredust.codesandbox.model.dto.ExecuteResponse;

import java.io.File;
import java.lang.reflect.Method;
import java.util.List;

/**
 * Python代码沙箱
 *
 * @author <a href="https://github.com/Mredust">Mredust</a>
 */

public class PythonCodeSandbox extends CodeSandboxTemplate{
    
    
    @Override
    protected File saveFile(String code, String parentPath, String fileName) {
        return null;
    }
    
    @Override
    protected File preprocessCode(String parentPath, String code) {
        return null;
    }
    
    @Override
    protected Method preprocessFile(File file) {
        return null;
    }
    
    @Override
    protected String generateTemplateCode(Method method) {
        return null;
    }
    
    @Override
    protected String mergeCode(String templateCode, String code) {
        return null;
    }
    
    @Override
    protected String compileCode(File file) {
        return null;
    }
    
    @Override
    protected List<String> runCode(File file, List<String[]> testCaseList, Long[] time, Long[] memory) {
        return null;
    }
    
    @Override
    protected String getErrorMessage(List<String> runMessageList) {
        return null;
    }
}
