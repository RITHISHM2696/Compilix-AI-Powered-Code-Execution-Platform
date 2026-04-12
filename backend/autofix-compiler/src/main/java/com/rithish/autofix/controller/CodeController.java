package com.rithish.autofix.controller;

import com.rithish.autofix.aiworks.AIService;
import com.rithish.autofix.model.*;
import com.rithish.autofix.service.ExecutionService;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/code")
public class CodeController {

    @Autowired
    private ExecutionService executionService;

    @Autowired
    private AIService aiService;

    //  MAIN PIPELINE
    @PostMapping("/run")
    public ExecutionResponse runCode(@RequestBody CodeRequest request) {

        ExecutionResult result = executionService.runCode(
                request.getCode(),
                request.getLanguage()
        );

        ExecutionResponse response = new ExecutionResponse();

        // ERROR CASE → ONE AI CALL
        if (!result.isSuccess()) {
            if (isCompilationError(result.getError())) {
            response.setCompilationError(true);
                Map<String, String> aiResult = aiService.analyzeAll(
                        request.getCode(),
                        result.getError(),
                        request.getLanguage()
                );

                response.setExplanation(summarizeCompilationError(result.getError()));
                String suggestedFix = aiResult.get("fixedCode");
                if (suggestedFix == null || suggestedFix.isBlank()) {
                    suggestedFix = request.getCode();
                }

                response.setSuggestedFix(suggestedFix);
                response.setTimeComplexity(aiResult.get("timeComplexity"));
                response.setSpaceComplexity(aiResult.get("spaceComplexity"));

                // Run corrected code and return the real JVM runtime error/output.
                ExecutionResult fixedResult = executionService.runCode(
                        suggestedFix,
                        request.getLanguage()
                );

                if (fixedResult.isSuccess()) {
                    response.setOutput(fixedResult.getOutput());
                } else {
                    response.setError(fixedResult.getError());
                }
            } else {
                response.setCompilationError(false);
                // For runtime/system errors, return code unchanged and skip AI fix.
                response.setError(result.getError());
                response.setSuggestedFix(request.getCode());
                response.setExplanation("Runtime error detected. Auto-fix is only applied for compilation errors.");
            }
        }

        // SUCCESS CASE → ONE AI CALL
        else {
            response.setCompilationError(false);

            response.setOutput(result.getOutput());

            Map<String, String> aiResult = aiService.analyzeAll(
                    request.getCode(),
                    "No Error",
                    request.getLanguage()
            );

            response.setTimeComplexity(aiResult.get("timeComplexity"));
            response.setSpaceComplexity(aiResult.get("spaceComplexity"));
        }

        return response;
    }

    private boolean isCompilationError(String error) {
        return error != null && error.startsWith("Compilation Error");
    }

    private String summarizeCompilationError(String error) {
        if (error == null || error.isBlank()) {
            return "Your code has a compile error.";
        }

        Pattern pattern = Pattern.compile("([^\\s:]+\\.java):(\\d+):\\s*error:\\s*(.+)");
        for (String line : error.split("\\R")) {
            Matcher matcher = pattern.matcher(line.trim());
            if (matcher.find()) {
                String file = matcher.group(1);
                String lineNo = matcher.group(2);
                String message = matcher.group(3);
                return toSimpleCompilationMessage(file, lineNo, message);
            }
        }

        return "Your code has a compile error. Please check the code syntax and try again.";
    }

    private String toSimpleCompilationMessage(String file, String lineNo, String rawMessage) {
        if (rawMessage == null || rawMessage.isBlank()) {
            return "There is a compile error in " + file + " at line " + lineNo + ".";
        }

        String message = rawMessage.trim();
        String lowerMessage = message.toLowerCase();

        if (lowerMessage.contains("';' expected")) {
            return "A semicolon (;) is missing in " + file + " at line " + lineNo + ".";
        }

        if (lowerMessage.contains("cannot find symbol")) {
            return "Java cannot find a variable, method, or class name in " + file + " at line " + lineNo + ".";
        }

        if (lowerMessage.contains("incompatible types")) {
            return "Two values have different data types in " + file + " at line " + lineNo + ".";
        }

        if (lowerMessage.contains("class, interface, enum, or record expected")) {
            return "There is extra or misplaced code in " + file + " at line " + lineNo + ".";
        }

        if (lowerMessage.contains("reached end of file while parsing")) {
            return "A closing bracket, parenthesis, or quote is missing before the end of " + file + ".";
        }

        return "Compile error in " + file + " at line " + lineNo + ": " + message;
    }

    // 🔥 RUN FIXED CODE (NO AI)
    @PostMapping("/run-fixed")
    public ExecutionResponse runFixedCode(@RequestBody CodeRequest request) {

        ExecutionResult result = executionService.runCode(
                request.getCode(),
                request.getLanguage()
        );

        ExecutionResponse response = new ExecutionResponse();

        if (!result.isSuccess()) {
            response.setCompilationError(false);
            response.setError(result.getError());
        } else {
            response.setCompilationError(false);
            response.setOutput(result.getOutput());
        }

        return response;
    }
}