package com.mredust.codesandbox.constant;

/**
 * @author <a href="https://github.com/Mredust">Mredust</a>
 */
public interface JavaConstant {
    /**
     * Java后缀
     */
    String JAVA_SUFFIX = ".java";
    /**
     * Java编译命令
     */
    String JAVA_COMPILE_CMD = "javac -encoding utf-8 %s";
    /**
     * Java运行命令
     */
    String JAVA_RUN_CMD = "java -Xmx256m -Dfile.encoding=UTF-8 -cp";
}
