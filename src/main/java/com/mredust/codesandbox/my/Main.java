package com.mredust.codesandbox.my;

import com.mredust.codesandbox.core.template.TempTemplate;
import com.mredust.codesandbox.model.dto.ExecuteResponse;

import java.util.Arrays;
import java.util.List;

/**
 * @author <a href="https://github.com/Mredust">Mredust</a>
 */
public class Main {
    public static void main(String[] args) {
        String code = "from typing import List\n" +
                "\n" +
                "\n" +
                "class Solution:\n" +
                "    def twoSum(self, nums: List[int], target: int) -> List[int]:\n" +
                "        n = len(nums)\n" +
                "        for i in range(n):\n" +
                "            for j in range(i + 1, n):\n" +
                "                if nums[i] + nums[j] == target:\n" +
                "                    return [i, j]\n" +
                "\n" +
                "        return []\n" +
                "\n" +
                "\n" +
                "if __name__ == '__main__':\n" +
                "    import sys\n" +
                "\n" +
                "    print(Solution().twoSum(list(map(int, sys.argv[1].split(\",\"))), int(sys.argv[2])))\n";
        String[] var1 = {"3,2,4","2,7,11,15","3,3"};
        String[] var2 = {"6","9","6"};
        List<String[]> testCaseList = Arrays.asList(var1, var2);
        ExecuteResponse executeResponse = new TempTemplate().executeCode(code, testCaseList);
        System.out.println(executeResponse);
    }
    
    
}


