export function formatOutput(output) {
  if (output === undefined || output === null) {
    return "";
  }

  if (typeof output === "string") {
    return output.trim() ? output : "No output.";
  }

  try {
    return JSON.stringify(output, null, 2);
  } catch {
    return String(output);
  }
}
