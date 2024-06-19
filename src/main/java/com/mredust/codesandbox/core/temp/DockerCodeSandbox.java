package com.mredust.codesandbox.core.temp;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ArrayUtil;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.async.ResultCallback;
import com.github.dockerjava.api.command.StatsCmd;
import com.github.dockerjava.api.model.*;
import com.github.dockerjava.core.command.ExecStartResultCallback;
import com.mredust.codesandbox.config.docker.DockerService;
import com.mredust.codesandbox.model.dto.ExecuteCodeRequest;
import com.mredust.codesandbox.model.dto.ExecuteCodeResponse;
import com.mredust.codesandbox.model.dto.ExecuteResult;
import com.mredust.codesandbox.model.enums.ExecuteCodeStatusEnum;
import com.mredust.codesandbox.utils.ProcessUtils;
import org.springframework.util.StopWatch;

import javax.annotation.Resource;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * JavaDocker代码沙箱
 * todo Docker沙箱代码
 *
 * @author <a href="https://github.com/Mredust">Mredust</a>
 */
public abstract class DockerCodeSandbox {
    
    private static final String GLOBAL_CODE_DIR_PATH = "tempcode";
    
    private static final String JAVA_DEFAULT_CLASS_NAME = "Main.java";
    
    private static final String JAVA_COMPILE_CMD = "javac -encoding utf-8 %s";
    
    public static final String IMAGE_NAME = "openjdk:8-alpine";
    
    @Resource
    private DockerService dockerService;
    @Resource
    private DockerClient dockerClient;
    
    
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {
        List<String> inputList = new ArrayList<>();
        String code = executeCodeRequest.getCode();
        String path = String.format("%s%s%s", System.getProperty("user.dir"), File.separator, GLOBAL_CODE_DIR_PATH);
        File file = saveCodeToFile(path, code);
        compileCode(file);
        dockerService.pullImage(IMAGE_NAME);
        
        HostConfig hostConfig = new HostConfig();
        hostConfig.withMemory(100 * 1024 * 1024L);
        hostConfig.withCpuCount(1L);
        hostConfig.setBinds(new Bind(file.getParentFile().getAbsolutePath(), new Volume("/app")));
        String containerId = dockerService.createContainer(IMAGE_NAME, hostConfig);
        
        List<ExecuteResult> executeResultList = runCompileFile(containerId, inputList);
        ExecuteCodeResponse executeCodeResponse = getExecuteCodeResponse(executeResultList);
        deleteFile(file);
        return executeCodeResponse;
    }
    
    public abstract File saveCodeToFile(String path, String code);
    
    public ExecuteResult compileCode(File file) {
        String compileCmd = String.format(JAVA_COMPILE_CMD, file.getAbsolutePath());
        try {
            Process compileProcess = Runtime.getRuntime().exec(compileCmd);
            return ProcessUtils.processHandler(compileProcess);
        } catch (IOException e) {
            throw new RuntimeException("compile file error", e);
        }
    }
    
    
    public List<ExecuteResult> runCompileFile(String containerId, List<String> inputList) {
        List<ExecuteResult> executeResultList = new ArrayList<>();
        for (String inputArgs : inputList) {
            StopWatch stopWatch = new StopWatch();
            String[] inputArgsList = inputArgs.split(" ");
            String[] cmd = ArrayUtil.append(new String[]{"java", "-Dfile.encoding=UTF-8", "-cp", "/app", "Main"}, inputArgsList);
            
            String cmdId = dockerService.executeCmd(cmd, containerId);
            
            ExecuteResult executeResult = new ExecuteResult();
            final String[] message = {null};
            long time = 0L;
            final long[] maxMemory = {0L};
            ExecStartResultCallback execStartResultCallback = new ExecStartResultCallback() {
                @Override
                public void onNext(Frame frame) {
                    StreamType streamType = frame.getStreamType();
                    message[0] = new String(frame.getPayload());
                    super.onNext(frame);
                }
            };
            
            StatsCmd statsCmd = dockerClient.statsCmd(containerId);
            ResultCallback<Statistics> statisticsResultCallback = statsCmd.exec(new ResultCallback<Statistics>() {
                @Override
                public void onNext(Statistics statistics) {
                    System.out.println("内存占用：" + statistics.getMemoryStats().getUsage());
                    maxMemory[0] = Math.max(statistics.getMemoryStats().getUsage(), maxMemory[0]);
                }
                
                @Override
                public void close() throws IOException {
                
                }
                
                @Override
                public void onStart(Closeable closeable) {
                
                }
                
                @Override
                public void onError(Throwable throwable) {
                
                }
                
                @Override
                public void onComplete() {
                
                }
            });
            statsCmd.exec(statisticsResultCallback);
            
            try {
                stopWatch.start();
                dockerClient.execStartCmd(cmdId).exec(execStartResultCallback).awaitCompletion();
                stopWatch.stop();
                time = stopWatch.getLastTaskTimeMillis();
                statsCmd.close();
            } catch (InterruptedException e) {
                getErrorResponse(e);
            }
            executeResult.setTime(time);
            executeResult.setMemory(maxMemory[0]);
            executeResultList.add(executeResult);
        }
        return executeResultList;
    }
    
    
    protected ExecuteCodeResponse getExecuteCodeResponse(List<ExecuteResult> executeResultList) {
        ExecuteCodeResponse executeCodeResponse = new ExecuteCodeResponse();
        ArrayList<String> outputList = new ArrayList<>();
        long totalTime = 0L;
        long totalMemory = 0L;
        boolean compileFailed = false;
        for (ExecuteResult executeResult : executeResultList) {
            String message = executeResult.getMessage();
            if (ExecuteCodeStatusEnum.COMPILE_FAILED.getCode().equals(executeResult.getExecuteCode())) {
                executeCodeResponse.setStatusCode(ExecuteCodeStatusEnum.COMPILE_FAILED.getCode());
                executeCodeResponse.setMessage(ExecuteCodeStatusEnum.COMPILE_FAILED.getMsg());
                executeCodeResponse.setErrorMessage(message);
                outputList.clear();
                compileFailed = true;
                break;
            }
            outputList.add(executeResult.getMessage());
            Long runTime = executeResult.getTime();
            Long runMemory = executeResult.getMemory();
            totalTime += (runTime != null) ? runTime : 0L;
            totalMemory += (runMemory != null) ? runMemory : 0L;
        }
        if (!compileFailed) {
            executeCodeResponse.setStatusCode(ExecuteCodeStatusEnum.SUCCESS.getCode());
            executeCodeResponse.setMessage(ExecuteCodeStatusEnum.SUCCESS.getMsg());
        }
        executeCodeResponse.setTime(totalTime);
        executeCodeResponse.setMemory(totalMemory);
        executeCodeResponse.setOutputList(outputList);
        return executeCodeResponse;
    }
    
    public void deleteFile(File file) {
        String path = file.getParentFile().getAbsolutePath();
        if (FileUtil.exist(path)) {
            FileUtil.del(path);
        }
    }
    
    
    private ExecuteCodeResponse getErrorResponse(Throwable e) {
        ExecuteCodeResponse executeCodeResponse = new ExecuteCodeResponse();
        executeCodeResponse.setMessage(e.getMessage());
        executeCodeResponse.setStatusCode(2);
        return executeCodeResponse;
    }
    
    
}
