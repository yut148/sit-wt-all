package org.sitoolkit.wt.gui.domain;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.sitoolkit.wt.gui.infra.FileIOUtils;
import org.sitoolkit.wt.gui.infra.MavenUtils;
import org.sitoolkit.wt.gui.infra.StrUtils;
import org.sitoolkit.wt.gui.infra.UnExpectedException;
import org.sitoolkit.wt.gui.infra.UnInitializedException;

public class SitWtRuntimeUtils {

    private static final Logger LOG = Logger.getLogger(SitWtRuntimeUtils.class.getName());

    private static String sitwtClasspath;

    public static String findTestedClasses(List<File> selectedFiles) {
        StringBuilder testedClases = new StringBuilder();
        for (File file : selectedFiles) {

            if (!file.isFile()) {
                continue;
            }

            String baseName = file.getName().replaceFirst("\\.xlsx$|\\\\.xls$|\\.csv$", "");

            if (baseName.equals(file.getName())) {
                continue;
            }

            if (testedClases.length() > 0) {
                testedClases.append(",");
            }

            testedClases.append(baseName);
            testedClases.append("IT");
        }

        return testedClases.toString();
    }

    public static List<String> buildCommand(String testedClasses, boolean isDebug,
            boolean isParallel, String browser, String baseUrl) {
        List<String> command = new ArrayList<>();

        command.add(MavenUtils.getCommand());
        command.add("verify");

        command.add("-T");
        command.add("1C");

        if (StrUtils.isNotEmpty(testedClasses)) {
            command.add("-Dit.test=" + testedClasses);
        }

        List<String> profiles = new ArrayList<>();
        if (isDebug) {
            profiles.add("debug");
        }
        if (isParallel) {
            profiles.add("parallel");
        }
        if (!profiles.isEmpty()) {
            command.add("-P" + String.join(",", profiles));
        }

        command.add("-Ddriver.type=" + browser);

        if (StrUtils.isNotEmpty(baseUrl)) {
            command.add("-DbaseUrl=" + baseUrl);
        }

        return command;
    }

    private static String loadClasspath(File pomFile) {

        List<String> command = new ArrayList<>();
        command.add(MavenUtils.getCommand());

        command.add("dependency:build-classpath");
        command.add("-f");
        command.add(pomFile.getAbsolutePath());

        ProcessBuilder builder = new ProcessBuilder(command);

        try {
            Process process = builder.start();
            LOG.log(Level.INFO, "process {0} starts {1}",
                    new Object[] { process, builder.command() });

            process.waitFor();
            return FileIOUtils.read(process.getInputStream());

        } catch (IOException | InterruptedException e) {
            throw new UnExpectedException(e);
        }
    }

    public static synchronized String loadSitWtClasspath(File pomFile) {

        if (sitwtClasspath == null) {
            String out = loadClasspath(pomFile);
            sitwtClasspath = filter(out, "[INFO] Dependencies classpath:", "[INFO]");
        }

        return sitwtClasspath;
    }

    private static String filter(String text, String startLine, String stopLine) {
        int start = text.indexOf(startLine) + startLine.length();
        int stop = text.indexOf(stopLine, start);

        return text.substring(start, stop).trim();
    }

    public static void main(String[] args) {
        MavenUtils.findAndInstall();
        System.out.println(loadSitWtClasspath(new File("target", "pom.xml")));
    }

    public static List<String> buildSampleCommand() {
        List<String> command = buildJavaCommand();
        command.add("org.sitoolkit.wt.app.sample.SampleManager");

        return command;
    }

    public static List<String> buildSingleTestCommand(List<File> scriptFiles, boolean isDebug,
            String browser, String baseUrl) {
        List<String> command = buildJavaCommand();
        addVmArgs(command, browser, baseUrl);

        command.add("-cp");
        command.add(getSitWtClasspath());

        command.add("org.sitoolkit.wt.app.test.TestRunner");

        command.add(StrUtils.join(scriptFiles));

        return command;
    }

    public static List<String> buildPage2ScriptCommand(String browser, String baseUrl) {
        List<String> command = buildJavaCommand();
        addVmArgs(command, browser, baseUrl);

        command.add("org.sitoolkit.wt.app.page2script.Page2Script");

        return command;
    }

    public static List<String> buildOpe2ScriptCommand() {
        List<String> command = buildJavaCommand();

        command.add("org.sitoolkit.wt.app.ope2script.FirefoxManager");

        return command;

    }

    private static List<String> buildJavaCommand() {
        List<String> command = new ArrayList<>();
        command.add("java");

        command.add("-cp");
        command.add(getSitWtClasspath());

        return command;
    }

    private static void addVmArgs(List<String> command, String browser, String baseUrl) {
        command.add("-Ddriver.type=" + browser);

        if (StrUtils.isNotEmpty(baseUrl)) {
            command.add("-DbaseUrl=" + baseUrl);
        }

    }

    private static String getSitWtClasspath() {
        if (sitwtClasspath == null) {
            throw new UnInitializedException();
        }
        return sitwtClasspath;
    }

}
