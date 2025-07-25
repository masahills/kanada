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
 * Client for interacting with OpenAI API to get context-aware readings for kanji.
 *
 * @author Masahiko Sato
 */
public class OpenAiClient implements LlmClient {
    private final String apiUrl;
    private final String apiKey;
    private final String model;
    private final HttpClient httpClient;

    /**
     * Creates a new OpenAI client with custom settings.
     *
     * @param apiKey The OpenAI API key
     * @param model  The model to use for inference (e.g., "gpt-4")
     */
    public OpenAiClient(String apiKey, String model) {
        this("https://api.openai.com/v1", apiKey, model);
    }

    /**
     * Creates a new OpenAI client with custom settings.
     *
     * @param apiUrl The URL of the OpenAI API
     * @param apiKey The OpenAI API key
     * @param model  The model to use for inference
     */
    public OpenAiClient(String apiUrl, String apiKey, String model) {
        this.apiUrl = apiUrl;
        this.apiKey = apiKey;
        this.model = model;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(30))
                .build();
    }

    /**
     * Gets the best reading for a kanji word based on context.
     *
     * @param kanji            The kanji word to get readings for
     * @param possibleReadings List of possible readings
     * @param context          The surrounding text for context
     * @return The best reading based on context
     * @throws IOException          If an I/O error occurs
     * @throws InterruptedException If the operation is interrupted
     */
    @Override
    public String selectBestReading(String kanji, List<String> possibleReadings, String context)
            throws IOException, InterruptedException {
        if (possibleReadings.size() <= 1) {
            return possibleReadings.isEmpty() ? "" : possibleReadings.get(0);
        }
        String systemMessage = """
                あなたは日本語の文章の最適な読み方を文脈から判断するAIです。
                文章中の回答対象部分の読み方を、送り仮名を含めずに判断してください。
                与えられた選択肢からひらがな1語を選んでください。
                その他の説明は一切不要です。
                """;
        String userMessage = "回答対象: [" + kanji + "] （この部分のみの読み）\n\n" +
                "文章: " + context.replace("\n", "") + "\n" +
                "選択肢: " + String.join(" / ", possibleReadings) + "\n\n";
        String response = generateCompletion(systemMessage, userMessage);
        return parseResponse(response, possibleReadings);
    }

    /**
     * Sends a request to the OpenAI API and gets the completion.
     */
    private String generateCompletion(String systemMessage, String userMessage) throws IOException, InterruptedException {
        Gson gson = new Gson();

        OpenAiRequest request = new OpenAiRequest(
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
                .header("Authorization", "Bearer " + apiKey)
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new IOException("API request failed with status code: " + response.statusCode());
        }

        return response.body();
    }

    /**
     * Parses the LLM response to extract the best reading.
     */
    private String parseResponse(String response, List<String> possibleReadings) {
        try {
            OpenAiResponse openAiResponse = new Gson().fromJson(response, OpenAiResponse.class);
            String content = openAiResponse.choices.get(0).message.content.trim();

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

    private record OpenAiRequest(String model, List<Message> messages) {
    }

    private record Message(String role, String content) {
    }

    private record OpenAiResponse(List<Choice> choices) {
    }

    private record Choice(Message message) {
    }
}