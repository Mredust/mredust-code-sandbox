package com.mredust.codesandbox.my;

/**
 * @author <a href="https://github.com/Mredust">Mredust</a>
 */
public class Main {

}

class Solution {
    public static void main(String[] args) {
        Runtime r = Runtime.getRuntime();
        r.gc();// 计算内存前先垃圾回收一次
        long start = System.currentTimeMillis();// 开始Time
        long startMem = r.totalMemory(); // 开始Memory
        new String("1");//！！！！！被测的程序！！！！！
        long endMem = r.freeMemory(); // 末尾Memory
        long end = System.currentTimeMillis();// 末尾Time
        // 输出
        System.out.println("用时消耗: " + (end - start) + "ms");
        System.out.println("内存消耗: " + ((startMem - endMem) / 1024) + "KB");
    }
}


