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
    
    public static String processHandler(String cmd) {
        Process process = null;
        try {
            process = Runtime.getRuntime().exec(cmd);
            int execResult = process.waitFor();
            InputStream stream = (execResult == 0) ? process.getInputStream() : process.getErrorStream();
            return getStreamMessage(stream);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            if (process != null) {
                closeProcessStreams(process);
                process.destroy();
            }
        }
    }
    
    public static String getStreamMessage(InputStream inputStream) throws IOException {
        StringBuilder outputList = new StringBuilder();
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "GBK"))) {
            String outputLine;
            while ((outputLine = bufferedReader.readLine()) != null) {
                outputList.append(outputLine);
            }
        }
        return outputList.toString();
    }
    
    private static void closeProcessStreams(Process process) {
        try {
            process.getInputStream().close();
            process.getOutputStream().close();
            process.getErrorStream().close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    // TODO：移除多余
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
    
}
