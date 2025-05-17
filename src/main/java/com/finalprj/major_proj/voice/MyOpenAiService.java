package com.finalprj.major_proj.voice;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class MyOpenAiService {

    private static final Logger logger = LoggerFactory.getLogger(MyOpenAiService.class);

    @Value("${spring.ai.openai.api-key}")
    private String apiKey;

    @Value("${spring.ai.openai.base-url}")
    private String baseUrl;

    // Transcribe using faster-whisper via Python script
    public String transcribeAudio(File audioFile) {
        try {
            logger.info("Starting transcription for file: {}", audioFile.getAbsolutePath());

            ProcessBuilder builder = new ProcessBuilder("python", "src/main/resources/scripts/transcribe.py", audioFile.getAbsolutePath());
            builder.redirectErrorStream(true);  // Combine stdout and stderr
            Process process = builder.start();

// Initialize transcription StringBuilder to accumulate the output
            StringBuilder transcription = new StringBuilder();

// Log both the output and error streams
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            String line;

            while ((line = reader.readLine()) != null) {
                logger.debug("Python output: {}", line);
                transcription.append(line).append("\n");
            }

            while ((line = errorReader.readLine()) != null) {
                logger.error("Python error: {}", line);
            }

            int exitCode = process.waitFor();
            if (exitCode != 0) {
                logger.error("Python transcription script failed with exit code: {}", exitCode);
                return "Error: Transcription failed.";
            }

            return transcription.toString().trim();


        } catch (IOException e) {
            logger.error("IOException during audio transcription", e);
            return "Error: Failed to transcribe audio (IOException).";
        } catch (InterruptedException e) {
            logger.error("InterruptedException during audio transcription", e);
            Thread.currentThread().interrupt();
            return "Error: Transcription was interrupted.";
        }
    }

    // Query OpenRouter LLM API
    public String ask(String prompt) {
        try {
            HttpClient client = HttpClient.newHttpClient();

            String requestBody = """
                {
                  "model": "mistralai/mistral-7b-instruct",
                  "messages": [
                    {"role": "user", "content": "%s"}
                  ]
                }
            """.formatted(prompt);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/chat/completions"))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + apiKey)
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            logger.debug("OpenRouter response: {}", response.body());

            JSONObject json = new JSONObject(response.body());

            return json
                    .getJSONArray("choices")
                    .getJSONObject(0)
                    .getJSONObject("message")
                    .getString("content");

        } catch (IOException e) {
            logger.error("IOException while querying OpenRouter", e);
            return "Error: Failed to get response from OpenRouter (IOException).";
        } catch (InterruptedException e) {
            logger.error("InterruptedException while querying OpenRouter", e);
            Thread.currentThread().interrupt();
            return "Error: Request to OpenRouter interrupted.";
        } catch (JSONException e) {
            logger.error("JSONException while parsing OpenRouter response", e);
            return "Error: Malformed response from OpenRouter.";
        }
    }
}
