export const LANGUAGE_OPTIONS = [
  { value: "java", label: "Java" },
  { value: "python", label: "Python" },
  { value: "c", label: "C" },
  { value: "cpp", label: "C++" },
];

export const DEFAULT_LANGUAGE = "java";

export const DEFAULT_SNIPPETS = {
  java: "public class Main {\n  public static void main(String[] args) {\n    System.out.println(\"Hello from Java\");\n  }\n}",
  python: "print(\"Hello from Python\")",
  c: "#include <stdio.h>\n\nint main() {\n  printf(\"Hello from C\\n\");\n  return 0;\n}",
  cpp: "#include <iostream>\nusing namespace std;\n\nint main() {\n  cout << \"Hello from C++\" << endl;\n  return 0;\n}",
};
