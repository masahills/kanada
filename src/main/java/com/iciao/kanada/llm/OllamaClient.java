/*
 * MIT License
 *
 * Copyright (C) 2025 Masahiko Sato
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.iciao.kanada.llm;

import com.google.gson.Gson;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Comparator;
import java.util.List;

/**
 * Client for interacting with Ollama API to get context-aware readings for kanji.
 *
 * @author Masahiko Sato
 */
public class OllamaClient implements LlmClient {
    private final String apiUrl;
    private final String model;
    private final HttpClient httpClient;
    private final LlmConfig.OllamaConfig config;

    /**
     * Creates a new Ollama client with default settings.
     */
    public OllamaClient(String model) {
        this("http://localhost:11434", model);
    }

    /**
     * Creates a new Ollama client with custom settings.
     *
     * @param apiUrl The URL of the Ollama API
     * @param model  The model to use for inference
     */
    public OllamaClient(String apiUrl, String model) {
        this.apiUrl = apiUrl;
        this.model = model;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
        try {
            this.config = LlmConfig.getInstance().getOllama();
        } catch (Exception e) {
            throw new RuntimeException("Failed to load Ollama configuration", e);
        }
    }

    /**
     * Tests connection to Ollama API.
     *
     * @return true if the connection is successful, false otherwise
     */
    public boolean testConnection() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(apiUrl + "/api/tags"))
                    .header("Content-Type", "application/json")
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            return response.statusCode() == 200;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Gets the model name being used.
     *
     * @return the model name
     */
    public String getModel() {
        return model;
    }

    /**
     * Select the most appropriate reading for a kanji word based on context.
     *
     * @param kanji            The kanji word to get readings for
     * @param possibleReadings List of possible readings
     * @param context          The surrounding text for context
     * @return The selected reading based on context by Generative AI
     * @throws IOException          If an I/O error occurs
     * @throws InterruptedException If the operation is interrupted
     */
    public String selectBestReading(String kanji, List<String> possibleReadings, String context)
            throws IOException, InterruptedException {
        if (possibleReadings.isEmpty()) {
            return "";
        }

        LlmConfig.ModelConfig modelConfig = config.models.get(model);
        String template = getConfigValue(modelConfig, m -> m.promptTemplate, config.promptTemplate);
            
        String prompt = template
            .replace("{kanji}", kanji)
            .replace("{context}", context.replace("\n", ""))
            .replace("{readings}", String.join(" / ", possibleReadings));

        String response = generateCompletion(prompt);
        return parseResponse(response, possibleReadings);
    }

    /**
     * Sends a request to the Ollama API and gets the completion.
     * <p>
     * If the specified model does not exist, throws an IOException with a helpful message.
     */
    private String generateCompletion(String prompt) throws IOException, InterruptedException {
        Gson gson = new Gson();

        OllamaRequest request = new OllamaRequest(model, prompt, false);
        String requestBody = gson.toJson(request);

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl + "/api/generate"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new IOException("API request failed with status code: " + response.statusCode() + ", body: " + response.body());
        }

        return response.body();
    }

    /**
     * Parses the LLM response to extract the best reading.
     */
    private String parseResponse(String response, List<String> possibleReadings) {
        try {
            OllamaResponse ollamaResponse = new Gson().fromJson(response, OllamaResponse.class);
            String content = ollamaResponse.response.trim();

            // Check longer readings first to avoid partial matches
            List<String> sortedReadings = possibleReadings.stream()
                    .sorted(Comparator.comparing(String::length).reversed())
                    .toList();

            for (String reading : sortedReadings) {
                if (content.contains(reading)) {
                    return reading;
                }
            }
        } catch (Exception e) {
            // Fall back to default on any error
        }

        return possibleReadings.get(0);
    }

    private record OllamaRequest(String model, String prompt, boolean stream) {
    }

    private record OllamaResponse(String response) {
    }

    private static <T> T getConfigValue(LlmConfig.ModelConfig modelConfig, java.util.function.Function<LlmConfig.ModelConfig, T> getter, T defaultValue) {
        return modelConfig != null && getter.apply(modelConfig) != null ? getter.apply(modelConfig) : defaultValue;
    }
}