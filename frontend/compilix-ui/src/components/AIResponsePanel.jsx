const formatCodeDisplay = (code) => {
  if (!code || typeof code !== "string") return code;

  const normalized = normalizeCodeStructure(code);
  const lines = normalized.split(/\r?\n/);
  let depth = 0;
  const formatted = [];

  for (const line of lines) {
    const trimmed = line.trim();

    if (!trimmed) {
      continue;
    }

    if (trimmed.startsWith("}")) {
      depth = Math.max(0, depth - 1);
    }

    formatted.push("  ".repeat(depth) + trimmed);

    if (trimmed.endsWith("{")) {
      depth++;
    }
  }

  return formatted.join("\n").replace(/\n{3,}/g, "\n\n");
};

const normalizeCodeStructure = (code) => {
  let out = "";
  let inDouble = false;
  let inSingle = false;
  let escaping = false;
  let parenDepth = 0;

  for (const c of code) {
    if (inDouble) {
      out += c;
      if (escaping) {
        escaping = false;
      } else if (c === "\\") {
        escaping = true;
      } else if (c === '"') {
        inDouble = false;
      }
      continue;
    }

    if (inSingle) {
      out += c;
      if (escaping) {
        escaping = false;
      } else if (c === "\\") {
        escaping = true;
      } else if (c === "'") {
        inSingle = false;
      }
      continue;
    }

    if (c === '"') {
      inDouble = true;
      out += c;
      continue;
    }

    if (c === "'") {
      inSingle = true;
      out += c;
      continue;
    }

    if (c === "{") {
      out += "{\n";
      continue;
    }

    if (c === "}") {
      out += "\n}\n";
      continue;
    }

    if (c === "(") {
      parenDepth += 1;
      out += c;
      continue;
    }

    if (c === ")") {
      parenDepth = Math.max(0, parenDepth - 1);
      out += c;
      continue;
    }

    if (c === ";") {
      out += ";";
      if (parenDepth === 0) {
        out += "\n";
      }
      continue;
    }

    out += c;
  }

  return out.replace(/\r/g, "");
};

const countJavaClasses = (code) => {
  if (!code || typeof code !== "string") return 0;
  const matches = code.match(/\bclass\s+[A-Za-z_][A-Za-z0-9_]*\b/g);
  return matches ? matches.length : 0;
};

function AIResponsePanel({ aiResponse, onApplySuggestedFix, currentCode, language }) {
  const {
    explanation,
    suggestedFix,
    timeComplexity,
    spaceComplexity,
    isCompilationError,
    hasError,
  } = aiResponse || {};

  const showErrorInsights = Boolean(
    (isCompilationError || hasError) && (explanation || suggestedFix)
  );
  const showComplexity = !hasError;

  const formattedCode = formatCodeDisplay(suggestedFix);
  const suggestedClassCount = countJavaClasses(formattedCode || suggestedFix);
  const currentClassCount = countJavaClasses(currentCode);
  const isPartialJavaFix =
    String(language || "").toLowerCase() === "java" &&
    currentClassCount > 1 &&
    suggestedClassCount > 0 &&
    suggestedClassCount < currentClassCount;
  const canApplyFix = Boolean((formattedCode || suggestedFix) && !isPartialJavaFix);

  return (
    <section className="panel panel-ai">
      <div className="panel-header panel-ai-header">
        <span>AI Insights</span>
      </div>
      <div className="ai-content">
        <div className="complexity-grid">
          <article className="metric-card">
            <h3>Time Complexity</h3>
            <p>{showComplexity ? timeComplexity || "-" : "-"}</p>
          </article>
          <article className="metric-card">
            <h3>Space Complexity</h3>
            <p>{showComplexity ? spaceComplexity || "-" : "-"}</p>
          </article>
        </div>

        {showErrorInsights ? (
          <div>
            <div className="suggested-fix-header">
              <h3>Suggested Fix</h3>
              <button
                type="button"
                className="apply-fix-button"
                onClick={() => onApplySuggestedFix?.(formattedCode || suggestedFix || "")}
                disabled={!canApplyFix}
                title="Apply AI suggested fix to editor"
              >
                Apply Fix
              </button>
            </div>
            <div className="fix-box">
              <pre className="fix-block">
                {formattedCode || "No suggested fix generated yet."}
              </pre>
            </div>
            {isPartialJavaFix ? (
              <p className="fix-warning">
                Suggested fix looks incomplete. Run again to get the full code before applying.
              </p>
            ) : null}
          </div>
        ) : null}

        {showErrorInsights ? (
          <div>
            <h3>{isCompilationError ? "Compilation Summary" : "Error Summary"}</h3>
            <div className="summary-block">
              <p>{explanation || "Error detected while running the code."}</p>
            </div>
          </div>
        ) : null}
      </div>
    </section>
  );
}

export default AIResponsePanel;