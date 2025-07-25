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

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
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

        String prompt = buildPrompt(kanji, possibleReadings, context);
        String response = generateCompletion(prompt);
        return parseResponse(response, possibleReadings);
    }

    /**
     * Builds a prompt for the LLM to determine the best reading.
     */
    private String buildPrompt(String kanji, List<String> possibleReadings, String context) {
        // Probably get better results by prompting in English for LLMs with fewer parameters.
        StringBuilder prompt = new StringBuilder();
        prompt.append("You are an AI that teaches how to read Japanese sentences.\n")
                .append("Choose the most appropriate reading of [").append(kanji).append("] for the context of the following sentence.\n")
                .append("Select one from the options.\n\n")
                .append("Sentence: ").append(context.replace("\n", "")).append("\n")
                .append("Options: ").append(String.join(" / ", possibleReadings)).append("\n\n")
                .append("Do not explain. Just show the answer in hiragana.\n");
        return prompt.toString();
    }

    /**
     * Sends a request to the Ollama API and gets the completion.
     * <p>
     * If the specified model does not exist, throws an IOException with a helpful message.
     */
    private String generateCompletion(String prompt) throws IOException, InterruptedException {
        // Ollama API expects raw prompt string, so escape only necessary characters
        String requestBody = String.format(
                "{\"model\":\"%s\",\"prompt\":\"%s\",\"stream\":false}",
                model,
                prompt.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n")
        );

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl + "/api/generate"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            // Check for model not found error and provide a helpful message
            if (response.body() != null && response.body().contains("model '")) {
                throw new IOException("Ollama API error: " + response.body());
            }
            throw new IOException("API request failed with status code: " + response.statusCode() + ", body: " + response.body());
        }

        return response.body();
    }

    /**
     * Parses the LLM response to extract the best reading.
     */
    private String parseResponse(String response, List<String> possibleReadings) {
        // Simple parsing logic - find the first reading that appears in the response
        // In a production environment, you might want more sophisticated parsing
        //System.out.println(response);
        for (String reading : possibleReadings) {
            if (response.contains(reading)) {
                return reading;
            }
        }

        // Default to first reading if no match found
        return possibleReadings.get(0);
    }
}