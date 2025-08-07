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

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class KanadaTest {

    private Kanada romaji;
    private Kanada hiragana;
    private Kanada katakana;

    @Before
    public void setUp() throws Exception {
        romaji = new Kanada().toRomaji();
        hiragana = new Kanada().toHiragana();
        katakana = new Kanada().toKatakana();
    }

    @Test
    public void testBasicHiraganaConversion() throws Exception {
        String result = hiragana.process("漢字");
        assertNotNull(result);
        assertEquals("かんじ", result);
    }

    @Test
    public void testBasicKatakanaConversion() throws Exception {
        String result = katakana.process("漢字");
        assertNotNull(result);
        assertEquals("カンジ", result);
    }

    @Test
    public void testBasicRomajiConversion() throws Exception {
        String result = romaji.process("漢字");
        assertNotNull(result);
        assertEquals("kanji", result);
    }

    @Test
    public void testNullInput() throws Exception {
        assertNull(romaji.process(null));
    }

    @Test
    public void testEmptyInput() throws Exception {
        assertEquals("", romaji.process(""));
    }

    @Test
    public void testBrailleInput() throws Exception {
        String text = """
                せんしゅう、GINZA 8びるにあるAppleすとあで「iPhone 16」を159,800えん（ぜいこみ）でこうにゅうしました。""";
        String tenji = """
                ⠻⠴⠈⠹⠉⠰⠀⠰⠠⠠⠛⠊⠝⠵⠁⠀⠼⠓⠐⠧⠙⠇⠁⠙⠰⠠⠁⠏⠏⠇⠑⠤⠹⠞⠁⠐⠟⠤⠰⠊⠠⠏⠓⠕⠝⠑⠀⠼⠁⠋⠤⠔⠼⠁⠑⠊⠄⠓⠚⠚⠤⠋⠴⠶⠐⠻⠃⠪⠷⠶⠐⠟⠪⠉⠈⠍⠉⠳⠵⠳⠕⠲\u2800\u2800""";
        assertEquals(text, hiragana.process(tenji));
    }

    @Test
    public void testBrailleInputSpecials() throws Exception {
        String text = """
                しぇ じぇ ちぇ つぁ つぇ つぉ
                ふぁ ふぃ ふぇ ふぉ てぃ でぃ でゅ
                いぇ うぃ うぇ うぉ くぁ くぃ くぇ くぉ ぐぁ つぃ
                ゔぁ ゔぃ ゔぇ ゔぉ とぅ どぅ てゅ ふゅ ゔゅ ゔ
                きぇ にぇ ひぇ ぐぃ ぐぇ ぐぉ すぃ ずぃ ふょ ゔょ
                """;

        String tenji = """
                ⠈⠻⠀⠘⠻⠀⠈⠟⠀⠢⠕⠀⠢⠟⠀⠢⠞
                ⠢⠥⠀⠢⠧⠀⠢⠯⠀⠢⠮⠀⠈⠗⠀⠘⠗⠀⠸⠝
                ⠈⠋⠀⠢⠃⠀⠢⠋⠀⠢⠊⠀⠢⠡⠀⠢⠣⠀⠢⠫⠀⠢⠪⠀⠲⠡⠀⠢⠗
                ⠲⠥⠀⠲⠧⠀⠲⠯⠀⠲⠮⠀⠢⠝⠀⠲⠝⠀⠨⠝⠀⠨⠬⠀⠸⠬⠀⠐⠉
                ⠈⠫⠀⠈⠏⠀⠈⠯⠀⠲⠣⠀⠲⠫⠀⠲⠪⠀⠈⠳⠀⠘⠳⠀⠨⠜⠀⠸⠜
                """;

        assertEquals(text, hiragana.process(tenji));
    }
}