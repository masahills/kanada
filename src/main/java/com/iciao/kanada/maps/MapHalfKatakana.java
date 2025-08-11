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
 * Convert half-width katakana characters to hiragana, katakana, romaji, or Braille.
 *
 * @author Masahiko Sato
 */
/*
U+FF6x	 	｡	｢	｣	､	･	ｦ	ｧ	ｨ	ｩ	ｪ	ｫ	ｬ	ｭ	ｮ	ｯ
U+FF7x	ｰ	ｱ	ｲ	ｳ	ｴ	ｵ	ｶ	ｷ	ｸ	ｹ	ｺ	ｻ	ｼ	ｽ	ｾ	ｿ
U+FF8x	ﾀ	ﾁ	ﾂ	ﾃ	ﾄ	ﾅ	ﾆ	ﾇ	ﾈ	ﾉ	ﾊ	ﾋ	ﾌ	ﾍ	ﾎ	ﾏ
U+FF9x	ﾐ	ﾑ	ﾒ	ﾓ	ﾔ	ﾕ	ﾖ	ﾗ	ﾘ	ﾙ	ﾚ	ﾛ	ﾜ	ﾝ	ﾞ	ﾟ
 */
public class MapHalfKatakana extends JMapper {
    private static final KanaMapping kanaMapping = KanaMapping.getInstance();

    public MapHalfKatakana(Kanada kanada) {
        super(kanada);
    }

    @Override
    protected void process(String halfKana, int param) {
        String str = toFullWidthKatakana(halfKana);
        matchedLength = halfKana.length();

        StringBuilder out = new StringBuilder();
        for (int i = 0; i < str.length(); ) {
            int codePoint = str.codePointAt(i);
            String kana = String.valueOf(Character.toChars(codePoint));
            String transliteration = null;
            if (param == TO_ASCII || param == TO_WIDE_ASCII || param == TO_KANA_BRAILLE) {
                KanaTrie.MatchResult result = kanaMapping.getTransliterations(str.substring(i));
                if (result != null) {
                    transliteration = result.values()[getConversionSystem().getColumnIndex() - 2];
                    i += result.length();
                } else {
                    i += Character.charCount(codePoint);
                }
            } else {
                i += Character.charCount(codePoint);
            }

            switch (param) {
                case TO_HIRAGANA:
                    if (codePoint < 0x30F7 || codePoint == 0x30FD || codePoint == 0x30FE) {
                        out.appendCodePoint(codePoint - 0x60);
                    } else {
                        out.appendCodePoint(codePoint);
                    }
                    break;
                case TO_ASCII:
                case TO_WIDE_ASCII:
                    if (transliteration != null) {
                        out.append(modeMacron() ? transliteration : kanaMapping.removeMacrons(transliteration));
                    } else {
                        String punctuation = processPunctuations(kana.charAt(0));
                        if (punctuation != null) {
                            if (punctuation.equals("ー")) {
                                char prevChar = out.charAt(out.length() - 1);
                                String longVowel = kanaMapping.processLongVowels(String.valueOf(prevChar), getConversionSystem());
                                out.setCharAt(out.length() - 1, longVowel.charAt(0));
                            } else {
                                out.append(punctuation);
                            }
                        } else {
                            out.append(kana);
                        }
                    }
                    break;
                case TO_KANA_BRAILLE:
                    out.append(Objects.requireNonNullElse(transliteration, kana));
                    break;
                default:
                    out.append(kana);
                    break;
            }
        }
        setString(out.toString());
    }

    private String toFullWidthKatakana(String str) {
        StringBuilder fullWidth = new StringBuilder();
        for (int i = 0; i < str.length(); i++) {
            char thisChar = str.charAt(i);
            if (i + 1 < str.length()) {
                char next = str.charAt(i + 1);
                if (next == 'ﾞ' || next == 'ﾟ') {
                    char thatChar = combineDakuten(thisChar, next);
                    if (thatChar > 0) {
                        fullWidth.appendCodePoint(thatChar);
                        i++;
                        continue;
                    }
                }
            }
            int index = thisChar - 0xff60;
            if (index > -1 && index < FULLWIDTH_KATAKANA.length()) {
                char thatChar = FULLWIDTH_KATAKANA.charAt(index);
                fullWidth.appendCodePoint(thatChar);
            }
        }
        return fullWidth.toString();
    }

    private static String processPunctuations(char c) {
        return switch (c) {
            case '。' -> ".";
            case '「', '」' -> "\"";
            case '、' -> ",";
            case '・' -> " ";
            case 'ー' -> "ー";
            default -> null;
        };
    }

    private char combineDakuten(char c, char mark) {
        int index = -1;
        if (mark == 'ﾞ') {
            if (c >= 'ｶ' && c <= 'ｺ') {
                index = c - 'ｶ';
            } else if (c >= 'ｻ' && c <= 'ｿ') {
                index = c - 'ｶ';
            } else if (c >= 'ﾀ' && c <= 'ﾄ') {
                index = c - 'ｶ';
            } else if (c >= 'ﾊ' && c <= 'ﾎ') {
                index = c - 'ｶ' - 5;
            }
        } else if (mark == 'ﾟ') { // handakuten
            if (c >= 'ﾊ' && c <= 'ﾎ') {
                index = c - 'ｶ';
            }
        }
        if (index > -1) {
            return FULLWIDTH_KATAKANA_DAKUON_HANDAKUON.charAt(index);
        }
        return 0;
    }

    private static final String FULLWIDTH_KATAKANA_DAKUON_HANDAKUON =
            "ガギグゲゴ" + "ザジズゼゾ" + "ダヂヅデド" + "バビブベボ" + "パピプペポ";

    private static final String FULLWIDTH_KATAKANA =
            "　。「」、・ヲァィゥェォャュョッ" +
                    "ーアイウエオカキクケコサシスセソ" +
                    "タチツテトナニヌネノハヒフヘホマ" +
                    "ミムメモヤユヨラリルレロワン゛゜";
}
