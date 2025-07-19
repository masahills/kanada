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
 * Remap non-kanji characters.<br>
 *
 * @author Masahiko Sato
 */
/*
U+300x	　	、	。	〃	〄	々	〆	〇	〈	〉	《	》	「	」	『	』
U+301x	【	】	〒	〓	〔	〕	〖	〗	〘	〙	〚	〛	〜	〝	〞	〟
U+302x	〠	〡	〢	〣	〤	〥	〦	〧	〨	〩	〪	〫	〬	〭	〮	〯
U+303x	〰	〱	〲	〳	〴	〵	〶	〷	〸	〹	〺	〻	〼	〽	 〾 	〿
 */
public class MapWideSymbol extends JMapper {
    private static final String[] CJK_SYMBOLS_AND_PUNCTUATION_TO_ASCII = {
            " ", ",", ".", "(repeat)", "(jis)", "(repeat)", "(closing mark)", "(circle)",
            "<", ">", "<<", ">>", "'", "'", "\"", "\"",

            "[", "]", "(postal mark)", "=", "[", "]", "{", "}",
            "[", "]", "[[", "]]", "~", "\"", "\"", "\"",

            "(postal mark face)", "1", "2", "3", "4", "5", "6", "7",
            "8", "9", "", "", "", "", "", "",

            "~", "(repeat)", "(repeat)", "(repeat)", "(repeat)", "(repeat)", "(postal mark circle)", "XX",
            "10", "20", "30", "(repeat)", "(masu mark)", "♪", " ", " "
    };

    public MapWideSymbol(Kanada kanada) {
        super(kanada);
    }

    @Override
    protected void process(String str, int param) {
        StringBuilder out = new StringBuilder();
        char thisChar = str.charAt(0);
        Character.UnicodeBlock block = Character.UnicodeBlock.of(thisChar);
        if (block == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION) {
            int index = thisChar - 0x3000;
            if (param == JMapper.TO_ASCII) {
                out.append(CJK_SYMBOLS_AND_PUNCTUATION_TO_ASCII[index]);
            } else if (param == JMapper.TO_HALF_SYMBOL) {
                out.append(cjkSymbolsAndPunctuationToHalfSymbol(thisChar));
            } else {
                out.appendCodePoint(thisChar);
            }
        } else {
            out.appendCodePoint(thisChar);
        }
        setString(out.toString());
    }

    private String cjkSymbolsAndPunctuationToHalfSymbol(char ch) {
        return switch (ch) {
            case '　' -> " "; // space
            case '、' -> "､"; // comma
            case '。' -> "｡"; // period
            case '「' -> "｢"; // left bracket
            case '」' -> "｣"; // right bracket
            case '〜', '〰' -> "ｰ"; // long vowel mark
            default -> String.valueOf(ch);
        };
    }
}
