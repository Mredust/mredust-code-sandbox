package com.mredust.codesandbox.core.strategy;

import com.mredust.codesandbox.core.template.CodeSandboxTemplate;
import com.mredust.codesandbox.core.template.JavaCodeSandbox;
import com.mredust.codesandbox.core.template.Python3CodeSandbox;
import com.mredust.codesandbox.model.enums.LanguageEnum;

/**
 * 代码沙箱工厂
 *
 * @author <a href="https://github.com/Mredust">Mredust</a>
 */
public class SimpleCodeSandboxFactory implements CodeSandboxFactory {
    
    @Override
    public CodeSandboxTemplate getCodeSandboxTemplate(LanguageEnum languageEnum) {
        switch (languageEnum) {
            case JAVA:
                return new JavaCodeSandbox();
            case PYTHON:
                return new Python3CodeSandbox();
            default:
                throw new IllegalArgumentException("Unsupported language: " + languageEnum);
        }
    }
}
