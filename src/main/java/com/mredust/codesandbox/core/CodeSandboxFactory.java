package com.mredust.codesandbox.core;

import com.mredust.codesandbox.core.template.JavaCodeSandbox;
import com.mredust.codesandbox.core.template.PythonCodeSandbox;
import com.mredust.codesandbox.model.enums.LanguageEnum;
import com.mredust.codesandbox.core.template.CodeSandboxTemplate;

import java.util.EnumMap;

/**
 * 代码沙箱工厂: 单例（饿汉式） + 工厂
 *
 * @author <a href="https://github.com/Mredust">Mredust</a>
 */
public class CodeSandboxFactory {
    private CodeSandboxFactory() {
    }
    
    private static final EnumMap<LanguageEnum, CodeSandboxTemplate> LANGUAGE_ENUM_CODE_SANDBOX_TEMPLATE_ENUM_MAP = new EnumMap<>(LanguageEnum.class);
    
    static {
        LANGUAGE_ENUM_CODE_SANDBOX_TEMPLATE_ENUM_MAP.put(LanguageEnum.JAVA, new JavaCodeSandbox());
        LANGUAGE_ENUM_CODE_SANDBOX_TEMPLATE_ENUM_MAP.put(LanguageEnum.PYTHON, new PythonCodeSandbox());
    }
    
    public static CodeSandboxTemplate getCodeSandboxTemplate(LanguageEnum languageEnum) {
        return LANGUAGE_ENUM_CODE_SANDBOX_TEMPLATE_ENUM_MAP.get(languageEnum);
    }
}
