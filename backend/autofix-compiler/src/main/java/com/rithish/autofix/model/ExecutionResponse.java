package com.rithish.autofix.model;

public class ExecutionResponse {

    private String output;
    private String error;
    private String explanation;
    private String suggestedFix;
    private String timeComplexity;
    private String spaceComplexity;
    private boolean compilationError;

    public String getOutput() { return output; }
    public void setOutput(String output) { this.output = output; }

    public String getError() { return error; }
    public void setError(String error) { this.error = error; }

    public String getExplanation() { return explanation; }
    public void setExplanation(String explanation) { this.explanation = explanation; }

    public String getSuggestedFix() { return suggestedFix; }
    public void setSuggestedFix(String suggestedFix) { this.suggestedFix = suggestedFix; }

    public String getTimeComplexity() { return timeComplexity; }
    public void setTimeComplexity(String timeComplexity) { this.timeComplexity = timeComplexity; }

    public String getSpaceComplexity() { return spaceComplexity; }
    public void setSpaceComplexity(String spaceComplexity) { this.spaceComplexity = spaceComplexity; }

    public boolean isCompilationError() { return compilationError; }
    public void setCompilationError(boolean compilationError) { this.compilationError = compilationError; }
}