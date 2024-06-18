package com.mredust.codesandbox;

import com.mredust.codesandbox.model.dto.ExecuteCodeRequest;
import com.mredust.codesandbox.model.dto.ExecuteCodeResponse;
import com.mredust.codesandbox.service.CodeSandboxService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SpringBootTest
class CodeSandboxApplicationTests {
    
    
    @Resource
    private CodeSandboxService codeSandboxService;
    
    @Test
    void javaSandboxTest() {
        ExecuteCodeRequest executeCodeRequest = new ExecuteCodeRequest();
        List<String> list1 = Arrays.asList("1", "10");
        List<String> list2 = Arrays.asList("1 2", "10");
        List<String> list3 = Arrays.asList("1", "6 10");
        List<String> list4 = Arrays.asList("1 7", "10 20");
        List<List<String>> testCases = Arrays.asList(list1, list2, list3, list4);
        String code = "public class Main {\n" +
                "    \n" +
                "    public static void main(String[] args) throws Exception {\n" +
                "        int a = Integer.parseInt(args[0]);\n" +
                "        int b = Integer.parseInt(args[1]);\n" +
                "        String answer = String.format(\"输出结果: %d\", a + b);\n" +
                "        System.out.println(answer);\n" +
                "    }\n" +
                "    \n" +
                "}";
        executeCodeRequest.setCode(code);
        executeCodeRequest.setLanguage("java");
        
        for (List<String> testCase : testCases) {
            executeCodeRequest.setInputList(testCase);
            ExecuteCodeResponse executeCodeResponse = codeSandboxService.executeCode(executeCodeRequest);
            System.out.println(executeCodeResponse);
        }
    }
    
    @Test
    void pythonSandboxTest() {
        ExecuteCodeRequest executeCodeRequest = new ExecuteCodeRequest();
        List<String> list1 = Arrays.asList("1", "10");
        List<String> list2 = Arrays.asList("1 2", "10");
        List<String> list3 = Arrays.asList("1", "6 10");
        List<String> list4 = Arrays.asList("1 7", "10 20");
        List<List<String>> testCases = Arrays.asList(list1, list2, list3, list4);
        String code = "import sys\n" +
                "\n" +
                "if __name__ == \"__main__\":\n" +
                "    a = int(sys.argv[1])\n" +
                "    b = int(sys.argv[2])\n" +
                "    print(a + b)\n";
        executeCodeRequest.setCode(code);
        executeCodeRequest.setLanguage("python");
        
        for (List<String> testCase : testCases) {
            executeCodeRequest.setInputList(testCase);
            ExecuteCodeResponse executeCodeResponse = codeSandboxService.executeCode(executeCodeRequest);
            System.out.println(executeCodeResponse);
        }
    }
    
    @Test
    void pythonSandboxTest2() {
        ExecuteCodeRequest executeCodeRequest = new ExecuteCodeRequest();
        executeCodeRequest.setInputList(new ArrayList<>(0));
        executeCodeRequest.setCode("print('Hello, World!')");
        executeCodeRequest.setLanguage("python");
        ExecuteCodeResponse executeCodeResponse = codeSandboxService.executeCode(executeCodeRequest);
        System.out.println(executeCodeResponse);
    }
    
    public static void main(String[] args) {
        System.out.println(Integer.parseInt(args[0]) + Integer.parseInt(args[1]));
    }
}
