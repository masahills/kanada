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
        boolean spaceIn = false;

        for (int i = 0; i < brailleText.length(); i++) {
            char thisChar = brailleText.charAt(i);
            if (thisChar == DOTS_56_TOUTEN || thisChar == DOTS_256_KUTEN) {
                punctuation = thisChar;
            }

            if (setBrailleMode(thisChar)) {
                continue;
            }

            char nextChar = 0;
            if (i + 1 < brailleText.length()) {
                nextChar = brailleText.charAt(i + 1);
            }

            if (punctuation == DOTS_56_TOUTEN && WHITE_SPACES.indexOf(thisChar) > -1) {
                result.append("、");
                resetBrailleMode();
                punctuation = 0;
                continue;
            }

            if (punctuation == DOTS_256_KUTEN) {
                boolean addKuten = false;
                if (CRLF.indexOf(thisChar) > -1) {
                    addKuten = true;
                } else if (SPACES.indexOf(thisChar) > -1 && SPACES.indexOf(nextChar) > -1) {
                    addKuten = true;
                }
                if (addKuten) {
                    result.append("。");
                    resetBrailleMode();
                    punctuation = 0;
                    spaceIn = true;
                    continue;
                }
            }

            if (spaceIn) {
                spaceIn = false;
                continue;
            }

            if (thisChar == DOTS_0_BLANK) {
                result.append(" ");
                continue;
            }

            if (currentMode == BrailleMode.NUMBER) {
                if (thisChar == DOTS_36_HYPHEN) {
                    // 次があ行・ら行の場合は、つなぎ符
                    String digit = BrailleMapping.DIGIT_MAP.get(nextChar);
                    if (digit != null && digit.length() == 1 && Character.isDigit(digit.charAt(0))) {
                        resetBrailleMode();
                        continue;
                    }
                }
                String number = BrailleMapping.DIGIT_MAP.get(thisChar);
                if (number != null) {
                    result.append(number);
                    continue;
                }
            }

            if (currentMode == BrailleMode.LATIN ||
                    currentMode == BrailleMode.LATIN_CAPITAL ||
                    currentMode == BrailleMode.LATIN_CAPITAL_ALL) {
                if (thisChar == DOTS_36_HYPHEN) {
                    resetBrailleMode();
                    continue;
                }
                if (thisChar == DOTS_356_CLOSE_QUOTE) {
                    resetBrailleMode();
                    continue;
                }
                String latin = BrailleMapping.LATIN_MAP.get(thisChar);
                if (latin != null) {
                    if (currentMode == BrailleMode.LATIN_CAPITAL) {
                        result.append(latin.toUpperCase());
                        currentMode = BrailleMode.LATIN;
                    } else if (currentMode == BrailleMode.LATIN_CAPITAL_ALL) {
                        result.append(latin.toUpperCase());
                    } else {
                        result.append(latin);
                    }
                    punctuation = 0;
                    continue;
                }
            }

            if (thisChar == DOTS_2356_PARENTHESES) {
                parenthesisIn = !parenthesisIn;
                if (parenthesisIn) {
                    result.append("（");
                } else {
                    result.append("）");
                }
                continue;
            }

            if (thisChar == DOTS_36_HYPHEN) {
                cornerBracketIn = !cornerBracketIn;
                if (cornerBracketIn) {
                    result.append("「");
                } else {
                    result.append("」");
                }
                continue;
            }

            String kana = BrailleMapping.KANA_MAP.get(thisChar);
            if (kana != null) {
                if (currentMode == BrailleMode.KANA_DAKUON) {
                    result.append(BrailleMapping.toDakuon(kana));
                } else if (currentMode == BrailleMode.KANA_HANDAKUON) {
                    result.append(BrailleMapping.toHandakuon(kana));
                } else if (currentMode == BrailleMode.KANA_YOUON) {
                    result.append(BrailleMapping.toYouon(kana));
                } else if (currentMode == BrailleMode.KANA_YOUDAKUON) {
                    result.append(BrailleMapping.toYouDakuon(kana));
                } else if (currentMode == BrailleMode.KANA_YOUHANDAKUON) {
                    result.append(BrailleMapping.toYouHandakuon(kana));
                } else {
                    result.append(kana);
                }
                resetBrailleMode();
            }
        }
        return result.toString();
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