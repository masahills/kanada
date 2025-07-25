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
package com.iciao.kanada.examples;

import com.iciao.kanada.Kanada;
import com.iciao.kanada.llm.LlmClient;
import com.iciao.kanada.llm.LlmClientFactory;


/**
 * Example usage of Kanada library demonstrating various text conversion features.
 * <p>
 * This class provides examples of:
 * - Converting Japanese text to Hiragana, Katakana, and Romaji
 * - Adding spaces between words (wakati-gaki)
 * - Converting between full-width and half-width characters
 * - Applying different capitalization styles
 */
public class KanadaExample {

    /**
     * Sample Japanese text about Tokyo Skytree for conversion examples
     */
    private static final String SAMPLE_JAPANESE_TEXT =
            """
                    東京スカイツリーは日本の代表的な観光スポットで、多くの旅行者が訪れる人気の名所です。
                    高さ634メートルの展望台からは、東京の素晴らしい街並みを360度見渡すことができます。
                    特に夜のライトアップは幻想的で、多くの写真愛好家が素晴らしい風景を撮影しに訪れます。
                    また、地上階にはショッピングモールやレストランもあり、一日中楽しめるスポットとなっています。
                    """;

    /**
     * Comprehensive kana chart for testing romanization
     */
    private static final String KANA_CHART =
            """
                    // 基本ひらがな (Basic Hiragana)
                    あ い う え お
                    か き く け こ
                    さ し す せ そ
                    た ち つ て と
                    な に ぬ ね の
                    は ひ ふ へ ほ
                    ま み む め も
                    や   ゆ   よ
                    ら り る れ ろ
                    わ       を
                    ん
                    
                    // 濁音・半濁音 (Dakuten and Handakuten)
                    が ぎ ぐ げ ご
                    ざ じ ず ぜ ぞ
                    だ ぢ づ で ど
                    ば び ぶ べ ぼ
                    ぱ ぴ ぷ ぺ ぽ
                    
                    // 拗音 (Youon - Contracted Sounds)
                    きゃ  きゅ  きょ
                    しゃ  しゅ  しょ
                    ちゃ  ちゅ  ちょ
                    にゃ  にゅ  にょ
                    ひゃ  ひゅ  ひょ
                    みゃ  みゅ  みょ
                    りゃ  りゅ  りょ
                    ぎゃ  ぎゅ  ぎょ
                    じゃ  じゅ  じょ
                    びゃ  びゅ  びょ
                    ぴゃ  ぴゅ  ぴょ
                    
                    // 外来語音 (Foreign Sounds)
                    ふぁ ふぃ  ふぇ ふぉ
                    ゔぁ ゔぃ ゔ ゔぇ ゔぉ
                    
                    // 基本カタカナ (Basic Katakana)
                    ア イ ウ エ オ
                    カ キ ク ケ コ
                    サ シ ス セ ソ
                    タ チ ツ テ ト
                    ナ ニ ヌ ネ ノ
                    ハ ヒ フ ヘ ホ
                    マ ミ ム メ モ
                    ヤ   ユ   ヨ
                    ラ リ ル レ ロ
                    ワ       ヲ
                    ン
                    
                    // カタカナ拗音 (Katakana Youon)
                    キャ  キュ  キョ
                    シャ  シュ  ショ
                    チャ  チュ  チョ
                    ニャ  ニュ  ニョ
                    ヒャ  ヒュ  ヒョ
                    ミャ  ミュ  ミョ
                    リャ  リュ  リョ
                    
                    // 外来語用カタカナ (Katakana for Foreign Words)
                    ファ フィ  フェ フォ
                    ヴァ ヴィ ヴ ヴェ ヴォ
                    ウェ ウォ
                    ティ トゥ
                    ディ ドゥ
                    
                    // 単語サンプル (Word Samples)
                    こんにちは さようなら ありがとう
                    トウキョウ ニホン サクラ
                    スマートフォン コンピューター インターネット
                    """;

    /**
     * Sample English text about Japan for width conversion examples
     */
    private static final String SAMPLE_ENGLISH_TEXT =
            """
                    “Japan is known for its unique blend of traditional culture and modern innovation.”
                    From ancient temples and gardens to cutting-edge technology and fashion trends,
                    visitors can experience a fascinating contrast between old and new throughout the country.
                    
                    The four distinct seasons also offer different experiences for travelers year-round.
                    """;

    private static LlmClient llmClient = null;

    public static void main(String[] args) throws Exception {
        // Display menu and get user choice
        if (args.length > 0) {
            // Process command line arguments if provided
            processCommandLineArgs(args);
        } else {
            // Run demo examples
            runAllExamples();
        }
    }

