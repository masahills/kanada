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
 * Client for interacting with Claude API to get context-aware readings for kanji.
 *
 * @author Masahiko Sato
 */
public class ClaudeClient implements LlmClient {
    private final String apiUrl;
    private final String apiKey;
    private final String model;
    private final HttpClient httpClient;
    private final LlmConfig.ClaudeConfig config;

    /**
     * Creates a new Claude client with custom settings.
     *
     * @param apiUrl The URL of the Claude API
     * @param apiKey The Claude API key
     * @param model  The model to use for inference
     */
    public ClaudeClient(String apiUrl, String apiKey, String model) {
        this.apiUrl = apiUrl;
        this.apiKey = apiKey;
        this.model = model;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(30))
                .build();
        try {
            this.config = LlmConfig.getInstance().getClaude();
        } catch (Exception e) {
            throw new RuntimeException("Failed to load Claude configuration", e);
        }
    }

    /**
     * Tests connection to Claude API.
     *
     * @return true if the connection is successful, false otherwise
     */
    @Override
    public boolean testConnection() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(apiUrl + "/models"))
                    .header("x-api-key", apiKey)
                    .header("anthropic-version", "2023-06-01")
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
    @Override
    public String getModel() {
        return model;
    }

    @Override
    public String selectBestReading(String kanji, List<String> possibleReadings, String context)
            throws IOException, InterruptedException {
        if (possibleReadings.size() <= 1) {
            return possibleReadings.isEmpty() ? "" : possibleReadings.get(0);
        }

        LlmConfig.ModelConfig modelConfig = config.models.get(model);
        String systemMessage = getConfigValue(modelConfig, m -> m.systemPrompt, config.systemPrompt);
        String template = getConfigValue(modelConfig, m -> m.userPromptTemplate, config.userPromptTemplate);

        String userMessage = template
                .replace("{kanji}", kanji)
                .replace("{context}", context.replace("\n", ""))
                .replace("{readings}", String.join(" / ", possibleReadings));

        String response = generateCompletion(systemMessage, userMessage);
        return parseResponse(response, possibleReadings);
    }

    private String generateCompletion(String systemMessage, String userMessage) throws IOException, InterruptedException {
        Gson gson = new Gson();

        ClaudeRequest request = new ClaudeRequest(
                model,
                1000,
                systemMessage,
                List.of(new Message("user", userMessage))
        );

        String requestBody = gson.toJson(request);

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl + "/messages"))
                .header("Content-Type", "application/json")
                .header("x-api-key", apiKey)
                .header("anthropic-version", "2023-06-01")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new IOException("API request failed with status code: " + response.statusCode());
        }

        return response.body();
    }

    private String parseResponse(String response, List<String> possibleReadings) {
        try {
            ClaudeResponse claudeResponse = new Gson().fromJson(response, ClaudeResponse.class);
            String content = claudeResponse.content.get(0).text.trim();

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

    private record ClaudeRequest(String model, int max_tokens, String system, List<Message> messages) {
    }

    private record Message(String role, String content) {
    }

    private record ClaudeResponse(List<Content> content) {
    }

    private record Content(String text) {
    }

    private static <T> T getConfigValue(LlmConfig.ModelConfig modelConfig, java.util.function.Function<LlmConfig.ModelConfig, T> getter, T defaultValue) {
        return modelConfig != null && getter.apply(modelConfig) != null ? getter.apply(modelConfig) : defaultValue;
    }
}