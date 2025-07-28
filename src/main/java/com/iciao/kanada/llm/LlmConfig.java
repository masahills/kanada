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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

/**
 * Configuration loader for LLM settings.
 *
 * @author Masahiko Sato
 */
public class LlmConfig {
    private static LlmConfig instance;
    private final Config config;

    private LlmConfig() throws IOException {
        Gson gson = new Gson();
        
        // Load default config
        try (InputStream is = getClass().getResourceAsStream("/config.json")) {
            if (is == null) {
                throw new IOException("config.json not found in resources");
            }
            this.config = gson.fromJson(new InputStreamReader(is), Config.class);
        }
        
        // Override with local config if exists
        try (InputStream localIs = getClass().getResourceAsStream("/config.local.json")) {
            if (localIs != null) {
                Config localConfig = gson.fromJson(new InputStreamReader(localIs), Config.class);
                mergeConfig(localConfig);
            }
        }
    }
    
    private void mergeConfig(Config localConfig) {
        if (localConfig.openai != null) {
            if (localConfig.openai.defaultModel != null) config.openai.defaultModel = localConfig.openai.defaultModel;
            if (localConfig.openai.apiUrl != null) config.openai.apiUrl = localConfig.openai.apiUrl;
            if (localConfig.openai.systemPrompt != null) config.openai.systemPrompt = localConfig.openai.systemPrompt;
            if (localConfig.openai.userPromptTemplate != null) config.openai.userPromptTemplate = localConfig.openai.userPromptTemplate;
            if (localConfig.openai.models != null) config.openai.models.putAll(localConfig.openai.models);
        }
        if (localConfig.ollama != null) {
            if (localConfig.ollama.defaultModel != null) config.ollama.defaultModel = localConfig.ollama.defaultModel;
            if (localConfig.ollama.apiUrl != null) config.ollama.apiUrl = localConfig.ollama.apiUrl;
            if (localConfig.ollama.promptTemplate != null) config.ollama.promptTemplate = localConfig.ollama.promptTemplate;
            if (localConfig.ollama.models != null) config.ollama.models.putAll(localConfig.ollama.models);
        }
        if (localConfig.claude != null) {
            if (localConfig.claude.defaultModel != null) config.claude.defaultModel = localConfig.claude.defaultModel;
            if (localConfig.claude.apiUrl != null) config.claude.apiUrl = localConfig.claude.apiUrl;
            if (localConfig.claude.systemPrompt != null) config.claude.systemPrompt = localConfig.claude.systemPrompt;
            if (localConfig.claude.userPromptTemplate != null) config.claude.userPromptTemplate = localConfig.claude.userPromptTemplate;
        }
    }

    public static LlmConfig getInstance() throws IOException {
        if (instance == null) {
            instance = new LlmConfig();
        }
        return instance;
    }

    public OpenAiConfig getOpenAi() { return config.openai; }
    public OllamaConfig getOllama() { return config.ollama; }
    public ClaudeConfig getClaude() { return config.claude; }

    public static class Config {
        public OpenAiConfig openai;
        public OllamaConfig ollama;
        public ClaudeConfig claude;
    }

    public static class OpenAiConfig {
        public String defaultModel;
        public String apiUrl;
        public String systemPrompt;
        public String userPromptTemplate;
        public Map<String, ModelConfig> models;
    }

    public static class OllamaConfig {
        public String defaultModel;
        public String apiUrl;
        public String promptTemplate;
        public Map<String, ModelConfig> models;
    }

    public static class ClaudeConfig {
        public String defaultModel;
        public String apiUrl;
        public String systemPrompt;
        public String userPromptTemplate;
    }

    public static class ModelConfig {
        public String modelName;
        public String modelDescription;
        public String systemPrompt;
        public String userPromptTemplate;
        public String promptTemplate;
    }
}