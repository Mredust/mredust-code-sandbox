package com.mredust.codesandbox.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.stream.Stream;

/**
 * @author <a href="https://github.com/Mredust">Mredust</a>
 */
@Getter
@AllArgsConstructor
public enum LanguageEnum {
    JAVA("java", "java"),
    
    PYTHON("python", "python");
    
    private final String language;
    
    private final String value;
    
    public static LanguageEnum getLanguageEnum(String language) {
        return Stream.of(LanguageEnum.values()).filter(languageEnum -> languageEnum.getLanguage().equals(language)).findFirst().orElse(null);
    }
}
