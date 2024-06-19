package com.mredust.codesandbox.my;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author <a href="https://github.com/Mredust">Mredust</a>
 */
public class Main {

}

class Solution {
    public static void main(String[] args) {
        // String input = "Exception in thread \\\"main\\\"java.lang.ArithmeticException: / by zero\\t at Solution.sum(Main.java:21)\\t at Main.main(Main.java:6)";
        String input = "F:\\\\mredust-code-sandbox\\\\tempcode\\\\9be52b94-54ed-40a2-8452-5b27b1d74c73\\\\Solution.java:4: 错误: 需要';'class Solution { public int sum(int a, int b)  {rturn a + b;}}                                                       ^F:\\\\mredust-code-sandbox\\\\tempcode\\\\9be52b94-54ed-40a2-8452-5b27b1d74c73\\\\Solution.java:4: 错误: 不是语句class Solution { public int sum(int a, int b)  {rturn a + b;}}                                                          ^2 个错误";

        String[] regex_list = {"java.lang.*?\\d+\\)", "\\w+\\.java:\\d+: 错误: .*?(?=(\\\\|$))"};
        for (String regex : regex_list) {
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(input);
            while (matcher.find()) {
                // 输出匹配到的部分
                String matched = matcher.group();
                System.out.println("Matched: " + matched);
            }
        }
    }
}


