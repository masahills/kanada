/**
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
package com.iciao.kanada;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Parse strings and look up the kanji dictionary.<br>
 *
 * @author Masahiko Sato
 */
public class KanjiParser {
    private final static Kanwadict kanwa = Kanwadict.getKanwadict();
    private final Kanada kanada;
    private final JWriter jWriter;
    private final StringBuilder outputBuffer;

    public KanjiParser(JWriter writer) {
        kanada = writer.getKanada();
        jWriter = writer;
        outputBuffer = new StringBuilder();
    }

    public String parse(String inputString) throws Exception {
        int strLen = inputString.length();
        outputBuffer.ensureCapacity(strLen);

        for (int i = 0; i < strLen; i++) {
            int thisChar = inputString.codePointAt(i);

            if (i > 0 && kanada.modeAddSpace) {
                int prevChar = inputString.codePointAt(i - 1);
                Character.UnicodeBlock prevBlock = Character.UnicodeBlock.of(prevChar);
                Character.UnicodeBlock currentBlock = Character.UnicodeBlock.of(thisChar);
                if (prevBlock != currentBlock) {
                    // Flush the buffer at the word boundary.
                    flushBuffer(true);
                }
            }

            if (!Pattern.matches("[\\p{IsHiragana}\\p{IsKatakana}\\p{IsHan}]",
                    String.valueOf(Character.toChars(thisChar)))) {
                jWriter.append(thisChar);
                continue;
            }

            Kanwadict.KanwaKey key = kanwa.getKey(thisChar);
            List<Kanwadict.YomiKanjiData> valueList = new ArrayList<>();

            if (kanwa.searchKey(key)) {
                valueList = kanwa.getValue(key);
            }

            if (valueList.isEmpty()) {
                jWriter.append(thisChar);
                continue;
            }

            // Flush non-dictionary characters before looking up the dictionary.
            flushBuffer(true);

            Iterator<Kanwadict.YomiKanjiData> dicIterator = valueList.iterator();

            int matchedLen = 0;
            String yomi = "";
            String kanji = "";
            int tail = ' ';

            while (dicIterator.hasNext()) {
                Kanwadict.YomiKanjiData term = dicIterator.next();

                int searchLen = term.getLength();
                if ((i + searchLen) > inputString.length() || searchLen <= matchedLen) {
                    continue;
                }

                String searchWord = inputString.substring(i, i + searchLen);

                if (searchWord.equals(term.kanji())) {
                    if (term.tail() == ' ') {
                        kanji = term.kanji();
                        yomi = term.yomi();
                        tail = term.tail();
                        matchedLen = searchLen;
                    } else if (inputString.length() > searchWord.length()) {
                        kanji = term.kanji();
                        yomi = term.yomi();
                        tail = term.tail();
                        matchedLen = searchLen;
                    }
                }
            }
            jWriter.tail = tail;

            if (matchedLen > 0 && !yomi.isEmpty()) {
                if (kanada.optionKanji == JMapper.AS_IS) {
                    jWriter.append(kanji);
                } else {
                    jWriter.append(yomi);
                }
                flushBuffer(true);
                i = i + matchedLen - 1;
            } else {
                outputBuffer.appendCodePoint(thisChar);
            }
        }

        // Flush the remaining characters in the buffer.
        flushBuffer(false);

        return outputBuffer.toString();
    }

    private void flushBuffer(boolean isBoundary) {
        if (!jWriter.buffer.isEmpty()) {
            if (isBoundary && kanada.modeAddSpace && jWriter.tail == ' ') {
                jWriter.append(' ');
            }
            StringBuilder str = jWriter.map();
            outputBuffer.append(str);
            jWriter.clear();
        }
    }
}
