package com.mredust.codesandbox.utils;

import com.mredust.codesandbox.exception.CompilationException;
import org.springframework.util.StopWatch;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;


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
            long startMemory = getUsedMemory(runtime);
            process = runtime.exec(cmd);
            boolean flag = process.waitFor(4L, TimeUnit.SECONDS);
            if (!flag) {
                process.destroyForcibly();
                throw new CompilationException("运行超时");
            }
            int execResult = process.exitValue();
            InputStream stream = (execResult == 0) ? process.getInputStream() : process.getErrorStream();
            String message = getStreamMessage(stream);
            long endMemory = getUsedMemory(runtime);
            watch.stop();
            long runTime = watch.getLastTaskTimeMillis();
            time[0] += runTime;
            memory[0] += (endMemory - startMemory);
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
    
    
    private static long getUsedMemory(Runtime runtime) {
        return runtime.totalMemory() - runtime.freeMemory();
    }
    
}
