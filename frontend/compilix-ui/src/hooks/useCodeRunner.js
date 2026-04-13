import { useState } from "react";
import { executeCode } from "../services/api";
import { parseAIResponse } from "../services/aiService";
import { DEFAULT_LANGUAGE, DEFAULT_SNIPPETS } from "../utils/constants";
import { formatOutput } from "../utils/formatOutput";

function createEmptyAiResponse() {
  return {
    explanation: "",
    suggestedFix: "",
    timeComplexity: "",
    spaceComplexity: "",
    isCompilationError: false,
    hasError: false,
  };
}

export function useCodeRunner() {
  const [language, setLanguage] = useState(DEFAULT_LANGUAGE);
  const [code, setCode] = useState(DEFAULT_SNIPPETS[DEFAULT_LANGUAGE]);
  const [output, setOutput] = useState("// Output will appear here");
  const [error, setError] = useState("");
  const [aiResponse, setAIResponse] = useState(createEmptyAiResponse);
  const [isRunning, setIsRunning] = useState(false);

  const canRun = Boolean(code && code.trim());

  const onLanguageChange = (nextLanguage) => {
    setLanguage(nextLanguage);
    setCode(DEFAULT_SNIPPETS[nextLanguage] || "");
    setOutput("// Output will appear here");
    setError("");
    setAIResponse(createEmptyAiResponse());
  };

  const runCode = async () => {
    if (!canRun || isRunning) {
      return;
    }

    setIsRunning(true);
    setError("");
    setOutput("Running...");

    try {
      const response = await executeCode({ code, language });
      const ai = parseAIResponse(response);
      const inferredCompilationError =
        response.compilationError === true ||
        ai.isCompilationError ||
        /compilation error/i.test(response.explanation || ai.explanation || "");
      const hasExecutionError = Boolean(response.error || inferredCompilationError);

      const fallbackExplanation = response.compilationError
        ? "Compilation error found and auto-fix applied."
        : "Error detected while running the code.";

      const nextAiResponse = {
        ...ai,
        isCompilationError: inferredCompilationError,
        hasError: hasExecutionError,
        explanation: hasExecutionError
          ? ai.explanation || fallbackExplanation
          : ai.explanation,
        suggestedFix: hasExecutionError
          ? ai.suggestedFix || response.suggestedFix || response.fixedCode || code
          : ai.suggestedFix,
      };

      setAIResponse(nextAiResponse);

      if (response.error) {
        setError(formatOutput(response.error));
        setOutput("Execution finished with errors.");
      } else {
        setError("");
        setOutput(formatOutput(response.output));
      }
    } catch (requestError) {
      setError(formatOutput(requestError.message));
      setOutput("Execution request failed.");
    } finally {
      setIsRunning(false);
    }
  };

  const clearOutput = () => {
    setOutput("No output yet.");
    setError("");
  };

  return {
    language,
    code,
    output,
    error,
    aiResponse,
    isRunning,
    canRun,
    setCode,
    onLanguageChange,
    runCode,
    clearOutput,
  };
}
