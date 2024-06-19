package com.mredust.codesandbox.my;

/**
 * @author <a href="https://github.com/Mredust">Mredust</a>
 */
public class Main {
    public static void main(String[] args) {
        String path = new Main().getPath();
        System.out.println(path);
    }
    
    public String getPath() {
        return this.getClass().getClassLoader().getResource("sandbox.policy").getFile();
    }
    
}


