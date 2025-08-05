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
 * Convert braille input to hiragana, katakana, or romaji.
 *
 * @author Masahiko Sato
 */
/*
U+280x	⠀	⠁	⠂	⠃	⠄	⠅	⠆	⠇	⠈	⠉	⠊	⠋	⠌	⠍	⠎	⠏
U+281x	⠐	⠑	⠒	⠓	⠔	⠕	⠖	⠗	⠘	⠙	⠚	⠛	⠜	⠝	⠞	⠟
U+282x	⠠	⠡	⠢	⠣	⠤	⠥	⠦	⠧	⠨	⠩	⠪	⠫	⠬	⠭	⠮	⠯
U+283x	⠰	⠱	⠲	⠳	⠴	⠵	⠶	⠷	⠸	⠹	⠺	⠻	⠼	⠽	⠾	⠿
 */
public class MapBraille extends JMapper {

    private static final char DOTS_0_BLANK = '⠀';   // U+2800 Braille blank pattern
    private static final char DOTS_2356_PARENTHESES = '⠶';
    private static final char DOTS_356_CLOSE_QUOTE = '⠴';
    private static final char DOTS_36_HYPHEN = '⠤';
    private static final char DOTS_256_KUTEN = '⠲'; // 句点「。」改行の前以外では後ろに空白を2つ挟む
    private static final char DOTS_56_TOUTEN = '⠰'; // 読点「、」改行の前以外では後ろに空白を挟む

    private static final String SPACES = " " + DOTS_0_BLANK;
    private static final String CRLF = "\n\r";
    private static final String WHITE_SPACES = CRLF + SPACES;

    private BrailleMode currentMode = BrailleMode.KANA;

    public MapBraille(Kanada kanada) {
        super(kanada);
    }

    @Override
    protected void process(String brailleStr, int param) {
        String str = brailleToText(brailleStr);
        matchedLength = brailleStr.length();
        setString(this.kanada.process(str));
    }

    private boolean setBrailleMode(char c) {
        return switch (c) {
            // 数字符
            case '⠼' -> {
                currentMode = BrailleMode.NUMBER;
                yield true;
            }
            // 外字符 / 読点「、」改行の前以外では後ろに空白を挟む
            case '⠰' -> {
                currentMode = BrailleMode.LATIN;
                yield true;
            }
            case '⠠' -> {
                if (currentMode == BrailleMode.LATIN_CAPITAL) {
                    currentMode = BrailleMode.LATIN_CAPITAL_ALL;
                } else {
                    currentMode = BrailleMode.LATIN_CAPITAL;
                }
                yield true;
            }
            // 外国語引用符（開始）
            case '⠦' -> {
                currentMode = BrailleMode.LATIN_CAPITAL_ALL;
                yield true;
            }
            // 濁音符
            case '⠐' -> {
                currentMode = BrailleMode.KANA_DAKUON;
                yield true;
            }
            // 拗音符
            case '⠈' -> {
                currentMode = BrailleMode.KANA_YOUON;
                yield true;
            }
            // 拗濁音符
            case '⠘' -> {
                currentMode = BrailleMode.KANA_YOUDAKUON;
                yield true;
            }
            // 拗半濁音符
            case '⠨' -> {
                currentMode = BrailleMode.KANA_YOUHANDAKUON;
                yield true;
            }
            default -> false;
        };
    }

    private void resetBrailleMode() {
        currentMode = BrailleMode.KANA;
    }

