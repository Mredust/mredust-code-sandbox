package com.mredust.codesandbox.exception;

import lombok.Getter;

/**
 * @author <a href="https://github.com/Mredust">Mredust</a>
 */
@Getter
public class CompilationException extends RuntimeException {
    public CompilationException(String msg) {
        super(msg);
    }
}
