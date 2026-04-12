function pickFirstNonEmpty(...values) {
  for (const value of values) {
    if (typeof value === "string" && value.trim()) {
      return value;
    }
  }
  return "";
}

export function parseAIResponse(apiResponse) {
  const root = apiResponse || {};
  const nested = root.aiResponse || root.ai || root.analysis || {};

  return {
    explanation: pickFirstNonEmpty(root.explanation, nested.explanation, root.reason),
    suggestedFix: pickFirstNonEmpty(
      root.suggestedFix,
      root.fixedCode,
      nested.suggestedFix,
      nested.fixedCode
    ),
    timeComplexity: pickFirstNonEmpty(
      root.timeComplexity,
      nested.timeComplexity,
      root.complexityTime
    ),
    spaceComplexity: pickFirstNonEmpty(
      root.spaceComplexity,
      nested.spaceComplexity,
      root.complexitySpace
    ),
    isCompilationError: Boolean(root.compilationError ?? nested.compilationError),
  };
}
