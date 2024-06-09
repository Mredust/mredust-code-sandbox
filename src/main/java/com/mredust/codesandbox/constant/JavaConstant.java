package com.mredust.codesandbox.constant;

/**
 * @author <a href="https://github.com/Mredust">Mredust</a>
 */
public interface JavaConstant {
    String JAVA = "java";
    String JAVA_DEFAULT_NAME = "Main.java";
    String JAVA_COMPILE_CMD = "javac -encoding utf-8 %s";
    String JAVA_RUN_CMD = "java -Dfile.encoding=UTF-8 -cp %s Main %s";
}
