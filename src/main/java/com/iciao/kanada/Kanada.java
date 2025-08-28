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
import com.iciao.kanada.maps.KanaMapping;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.logging.Level;
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

    public Kanada() throws java.io.IOException {
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
        } catch (java.io.IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String toRomaji(String text) {
        try {
            return new Kanada().toRomaji().withSpaces().process(text);
        } catch (java.io.IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String toHiragana(String text) {
        try {
            return new Kanada().toHiragana().process(text);
        } catch (java.io.IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String toKatakana(String text) {
        try {
            return new Kanada().toKatakana().process(text);
        } catch (java.io.IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        if (args.length < 1 || args[0].equals("help")) {
            System.out.println("""
                    Usage:
                        java -jar kanada-1.0.0.jar <mode> [options]
                    
                    Modes:
                        romaji      Convert input text to romaji
                        hiragana    Convert input text to hiragana
                        katakana    Convert input text to katakana
                        parse       Parse input and apply options (-s, -r, -R)
                        help        Show this help
                    
                    Options:
                        -s          Insert spaces at segmentation points
                        -u          Capitalize each word (romaji mode)
                        -U          Uppercase all letters (romaji mode)
                        -m          Output romaji with macrons
                        -r          Add furigana readings for kanji words
                        -R          Add all possible readings for kanji words
                    
                    Input:
                        The program reads from standard input via piping or redirection.
                    
                    Examples:
                        cat input.txt | java -jar kanada-1.0.0.jar parse -s
                        java -jar kanada-1.0.0.jar romaji < input.txt
                    
                    Note:
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
                    
                    - AI-assisted conversion is not yet supported in the command-line interface.
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

        for (int i = 1; i < args.length; i++) {
            switch (args[i]) {
                case "-s" -> {
                    spaces = true;
                }
                case "-u" -> {
                    upperFirst = true;
                    upperAll = false;
                }
                case "-U" -> {
                    upperFirst = false;
                    upperAll = true;
                }
                case "-m" -> {
                    macrons = true;
                }
                case "-r" -> {
                    furigana = true;
                    allYomi = false;
                }
                case "-R" -> {
                    furigana = false;
                    allYomi = true;
                }
                default -> {
                    System.err.println("Unknown option: " + args[i]);
                    System.err.println("Available options: -s, -u, -U, -m, -r, -R");
                    System.exit(1);
                }
            }
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
            Kanada converter = create();
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

            String line;
            StringBuilder sb = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }
            System.out.println(converter.process(sb.toString()));

        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            System.exit(1);
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

    public String process(BufferedReader reader) {
        if (reader == null) {
            return null;
        }
        String parsedStr = null;
        try {
            JWriter writer = new JWriter(this);
            KanjiParser parser = new KanjiParser(writer, llmClient);
            parsedStr = parser.parse(reader);
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, e.getMessage());
        }
        return parsedStr;
    }

    public String process(String str) {
        if (str == null) {
            return null;
        }
        try (BufferedReader reader = new BufferedReader(new StringReader(str))) {
            return process(reader);
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, e.getMessage());
            return str;
        }
    }
}
