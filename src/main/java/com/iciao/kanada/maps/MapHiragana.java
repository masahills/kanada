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

import java.util.Objects;

/**
 * Convert hiragana input to romaji using TSV-based mapping.
 *
 * @author Masahiko Sato
 */
public class MapHiragana extends JMapper {
    private static final KanaMapping kanaMapping = KanaMapping.getInstance();

    public MapHiragana(Kanada kanada) {
        super(kanada);
    }

    @Override
    protected void process(String str, int param) {
        StringBuilder out = new StringBuilder();
        int thisChar = str.codePointAt(0);
        String kana = String.valueOf(Character.toChars(thisChar));
        String transliteration = null;
        if (param == TO_ASCII || param == TO_WIDE_ASCII || param == TO_KANA_BRAILLE) {
            KanaTrie.MatchResult result = kanaMapping.getTransliterations(str);
            if (result != null) {
                transliteration = result.values()[getConversionSystem().getColumnIndex() - 2];
                matchedLength = result.length();
            }
        }

        switch (param) {
            case TO_KATAKANA:
                out.appendCodePoint(thisChar + 0x60);
                break;
            case TO_HALF_KATAKANA:
                String halfKatakana = kanaMapping.toHalfWidthKana(kana);
                out.append(Objects.requireNonNullElse(halfKatakana, kana));
                break;
            case TO_ASCII:
            case TO_WIDE_ASCII:
                if (transliteration != null) {
                    out.append(modeMacron() ? transliteration : kanaMapping.removeMacrons(transliteration));
                } else {
                    out.append(kana);
                }
                break;
            case TO_KANA_BRAILLE:
                if (transliteration != null) {
                    out.append(transliteration);
                } else {
                    out.append(kana);
                }
                break;
            default:
                out.appendCodePoint(thisChar);
                break;
        }
        setString(out.toString());
    }


}