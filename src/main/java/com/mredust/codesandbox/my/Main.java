package com.mredust.codesandbox.my;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author <a href="https://github.com/Mredust">Mredust</a>
 */
public class Main {
    private static final String RUN_CMD = "java -Dfile.encoding=UTF-8 -cp ";
    // private static final String RUN_CMD = "java -Dfile.encoding=UTF-8 -cp %s %s";
    private static final String CLASS_NAME = "Main";
    public static void main(String[] args) {
        List<String> params = new ArrayList<>();
        // java -Dfile.encoding=UTF-8 -cp file.getParent() class_name var1 var2 ...
        //   String rumCmd = String.format(createRunCmd(size), file.getParent(), CLASS_NAME, var1);
        params.add("filePath");
        params.add(CLASS_NAME);
        for (int i = 0; i < 2; i++) {
            params.add("4");
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < params.size(); i++) {
            if (i > 0) {
                sb.append(" ");
            }
            sb.append(params.get(i));
        }
        String joinedParams = sb.toString();

        System.out.println(RUN_CMD + joinedParams);
    }
}
