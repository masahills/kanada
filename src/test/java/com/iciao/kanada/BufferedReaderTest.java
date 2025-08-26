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

import java.io.BufferedReader;
import java.io.StringReader;

public class BufferedReaderTest {
    public static void main(String[] args) throws Exception {
        String testText = "日本語の文章です。漢字をひらがなに変換します。";

        // Test hiragana conversion
        System.out.println("=== Hiragana Conversion ===");
        System.out.println("String method: " + Kanada.toHiragana(testText));
        testBufferedReader(testText, Kanada.create().toHiragana());

        // Test katakana conversion
        System.out.println("\n=== Katakana Conversion ===");
        System.out.println("String method: " + Kanada.toKatakana(testText));
        testBufferedReader(testText, Kanada.create().toKatakana());

        // Test romaji conversion
        System.out.println("\n=== Romaji Conversion ===");
        System.out.println("String method: " + Kanada.toRomaji(testText));
        testBufferedReader(testText, Kanada.create().toRomaji());

        // Test romaji with spaces
        System.out.println("\n=== Romaji with Spaces ===");
        System.out.println("String method: " + Kanada.create().toRomaji().withSpaces().process(testText));
        testBufferedReader(testText, Kanada.create().toRomaji().withSpaces());
    }

    private static void testBufferedReader(String text, Kanada kanada) throws Exception {
        try (BufferedReader reader = new BufferedReader(new StringReader(text))) {
            JWriter writer = new JWriter(kanada);
            KanjiParser parser = new KanjiParser(writer);
            String result = parser.parse(reader);
            System.out.println("BufferedReader method: " + result);
        }
    }
}