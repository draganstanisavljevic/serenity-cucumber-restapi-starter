package com.orgname.qa.lib.helpers;

import java.lang.management.ManagementFactory;
import java.util.regex.Pattern;

public class DebuggerHelper {
    private static final Pattern debugPattern = Pattern.compile("-Xdebug|jdwp");

    /**
     * @return true if debugging pattern matches example
     */
    public static boolean isDebugging() {
        for (String arg : ManagementFactory.getRuntimeMXBean().getInputArguments()) {
            if (debugPattern.matcher(arg).find()) {
                return true;
            }
        }
        return false;
    }
}
