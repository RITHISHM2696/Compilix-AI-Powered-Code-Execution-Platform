package com.rithish.autofix.service;

import com.rithish.autofix.model.ExecutionResult;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.concurrent.TimeUnit;

@Service
public class ExecutionService {

    private static final int EXECUTION_TIMEOUT_SECONDS = 5;

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

    public ExecutionResult runJavaCode(String code) {
        File javaFile = new File("Main.java");
        File classFile = new File("Main.class");

        try {
            writeCode(javaFile, code);

            Process compile = Runtime.getRuntime().exec("javac Main.java");
            compile.waitFor();

            String compileError = readErrorStream(compile);
            if (!compileError.isEmpty()) {
                return new ExecutionResult(false, null,
                        "Compilation Error:\n" + compileError);
            }

            return executeRunningProcess(Runtime.getRuntime().exec("java Main"));

        } catch (Exception e) {
            return new ExecutionResult(false, null,
                    "System Error: " + e.getMessage());
        } finally {
            javaFile.delete();
            classFile.delete();
        }
    }

    public ExecutionResult runPythonCode(String code) {
        File file = new File("Main.py");

        try {
            writeCode(file, code);
            return executeRunningProcess(Runtime.getRuntime().exec("python Main.py"));

        } catch (Exception e) {
            return new ExecutionResult(false, null,
                    "System Error: " + e.getMessage());
        } finally {
            file.delete();
        }
    }

    public ExecutionResult runCCode(String code) {
        File file = new File("Main.c");
        File exe = new File("Main.exe");

        try {
            writeCode(file, code);

            Process compile = Runtime.getRuntime().exec("gcc Main.c -o Main.exe");
            compile.waitFor();

            String compileError = readErrorStream(compile);
            if (!compileError.isEmpty()) {
                return new ExecutionResult(false, null,
                        "Compilation Error:\n" + compileError);
            }

            return executeRunningProcess(Runtime.getRuntime().exec("Main.exe"));

        } catch (Exception e) {
            return new ExecutionResult(false, null,
                    "System Error: " + e.getMessage());
        } finally {
            file.delete();
            exe.delete();
        }
    }

    public ExecutionResult runCppCode(String code) {
        File file = new File("Main.cpp");
        File exe = new File("Main.exe");

        try {
            writeCode(file, code);

            Process compile = Runtime.getRuntime().exec("g++ Main.cpp -o Main.exe");
            compile.waitFor();

            String compileError = readErrorStream(compile);
            if (!compileError.isEmpty()) {
                return new ExecutionResult(false, null,
                        "Compilation Error:\n" + compileError);
            }

            return executeRunningProcess(Runtime.getRuntime().exec("Main.exe"));

        } catch (Exception e) {
            return new ExecutionResult(false, null,
                    "System Error: " + e.getMessage());
        } finally {
            file.delete();
            exe.delete();
        }
    }

    private void writeCode(File file, String code) throws IOException {
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(code);
        }
    }

    private ExecutionResult executeRunningProcess(Process process) throws IOException, InterruptedException {
        boolean finished = process.waitFor(EXECUTION_TIMEOUT_SECONDS, TimeUnit.SECONDS);

        if (!finished) {
            process.destroy();
            return new ExecutionResult(false, null, "Error: Execution timed out");
        }

        String output = readProcessOutput(process);

        if (output.startsWith("Runtime Error")) {
            return new ExecutionResult(false, null, output);
        }

        return new ExecutionResult(true, output, null);
    }

    private String readProcessOutput(Process process) throws IOException {
        try (
                BufferedReader output = new BufferedReader(
                        new InputStreamReader(process.getInputStream())
                );
                BufferedReader error = new BufferedReader(
                        new InputStreamReader(process.getErrorStream())
                )
        ) {
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
    }

    private String readErrorStream(Process process) throws IOException {
        try (BufferedReader error = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
            StringBuilder errorMsg = new StringBuilder();
            String line;

            while ((line = error.readLine()) != null) {
                errorMsg.append(line).append("\n");
            }

            return errorMsg.toString();
        }
    }
}
