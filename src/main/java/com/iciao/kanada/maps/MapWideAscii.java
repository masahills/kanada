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

import com.iciao.kanada.JMapper;
import com.iciao.kanada.Kanada;

/**
 * Map fullwidth ASCII characters to halfwidth forms when appropriate.
 *
 * @author Masahiko Sato
 */
public class MapWideAscii extends JMapper {

    public MapWideAscii(Kanada kanada) {
        super(kanada);
    }

    @Override
    protected void process(String str, int param) {
        StringBuilder out = new StringBuilder();
        int thisChar = str.codePointAt(0);
        if (param == JMapper.TO_ASCII) {
            if (thisChar >= 0xff00 && thisChar <= 0xff5e) {
                out.appendCodePoint(thisChar - 0xfee0);
            } else {
                out.append(fullwidthSymbolToHalf(str.charAt(0)));
            }
        } else if (param == JMapper.TO_KANA_BRAILLE) {
            if (thisChar >= '０' && thisChar <= '９') {
                out.append(fullwidthNumbersToBraille(str));
            } else if (thisChar >= 'Ａ' && thisChar <= 'Ｚ' || thisChar >= 'ａ' && thisChar <= 'ｚ') {
                out.append(fullwidthAlphabetsToBraille(str));
            } else {
                out.appendCodePoint(thisChar);
            }
        } else {
            out.appendCodePoint(thisChar);
        }
        setString(out.toString());
    }

    private char fullwidthSymbolToHalf(char ch) {
        return switch (ch) {
            case '￠' -> '¢'; // FULLWIDTH CENT SIGN → CENT SIGN
            case '￡' -> '£'; // FULLWIDTH POUND SIGN → POUND SIGN
            case '￢' -> '¬'; // FULLWIDTH NOT SIGN → NOT SIGN
            case '￣' -> '¯'; // FULLWIDTH MACRON → MACRON
            case '￤' -> '¦'; // FULLWIDTH BROKEN BAR → BROKEN BAR
            case '￥' -> '¥'; // FULLWIDTH YEN SIGN → YEN SIGN
            case '￦' -> '₩'; // FULLWIDTH WON SIGN → WON SIGN
            default -> ch;
        };
    }

    private String fullwidthAlphabetsToBraille(String str) {
        int count = 0;
        StringBuilder alphabets = new StringBuilder("⠦");
        for (char c : str.toCharArray()) {
            if (c >= 'A' && c <= 'Z' || c >= 'ａ' && c <= 'ｚ' || c == '，' || c == '．') {
                alphabets.append(FULLWIDTH_LATIN_TO_BRAILLE[c - 0xff00]);
                count++;
            } else {
                break;
            }
        }
        matchedLength = count;
        return alphabets.toString();
    }

    private String fullwidthNumbersToBraille(String str) {
        int count = 0;
        StringBuilder numbers = new StringBuilder("⠼");
        for (char c : str.toCharArray()) {
            if (c >= '０' && c <= '９' || c == '，' || c == '．' || c == ' ' || c == '\u2800') {
                if (c == ' ' || c == '\u2800') {
                    numbers.append('⠤');
                } else {
                    numbers.append(FULLWIDTH_LATIN_TO_BRAILLE[c - 0xff00]);
                }
                count++;
            } else {
                break;
            }
        }
        matchedLength = count;
        return numbers.toString();
    }

    /* A part of Halfwidth and Fullwidth Forms block
    U+FF0x		！	＂	＃	＄	％	＆	＇	（	）	＊	＋	，	－	．	／
    U+FF1x	０	１	２	３	４	５	６	７	８	９	：	；	＜	＝	＞	？
    U+FF2x	＠	Ａ	Ｂ	Ｃ	Ｄ	Ｅ	Ｆ	Ｇ	Ｈ	Ｉ	Ｊ	Ｋ	Ｌ	Ｍ	Ｎ	Ｏ
    U+FF3x	Ｐ	Ｑ	Ｒ	Ｓ	Ｔ	Ｕ	Ｖ	Ｗ	Ｘ	Ｙ	Ｚ	［	＼	］	＾	＿
    U+FF4x	｀	ａ	ｂ	ｃ	ｄ	ｅ	ｆ	ｇ	ｈ	ｉ	ｊ	ｋ	ｌ	ｍ	ｎ	ｏ
    U+FF5x	ｐ	ｑ	ｒ	ｓ	ｔ	ｕ	ｖ	ｗ	ｘ	ｙ	ｚ	｛	｜	｝	～
     */
    // TODO: Need to find out how to convert English symbols to Japanese braille.
    private static final String[] FULLWIDTH_LATIN_TO_BRAILLE = {
            "⠀", "⠖", "\"", "⠰⠩", "$", "⠰⠏", "⠰⠯", "'", "⠶", "⠶", "⠰⠡", "+", "⠠", "-", "⠲", "⠸⠌",
            "⠚", "⠁", "⠃", "⠉", "⠙", "⠑", "⠋", "⠛", "⠓", "⠊", "⠒", "⡠", "<", "=", ">", "⠢",
            "@", "⠰⠠⠁", "⠠⠃", "⠠⠉", "⠠⠙", "⠠⠑", "⠠⠋", "⠠⠛", "⠠⠓", "⠠⠊", "⠠⠚", "⠠⠅", "⠠⠇", "⠠⠍", "⠠⠝", "⠠⠕",
            "⠠⠏", "⠠⠟", "⠠⠗", "⠠⠎", "⠠⠞", "⠠⠥", "⠠⠧", "⠠⠺", "⠠⠭", "⠠⠽", "⠠⠵", "[", "", "]", "^", "_",
            "`", "⠁", "⠃", "⠉", "⠙", "⠑", "⠋", "⠛", "⠓", "⠊", "⠚", "⠅", "⠇", "⠍", "⠝", "⠕",
            "⠏", "⠟", "⠗", "⠎", "⠞", "⠥", "⠧", "⠺", "⠭", "⠽", "⠵", "{", "|", "}", "~"
    };
}