    /**
     * Run all example conversions to demonstrate Kanada functionality
     *
     * @throws java.io.IOException if there is an error initializing Kanada
     */
    private static void runAllExamples() throws java.io.IOException {
        System.out.println("===== Kanada Library Examples =====\n");

        // Basic conversions with Japanese news text
        System.out.println("===== Basic Japanese Text Conversions =====\n");
        System.out.println("Original Text:");
        System.out.println(SAMPLE_JAPANESE_TEXT);
        System.out.println();

        // Create converters with different settings
        Kanada romaji = new Kanada().toRomaji().withSpaces().withMacrons();
        Kanada wakatigaki = new Kanada().withSpaces();
        Kanada wakatiallyomi = new Kanada().withSpaces().withAllYomi();
        Kanada wakatifurigana = new Kanada().withSpaces().withFurigana();
        Kanada hiragana = new Kanada().toHiragana().withSpaces();
        Kanada katakana = new Kanada().toKatakana().withSpaces();
        Kanada fullwidth = new Kanada().toFullWidthAll().withSpaces();
        Kanada hankaku = new Kanada().toHankakuKatakana().withSpaces();

        initLlmClient();
        Kanada hiraganallm = Kanada.create().toHiragana().withSpaces().withLlmClient(llmClient);

        System.out.println("Wakatigaki (Word Segmentation):");
        convert(wakatigaki, SAMPLE_JAPANESE_TEXT);

        System.out.println("\nWakatigaki with Furigana:");
        convert(wakatifurigana, SAMPLE_JAPANESE_TEXT);

        System.out.println("\nWakatigaki with all Yomi:");
        convert(wakatiallyomi, SAMPLE_JAPANESE_TEXT);

        System.out.println("\nTo Hiragana:");
        convert(hiragana, SAMPLE_JAPANESE_TEXT);

        System.out.println("\nTo Hiragana with LLM assist:");
        convert(hiraganallm, SAMPLE_JAPANESE_TEXT);
        System.out.println();

        System.out.println("\nTo Katakana:");
        convert(katakana, SAMPLE_JAPANESE_TEXT);

        System.out.println("\nTo Hankaku Katakana (Half-width):");
        convert(hankaku, SAMPLE_JAPANESE_TEXT);

        System.out.println("\nTo Romaji (First Letter Capitalized):");
        convert(romaji.upperCaseFirst(), SAMPLE_JAPANESE_TEXT);

        System.out.println("\nTo Romaji (All Caps):");
        convert(romaji.upperCaseAll(), SAMPLE_JAPANESE_TEXT);

        // Kana chart examples
        System.out.println("\n\n===== Kana Chart Romanization =====\n");
        System.out.println("Converting Kana Chart to Romaji:");
        convert(romaji, KANA_CHART);

        // Width conversion examples
        System.out.println("\n\n===== Width Conversion Examples =====\n");
        System.out.println("Original English Text:");
        System.out.println(SAMPLE_ENGLISH_TEXT);
        System.out.println("\nConverted to Full Width:");
        convert(fullwidth, SAMPLE_ENGLISH_TEXT);
    }

    /**
     * Process command line arguments for specific conversions
     *
     * @throws java.io.IOException if there is an error initializing Kanada
     */
    private static void processCommandLineArgs(String[] args) throws java.io.IOException {
        String mode = args[0].toLowerCase();
        String text = args.length > 1 ? args[1] : SAMPLE_JAPANESE_TEXT;

        switch (mode) {
            case "romaji":
                convert(new Kanada().toRomaji().withSpaces(), text);
                break;
            case "hiragana":
                convert(new Kanada().toHiragana(), text);
                break;
            case "katakana":
                convert(new Kanada().toKatakana(), text);
                break;
            case "wakati":
                convert(new Kanada().withSpaces(), text);
                break;
            case "fullwidth":
                convert(new Kanada().toFullWidthAll(), text);
                break;
            case "halfwidth":
                convert(new Kanada().toHankakuKatakana(), text);
                break;
            case "llm":
                initLlmClient();
                convert(new Kanada().toHiragana().withSpaces().withLlmClient(llmClient), text);
                break;
            default:
                System.out.println("Unknown conversion mode: " + mode);
                System.out.println("Available modes: romaji, hiragana, katakana, wakati, fullwidth, halfwidth, llm");
        }
    }

    /**
     * Convert text using the specified Kanada converter and measure execution time
     *
     * @param converter The Kanada converter to use
     * @param text      The text to convert
     */
    private static void convert(Kanada converter, String text) {
        // Measure conversion time
        long startTime = System.currentTimeMillis();
        String result = converter.process(text);
        long endTime = System.currentTimeMillis();
        long executionTime = endTime - startTime;

        // Print result with execution time
        System.out.println("Execution time: " + executionTime + " ms");
        System.out.println(result);
    }

    private static void initLlmClient() {
        if (llmClient != null) {
            return;
        }
        System.out.println("Setting up an LLM client...");
        try {
            // Create LLM client and test connection
            llmClient = LlmClientFactory.createClient(LlmClientFactory.LlmProvider.OLLAMA);

            // Test connection if it's an OllamaClient
            if (llmClient instanceof com.iciao.kanada.llm.OllamaClient ollamaClient) {
                System.out.println("Testing Ollama connection...");
                System.out.println("Using model: " + ollamaClient.getModel());

                if (ollamaClient.testConnection()) {
                    System.out.println("✓ Ollama connection successful");
                } else {
                    System.out.println("✗ Ollama connection failed");
                    System.out.println("Make sure Ollama is running: ollama serve");
                    return;
                }
                System.out.println();
            }

        } catch (Exception e) {
            System.out.println("LLM setup failed: " + e.getMessage());
        }
    }
}
