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

/**
 * Factory for creating LLM clients based on configuration.
 *
 * @author Masahiko Sato
 */
public class LlmClientFactory {

    /**
     * LLM provider types supported by the factory.
     */
    public enum LlmProvider {
        OPENAI,
        CLAUDE,
        OLLAMA,
        LMSTUDIO
    }

    /**
     * Creates an LLM client based on the specified provider using config.json settings.
     *
     * @param provider The LLM provider to use
     * @return An LlmClient instance
     */
    public static LlmClient createClient(LlmProvider provider) {
        try {
            LlmConfig config = LlmConfig.getInstance();
            return switch (provider) {
                case OLLAMA -> {
                    LlmConfig.OllamaConfig ollamaConfig = config.getOllama();
                    yield new OllamaClient(ollamaConfig.apiUrl, ollamaConfig.defaultModel);
                }
                case OPENAI -> {
                    String apiKey = System.getenv("OPENAI_API_KEY");
                    if (apiKey == null || apiKey.isEmpty()) {
                        throw new IllegalStateException("OPENAI_API_KEY environment variable not set");
                    }
                    LlmConfig.OpenAiConfig openAiConfig = config.getOpenAi();
                    yield new OpenAiClient(openAiConfig.apiUrl, apiKey, openAiConfig.defaultModel);
                }
                case CLAUDE -> {
                    String apiKey = System.getenv("ANTHROPIC_API_KEY");
                    if (apiKey == null || apiKey.isEmpty()) {
                        throw new IllegalStateException("ANTHROPIC_API_KEY environment variable not set");
                    }
                    LlmConfig.ClaudeConfig claudeConfig = config.getClaude();
                    yield new ClaudeClient(claudeConfig.apiUrl, apiKey, claudeConfig.defaultModel);
                }
                case LMSTUDIO -> {
                    LlmConfig.LMStudioConfig lmStudioConfig = config.getLMStudio();
                    yield new LMStudioClient(lmStudioConfig.apiUrl, lmStudioConfig.defaultModel);
                }
            };
        } catch (IOException e) {
            throw new RuntimeException("Failed to load configuration", e);
        }
    }

    /**
     * Creates an Ollama client with custom settings.
     *
     * @param apiUrl The URL of the Ollama API
     * @param model  The model to use for inference
     * @return An OllamaClient instance
     */
    public static OllamaClient createOllamaClient(String apiUrl, String model) {
        return new OllamaClient(apiUrl, model);
    }

    /**
     * Creates an OpenAI client with custom settings.
     *
     * @param apiKey The OpenAI API key
     * @param model  The model to use for inference
     * @return An OpenAiClient instance
     */
    public static OpenAiClient createOpenAiClient(String apiKey, String model) {
        return new OpenAiClient(apiKey, model);
    }

    /**
     * Creates an LM Studio client with custom settings.
     *
     * @param apiUrl The URL of the LM Studio API
     * @param model  The model to use for inference
     * @return An LMStudioClient instance
     */
    public static LMStudioClient createLMStudioClient(String apiUrl, String model) {
        return new LMStudioClient(apiUrl, model);
    }
}