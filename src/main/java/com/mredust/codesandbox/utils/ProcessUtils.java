package com.mredust.codesandbox.utils;

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
    
    public static String processHandler(String cmd, Long[] time, Long[] memory) {
        Process process = null;
        try {
            Runtime runtime = Runtime.getRuntime();
            runtime.gc();
            StopWatch watch = new StopWatch();
            watch.start();
            long startMemory = runtime.totalMemory();
            process = runtime.exec(cmd);
            int execResult = process.waitFor();
            InputStream stream = (execResult == 0) ? process.getInputStream() : process.getErrorStream();
            String message = getStreamMessage(stream);
            long endMemory = runtime.freeMemory();
            watch.stop();
            long runTime = watch.getLastTaskTimeMillis();
            time[0] += runTime;
            memory[0] += (startMemory - endMemory);
            return message;
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
    
    
}
