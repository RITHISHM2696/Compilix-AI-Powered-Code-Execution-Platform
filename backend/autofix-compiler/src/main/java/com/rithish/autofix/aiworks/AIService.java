package com.rithish.autofix.aiworks;
import org.springframework.http.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AIService {

    private static final String GROQ_URL = "https://api.groq.com/openai/v1/chat/completions";

    @Value("${groq.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @SuppressWarnings("unchecked")
    private String callGroq(String prompt) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiKey);

            Map<String, Object> body = new HashMap<>();
            body.put("model", "llama-3.1-8b-instant");
            body.put("temperature", 0);

            Map<String, String> responseFormat = new HashMap<>();
            responseFormat.put("type", "json_object");
            body.put("response_format", responseFormat);

            List<Map<String, String>> messages = new ArrayList<>();

            Map<String, String> message = new HashMap<>();
            message.put("role", "user");
            message.put("content", prompt);

            messages.add(message);
            body.put("messages", messages);

            HttpEntity<Map<String, Object>> request =
                    new HttpEntity<>(body, headers);

            ResponseEntity<Map<String, Object>> response =
                    restTemplate.postForEntity(
                            GROQ_URL,
                            request,
                            (Class<Map<String, Object>>) (Class<?>) Map.class
                    );

            Map<String, Object> responseBody = response.getBody();

            if (responseBody == null || !responseBody.containsKey("choices")) {
                return "AI Error: Invalid response from Groq";
            }

            List<Map<String, Object>> choices =
                    (List<Map<String, Object>>) responseBody.get("choices");

            Map<String, Object> choice = choices.get(0);
            Map<String, Object> messageMap =
                    (Map<String, Object>) choice.get("message");

            String raw = messageMap.get("content").toString();

            return cleanResponse(raw);

        } catch (Exception e) {
            e.printStackTrace();
            return "AI Error: " + e.getMessage();
        }
    }

    private String cleanResponse(String response) {
        return response
                .replace("```json", "")
                .replace("```java", "")
                .replace("```", "")
                .trim();
    }

    public Map<String, String> analyzeAll(String code, String error, String language) {
        String prompt = "Analyze the following " + language + " code.\n\n"
                + "Code:\n" + code +
                "\n\nError:\n" + error +
                "\n\nRules:\n"
                + "- Fix code correctly according to " + language + " syntax\n"
                + "- If Error starts with 'Compilation Error', fix ONLY compilation errors\n"
                + "- Keep algorithm and behavior unchanged unless a change is required to compile\n"
                + "- Do NOT optimize or remove loops/conditions unless required for compilation\n"
                + "- If Error is not a compilation error, return fixedCode exactly same as input code\n"
                + "- explanation must describe only the actual compiler error from Error text\n"
                + "- Use correct quotes (Java uses double quotes \" \" )\n"
                + "- The field fixedCode must contain ONLY valid " + language + " code\n"
                + "- Time and space complexity must be ONLY Big-O (like O(1))\n"
                + "- No explanations inside fields\n"
                + "\nReturn STRICT JSON:\n"
                + "{\n"
                + "  \"explanation\": \"...\",\n"
                + "  \"fixedCode\": \"...\",\n"
                + "  \"timeComplexity\": \"O(...) \",\n"
                + "  \"spaceComplexity\": \"O(...)\"\n"
                + "}";

        String rawResponse = callGroq(prompt);

        return parseAIResponse(rawResponse);
    }

    private Map<String, String> parseAIResponse(String response) {
        Map<String, String> result = emptyAIResult();

        if (response == null || response.isBlank()) {
            result.put("explanation", "AI returned empty response");
            return result;
        }

        if (response.startsWith("AI Error:")) {
            result.put("explanation", response);
            return result;
        }

        try {
            String cleaned = cleanResponse(response);

            Map<String, String> parsed = objectMapper.readValue(
                    cleaned,
                    new com.fasterxml.jackson.core.type.TypeReference<Map<String, String>>() {}
            );

            return mergeParsedValues(result, parsed);

        } catch (Exception firstParseError) {
            try {
                String jsonOnly = extractFirstJsonObject(cleanResponse(response));
                if (jsonOnly != null) {
                    Map<String, String> parsed = objectMapper.readValue(
                            jsonOnly,
                            new com.fasterxml.jackson.core.type.TypeReference<Map<String, String>>() {}
                    );
                    return mergeParsedValues(result, parsed);
                }
            } catch (Exception ignored) {
                // Fallback handled below.
            }

            result.put("explanation", "AI parsing error: " + firstParseError.getMessage());
        }

        return result;
    }

    private Map<String, String> emptyAIResult() {
        Map<String, String> result = new HashMap<>();
        result.put("explanation", "");
        result.put("fixedCode", "");
        result.put("timeComplexity", "");
        result.put("spaceComplexity", "");
        return result;
    }

    private Map<String, String> mergeParsedValues(Map<String, String> result, Map<String, String> parsed) {
        result.put("explanation", parsed.getOrDefault("explanation", ""));
        result.put("fixedCode", parsed.getOrDefault("fixedCode", ""));
        result.put("timeComplexity", parsed.getOrDefault("timeComplexity", ""));
        result.put("spaceComplexity", parsed.getOrDefault("spaceComplexity", ""));
        return result;
    }

    private String extractFirstJsonObject(String text) {
        if (text == null || text.isBlank()) {
            return null;
        }

        int start = text.indexOf('{');
        if (start < 0) {
            return null;
        }

        int depth = 0;
        boolean inString = false;
        boolean escaping = false;

        for (int i = start; i < text.length(); i++) {
            char c = text.charAt(i);

            if (inString) {
                if (escaping) {
                    escaping = false;
                } else if (c == '\\') {
                    escaping = true;
                } else if (c == '"') {
                    inString = false;
                }
                continue;
            }

            if (c == '"') {
                inString = true;
                continue;
            }

            if (c == '{') {
                depth++;
            } else if (c == '}') {
                depth--;
                if (depth == 0) {
                    return text.substring(start, i + 1);
                }
            }
        }

        return null;
    }
}
