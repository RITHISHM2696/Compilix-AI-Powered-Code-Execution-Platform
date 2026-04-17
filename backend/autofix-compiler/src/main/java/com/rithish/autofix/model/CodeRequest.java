package com.rithish.autofix.model;

public class CodeRequest {

    // Source code sent by the frontend for compilation/execution.
    private String code;
    // Programming language selected for the submitted code (e.g., java, python).
    private String language;

    // Returns the submitted source code.
    public String getCode() { return code; }
    // Returns the selected programming language.
    public String getLanguage() { return language; }
}