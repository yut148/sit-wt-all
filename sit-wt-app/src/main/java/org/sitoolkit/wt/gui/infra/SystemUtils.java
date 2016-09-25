package org.sitoolkit.wt.gui.infra;

import java.util.ArrayList;
import java.util.List;

public class SystemUtils {

    private static boolean windows;
    private static boolean osx;

    static {
        // OS判定方法はorg.apache.commons.lang3.SystemUtilsを参照
        String osName = System.getProperty("os.name");
        windows = osName.startsWith("Windows");
        osx = osName.startsWith("Mac");
    }

    public static boolean isWindows() {
        return windows;
    }

    public static boolean isOsX() {
        return osx;
    }

    public static List<String> getBrowsers() {
        List<String> browsers = new ArrayList<>();

        browsers.add("firefox");
        browsers.add("chrome");

        if (isWindows()) {
            browsers.add("ie");
            browsers.add("egde");
        }

        if (isOsX()) {
            browsers.add("safari");
        }

        return browsers;
    }
}
