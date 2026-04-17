package com.rithish.autofix.model;

public class ExecutionResponse {

    // Standard output produced by the executed program.
    private String output;
    // Error message returned during compilation or execution.
    private String error;
    // Human-readable explanation of what happened.
    private String explanation;
    // Suggested code change to fix detected compilation issues.
    private String suggestedFix;
    // Estimated time complexity of the submitted solution.
    private String timeComplexity;
    // Estimated space complexity of the submitted solution.
    private String spaceComplexity;
    // True if the failure happened at compile time, false otherwise.
    private boolean compilationError;

    // Gets program output.
    public String getOutput() { return output; }
    // Sets program output.
    public void setOutput(String output) { this.output = output; }

    // Gets the error message.
    public String getError() { return error; }
    // Sets the error message.
    public void setError(String error) { this.error = error; }

    // Gets the explanation text.
    public String getExplanation() { return explanation; }
    // Sets the explanation text.
    public void setExplanation(String explanation) { this.explanation = explanation; }

    // Gets the suggested code fix.
    public String getSuggestedFix() { return suggestedFix; }
    // Sets the suggested code fix.
    public void setSuggestedFix(String suggestedFix) { this.suggestedFix = suggestedFix; }

    // Gets estimated time complexity.
    public String getTimeComplexity() { return timeComplexity; }
    // Sets estimated time complexity.
    public void setTimeComplexity(String timeComplexity) { this.timeComplexity = timeComplexity; }

    // Gets estimated space complexity.
    public String getSpaceComplexity() { return spaceComplexity; }
    // Sets estimated space complexity.
    public void setSpaceComplexity(String spaceComplexity) { this.spaceComplexity = spaceComplexity; }

    // Returns whether this response represents a compilation error.
    public boolean isCompilationError() { return compilationError; }
    // Marks whether this response represents a compilation error.
    public void setCompilationError(boolean compilationError) { this.compilationError = compilationError; }
}