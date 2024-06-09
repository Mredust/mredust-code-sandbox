package com.mredust.codesandbox.utils;

import com.mredust.codesandbox.model.dto.ExecuteResult;
import org.springframework.util.StopWatch;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


/**
 * 进程工具类
 *
 * @author <a href="https://github.com/Mredust">Mredust</a>
 */
public class ProcessUtils {
    private ProcessUtils() {
    }
    
    public static ExecuteResult processHandler(Process compileProcess) {
        ExecuteResult executeResult = new ExecuteResult();
        try {
            StopWatch stopWatch = new StopWatch();
            stopWatch.start();
            int execResult = compileProcess.waitFor();
            executeResult.setExecuteCode(execResult);
            if (execResult == 0) {
                executeResult.setMessage(getStreamMessage(compileProcess.getInputStream()));
            } else {
                executeResult.setMessage(getStreamMessage(compileProcess.getErrorStream()));
            }
            stopWatch.stop();
            executeResult.setTime(stopWatch.getLastTaskTimeMillis());
        } catch (InterruptedException | IOException e) {
            throw new RuntimeException(e);
        } finally {
            compileProcess.destroy();
        }
        return executeResult;
    }
    
    
    public static String getStreamMessage(InputStream inputStream) throws IOException {
        StringBuilder outputList = new StringBuilder();
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {
            String outputLine;
            while ((outputLine = bufferedReader.readLine()) != null) {
                outputList.append(outputLine);
            }
        }
        return outputList.toString();
    }
}
