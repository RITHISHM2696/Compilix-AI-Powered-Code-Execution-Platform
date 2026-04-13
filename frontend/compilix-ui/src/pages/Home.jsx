import { useRef, useState } from "react";
import LanguageSelector from "../components/LanguageSelector";
import CodeEditor from "../components/CodeEditor";
import OutputPanel from "../components/OutputPanel";
import AIResponsePanel from "../components/AIResponsePanel";
import { useCodeRunner } from "../hooks/useCodeRunner";

function Home() {
  const shellRef = useRef(null);
  const [editorTheme, setEditorTheme] = useState("vs-dark");
  const isDarkTheme = editorTheme === "vs-dark";

  const {
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
  } = useCodeRunner();

  const toggleTheme = () => {
    setEditorTheme((currentTheme) =>
      currentTheme === "vs-dark" ? "vs-light" : "vs-dark"
    );
  };

  const toggleFullscreen = async () => {
    if (!document.fullscreenElement) {
      await shellRef.current?.requestFullscreen();
      return;
    }

    await document.exitFullscreen();
  };

  return (
    <div className="app-shell" ref={shellRef}>
      <section className="workspace-toolbar" aria-label="Editor controls">
        <div className="toolbar-language">
          <LanguageSelector value={language} onChange={onLanguageChange} />
        </div>

        <div className="toolbar-controls">
          <button
            type="button"
            className="toolbar-button toolbar-icon-button"
            onClick={toggleFullscreen}
            title="Fullscreen"
          >
            ⛶
          </button>
          <button
            type="button"
            className="toolbar-button toolbar-icon-button"
            onClick={toggleTheme}
            title={isDarkTheme ? "Light Mode" : "Dark Mode"}
          >
            {isDarkTheme ? "☀" : "☾"}
          </button>
          <button
            type="button"
            className="toolbar-button toolbar-run"
            onClick={runCode}
            disabled={isRunning || !canRun}
            title="Run Code"
          >
            {isRunning ? (
              <span className="running-indicator">
                <span className="spinner" />
              </span>
            ) : (
              "Run"
            )}
          </button>
        </div>

        <div className="toolbar-logo">
          <span className="logo-text">Compilix</span>
        </div>
      </section>

      <main className="workspace-grid">
        <CodeEditor
          code={code}
          onCodeChange={setCode}
          language={language}
          theme={editorTheme}
        />

        <section className="stacked-panels">
          <OutputPanel
            output={output}
            error={error}
            isRunning={isRunning}
            onClear={clearOutput}
          />
          <AIResponsePanel
            aiResponse={aiResponse}
            currentCode={code}
            language={language}
            onApplySuggestedFix={(nextCode) => {
              if (typeof nextCode === "string" && nextCode.trim()) {
                setCode(nextCode);
              }
            }}
          />
        </section>
      </main>
    </div>
  );
}

export default Home;