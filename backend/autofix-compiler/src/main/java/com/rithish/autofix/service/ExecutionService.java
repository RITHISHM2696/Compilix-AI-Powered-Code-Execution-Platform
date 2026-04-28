package com.rithish.autofix.service;

import com.rithish.autofix.model.ExecutionResult;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.concurrent.TimeUnit;

@Service
// Service that compiles/runs submitted source code and captures output.
// It writes temporary files, runs compilers/interpreters, then returns
// the program output or any compilation/runtime errors.
public class ExecutionService {

    // Max seconds to wait for a user program before timing out.
    private static final int EXECUTION_TIMEOUT_SECONDS = 5;

    // Decide which language-specific runner to use for the given code.
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

    // Save Java code to Main.java, compile it, and run Main.
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

    // Save Python code to Main.py and run it with the system python.
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

    // Save C code to Main.c, compile with gcc, then run the produced exe.
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

    // Save C++ code to Main.cpp, compile with g++, then run the exe.
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

    // Write the given source string to a file on disk.
    private void writeCode(File file, String code) throws IOException {
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(code);
        }
    }

    // Wait for a process to finish (with timeout) and collect output.
    // Returns success with stdout, or error with stderr text.
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

    // Read both stdout and stderr from a finished process. If stderr
    // has content, return it marked as a runtime error.
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

    // Read only the error stream from a process (used for compile errors).
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
