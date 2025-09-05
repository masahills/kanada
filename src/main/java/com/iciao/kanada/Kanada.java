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
package com.iciao.kanada;

import com.iciao.kanada.llm.LlmClient;
import com.iciao.kanada.llm.LlmClientFactory;
import com.iciao.kanada.maps.KanaMapping;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.charset.StandardCharsets;
import java.nio.charset.UnsupportedCharsetException;
import java.util.logging.Logger;

/**
 * Japanese text transliteration library for converting between Kanji, Hiragana, Katakana, and Romaji.
 *
 * <h3>Simple Usage:</h3>
 * <pre>{@code
 * String romaji = Kanada.toRomaji("日本語");     // "nihongo"
 * String hiragana = Kanada.toHiragana("日本語"); // "にほんご"
 * String katakana = Kanada.toKatakana("日本語");  // "ニホンゴ"
 * }</pre>
 *
 * <h3>Advanced Usage:</h3>
 * <pre>{@code
 * Kanada converter = Kanada.create()
 *     .toRomaji()
 *     .withSpaces()
 *     .upperCaseFirst();
 * String result = converter.process("東京都"); // "Tokyo To"
 * }</pre>
 *
 * @author Masahiko Sato
 */
public class Kanada {
    private static final Logger LOGGER = Logger.getLogger(Kanada.class.getName());

    protected int optionKanji;
    protected int optionHiragana;
    protected int optionKatakana;
    protected int optionWideAscii;
    protected int optionWideSymbol;
    protected int optionHalfKatakana;
    protected int optionAscii;
    protected int optionHalfSymbol;
    protected int optionBraille;

    protected boolean modeAddSpace = false;
    protected boolean modeUcFirst = false;
    protected boolean modeUcAll = false;
    protected boolean modeMacron = false;
    protected boolean modeShowAllYomi = false;
    protected boolean modeFurigana = false;
    protected LlmClient llmClient = null;
    protected KanaMapping.ConversionSystem conversionSystem = KanaMapping.ConversionSystem.MODIFIED_HEPBURN;

    protected char settingSeparatorChar = ' ';

    public Kanada() throws IOException {
        setParam(
                JMapper.AS_IS,
                JMapper.AS_IS,
                JMapper.AS_IS,
                JMapper.AS_IS,
                JMapper.AS_IS,
                JMapper.AS_IS,
                JMapper.AS_IS,
                JMapper.AS_IS,
                JMapper.AS_IS);
    }

