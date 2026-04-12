package com.rithish.autofix.service;

import com.rithish.autofix.model.ExecutionResult;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.concurrent.TimeUnit;

@Service
public class ExecutionService {

    // 🔥 MAIN ENTRY METHOD
    public ExecutionResult runCode(String code, String language) {

        if (language.equalsIgnoreCase("java")) {
            return runJavaCode(code);

        } else if (language.equalsIgnoreCase("python")) {
            return runPythonCode(code);

        } else if (language.equalsIgnoreCase("c")) {
            return runCCode(code);

        } else if (language.equalsIgnoreCase("cpp")) {
            return runCppCode(code);

        } else {
            return new ExecutionResult(false, null, "Unsupported language");
        }
    }

    // ================= JAVA =================
    public ExecutionResult runJavaCode(String code) {

        File javaFile = new File("Main.java");
        File classFile = new File("Main.class");

        try {
            FileWriter writer = new FileWriter(javaFile);
            writer.write(code);
            writer.close();

            Process compile = Runtime.getRuntime().exec("javac Main.java");
            compile.waitFor();

            String compileError = readErrorStream(compile);
            if (!compileError.isEmpty()) {
                return new ExecutionResult(false, null,
                        "Compilation Error:\n" + compileError);
            }

            Process run = Runtime.getRuntime().exec("java Main");

            boolean finished = run.waitFor(5, TimeUnit.SECONDS);

            if (!finished) {
                run.destroy();
                return new ExecutionResult(false, null,
                        "Error: Execution timed out");
            }

            String output = readProcessOutput(run);

            if (output.startsWith("Runtime Error")) {
                return new ExecutionResult(false, null, output);
            }

            return new ExecutionResult(true, output, null);

        } catch (Exception e) {
            return new ExecutionResult(false, null,
                    "System Error: " + e.getMessage());
        } finally {
            javaFile.delete();
            classFile.delete();
        }
    }

    // ================= PYTHON =================
    public ExecutionResult runPythonCode(String code) {

        File file = new File("Main.py");

        try {
            FileWriter writer = new FileWriter(file);
            writer.write(code);
            writer.close();

            Process run = Runtime.getRuntime().exec("python Main.py");

            boolean finished = run.waitFor(5, TimeUnit.SECONDS);

            if (!finished) {
                run.destroy();
                return new ExecutionResult(false, null,
                        "Error: Execution timed out");
            }

            String output = readProcessOutput(run);

            if (output.startsWith("Runtime Error")) {
                return new ExecutionResult(false, null, output);
            }

            return new ExecutionResult(true, output, null);

        } catch (Exception e) {
            return new ExecutionResult(false, null,
                    "System Error: " + e.getMessage());
        } finally {
            file.delete();
        }
    }

    // ================= C =================
    public ExecutionResult runCCode(String code) {

        File file = new File("Main.c");
        File exe = new File("Main.exe");

        try {
            FileWriter writer = new FileWriter(file);
            writer.write(code);
            writer.close();

            Process compile = Runtime.getRuntime().exec("gcc Main.c -o Main.exe");
            compile.waitFor();

            String compileError = readErrorStream(compile);
            if (!compileError.isEmpty()) {
                return new ExecutionResult(false, null,
                        "Compilation Error:\n" + compileError);
            }

            Process run = Runtime.getRuntime().exec("Main.exe");

            boolean finished = run.waitFor(5, TimeUnit.SECONDS);

            if (!finished) {
                run.destroy();
                return new ExecutionResult(false, null,
                        "Error: Execution timed out");
            }

            String output = readProcessOutput(run);

            if (output.startsWith("Runtime Error")) {
                return new ExecutionResult(false, null, output);
            }

            return new ExecutionResult(true, output, null);

        } catch (Exception e) {
            return new ExecutionResult(false, null,
                    "System Error: " + e.getMessage());
        } finally {
            file.delete();
            exe.delete();
        }
    }

    // ================= C++ =================
    public ExecutionResult runCppCode(String code) {

        File file = new File("Main.cpp");
        File exe = new File("Main.exe");

        try {
            FileWriter writer = new FileWriter(file);
            writer.write(code);
            writer.close();

            Process compile = Runtime.getRuntime().exec("g++ Main.cpp -o Main.exe");
            compile.waitFor();

            String compileError = readErrorStream(compile);
            if (!compileError.isEmpty()) {
                return new ExecutionResult(false, null,
                        "Compilation Error:\n" + compileError);
            }

            Process run = Runtime.getRuntime().exec("Main.exe");

            boolean finished = run.waitFor(5, TimeUnit.SECONDS);

            if (!finished) {
                run.destroy();
                return new ExecutionResult(false, null,
                        "Error: Execution timed out");
            }

            String output = readProcessOutput(run);

            if (output.startsWith("Runtime Error")) {
                return new ExecutionResult(false, null, output);
            }

            return new ExecutionResult(true, output, null);

        } catch (Exception e) {
            return new ExecutionResult(false, null,
                    "System Error: " + e.getMessage());
        } finally {
            file.delete();
            exe.delete();
        }
    }

    // 🔥 COMMON METHOD: Read Output + Errors
    private String readProcessOutput(Process process) throws IOException {

        BufferedReader output = new BufferedReader(
                new InputStreamReader(process.getInputStream())
        );

        BufferedReader error = new BufferedReader(
                new InputStreamReader(process.getErrorStream())
        );

        StringBuilder result = new StringBuilder();
        StringBuilder errorMsg = new StringBuilder();
        String line;

        while ((line = output.readLine()) != null) {
            result.append(line).append("\n");
        }

        while ((line = error.readLine()) != null) {
            errorMsg.append(line).append("\n");
        }

        if (errorMsg.length() > 0) {
            return "Runtime Error:\n" + errorMsg;
        }

        return result.toString();
    }

    // 🔥 COMMON METHOD: Read Compile Errors
    private String readErrorStream(Process process) throws IOException {

        BufferedReader error = new BufferedReader(
                new InputStreamReader(process.getErrorStream())
        );

        StringBuilder errorMsg = new StringBuilder();
        String line;

        while ((line = error.readLine()) != null) {
            errorMsg.append(line).append("\n");
        }

        return errorMsg.toString();
    }
}