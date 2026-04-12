import { useState } from "react";
import Editor from "@monaco-editor/react";

function CodeEditor({ code, onCodeChange, language, theme }) {
  const [copyLabel, setCopyLabel] = useState("Copy");
  const [fontSize, setFontSize] = useState(14);

  const handleCopy = async () => {
    try {
      await navigator.clipboard.writeText(code || "");
      setCopyLabel("Copied");
      setTimeout(() => setCopyLabel("Copy"), 1200);
    } catch {
      setCopyLabel("Failed");
      setTimeout(() => setCopyLabel("Copy"), 1200);
    }
  };

  const increaseFont = () => {
    setFontSize((currentSize) => Math.min(currentSize + 1, 28));
  };

  const decreaseFont = () => {
    setFontSize((currentSize) => Math.max(currentSize - 1, 10));
  };

  return (
    <section className="panel panel-editor">
      <div className="panel-header panel-head-row">
        <span>Code Editor</span>
        <div className="panel-header-actions">
          <div className="panel-font-controls" aria-label="Font size controls">
            <button
              type="button"
              className="toolbar-button panel-font-btn"
              onClick={decreaseFont}
              disabled={fontSize <= 10}
            >
              A-
            </button>
            <button
              type="button"
              className="toolbar-button panel-font-btn"
              onClick={increaseFont}
              disabled={fontSize >= 28}
            >
              A+
            </button>
          </div>
          <button type="button" className="toolbar-button panel-copy" onClick={handleCopy}>
            {copyLabel}
          </button>
        </div>
      </div>
      <div className="editor-wrap">
        <Editor
          height="100%"
          language={language}
          theme={theme}
          value={code}
          onChange={(value) => onCodeChange(value || "")}
          options={{
            minimap: { enabled: false },
            fontSize,
            smoothScrolling: true,
            scrollBeyondLastLine: false,
            automaticLayout: true,
          }}
        />
      </div>
    </section>
  );
}

export default CodeEditor;