    // Convenience static methods
    public static Kanada create() {
        try {
            return new Kanada();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String toRomaji(String text) {
        try {
            return new Kanada().toRomaji().withSpaces().process(text);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String toHiragana(String text) {
        try {
            return new Kanada().toHiragana().process(text);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String toKatakana(String text) {
        try {
            return new Kanada().toKatakana().process(text);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        if (args.length < 1 || args[0].equals("help")) {
            System.out.println("""
                    Usage:
                        java -jar kanada-1.0.0.jar <mode> [options] [<input file>]
                    
                    Modes:
                        romaji       Convert input text to romaji
                        hiragana     Convert input text to hiragana
                        katakana     Convert input text to katakana
                        parse        Parse input and apply options (-s, -r, -R)
                        help         Show this help
                    
                    Options:
                        -s           Insert spaces at segmentation points
                        -u           Capitalize each word (romaji mode)
                        -U           Uppercase all letters (romaji mode)
                        -m           Output romaji with macrons
                        -r           Add furigana readings for kanji words
                        -R           Add all possible readings for kanji words
                        -i <charset> Set input charset (Default: UTF-8)
                        -o <charset> Set output charset (Default: UTF-8)
                    
                    Options for AI-assisted conversion:
                        --openai     Use OpenAI for LLM service
                        --claude     Use Claude for LLM service
                        --ollama     Use Ollama for LLM service
                        --lmstudio   Use LM Studio for LLM service
                    
                    Input:
                        The program reads from standard input via piping or redirection.
                        You can also specify an input file as the last argument.
                    
                    Examples:
                        cat input.txt | java -jar kanada-1.0.0.jar parse -s
                        java -jar kanada-1.0.0.jar romaji < input.txt
                    
                    Note for dictionary files:
                    - Place the kakasidict dictionary file in the ./dictionary/Japanese directory
                      relative to the jar file. The kanwadict.dat file is generated at runtime.
                        .
                        ├── kanada-1.0.0.jar
                        └── dictionary
                            └── japanese
                                ├── kakasidict
                                └── kanwadict.dat
                    
                    - You can use your own dictionary files by specifying the -Ddictionaries property:
                      e.g. -Ddictionaries=SKK-JISYO.ML,SKK-JISYO.propernoun (comma-separated list)
                    
                    - Delete the kanwadict.dat file when loading new dictionary files.
                    
                    Note for AI-assisted conversion:
                    - Place the llm-config.json file in the /etc/kanada or ~/.kanada directory.
                    
                    - Set the provider and model names you want to use in the JSON key-value pairs.
                    
                    - llm-config.json file example:
                    {
                      "openai": {
                        "defaultModel": "gpt-5-mini"
                      },
                       "claude": {
                        "defaultModel": "claude-3-haiku-20240307"
                      },
                      "ollama": {
                        "defaultModel": "qwen3:1.7b"
                      },
                      "lmstudio": {
                        "defaultModel": "google/gemma-3-1b"
                      }
                    }
                    
                    - The API keys for OpenAI and Claude must be set as environment variables:
                      OPENAI_API_KEY and ANTHROPIC_API_KEY respectively.
                    
                    - Rate-limit and token-limit:
                      This library does not currently manage request rates.
                      Be aware of the rate limits and token limits of the LLM service you use.
                      Excessive requests may result in temporary bans or additional charges.
                    """);

            System.exit(1);
        }

        String mode = args[0];

        boolean spaces = false;
        boolean upperFirst = false;
        boolean upperAll = false;
        boolean macrons = false;
        boolean furigana = false;
        boolean allYomi = false;

        Charset inputCharset = StandardCharsets.UTF_8;
        Charset outputCharset = StandardCharsets.UTF_8;
        LlmClientFactory.LlmProvider llmProvider = null;
        String inputFilename = null;

        for (int i = 1; i < args.length; i++) {
            // If this is the last argument and not an option, treat as file name
            if (i == args.length - 1 && !args[i].startsWith("-")) {
                inputFilename = args[i];
                continue;
            }
            switch (args[i]) {
                // Formatting options
                case "-s" -> spaces = true;
                case "-u" -> {
                    upperFirst = true;
                    upperAll = false;
                }
                case "-U" -> {
                    upperFirst = false;
                    upperAll = true;
                }
                case "-m" -> macrons = true;
                case "-r" -> {
                    furigana = true;
                    allYomi = false;
                }
                case "-R" -> {
                    furigana = false;
                    allYomi = true;
                }
                // Charset options
                case "-i", "-o" -> {
                    if (i + 1 >= args.length || args[i + 1].startsWith("-")) {
                        System.err.println("Missing charset name for " + args[i] + " option");
                        System.exit(1);
                    }
                    String charsetName = args[++i];
                    try {
                        Charset charset = Charset.forName(charsetName);
                        if (args[i - 1].equals("-i")) {
                            inputCharset = charset;
                        } else {
                            outputCharset = charset;
                        }
                    } catch (IllegalCharsetNameException | UnsupportedCharsetException e) {
                        System.err.println("Unknown charset name for " + args[i - 1] + " option (" + e + ")");
                        System.exit(1);
                    }
                }
                // AI-assist options
                case "--openai" -> llmProvider = LlmClientFactory.LlmProvider.OPENAI;
                case "--claude" -> llmProvider = LlmClientFactory.LlmProvider.CLAUDE;
                case "--ollama" -> llmProvider = LlmClientFactory.LlmProvider.OLLAMA;
                case "--lmstudio" -> llmProvider = LlmClientFactory.LlmProvider.LMSTUDIO;

                default -> {
                    System.err.println("Unknown option: " + args[i]);
                    System.err.println("Available options: -s, -u, -U, -m, -r, -R, -i <charset>, -o <charset>");
                    System.err.println("AI-assist options: --openai, --claude, --ollama, --lmstudio");
                    System.exit(1);
                }
            }
        }

        Kanada converter = create();
        if (llmProvider != null) {
            LlmClient llmClient = LlmClientFactory.createClient(llmProvider);
            if (!llmClient.testConnection()) {
                System.err.println("Failed to connect to LLM server for " + llmProvider);
                System.exit(1);
            }
            converter.withLlmClient(llmClient);
            LOGGER.info("Using LLM provider: " + llmProvider + " (model: " + llmClient.getModel() + ")");
        }
        switch (mode) {
            case "romaji" -> converter.toRomaji();
            case "hiragana" -> converter.toHiragana();
            case "katakana" -> converter.toKatakana();
            case "parse" -> { /* parsing only */ }
            default -> {
                System.err.println("Unknown mode: " + mode);
                System.err.println("Available modes: romaji, hiragana, katakana, parse, help");
                System.exit(1);
            }
        }

        if (spaces) converter.withSpaces();
        if (upperFirst) converter.upperCaseFirst();
        if (upperAll) converter.upperCaseAll();
        if (macrons) converter.withMacrons();
        if (furigana) converter.withFurigana();
        if (allYomi) converter.withAllYomi();

        if (inputFilename != null) {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(args[args.length - 1]), inputCharset));
                 BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(System.out, outputCharset))) {
                converter.process(reader, writer);
                writer.flush();
            } catch (Exception e) {
                System.err.println("Error: " + e.getMessage());
                System.exit(1);
            }
        } else {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in, inputCharset));
                 BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(System.out, outputCharset))) {
                converter.process(reader, writer);
                writer.flush();
            } catch (Exception e) {
                System.err.println("Error: " + e.getMessage());
                System.exit(1);
            }
        }
    }

    // Builder pattern methods
    public Kanada toRomaji() {
        setParam(
                JMapper.TO_ASCII,
                JMapper.TO_ASCII,
                JMapper.TO_ASCII,
                JMapper.TO_ASCII,
                JMapper.TO_ASCII,
                JMapper.AS_IS,
                JMapper.TO_ASCII,
                JMapper.TO_ASCII,
                JMapper.TO_ASCII);
        // default to Modified Hepburn
        conversionSystem = KanaMapping.ConversionSystem.MODIFIED_HEPBURN;
        return this;
    }

    public Kanada toHiragana() {
        setParam(
                JMapper.TO_HIRAGANA,
                JMapper.AS_IS,
                JMapper.TO_HIRAGANA,
                JMapper.AS_IS,
                JMapper.AS_IS,
                JMapper.TO_HIRAGANA,
                JMapper.AS_IS,
                JMapper.AS_IS,
                JMapper.TO_HIRAGANA);
        return this;
    }

    public Kanada toKatakana() {
        setParam(
                JMapper.TO_KATAKANA,
                JMapper.TO_KATAKANA,
                JMapper.AS_IS,
                JMapper.AS_IS,
                JMapper.AS_IS,
                JMapper.TO_KATAKANA,
                JMapper.AS_IS,
                JMapper.AS_IS,
                JMapper.TO_KATAKANA);
        return this;
    }

    public Kanada toHankakuKatakana() {
        setParam(
                JMapper.TO_HALF_KATAKANA,
                JMapper.TO_HALF_KATAKANA,
                JMapper.TO_HALF_KATAKANA,
                JMapper.TO_ASCII,
                JMapper.TO_HALF_SYMBOL,
                JMapper.TO_HALF_KATAKANA,
                JMapper.AS_IS,
                JMapper.AS_IS,
                JMapper.TO_HALF_KATAKANA);
        return this;
    }

    public Kanada toFullWidthKana() {
        setParam(
                JMapper.AS_IS,
                JMapper.AS_IS,
                JMapper.AS_IS,
                JMapper.AS_IS,
                JMapper.TO_HIRAGANA,
                JMapper.AS_IS,
                JMapper.TO_KATAKANA,
                JMapper.TO_WIDE_SYMBOL,
                JMapper.TO_HIRAGANA);
        return this;
    }

    public Kanada toFullWidthAll() {
        setParam(
                JMapper.AS_IS,
                JMapper.AS_IS,
                JMapper.AS_IS,
                JMapper.AS_IS,
                JMapper.TO_HIRAGANA,
                JMapper.TO_WIDE_ASCII,
                JMapper.TO_KATAKANA,
                JMapper.TO_WIDE_SYMBOL,
                JMapper.TO_HIRAGANA);
        return this;
    }

    public Kanada toKanaTenji() {
        setParam(
                JMapper.TO_KANA_BRAILLE,
                JMapper.TO_KANA_BRAILLE,
                JMapper.TO_KANA_BRAILLE,
                JMapper.TO_KANA_BRAILLE,
                JMapper.TO_KANA_BRAILLE,
                JMapper.TO_KANA_BRAILLE,
                JMapper.TO_KANA_BRAILLE,
                JMapper.TO_KANA_BRAILLE,
                JMapper.AS_IS);
        conversionSystem = KanaMapping.ConversionSystem.KANA_BRAILLE;
        return this;
    }

    public Kanada withMacrons() {
        modeMacron = true;
        return this;
    }

    public Kanada withSpaces() {
        return this.withSpaces(' ');
    }

    public Kanada withSpaces(char separator) {
        modeAddSpace = true;
        settingSeparatorChar = separator;
        return this;
    }

    public Kanada withFurigana() {
        modeShowAllYomi = false;
        modeFurigana = true;
        return this;
    }

    public Kanada withAllYomi() {
        modeShowAllYomi = true;
        modeFurigana = false;
        return this;
    }

    public Kanada withLlmClient(LlmClient llmClient) {
        this.llmClient = llmClient;
        return this;
    }

    public Kanada upperCaseFirst() {
        modeUcFirst = true;
        modeUcAll = false;
        return this;
    }

    public Kanada upperCaseAll() {
        modeUcAll = true;
        modeUcFirst = false;
        return this;
    }

    public Kanada romanizationSystem(KanaMapping.ConversionSystem system) {
        conversionSystem = system;
        return this;
    }

    private void setParam(int paramKanji,
                          int paramHiragana,
                          int paramKatakana,
                          int paramWideAscii,
                          int paramWideSymbol,
                          int paramAscii,
                          int paramHalfKatakana,
                          int paramHalfSymbol,
                          int paramBraille) {
        optionKanji = paramKanji;
        optionHiragana = paramHiragana;
        optionKatakana = paramKatakana;
        optionWideAscii = paramWideAscii;
        optionWideSymbol = paramWideSymbol;
        optionAscii = paramAscii;
        optionHalfKatakana = paramHalfKatakana;
        optionHalfSymbol = paramHalfSymbol;
        optionBraille = paramBraille;
    }

    public void process(Reader reader, Writer writer) {
        if (reader == null || writer == null) {
            throw new IllegalArgumentException("Reader and Writer must not be null");
        }
        try {
            JWriter jWriter = new JWriter(this);
            KanjiParser parser = new KanjiParser(jWriter, llmClient);
            parser.parse(reader, writer);
        } catch (Exception e) {
            LOGGER.warning(e.getMessage());
        }
    }

    public String process(Reader reader) {
        if (reader == null) {
            throw new IllegalArgumentException("Reader must not be null");
        }
        StringWriter writer = new StringWriter();
        process(reader, writer);
        return writer.toString();
    }

    public String process(String str) {
        if (str == null) {
            return null;
        }
        StringReader reader = new StringReader(str);
        return process(reader);
    }
}
