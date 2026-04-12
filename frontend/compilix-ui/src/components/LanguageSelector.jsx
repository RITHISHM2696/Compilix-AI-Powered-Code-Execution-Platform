import { LANGUAGE_OPTIONS } from "../utils/constants";

function LanguageSelector({ value, onChange }) {
  return (
    <div className="language-selector-wrap">
      <select
        value={value}
        onChange={(event) => onChange(event.target.value)}
        className="language-selector"
        aria-label="Choose language"
      >
        {LANGUAGE_OPTIONS.map((option) => (
          <option key={option.value} value={option.value}>
            {option.label}
          </option>
        ))}
      </select>
      <span className="language-selector-chevron" aria-hidden="true" />
    </div>
  );
}

export default LanguageSelector;
