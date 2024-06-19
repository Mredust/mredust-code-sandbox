package com.mredust.codesandbox;

import com.mredust.codesandbox.model.dto.ExecuteRequest;
import com.mredust.codesandbox.service.CodeSandboxService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;

@SpringBootTest
class CodeSandboxApplicationTests {
    
    
    @Resource
    private CodeSandboxService codeSandboxService;
    
    @Test
    void javaSandboxTest() {
        ExecuteRequest executeRequest = new ExecuteRequest();
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
        executeRequest.setCode(code);
        executeRequest.setLanguage("java");
        
    }
    
    @Test
    void pythonSandboxTest() {
        ExecuteRequest executeRequest = new ExecuteRequest();
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
        executeRequest.setCode(code);
        executeRequest.setLanguage("python");
        
        
    }
    
    @Test
    void pythonSandboxTest2() {
        ExecuteRequest executeRequest = new ExecuteRequest();
        executeRequest.setCode("print('Hello, World!')");
        executeRequest.setLanguage("python");
    }
    
    public static void main(String[] args) {
        System.out.println(Integer.parseInt(args[0]) + Integer.parseInt(args[1]));
    }
}
