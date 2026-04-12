function OutputPanel({ output, error, isRunning, onClear }) {
  const content = isRunning ? "Running your code..." : error || output || "No output yet.";

  return (
    <section className="panel panel-output">
      <div className="panel-header panel-head-row">
        <span>Execution Output</span>
        <button type="button" className="panel-clear" onClick={onClear}>Clear</button>
      </div>
      <pre className={error ? "console-output error" : "console-output"}>{content}</pre>
    </section>
  );
}

export default OutputPanel;