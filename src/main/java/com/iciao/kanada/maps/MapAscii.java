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
 * Map ASCII characters to fullwidth forms when appropriate.
 *
 * @author Masahiko Sato
 */
public class MapAscii extends JMapper {

    public MapAscii(Kanada kanada) {
        super(kanada);
    }

    @Override
    protected void process(String str, int param) {
        StringBuilder out = new StringBuilder();
        int thisChar = str.codePointAt(0);
        if (param == JMapper.TO_WIDE_ASCII && thisChar > 0x20 && thisChar < 0x7f) {
            out.appendCodePoint(thisChar + 0xfee0);
        } else if (param == JMapper.TO_KANA_BRAILLE) {
            if (thisChar >= 0x30 && thisChar <= 0x39) {
                out.append(numbersToBrailleString(str));
            } else if (thisChar >= 0x41 && thisChar <= 0x5a || thisChar >= 0x61 && thisChar <= 0x7a) {
                out.append(alphabetsToBrailleString(str));
            } else {
                out.appendCodePoint(thisChar);
            }
        } else {
            out.appendCodePoint(thisChar);
        }
        setString(out.toString());
    }

    private String alphabetsToBrailleString(String str) {
        int count = 0;
        StringBuilder alphabets = new StringBuilder("⠦");
        for (char c : str.toCharArray()) {
            if (c >= 'A' && c <= 'Z' || c >= 'a' && c <= 'z' || c == '.' || c == ',' || c == ' ') {
                alphabets.append(BASIC_LATIN_TO_BRAILLE[c - 0x0020]);
                count++;
            } else {
                break;
            }
        }
        matchedLength = count;
        return alphabets.toString();
    }

    private String numbersToBrailleString(String str) {
        int count = 0;
        StringBuilder numbers = new StringBuilder("⠼");
        for (char c : str.toCharArray()) {
            if (c >= '0' && c <= '9' || c == '.' || c == ',' || c == ' ' || c == '\u2800') {
                if (c == ' ' || c == '\u2800') {
                    numbers.append('⠤');
                } else {
                    numbers.append(BASIC_LATIN_TO_BRAILLE[c - 0x0020]);
                }
                count++;
            } else {
                break;
            }
        }
        matchedLength = count;
        return numbers.toString();
    }

    // TODO: Need to find out how to convert English symbols to Japanese braille.
    private static final String[] BASIC_LATIN_TO_BRAILLE = {
            "⠀", "⠖", "\"", "⠰⠩", "$", "⠰⠏", "⠰⠯", "'", "⠶", "⠶", "⠰⠡", "+", "⠠", "-", "⠲", "⠸⠌",
            "⠚", "⠁", "⠃", "⠉", "⠙", "⠑", "⠋", "⠛", "⠓", "⠊", "⠒", "⡠", "<", "=", ">", "⠢",
            "@", "⠰⠠⠁", "⠠⠃", "⠠⠉", "⠠⠙", "⠠⠑", "⠠⠋", "⠠⠛", "⠠⠓", "⠠⠊", "⠠⠚", "⠠⠅", "⠠⠇", "⠠⠍", "⠠⠝", "⠠⠕",
            "⠠⠏", "⠠⠟", "⠠⠗", "⠠⠎", "⠠⠞", "⠠⠥", "⠠⠧", "⠠⠺", "⠠⠭", "⠠⠽", "⠠⠵", "[", "", "]", "^", "_",
            "`", "⠁", "⠃", "⠉", "⠙", "⠑", "⠋", "⠛", "⠓", "⠊", "⠚", "⠅", "⠇", "⠍", "⠝", "⠕",
            "⠏", "⠟", "⠗", "⠎", "⠞", "⠥", "⠧", "⠺", "⠭", "⠽", "⠵", "{", "|", "}", "~"
    };
}
