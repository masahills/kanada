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
package com.iciao.kanada.maps;

import java.util.HashMap;
import java.util.Map;

/**
 * Mapping for braille characters to plain text.
 *
 * @author Masahiko Sato
 */
class BrailleMapping {
    protected static final Map<Character, String> DIGIT_MAP = new HashMap<>();
    protected static final Map<Character, String> LATIN_MAP = new HashMap<>();
    protected static final Map<Character, String> KANA_MAP = new HashMap<>();

    static {
        // English alphabet
        LATIN_MAP.put('⠁', "a");
        LATIN_MAP.put('⠃', "b");
        LATIN_MAP.put('⠉', "c");
        LATIN_MAP.put('⠙', "d");
        LATIN_MAP.put('⠑', "e");
        LATIN_MAP.put('⠋', "f");
        LATIN_MAP.put('⠛', "g");
        LATIN_MAP.put('⠓', "h");
        LATIN_MAP.put('⠊', "i");
        LATIN_MAP.put('⠚', "j");
        LATIN_MAP.put('⠅', "k");
        LATIN_MAP.put('⠇', "l");
        LATIN_MAP.put('⠍', "m");
        LATIN_MAP.put('⠝', "n");
        LATIN_MAP.put('⠕', "o");
        LATIN_MAP.put('⠏', "p");
        LATIN_MAP.put('⠟', "q");
        LATIN_MAP.put('⠗', "r");
        LATIN_MAP.put('⠎', "s");
        LATIN_MAP.put('⠞', "t");
        LATIN_MAP.put('⠥', "u");
        LATIN_MAP.put('⠧', "v");
        LATIN_MAP.put('⠺', "w");
        LATIN_MAP.put('⠭', "x");
        LATIN_MAP.put('⠽', "y");
        LATIN_MAP.put('⠵', "z");

        LATIN_MAP.put('⠀', " ");
        LATIN_MAP.put('⠂', ",");
        LATIN_MAP.put('⠆', ";");
        LATIN_MAP.put('⠒', ":");
        LATIN_MAP.put('⠲', ".");
        LATIN_MAP.put('⠖', "!");
        LATIN_MAP.put('⠦', "?");
        LATIN_MAP.put('⠤', "-");

        // Numerals
        DIGIT_MAP.put('⠁', "1");
        DIGIT_MAP.put('⠃', "2");
        DIGIT_MAP.put('⠉', "3");
        DIGIT_MAP.put('⠙', "4");
        DIGIT_MAP.put('⠑', "5");
        DIGIT_MAP.put('⠋', "6");
        DIGIT_MAP.put('⠛', "7");
        DIGIT_MAP.put('⠓', "8");
        DIGIT_MAP.put('⠊', "9");
        DIGIT_MAP.put('⠚', "0");
        DIGIT_MAP.put('⠂', ".");
        DIGIT_MAP.put('⠄', ",");

        // Hiragana
        KANA_MAP.put('⠁', "あ");
        KANA_MAP.put('⠃', "い");
        KANA_MAP.put('⠉', "う");
        KANA_MAP.put('⠋', "え");
        KANA_MAP.put('⠊', "お");
        KANA_MAP.put('⠡', "か");
        KANA_MAP.put('⠣', "き");
        KANA_MAP.put('⠩', "く");
        KANA_MAP.put('⠫', "け");
        KANA_MAP.put('⠪', "こ");
        KANA_MAP.put('⠱', "さ");
        KANA_MAP.put('⠳', "し");
        KANA_MAP.put('⠹', "す");
        KANA_MAP.put('⠻', "せ");
        KANA_MAP.put('⠺', "そ");
        KANA_MAP.put('⠕', "た");
        KANA_MAP.put('⠗', "ち");
        KANA_MAP.put('⠝', "つ");
        KANA_MAP.put('⠟', "て");
        KANA_MAP.put('⠞', "と");
        KANA_MAP.put('⠅', "な");
        KANA_MAP.put('⠇', "に");
        KANA_MAP.put('⠍', "ぬ");
        KANA_MAP.put('⠏', "ね");
        KANA_MAP.put('⠎', "の");
        KANA_MAP.put('⠥', "は");
        KANA_MAP.put('⠧', "ひ");
        KANA_MAP.put('⠭', "ふ");
        KANA_MAP.put('⠯', "へ");
        KANA_MAP.put('⠮', "ほ");
        KANA_MAP.put('⠵', "ま");
        KANA_MAP.put('⠷', "み");
        KANA_MAP.put('⠽', "む");
        KANA_MAP.put('⠿', "め");
        KANA_MAP.put('⠾', "も");
        KANA_MAP.put('⠌', "や");
        KANA_MAP.put('⠬', "ゆ");
        KANA_MAP.put('⠜', "よ");
        KANA_MAP.put('⠑', "ら");
        KANA_MAP.put('⠓', "り");
        KANA_MAP.put('⠙', "る");
        KANA_MAP.put('⠛', "れ");
        KANA_MAP.put('⠚', "ろ");
        KANA_MAP.put('⠄', "わ");
        KANA_MAP.put('⠔', "を");
        KANA_MAP.put('⠴', "ん");

        KANA_MAP.put('⠒', "ー");
        KANA_MAP.put('⠂', "っ");

        KANA_MAP.put('⠢', "？");
        KANA_MAP.put('⠖', "！");
    }

