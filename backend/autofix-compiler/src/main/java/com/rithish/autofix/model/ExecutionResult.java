package com.rithish.autofix.model;

public class ExecutionResult {

    // True when code execution completed successfully.
    private boolean success;
    // Captured standard output from program execution.
    private String output;
    // Captured error output when execution fails.
    private String error;

    // Creates a result object with success status, output, and error details.
    public ExecutionResult(boolean success, String output, String error) {
        this.success = success;
        this.output = output;
        this.error = error;
    }

    // Returns whether execution was successful.
    public boolean isSuccess() { return success; }
    // Returns standard output.
    public String getOutput() { return output; }
    // Returns error output/message.
    public String getError() { return error; }
}