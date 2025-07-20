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
            "東京スカイツリーは日本の代表的な観光スポットで、多くの旅行者が訪れる人気の名所です。\n" +
                    "高さ634メートルの展望台からは、東京の素晴らしい街並みを360度見渡すことができます。\n" +
                    "特に夜のライトアップは幻想的で、多くの写真愛好家が素晴らしい風景を撮影しに訪れます。\n" +
                    "また、地上階にはショッピングモールやレストランもあり、一日中楽しめるスポットとなっています。\n\n";

    /**
     * Comprehensive kana chart for testing romanization
     */
    private static final String KANA_CHART =
            "// 基本ひらがな (Basic Hiragana)\n" +
                    "あ い う え お\nか き く け こ\nさ し す せ そ\nた ち つ て と\nな に ぬ ね の\n" +
                    "は ひ ふ へ ほ\nま み む め も\nや   ゆ   よ\nら り る れ ろ\nわ       を\n" +
                    "ん\n\n" +
                    "// 濁音・半濁音 (Dakuten and Handakuten)\n" +
                    "が ぎ ぐ げ ご\nざ じ ず ぜ ぞ\nだ ぢ づ で ど\nば び ぶ べ ぼ\nぱ ぴ ぷ ぺ ぽ\n\n" +
                    "// 拗音 (Youon - Contracted Sounds)\n" +
                    "きゃ  きゅ  きょ\nしゃ  しゅ  しょ\nちゃ  ちゅ  ちょ\nにゃ  にゅ  にょ\n" +
                    "ひゃ  ひゅ  ひょ\nみゃ  みゅ  みょ\nりゃ  りゅ  りょ\n" +
                    "ぎゃ  ぎゅ  ぎょ\nじゃ  じゅ  じょ\nびゃ  びゅ  びょ\nぴゃ  ぴゅ  ぴょ\n\n" +
                    "// 外来語音 (Foreign Sounds)\n" +
                    "ふぁ ふぃ  ふぇ ふぉ\nゔぁ ゔぃ ゔ ゔぇ ゔぉ\n\n" +
                    "// 基本カタカナ (Basic Katakana)\n" +
                    "ア イ ウ エ オ\nカ キ ク ケ コ\nサ シ ス セ ソ\nタ チ ツ テ ト\nナ ニ ヌ ネ ノ\n" +
                    "ハ ヒ フ ヘ ホ\nマ ミ ム メ モ\nヤ   ユ   ヨ\nラ リ ル レ ロ\nワ       ヲ\n" +
                    "ン\n\n" +
                    "// カタカナ拗音 (Katakana Youon)\n" +
                    "キャ  キュ  キョ\nシャ  シュ  ショ\nチャ  チュ  チョ\nニャ  ニュ  ニョ\n" +
                    "ヒャ  ヒュ  ヒョ\nミャ  ミュ  ミョ\nリャ  リュ  リョ\n\n" +
                    "// 外来語用カタカナ (Katakana for Foreign Words)\n" +
                    "ファ フィ  フェ フォ\nヴァ ヴィ ヴ ヴェ ヴォ\nウェ ウォ\nティ トゥ\nディ ドゥ\n\n" +
                    "// 単語サンプル (Word Samples)\n" +
                    "こんにちは さようなら ありがとう\nトウキョウ ニホン サクラ\nスマートフォン コンピューター インターネット";

    /**
     * Sample English text about Japan for width conversion examples
     */
    private static final String SAMPLE_ENGLISH_TEXT =
            "“Japan is known for its unique blend of traditional culture and modern innovation.”\n" +
                    "From ancient temples and gardens to cutting-edge technology and fashion trends,\n" +
                    "visitors can experience a fascinating contrast between old and new throughout the country.\n" +
                    "\n" +
                    "The four distinct seasons also offer different experiences for travelers year-round.";

    // Sample text with special characters for additional tests if needed
    private static final String SPECIAL_CHARACTERS =
            "\u008ex\u00a1x\u00a2x\u00a3x\u00a4x\u00a5x\u00a6x\u00a7x\u00a8x\u00adx";

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
        Kanada hiragana = new Kanada().toHiragana().withSpaces();
        Kanada katakana = new Kanada().toKatakana().withSpaces();
        Kanada fullwidth = new Kanada().toFullWidthAll().withSpaces();
        Kanada hankaku = new Kanada().toHankakuKatakana().withSpaces();

        // Demonstrate different conversions
        System.out.println("Wakatigaki (Word Separation):");
        convert(wakatigaki, SAMPLE_JAPANESE_TEXT);

        System.out.println("\nTo Hiragana:");
        convert(hiragana, SAMPLE_JAPANESE_TEXT);

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
            default:
                System.out.println("Unknown conversion mode: " + mode);
                System.out.println("Available modes: romaji, hiragana, katakana, wakati, fullwidth, halfwidth");
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

}
