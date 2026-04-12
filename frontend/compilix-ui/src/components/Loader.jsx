function Loader({ label = "Running" }) {
  return (
    <span className="loader" role="status" aria-live="polite">
      <span className="loader-dot" />
      {label}
    </span>
  );
}

export default Loader;
