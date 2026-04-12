const RAW_BASE_URL = import.meta.env.VITE_API_BASE_URL || "";
const API_PREFIX = "/api/code";

function normalizeBaseUrl(url) {
  if (!url) {
    return "";
  }
  return url.endsWith("/") ? url.slice(0, -1) : url;
}

function buildUrl(path) {
  return `${normalizeBaseUrl(RAW_BASE_URL)}${API_PREFIX}${path}`;
}

async function postJson(path, payload) {
  const response = await fetch(buildUrl(path), {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify(payload),
  });

  if (!response.ok) {
    const text = await response.text();
    throw new Error(text || `Request failed with status ${response.status}`);
  }

  return response.json();
}

export function executeCode(payload) {
  return postJson("/run", payload);
}

export function runFixedCode(payload) {
  return postJson("/run-fixed", payload);
}

export const runCode = executeCode;