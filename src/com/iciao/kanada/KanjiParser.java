/**
 * Kanada (Kanji-Kana Transliteration Library for Java)
 * Copyright (C) 2002-2014 Masahiko Sato
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package com.iciao.kanada;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Parse strings and look up Kanji dictionary.<br>
 *
 * @author Masahiko Sato
 */
public class KanjiParser {
    private static Kanwadict kanwa = Kanwadict.getKanwa();
    private Kanada kanada;
    private JWriter jWriter;
    private StringBuilder outputBuffer;

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

            if (!Pattern.matches("[\\p{IsHiragana}\\p{IsKatakana}\\p{IsHan}]",
                    String.valueOf(Character.toChars(thisChar)))) {
                jWriter.append(thisChar);
                continue;
            }

            Kanwadict.KanwaKey key = kanwa.getKey(thisChar);
            List valueList = new ArrayList();

            if (kanwa.searchKey(key)) {
                valueList = kanwa.getValue(key);
            }

            if (valueList.isEmpty()) {
                jWriter.append(thisChar);
                continue;
            }

            if (jWriter.buffer.length() > 0) {
                if (kanada.modeAddSpace && outputBuffer.length() > 0) {
                    int nextChar = 0;
                    if (i < strLen - 1) {
                        nextChar = inputString.codePointAt(i);
                    }
                    if (!Pattern.matches("[\\p{Cntrl}\\p{IsCommon}]", String.valueOf(Character.toChars(nextChar)))
                            && !Pattern.matches("(?s).*?[\\p{IsCommon}]$", jWriter.buffer.toString())) {
                        jWriter.append(' ');
                    }
//                    System.out.println("### '" + jWriter.buffer.toString()
//                            + "':'" + String.valueOf(Character.toChars(nextChar)) + "'");
                }
                StringBuilder nonDicStr = jWriter.map();
                outputBuffer.append(nonDicStr);
                jWriter.clear();
            }

            Iterator dicIterator = valueList.iterator();

            int matchedLen = 0;
            String yomi = "";
            String kanji = "";
            int tail = ' ';

            while (dicIterator.hasNext()) {
                Kanwadict.YomiKanjiData term = (Kanwadict.YomiKanjiData) dicIterator.next();

                int searchLen = term.getLength();
                if ((i + searchLen) > inputString.length() || searchLen <= matchedLen) {
                    continue;
                }

                String searchWord = inputString.substring(i, i + searchLen);

                if (searchWord.equals(term.getKanji())) {
                    if (term.getTail() == ' ') {
                        kanji = term.getKanji();
                        yomi = term.getYomi();
                        tail = term.getTail();
                        matchedLen = searchLen;
                    } else if (inputString.length() > searchWord.length()) {
                        kanji = term.getKanji();
                        yomi = term.getYomi();
                        tail = term.getTail();
                        matchedLen = searchLen;
                    }
                }
            }

            if (matchedLen == 0 || yomi.length() == 0) {
                outputBuffer.appendCodePoint(thisChar);
            } else {
                if (kanada.optionKanji == Kanada.CONFIG_GET_AS_IS) {
                    jWriter.append(kanji);
                } else {
                    jWriter.append(yomi);
                }
                if (jWriter.buffer.length() > 0) {
                    int nextChar = 0;
                    if (kanada.modeAddSpace && tail == ' ') {
                        if (i < strLen - matchedLen - 1) {
                            nextChar = inputString.codePointAt(i + matchedLen);
                        }
                        if (!Pattern.matches("[\\p{Cntrl}\\p{IsCommon}]", String.valueOf(Character.toChars(nextChar)))) {
                            jWriter.append(' ');
                        }
                    }
//                    System.out.println(">>> '" + kanji
//                            + "':'" + String.valueOf(Character.toChars(tail))
//                            + "':'" + jWriter.buffer.toString()
//                            + "':'" + String.valueOf(Character.toChars(nextChar)) + "'");
                }
                StringBuilder dicStr = jWriter.map();
//                System.out.println("# '" + jWriter.buffer.toString() + "':'" + dicStr +"'");
                outputBuffer.append(dicStr);
                jWriter.clear();
                i = i + matchedLen - 1;
            }

            jWriter.tail = tail;
        }

        if (jWriter.buffer.length() > 0) {
            outputBuffer.append(jWriter.map());
            jWriter.clear();
        }

        return outputBuffer.toString();
    }
}

/*
 * $History: $
 */
