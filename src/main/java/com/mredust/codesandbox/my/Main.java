package com.mredust.codesandbox.my;

import com.mredust.codesandbox.core.SimpleCodeSandboxFactory;
import com.mredust.codesandbox.model.dto.ExecuteResponse;
import com.mredust.codesandbox.model.enums.LanguageEnum;

import java.util.Arrays;
import java.util.List;

/**
 * @author <a href="https://github.com/Mredust">Mredust</a>
 */
public class Main {
    public static void main(String[] args) {
        String pythonCode = "class Solution:\n" +
                "    def twoSum(self, nums: List[int], target: int) -> List[int]:\n" +
                "        n = len(nums)\n" +
                "        for i in range(n):\n" +
                "            for j in range(i + 1, n):\n" +
                "                if nums[i] + nums[j] == target:\n" +
                // "                    aa = 1/0 \n" +
                "                    return [i, j]\n" +
                "\n" +
                "        return []";
        String javaCode = "class Solution {\n" +
                "    public int[] twoSum(int[] nums, int target) {\n" +
                "        int n = nums.length;\n" +
                "        for (int i = 0; i < n; ++i) {\n" +
                "            for (int j = i + 1; j < n; ++j) {\n" +
                "                if (nums[i] + nums[j] == target) {\n" +
                "                    return new int[]{i, j};\n" +
                "                }\n" +
                "            }\n" +
                "        }\n" +
                "        return new int[0];\n" +
                "    }\n" +
                "}";
        String[] var1 = {"3,2,4", "2,7,11,15", "3,3"};
        // String[] var1 = {"3", "2", "3"};
        String[] var2 = {"6", "9", "6"};
        List<String[]> testCaseList = Arrays.asList(var1, var2);
        // ExecuteResponse executeResponse1 = new SimpleCodeSandboxFactory().getCodeSandboxTemplate(LanguageEnum.JAVA).executeCode(javaCode, testCaseList);
        // System.out.println(executeResponse1);
        ExecuteResponse executeResponse2 = new SimpleCodeSandboxFactory().getCodeSandboxTemplate(LanguageEnum.PYTHON).executeCode(pythonCode, testCaseList);
        System.out.println(executeResponse2);
    }
    
    
}


