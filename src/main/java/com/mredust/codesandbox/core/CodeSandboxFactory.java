package com.mredust.codesandbox.core;

import com.mredust.codesandbox.core.template.CodeSandboxTemplate;
import com.mredust.codesandbox.model.enums.LanguageEnum;

/**
 * @author <a href="https://github.com/Mredust">Mredust</a>
 */
public interface CodeSandboxFactory {
    CodeSandboxTemplate getCodeSandboxTemplate(LanguageEnum languageEnum);
}