    protected static String toDakuon(String kana) {
        if (kana == null) {
            return null;
        }
        return switch (kana) {
            case "か" -> "が";
            case "き" -> "ぎ";
            case "く" -> "ぐ";
            case "け" -> "げ";
            case "こ" -> "ご";
            case "さ" -> "ざ";
            case "し" -> "じ";
            case "す" -> "ず";
            case "せ" -> "ぜ";
            case "そ" -> "ぞ";
            case "た" -> "だ";
            case "ち" -> "ぢ";
            case "つ" -> "づ";
            case "て" -> "で";
            case "と" -> "ど";
            case "は" -> "ば";
            case "ひ" -> "び";
            case "ふ" -> "ぶ";
            case "へ" -> "べ";
            case "ほ" -> "ぼ";
            // Specials
            case "う" -> "ゔ";
            default -> kana;
        };
    }

    protected static String toHandakuon(String kana) {
        if (kana == null) {
            return null;
        }
        return switch (kana) {
            case "は" -> "ぱ";
            case "ひ" -> "ぴ";
            case "ふ" -> "ぷ";
            case "へ" -> "ぺ";
            case "ほ" -> "ぽ";
            default -> kana;
        };
    }

    protected static String toYouon(String kana) {
        if (kana == null) {
            return null;
        }
        return switch (kana) {
            case "か" -> "きゃ";
            case "く" -> "きゅ";
            case "こ" -> "きょ";
            case "さ" -> "しゃ";
            case "す" -> "しゅ";
            case "そ" -> "しょ";
            case "た" -> "ちゃ";
            case "つ" -> "ちゅ";
            case "と" -> "ちょ";
            case "な" -> "にゃ";
            case "ぬ" -> "にゅ";
            case "の" -> "にょ";
            case "は" -> "ひゃ";
            case "ふ" -> "ひゅ";
            case "ほ" -> "ひょ";
            case "ま" -> "みゃ";
            case "む" -> "みゅ";
            case "も" -> "みょ";
            case "ら" -> "りゃ";
            case "る" -> "りゅ";
            case "ろ" -> "りょ";
            // specials
            case "せ" -> "しぇ";
            case "て" -> "ちぇ";
            case "ち" -> "てぃ";
            case "え" -> "いぇ";
            case "け" -> "きぇ";
            case "ね" -> "にぇ";
            case "へ" -> "ひぇ";
            case "し" -> "すぃ";
            default -> kana;
        };
    }

    protected static String toYouDakuon(String kana) {
        if (kana == null) {
            return null;
        }
        return switch (kana) {
            case "か" -> "ぎゃ";
            case "く" -> "ぎゅ";
            case "こ" -> "ぎょ";
            case "さ" -> "じゃ";
            case "す" -> "じゅ";
            case "そ" -> "じょ";
            case "た" -> "ぢゃ";
            case "つ" -> "ぢゅ";
            case "と" -> "ぢょ";
            case "は" -> "びゃ";
            case "ふ" -> "びゅ";
            case "ほ" -> "びょ";
            // Specials
            case "せ" -> "じぇ";
            case "ち" -> "でぃ";
            case "し" -> "ずぃ";
            default -> kana;
        };
    }

    protected static String toYouHandakuon(String kana) {
        if (kana == null) {
            return null;
        }
        return switch (kana) {
            case "は" -> "ぴゃ";
            case "ふ" -> "ぴゅ";
            case "ほ" -> "ぴょ";
            // Specials
            case "つ" -> "てゅ";
            case "ゆ" -> "ふゅ";
            case "よ" -> "ふょ";
            default -> kana;
        };
    }

    protected static String toSpecial26(String kana) {
        if (kana == null) {
            return null;
        }
        return switch (kana) {
            case "い" -> "うぃ";
            case "え" -> "うぇ";
            case "お" -> "うぉ";
            case "か" -> "くぁ";
            case "き" -> "くぃ";
            case "け" -> "くぇ";
            case "こ" -> "くぉ";
            case "た" -> "つぁ";
            case "ち" -> "つぃ";
            case "て" -> "つぇ";
            case "と" -> "つぉ";
            case "は" -> "ふぁ";
            case "ひ" -> "ふぃ";
            case "へ" -> "ふぇ";
            case "ほ" -> "ふぉ";
            case "つ" -> "とぅ";
            default -> kana;
        };
    }

    protected static String toSpecial256(String kana) {
        if (kana == null) {
            return null;
        }
        return switch (kana) {
            case "は" -> "ゔぁ";
            case "ひ" -> "ゔぃ";
            case "へ" -> "ゔぇ";
            case "ほ" -> "ゔぉ";
            case "つ" -> "どぅ";
            case "か" -> "ぐぁ";
            case "き" -> "ぐぃ";
            case "け" -> "ぐぇ";
            case "こ" -> "ぐぉ";
            default -> kana;
        };
    }

    protected static String toSpecial456(String kana) {
        if (kana == null) {
            return null;
        }
        return switch (kana) {
            case "つ" -> "でゅ";
            case "ゆ" -> "ゔゅ";
            case "よ" -> "ゔょ";
            default -> kana;
        };
    }
}