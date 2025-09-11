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

    // 符号
    private static final char DOTS_3456 = '⠼'; // U+283C 数字符
    private static final char DOTS_4 = '⠈';    // U+2808 拗音符 / 情報処理用点字の場合、2行目以降の行頭で行継続符。
    private static final char DOTS_5 = '⠐';    // U+2810 濁音符 / 中点「・」改行の前以外では後ろに空白を挟む。
    private static final char DOTS_45 = '⠘';   // U+2818 拗濁音符
    private static final char DOTS_46 = '⠨';   // U+2828 拗半濁音符
    private static final char DOTS_6 = '⠠';    // U+2820 半濁音符 / 大文字符 / 二重大文字符（２連続で）
    private static final char DOTS_56 = '⠰';   // U+2830 外字符 / 読点「、」改行の前以外では後ろに空白を挟む
    private static final char DOTS_236 = '⠦';  // U+2826 外国語引用符（開始）
    private static final char DOTS_356 = '⠴';  // U+2834 外国語引用符（終了）/ 「ん」

    private static final char DOTS_35 = '⠔';   // U+2814 「を」/ 第１星印の要素
    private static final char DOTS_36 = '⠤';   // U+2824 第一つなぎ符
    private static final char DOTS_256 = '⠲';  // U+2832 句点「。」改行の前以外では後ろに空白を2つ挟む / 特殊音「ゔぁ、ゔぃ、ゔぇ」など
    private static final char DOTS_26 = '⠢';   // U+2822 特殊音「うぃ、うぇ、うぉ」 など
    private static final char DOTS_456 = '⠸';  // U+2838 特殊音「でゅ、ゔゅ、ゔょ」
    private static final char DOTS_2 = '⠂';    // U+2802 促音譜「っ」/ 小数点 / コンマ / 点線（３こ）
    private static final char DOTS_23 = '⠆';   // U+2806 「ゐ」/ 二重カギ閉じ２マス目
    private static final char DOTS_25 = '⠒';   // U+2812 長音付 / 棒線（２こ）
    private static final char DOTS_0 = '⠀';    // U+2800 Braille blank pattern
    private static final char DOTS_2356 = '⠶'; // U+2836 丸カッコ
    private static final char DOTS_3 = '⠄';    // U+2804 「わ」 / 第二カギ開き２マス目
    private BrailleMode currentMode = BrailleMode.KANA;

    public MapBraille(Kanada kanada) {
        super(kanada);
    }

    private static BracketType getBracketType(char thisChar, char nextChar) {
        if (thisChar == DOTS_2356) {
            if (nextChar == DOTS_23) {
                return BracketType.DOUBLE_PARENTHESIS;
            } else if (nextChar == DOTS_2356) {
                return BracketType.TRANSLATORS_NOTE;
            } else {
                return BracketType.PARENTHESIS;
            }
        } else if (thisChar == DOTS_36) {
            if (nextChar == DOTS_23) {
                return BracketType.DOUBLE_CORNER_BRACKET;
            } else {
                return BracketType.CORNER_BRACKET;
            }
        } else if (thisChar == DOTS_56) {
            if (nextChar == DOTS_2356) {
                return BracketType.DOUBLE_PARENTHESIS;
            } else if (nextChar == DOTS_3) {
                return BracketType.SECONDARY_CORNER_BRACKET;
            } else if (nextChar == DOTS_36) {
                return BracketType.DOUBLE_CORNER_BRACKET;
            }
        } else if (thisChar == DOTS_6) {
            if (nextChar == DOTS_23) {
                return BracketType.SECONDARY_PARENTHESIS;
            }
        }
        return null;
    }

    private static StarType getStarType(String text, int i) {
        if (text.length() - i < 3) {
            return null;
        }
        char thisChar = text.charAt(i);
        if (thisChar != DOTS_35 && thisChar != DOTS_26 && thisChar != DOTS_6) {
            return null;
        }
        char nextNextChar = text.charAt(i + 2);
        if (nextNextChar != DOTS_0) {
            return null;
        }
        char nextChar = text.charAt(i + 1);
        if (thisChar == DOTS_6 && nextChar == DOTS_25) {
            return StarType.THIRD_STAR;
        }
        if (i < 2 || text.charAt(i - 1) != DOTS_0 || text.charAt(i - 2) != DOTS_0) {
            return null;
        }
        if (i > 3 && text.charAt(i - 3) != '\n') {
            return null;
        }
        if (thisChar == DOTS_35 && nextChar == DOTS_35) {
            return StarType.FIRST_STAR;
        } else if (thisChar == DOTS_26 && nextChar == DOTS_26) {
            return StarType.SECOND_STAR;
        }
        return null;
    }

    private static boolean isBlankSpace(char c) {
        return c == ' ' || c == DOTS_0;
    }

    private static boolean isLineBreak(char c) {
        return c == '\n' || c == '\r';
    }

    private static boolean isPunctuation(char c) {
        return BrailleMapping.KUTOUTEN_MAP.containsKey(c);
    }

    private static int findDashes(String text, int i) {
        if (text.length() - i < 2) {
            return 0;
        }
        if (text.charAt(i) != DOTS_25 || text.charAt(i + 1) != DOTS_25) {
            return 0;
        }
        // Require a blank cell immediately before
        if (i != 0 && !isBlankSpace(text.charAt(i - 1)) && !isLineBreak(text.charAt(i - 1))) {
            return 0;
        }
        // DOTS_25 may be repeated more than twice
        int dashes = 2;
        while (dashes < text.length() - i && text.charAt(i + dashes) == DOTS_25) {
            dashes++;
        }

        // Require a blank cell immediately after
        if (i + dashes == text.length()) {
            return dashes;
        }
        char charAfter = text.charAt(i + dashes);
        if (isBlankSpace(charAfter) || isLineBreak(charAfter)) {
            return dashes;
        }
        return 0;
    }

    private static int findEllipses(String text, int i) {
        if (text.length() - i < 3) {
            return 0;
        }
        if (text.charAt(i) != DOTS_2 || text.charAt(i + 1) != DOTS_2 || text.charAt(i + 2) != DOTS_2) {
            return 0;
        }
        // DOTS_2 may be repeated more than three times
        int ellipses = 3;
        while (ellipses < text.length() - i && text.charAt(i + ellipses) == DOTS_2) {
            ellipses++;
        }
        // 原則として、棒線と点線の前後は必ず一マスあけである。
        // ただし、点線を語頭や語中に使うときは、後ろにマスあけしなくてもよい。句点が続くときも、後はマスあけをしない。
        // 前は必ずマスあけをする。マスあけをしないと、「っっっ」になってしまう。
        if (i == 0) {
            return ellipses;
        }
        char charBefore = text.charAt(i - 1);
        if (isBlankSpace(charBefore) || isLineBreak(charBefore)) {
            return ellipses;
        }
        return 0;
    }

    @Override
    protected void process(String brailleStr, int param) {
        String str = brailleToText(brailleStr);
        setString(this.kanada.process(str));
    }

    private boolean setBrailleMode(char c1, char c2) {
        return switch (c1) {
            // 数字符
            case DOTS_3456 -> {
                currentMode = BrailleMode.NUMBER;
                yield true;
            }
            // 外字符
            case DOTS_56 -> {
                if (c2 == DOTS_3 || c2 == DOTS_36 || c2 == DOTS_2356 || c2 == DOTS_0 || c2 == '\n') {
                    // 第２カギ、二重カギ、二重カッコの開始、読点
                    resetBrailleMode();
                    yield false;
                } else {
                    currentMode = BrailleMode.LATIN;
                    yield true;
                }
            }
            // 大文字符 / 二重大文字符（２連続で）/ 半濁音符
            case DOTS_6 -> {
                if (currentMode == BrailleMode.LATIN) {
                    currentMode = BrailleMode.LATIN_CAPITAL;
                } else if (currentMode == BrailleMode.LATIN_CAPITAL) {
                    currentMode = BrailleMode.LATIN_CAPITAL_ALL;
                } else {
                    resetBrailleMode(); // 半濁音符
                    yield false;
                }
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
        boolean parenthesisSecondaryIn = false;
        boolean parenthesisDoubleIn = false;
        boolean cornerBracketIn = false;
        boolean cornerBracketDoubleIn = false;
        boolean cornerBracketSecondaryIn = false;
        boolean translatorsNoteIn = false;
        boolean latinQuoteIn = false;

        for (int i = 0; i < brailleText.length(); i++) {
            char thisChar = brailleText.charAt(i);
            Character.UnicodeBlock block = Character.UnicodeBlock.of(thisChar);
            if (block != Character.UnicodeBlock.BRAILLE_PATTERNS && !isLineBreak(thisChar)) {
                matchedLength = i;
                return result.toString();
            }
            // Store punctuation indicator
            if (isPunctuation(thisChar)) {
                punctuation = thisChar;
            }

            // Store next char for an additional clue
            char nextChar = 0;
            if (i + 1 < brailleText.length()) {
                nextChar = brailleText.charAt(i + 1);
            }

            // Update braille mode
            if (setBrailleMode(thisChar, nextChar)) {
                continue;
            }

            // 情報処理用点字表記
            // TODO: 外国語引用符との区別の仕方
            if (thisChar == DOTS_6 && nextChar == DOTS_236 && !latinQuoteIn) {
                currentMode = BrailleMode.LATIN;
                latinQuoteIn = true;
                i += 1;
                continue;
            }
            if (thisChar == DOTS_6 && nextChar == DOTS_356 && latinQuoteIn) {
                resetBrailleMode();
                latinQuoteIn = false;
                i += 1;
                continue;
            }

            // 第１小見出し符
            if (thisChar == DOTS_6 && nextChar == DOTS_36) {
                if (i + 2 < brailleText.length()) {
                    char nextNextChar = brailleText.charAt(i + 2);
                    if (nextNextChar == DOTS_0 || nextNextChar == '\n') {
                        result.append("  ");
                        i += 1;
                        continue;
                    }
                }
            }

            // 外国語引用符
            if (thisChar == DOTS_236 && !latinQuoteIn) {
                currentMode = BrailleMode.LATIN;
                latinQuoteIn = true;
                continue;
            }

            // Punctuation marks
            if (punctuation > 0) {
                String punctuationStr = getPunctuation(thisChar, nextChar, punctuation);
                if (punctuationStr != null) {
                    result.append(punctuationStr);
                    if (punctuationStr.length() > 2) {
                        i += 1; // 句点の２つ目の空白を飛ばす
                    }
                    punctuation = 0;
                    continue;
                }
            }

            // Braille blank space
            if (thisChar == DOTS_0) {
                result.append(" ");
                if (!latinQuoteIn) {
                    resetBrailleMode(); // 空白または改行なので外字符などの効力が切れる
                }
                continue;
            }

            // Numerals
            if (currentMode == BrailleMode.NUMBER) {
                String number = getNumeric(thisChar, nextChar, latinQuoteIn);
                if (number != null) {
                    result.append(number);
                    continue;
                }
            }

            // Latin characters
            if (currentMode == BrailleMode.LATIN ||
                    currentMode == BrailleMode.LATIN_CAPITAL ||
                    currentMode == BrailleMode.LATIN_CAPITAL_ALL) {
                String latin = getLatin(thisChar, latinQuoteIn);
                if (latin != null) {
                    result.append(latin);
                    punctuation = 0; // 読点ではなく外字符のため punctuation をリセット
                    continue;
                }
                if (latinQuoteIn) {
                    if (thisChar == DOTS_4) {
                        // 情報処理用点字の行継続符
                        continue;
                    }
                    if (thisChar == DOTS_5 && nextChar == DOTS_36) {
                        result.append("_");
                        punctuation = 0;
                        i += 1; // 情報処理用点字の組み合わせなので一つ飛ばす
                        continue;
                    }
                    if (thisChar == DOTS_356) {
                        resetBrailleMode(); // 外国語引用符（終了）
                        if (nextChar == DOTS_36) {
                            i += 1; // 次が第一つなぎ符なので一つ飛ばす
                        }
                        latinQuoteIn = false;
                        continue;
                    }
                } else {
                    // 外字符の効力が切れた
                    resetBrailleMode();
                    if (thisChar == DOTS_36) {
                        // 第一つなぎ符なのでスキップ
                        continue;
                    }
                }
            }

            // Wave dash
            if (thisChar == DOTS_36 && nextChar == DOTS_36) {
                result.append("〜");
                i += 1;
                continue;
            }

            // Brackets
            BracketType thisBracket = getBracketType(thisChar, nextChar);
            if (thisBracket != null) {
                switch (thisBracket) {
                    case PARENTHESIS -> {
                        parenthesisIn = !parenthesisIn;
                        result.append(parenthesisIn ? "（" : "）");
                        continue;
                    }
                    case SECONDARY_PARENTHESIS -> {
                        parenthesisSecondaryIn = !parenthesisSecondaryIn;
                        result.append(parenthesisSecondaryIn ? "〈" : "〉");
                        i += 1;
                        continue;
                    }
                    case DOUBLE_PARENTHESIS -> {
                        parenthesisDoubleIn = !parenthesisDoubleIn;
                        result.append(parenthesisDoubleIn ? "⸨" : "⸩");
                        i += 1;
                        continue;
                    }
                    case CORNER_BRACKET -> {
                        cornerBracketIn = !cornerBracketIn;
                        result.append(cornerBracketIn ? "「" : "」");
                        continue;
                    }
                    case SECONDARY_CORNER_BRACKET -> {
                        cornerBracketSecondaryIn = !cornerBracketSecondaryIn;
                        result.append(cornerBracketSecondaryIn ? "〔" : "〕");
                        i += 1;
                        continue;
                    }
                    case DOUBLE_CORNER_BRACKET -> {
                        cornerBracketDoubleIn = !cornerBracketDoubleIn;
                        result.append(cornerBracketDoubleIn ? "『" : "』");
                        i += 1;
                        continue;
                    }
                    case TRANSLATORS_NOTE -> {
                        translatorsNoteIn = !translatorsNoteIn;
                        result.append(translatorsNoteIn ? "（（" : "））");
                        i += 1;
                        continue;
                    }
                }
            }

            // Ellipses
            int ellipses = findEllipses(brailleText, i);
            if (thisChar == DOTS_2 && ellipses > 0) {
                result.append("…".repeat(ellipses));
                i += ellipses - 1;
                continue;
            }

            // Dashes
            int dashes = findDashes(brailleText, i);
            if (thisChar == DOTS_25 && dashes > 0) {
                result.append("―".repeat(dashes));
                i += dashes - 1;
                continue;
            }

            // Stars
            StarType star = getStarType(brailleText, i);
            if (star != null) {
                switch (star) {
                    case FIRST_STAR -> result.append("★");
                    case SECOND_STAR -> result.append("☆");
                    case THIRD_STAR -> result.append("◇");
                }
                i += 2;
                continue;
            }

            // Kana characters
            String kana = getKana(thisChar);
            if (kana != null) {
                result.append(kana);
                continue;
            }
            kana = getKana(thisChar, nextChar);
            if (kana != null) {
                result.append(kana);
                i += 1;
                if (isPunctuation(thisChar)) {
                    punctuation = 0; // 中点・句点ではなく濁音・特殊音のため punctuation をリセット
                }
                continue;
            }

            if (punctuation != 0) {
                // Process nakaten / kuten on the next path
                continue;
            }

            if (Character.isWhitespace(thisChar)) {
                result.append(thisChar);
            } else {
                System.err.println("Unknown character: " + thisChar);
            }
        }
        matchedLength = brailleText.length();
        return result.toString();
    }

    private String getPunctuation(char thisChar, char nextChar, char punctuation) {
        String result = null;
        if (punctuation == DOTS_5 && thisChar == DOTS_5 && nextChar == DOTS_5) {
            return "・"; // おそらく点線のつもり（DOTS-2とDOTS-5を誤用している）
        }
        if (!isLineBreak(thisChar) && !isBlankSpace(thisChar)) {
            return null;
        }
        String mark = BrailleMapping.KUTOUTEN_MAP.get(punctuation);
        if (mark.equals("、") || mark.equals("・")) {
            if (isLineBreak(thisChar)) {
                result = mark + thisChar; // 改行は残す
            } else if (isBlankSpace(thisChar)) {
                result = mark; // 空白は消す
            } else {
                return null;
            }
            resetBrailleMode();
        }
        if (mark.equals("。") || mark.equals("！") || mark.equals("？")) {
            if (isLineBreak(thisChar)) {
                result = mark + thisChar;
            } else if (isBlankSpace(thisChar) && isBlankSpace(nextChar)) {
                result = mark;
            } else {
                return null;
            }
            resetBrailleMode();
        }
        return result;
    }

    private String getNumeric(char thisChar, char nextChar, boolean latinQuoteIn) {
        if (thisChar == DOTS_36) {
            // 次があ行・ら行の場合は、第一つなぎ符
            String nextDigit = BrailleMapping.DIGIT_MAP.get(nextChar);
            if (nextDigit != null && nextDigit.length() == 1 && Character.isDigit(nextDigit.charAt(0))) {
                currentMode = latinQuoteIn ? BrailleMode.LATIN : BrailleMode.KANA;
                return "";  // 数字の終端なので、nullではなく空文字を返して次の文字に進む
            } else if (nextChar == DOTS_3456 || nextChar == DOTS_56) {
                return "-"; // 次が数字符・外字符なのでハイフンとみなす
            }
        }
        String digit = BrailleMapping.DIGIT_MAP.get(thisChar);
        if (digit == null) {
            currentMode = latinQuoteIn ? BrailleMode.LATIN : BrailleMode.KANA;
        }
        return digit;
    }

    private String getLatin(char thisChar, boolean latinQuoteIn) {
        String result = null;
        // 第一つなぎ符の場合は、かな表記にリセット
        if (thisChar == DOTS_36 && !latinQuoteIn) {
            resetBrailleMode();
            return null;
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
        String kana = BrailleMapping.KANA_MAP.get(thisChar);
        if (kana != null) {
            resetBrailleMode();
        }
        return kana;
    }

    private String getKana(char thisChar, char nextChar) {
        String result = null;
        if (thisChar == DOTS_456 && isLineBreak(nextChar)) {
            return "｜" + nextChar; // 枠線（閉じ） として使われている
        }
        String kana = BrailleMapping.KANA_MAP.get(nextChar);
        if (kana == null) {
            return null;
        }
        switch (thisChar) {
            case DOTS_4 -> result = BrailleMapping.toYouon(kana);
            case DOTS_5 -> result = BrailleMapping.toDakuon(kana);
            case DOTS_6 -> result = BrailleMapping.toHandakuon(kana);
            case DOTS_45 -> result = BrailleMapping.toYouDakuon(kana);
            case DOTS_46 -> result = BrailleMapping.toYouHandakuon(kana);
            case DOTS_26 -> result = BrailleMapping.toSpecial26(kana);
            case DOTS_256 -> result = BrailleMapping.toSpecial256(kana);
            case DOTS_456 -> result = BrailleMapping.toSpecial456(kana);
            case DOTS_56 -> result = BrailleMapping.toSpecial56(kana);
        }
        if (result != null) {
            resetBrailleMode();
        }
        return result;
    }

    private enum BracketType {
        PARENTHESIS,
        SECONDARY_PARENTHESIS,
        DOUBLE_PARENTHESIS,
        CORNER_BRACKET,
        SECONDARY_CORNER_BRACKET,
        DOUBLE_CORNER_BRACKET,
        TRANSLATORS_NOTE,
    }

    private enum StarType {
        FIRST_STAR,
        SECOND_STAR,
        THIRD_STAR
    }

    private enum BrailleMode {
        NUMBER,
        LATIN,
        LATIN_CAPITAL,
        LATIN_CAPITAL_ALL,
        KANA
    }
}