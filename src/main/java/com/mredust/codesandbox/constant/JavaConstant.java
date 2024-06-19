package com.mredust.codesandbox.constant;

/**
 * @author <a href="https://github.com/Mredust">Mredust</a>
 */
public interface JavaConstant {
    String JAVA_MAIN_CLASS_NAME = "Main";
    String JAVA_SUFFIX = ".java";
    String JAVA_COMPILE_CMD = "javac -encoding utf-8 %s";
    String JAVA_RUN_CMD = "java -Xmx256m -Dfile.encoding=UTF-8 -cp";
}
