import { createContext, useContext, useMemo, useState } from "react";
import { DEFAULT_LANGUAGE } from "../utils/constants";

const AppContext = createContext(null);

export function AppProvider({ children }) {
  const [language, setLanguage] = useState(DEFAULT_LANGUAGE);

  const value = useMemo(
    () => ({
      language,
      setLanguage,
    }),
    [language]
  );

  return <AppContext.Provider value={value}>{children}</AppContext.Provider>;
}

export function useAppContext() {
  const context = useContext(AppContext);
  if (!context) {
    throw new Error("useAppContext must be used within AppProvider");
  }
  return context;
}
