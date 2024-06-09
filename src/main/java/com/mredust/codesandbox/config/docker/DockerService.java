package com.mredust.codesandbox.config.docker;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerCmd;
import com.github.dockerjava.api.command.PullImageCmd;
import com.github.dockerjava.api.command.PullImageResultCallback;
import com.github.dockerjava.api.model.*;
import com.github.dockerjava.core.command.LogContainerResultCallback;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Docker服务
 * @author <a href="https://github.com/Mredust">Mredust</a>
 */
@Component
public class DockerService {
    
    @Resource
    private DockerClient dockerClient;
    
    
    public void pullImage(String image) {
        pullImage(image, new PullImageResultCallback() {
            @Override
            public void onNext(PullResponseItem item) {
                super.onNext(item);
            }
        });
    }
    
    public void pullImage(String image, PullImageResultCallback pullImageResultCallback) {
        List<Image> imageList = dockerClient.listImagesCmd().exec().stream().filter(i -> i.getRepoTags()[0].equals(image)).collect(Collectors.toList());
        if (imageList.isEmpty()) {
            PullImageCmd pullImageCmd = dockerClient.pullImageCmd(image);
            try {
                pullImageCmd.exec(pullImageResultCallback).awaitCompletion();
            } catch (InterruptedException e) {
                throw new RuntimeException("pull Image error", e);
            }
        }
    }
    
    public String createContainer(String image) {
        return createContainer(image, new HostConfig());
    }
    
    public String createContainer(String image, HostConfig hostConfig) {
        String containerId;
        try (CreateContainerCmd containerCmd = dockerClient.createContainerCmd(image)) {
            containerId = containerCmd.withHostConfig(hostConfig)
                    .withNetworkDisabled(true)
                    .withAttachStdin(true)
                    .withAttachStdout(true)
                    .withAttachStderr(true)
                    .withTty(true)
                    .exec().getId();
        }
        dockerClient.startContainerCmd(containerId).exec();
        return containerId;
    }
    
    public List<Container> listContainer() {
        return dockerClient.listContainersCmd().withShowAll(true).exec();
    }
    
    public void startContainer(String containerId) {
        dockerClient.startContainerCmd(containerId).exec();
    }
    
    public String executeCmd(String[] cmd, String containerId) {
        return dockerClient.execCreateCmd(containerId)
                .withCmd(cmd)
                .withAttachStdin(true)
                .withAttachStdout(true)
                .withAttachStderr(true)
                .exec().getId();
    }
    
    public LogContainerResultCallback logContainerResultCallback(String containerId) throws InterruptedException {
        LogContainerResultCallback logContainerResultCallback = new LogContainerResultCallback() {
            @Override
            public void onNext(Frame item) {
                System.out.println(item.getStreamType());
                System.out.println("日志：" + new String(item.getPayload()));
                super.onNext(item);
            }
        };
        return dockerClient.logContainerCmd(containerId)
                .withStdErr(true)
                .withStdOut(true)
                .exec(logContainerResultCallback)
                .awaitCompletion();
    }
    
    public void removeContainer(String containerId) {
        dockerClient.removeContainerCmd(containerId).withForce(true).exec();
    }
    
    public void removeImage(String image) {
        dockerClient.removeImageCmd(image).exec();
    }
}
