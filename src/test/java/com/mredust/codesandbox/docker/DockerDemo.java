package com.mredust.codesandbox.docker;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.PullImageCmd;
import com.github.dockerjava.api.command.PullImageResultCallback;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.Frame;
import com.github.dockerjava.api.model.Image;
import com.github.dockerjava.api.model.PullResponseItem;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientImpl;
import com.github.dockerjava.core.command.LogContainerResultCallback;
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient;
import com.github.dockerjava.transport.DockerHttpClient;
import org.springframework.beans.factory.annotation.Value;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author <a href="https://github.com/Mredust">Mredust</a>
 */
public class DockerDemo {
    
    @Value("${docker.host}")
    private static String host;
    
    @Value("${docker.port:2375}")
    private static String port;
    
    public static final String DOCKER_HOST = "tcp://192.168.91.131:2375";
    
    private static final DockerClient dockerClient;
    
    static {
        String dockerHost = java.lang.String.format("tcp://%s:%s", host, port);
        DefaultDockerClientConfig config = DefaultDockerClientConfig.createDefaultConfigBuilder().withDockerHost(DOCKER_HOST).build();
        DockerHttpClient httpClient = new ApacheDockerHttpClient.Builder()
                .dockerHost(config.getDockerHost())
                .sslConfig(config.getSSLConfig())
                .maxConnections(100)
                .connectionTimeout(Duration.ofSeconds(30))
                .responseTimeout(Duration.ofSeconds(45))
                .build();
        dockerClient = DockerClientImpl.getInstance(config, httpClient);
    }
    
    public static void main(String[] args) throws InterruptedException {
        // 镜像拉取
        String image = "nginx:latest";
        pullImage(image);
    }
    
    public static void main1(String[] args) throws InterruptedException {
        // 镜像拉取
        String image = "nginx:latest";
        pullImage(image);
        
        
        // 创建容器
        String containerName = "test-nginx";
        String containerId = createContainer(image, containerName);
        
        // 查看容器状态
        // List<Container> containerList = selectAllContainer();
        // for (Container container : containerList) {
        //     System.out.println(container);
        // }
        
        // 启动容器
        dockerClient.startContainerCmd(containerId).exec();
        
        
        // 查看日志
        @Deprecated
        LogContainerResultCallback logContainerResultCallback = new LogContainerResultCallback() {
            @Override
            public void onNext(Frame item) {
                System.out.println(item.getStreamType());
                System.out.println("日志：" + new String(item.getPayload()));
                super.onNext(item);
            }
        };
        dockerClient.logContainerCmd(containerId)
                .withStdErr(true)
                .withStdOut(true)
                .exec(logContainerResultCallback)
                .awaitCompletion();
        
        // 删除容器
        dockerClient.removeContainerCmd(containerId).withForce(true).exec();
        // // 删除镜像
        dockerClient.removeImageCmd(image).withForce(true).exec();
    }
    
    
    private static List<Container> selectAllContainer() {
        return dockerClient.listContainersCmd().withShowAll(true).exec();
    }
    
    
    private static String createContainer(String image, String containerName) {
        List<Container> containerList = dockerClient.listContainersCmd()
                .withShowAll(true)
                .withNameFilter(Collections.singletonList(containerName))
                .exec();
        if (!containerList.isEmpty()) {
            return containerList.get(0).getId();
        }
        return dockerClient
                .createContainerCmd(image)
                .withName(containerName)
                .withCmd("echo", "Hello Docker")
                .exec().getId();
    }
    
    private static void pullImage(String image) throws InterruptedException {
        pullImage(image, new PullImageResultCallback() {
            @Override
            public void onNext(PullResponseItem item) {
                super.onNext(item);
            }
        });
    }
    
    private static void pullImage(String image, PullImageResultCallback pullImageResultCallback) throws InterruptedException {
        List<Image> imageList = dockerClient.listImagesCmd().exec().stream().filter(i -> i.getRepoTags()[0].equals(image)).collect(Collectors.toList());
        if (imageList.isEmpty()) {
            PullImageCmd pullImageCmd = dockerClient.pullImageCmd(image);
            pullImageCmd.exec(pullImageResultCallback).awaitCompletion();
        }
    }
    
}
