import LanguageSelector from "./LanguageSelector";
import Loader from "./Loader";

function Navbar({ language, onLanguageChange, onRun, isRunning, canRun }) {
  return (
    <header className="topbar">
      <div>
        <h1 className="brand-title">Compilix Studio</h1>
        <p className="brand-subtitle">Compile, run, and inspect AI-guided fixes</p>
      </div>

      <div className="topbar-actions">
        <LanguageSelector value={language} onChange={onLanguageChange} />
        <button
          onClick={onRun}
          className="run-button"
          type="button"
          disabled={isRunning || !canRun}
        >
          {isRunning ? <Loader label="Running" /> : "Run Code"}
        </button>
      </div>
    </header>
  );
}

export default Navbar;