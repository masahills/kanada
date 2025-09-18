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
 * Client for interacting with LM Studio API to get context-aware readings for kanji.
 * LM Studio provides an OpenAI-compatible API running locally.
 *
 * @author Masahiko Sato
 */
public class LMStudioClient implements LlmClient {
    private final String apiUrl;
    private final String model;
    private final HttpClient httpClient;
    private final LlmConfig.LMStudioConfig config;

    /**
     * Creates a new LM Studio client with default settings.
     */
    public LMStudioClient(String model) {
        this("http://127.0.0.1:1234/v1", model);
    }

    /**
     * Creates a new LM Studio client with custom settings.
     *
     * @param apiUrl The URL of the LM Studio API
     * @param model  The model to use for inference
     */
    public LMStudioClient(String apiUrl, String model) {
        this.apiUrl = apiUrl;
        this.model = model;
        this.httpClient = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)  // Need to force HTTP/1.1
                .connectTimeout(Duration.ofSeconds(30))
                .build();
        try {
            this.config = LlmConfig.getInstance().getLMStudio();
        } catch (Exception e) {
            throw new RuntimeException("Failed to load LM Studio configuration", e);
        }
    }

    /**
     * Tests connection to LM Studio API.
     *
     * @return true if the connection is successful, false otherwise
     */
    @Override
    public boolean testConnection() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(apiUrl + "/models"))
                    .header("Content-Type", "application/json")
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

    /**
     * Sends a request to the LM Studio API and gets the completion.
     */
    private String generateCompletion(String systemMessage, String userMessage) throws IOException, InterruptedException {
        Gson gson = new Gson();

        LMStudioRequest request = new LMStudioRequest(
                model,
                List.of(
                        new Message("system", systemMessage),
                        new Message("user", userMessage)
                )
        );

        String requestBody = gson.toJson(request);

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl + "/chat/completions"))
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
            LMStudioResponse lmStudioResponse = new Gson().fromJson(response, LMStudioResponse.class);
            String content = lmStudioResponse.choices.get(0).message.content.trim();

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

    private record LMStudioRequest(String model, List<Message> messages) {
    }

    private record Message(String role, String content) {
    }

    private record LMStudioResponse(List<Choice> choices) {
    }

    private record Choice(Message message) {
    }

    private static <T> T getConfigValue(LlmConfig.ModelConfig modelConfig, java.util.function.Function<LlmConfig.ModelConfig, T> getter, T defaultValue) {
        return modelConfig != null && getter.apply(modelConfig) != null ? getter.apply(modelConfig) : defaultValue;
    }
}