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
public class BrailleMapping {
    private static final Map<Character, String> DIGIT_MAP = new HashMap<>();
    private static final Map<Character, String> LATIN_MAP = new HashMap<>();
    private static final Map<Character, String> KANA_MAP = new HashMap<>();

    private static final char DOTS_0_BLANK = '⠀';   // U+2800 Braille blank pattern
    private static final char DOTS_2356_PARENTHESES = '⠶';
    private static final char DOTS_36_HYPHEN = '⠤';
    private static final char DOTS_56_TOUTEN = '⠰'; // 読点「、」改行の前以外では後ろに空白を挟む
    private static final char DOTS_256_KUTEN = '⠲'; // 句点「。」改行の前以外では後ろに空白を2つ挟む
    private static final char DOTS_356_CLOSE_QUOTE = '⠴';

    private static final String SPACES = " " + DOTS_0_BLANK;
    private static final String CRLF = "\n\r";
    private static final String WHITE_SPACES = CRLF + SPACES;

    private static BrailleMapping instance;

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

    private BrailleMode currentMode = BrailleMode.KANA;

    private BrailleMapping() {
    }

    public static synchronized BrailleMapping getInstance() {
        if (instance == null) {
            instance = new BrailleMapping();
        }
        return instance;
    }

    private static String toDakuon(String kana) {
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
            default -> kana;
        };
    }

    private static String toHandakuon(String kana) {
        return switch (kana) {
            case "は" -> "ぱ";
            case "ひ" -> "ぴ";
            case "ふ" -> "ぷ";
            case "へ" -> "ぺ";
            case "ほ" -> "ぽ";
            default -> kana;
        };
    }

    private static String toYouon(String kana) {
        return switch (kana) {
            case "か" -> "きゃ";
            case "く" -> "きゅ";
            case "こ" -> "きょ";
            case "さ" -> "しゃ";
            case "す" -> "しゅ";
            case "そ" -> "しょ";
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
            default -> kana;
        };
    }

    private static String toYouDakuon(String kana) {
        return switch (kana) {
            case "か" -> "ぎゃ";
            case "く" -> "ぎゅ";
            case "こ" -> "ぎょ";
            case "さ" -> "じゃ";
            case "す" -> "じゅ";
            case "そ" -> "じょ";
            case "は" -> "びゃ";
            case "ふ" -> "びゅ";
            case "ほ" -> "びょ";
            default -> kana;
        };
    }

    private static String toYouHandakuon(String kana) {
        return switch (kana) {
            case "は" -> "ぴゃ";
            case "ふ" -> "ぴゅ";
            case "ほ" -> "ぴょ";
            default -> kana;
        };
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

    public String brailleToText(String brailleText) {
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
                    String digit = DIGIT_MAP.get(nextChar);
                    if (digit != null && digit.length() == 1 && Character.isDigit(digit.charAt(0))) {
                        resetBrailleMode();
                        continue;
                    }
                }
                String number = DIGIT_MAP.get(thisChar);
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
                String latin = LATIN_MAP.get(thisChar);
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

            String kana = KANA_MAP.get(thisChar);
            if (kana != null) {
                if (currentMode == BrailleMode.KANA_DAKUON) {
                    result.append(toDakuon(kana));
                } else if (currentMode == BrailleMode.KANA_HANDAKUON) {
                    result.append(toHandakuon(kana));
                } else if (currentMode == BrailleMode.KANA_YOUON) {
                    result.append(toYouon(kana));
                } else if (currentMode == BrailleMode.KANA_YOUDAKUON) {
                    result.append(toYouDakuon(kana));
                } else if (currentMode == BrailleMode.KANA_YOUHANDAKUON) {
                    result.append(toYouHandakuon(kana));
                } else {
                    result.append(kana);
                }
                resetBrailleMode();
            }
        }
        return result.toString();
    }

    public enum BrailleMode {
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