    private String brailleToText(String brailleText) {
        StringBuilder result = new StringBuilder();
        char punctuation = 0;
        boolean parenthesisIn = false;
        boolean cornerBracketIn = false;

        for (int i = 0; i < brailleText.length(); i++) {
            char thisChar = brailleText.charAt(i);
            // Store punctuation indicator
            if (thisChar == DOTS_56_TOUTEN || thisChar == DOTS_256_KUTEN) {
                punctuation = thisChar;
            }

            // Store next char for an additional clue
            char nextChar = 0;
            if (i + 1 < brailleText.length()) {
                nextChar = brailleText.charAt(i + 1);
            }

            // Update braille mode
            if (setBrailleMode(thisChar)) {
                // This is an indicator. Skip to the next char
                continue;
            }

            // Punctuation marks
            if (punctuation == DOTS_56_TOUTEN || punctuation == DOTS_256_KUTEN) {
                String punctuationStr = getPunctuation(thisChar, nextChar, punctuation);
                if (punctuationStr != null) {
                    result.append(punctuationStr);
                    if (punctuation == DOTS_256_KUTEN && nextChar == DOTS_0_BLANK) {
                        i++;
                    }
                    continue;
                }
            }

            // White space
            if (thisChar == DOTS_0_BLANK) {
                result.append(" ");
                continue;
            }

            // Numerals
            if (currentMode == BrailleMode.NUMBER) {
                String number = getNumeric(thisChar, nextChar);
                if (number != null) {
                    result.append(number);
                    continue;
                }
            }

            // Latin characters
            if (currentMode == BrailleMode.LATIN ||
                    currentMode == BrailleMode.LATIN_CAPITAL ||
                    currentMode == BrailleMode.LATIN_CAPITAL_ALL) {
                String latin = getLatin(thisChar);
                if (latin != null) {
                    result.append(latin);
                    punctuation = 0; // 読点ではなく外字符のため punctuation をリセット
                }
                continue;
            }

            // Brackets
            if (thisChar == DOTS_2356_PARENTHESES || thisChar == DOTS_36_HYPHEN) {
                if (thisChar == DOTS_2356_PARENTHESES) {
                    parenthesisIn = !parenthesisIn;
                    result.append(parenthesisIn ? "（" : "）");
                } else {
                    cornerBracketIn = !cornerBracketIn;
                    result.append(cornerBracketIn ? "「" : "」");
                }
                continue;
            }

            // Kana characters
            String kana = getKana(thisChar);
            if (kana != null) {
                result.append(kana);
            }
        }
        return result.toString();
    }

    private String getPunctuation(char thisChar, char nextChar, char punctuation) {
        String result = null;
        if (WHITE_SPACES.indexOf(thisChar) < 0) {
            return result;
        }

        if (punctuation == DOTS_56_TOUTEN) {
            result = "、";
            resetBrailleMode();
        }

        if (punctuation == DOTS_256_KUTEN) {
            boolean addKuten = false;
            if (CRLF.indexOf(thisChar) > -1) {
                addKuten = true;
            } else if (SPACES.indexOf(thisChar) > -1 && SPACES.indexOf(nextChar) > -1) {
                addKuten = true;
            }
            if (addKuten) {
                result = "。";
                resetBrailleMode();
            }
        }
        return result;
    }

    private String getNumeric(char thisChar, char nextChar) {
        String result = null;
        if (thisChar == DOTS_36_HYPHEN && nextChar > 0) {
            // 次があ行・ら行の場合は、つなぎ符
            String digit = BrailleMapping.DIGIT_MAP.get(nextChar);
            if (digit != null && digit.length() == 1 && Character.isDigit(digit.charAt(0))) {
                resetBrailleMode();
                return "";  // Return an empty string instead of null.
            }
        }
        String number = BrailleMapping.DIGIT_MAP.get(thisChar);
        if (number != null) {
            result = number;
        }
        return result;
    }

    private String getLatin(char thisChar) {
        String result = null;
        if (thisChar == DOTS_36_HYPHEN || thisChar == DOTS_356_CLOSE_QUOTE) {
            resetBrailleMode();
            return result;
        }
        String latin = BrailleMapping.LATIN_MAP.get(thisChar);
        if (latin != null) {
            if (currentMode == BrailleMode.LATIN_CAPITAL) {
                result = latin.toUpperCase();
                currentMode = BrailleMode.LATIN;
            } else if (currentMode == BrailleMode.LATIN_CAPITAL_ALL) {
                result = latin.toUpperCase();
            } else {
                result = latin;
            }
        }
        return result;
    }

    private String getKana(char thisChar) {
        String result = null;
        String baseKana = BrailleMapping.KANA_MAP.get(thisChar);
        if (baseKana != null) {
            if (currentMode == BrailleMode.KANA_DAKUON) {
                result = BrailleMapping.toDakuon(baseKana);
            } else if (currentMode == BrailleMode.KANA_HANDAKUON) {
                result = BrailleMapping.toHandakuon(baseKana);
            } else if (currentMode == BrailleMode.KANA_YOUON) {
                result = BrailleMapping.toYouon(baseKana);
            } else if (currentMode == BrailleMode.KANA_YOUDAKUON) {
                result = BrailleMapping.toYouDakuon(baseKana);
            } else if (currentMode == BrailleMode.KANA_YOUHANDAKUON) {
                result = BrailleMapping.toYouHandakuon(baseKana);
            } else {
                result = baseKana;
            }
            resetBrailleMode();
        }
        return result;
    }

    private enum BrailleMode {
        NUMBER,
        LATIN,
        LATIN_CAPITAL,
        LATIN_CAPITAL_ALL,
        KANA,
        KANA_DAKUON,
        KANA_HANDAKUON,
        KANA_YOUON,
        KANA_YOUDAKUON,
        KANA_YOUHANDAKUON
    }
}