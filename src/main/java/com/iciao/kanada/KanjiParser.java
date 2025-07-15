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

            if (!jWriter.buffer.isEmpty()) {
                if (kanada.modeAddSpace && !outputBuffer.isEmpty()) {
                    int nextChar = 0;
                    if (i < strLen - 1) {
                        nextChar = inputString.codePointAt(i);
                    }
                    if (!Pattern.matches("[\\p{Cntrl}\\p{IsCommon}]", String.valueOf(Character.toChars(nextChar)))
                            && !Pattern.matches("(?s).*?\\p{IsCommon}$", jWriter.buffer.toString())) {
                        jWriter.append(' ');
                    }
                }
                StringBuilder nonDicStr = jWriter.map();
                outputBuffer.append(nonDicStr);
                jWriter.clear();
            }

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

            if (matchedLen == 0 || yomi.isEmpty()) {
                outputBuffer.appendCodePoint(thisChar);
            } else {
                if (kanada.optionKanji == JMapper.AS_IS) {
                    jWriter.append(kanji);
                } else {
                    jWriter.append(yomi);
                }
                if (!jWriter.buffer.isEmpty()) {
                    int nextChar = 0;
                    if (kanada.modeAddSpace && tail == ' ') {
                        if (i < strLen - matchedLen - 1) {
                            nextChar = inputString.codePointAt(i + matchedLen);
                        }
                        if (!Pattern.matches("[\\p{Cntrl}\\p{IsCommon}]", String.valueOf(Character.toChars(nextChar)))) {
                            jWriter.append(' ');
                        }
                    }
                }
                StringBuilder dicStr = jWriter.map();
                outputBuffer.append(dicStr);
                jWriter.clear();
                i = i + matchedLen - 1;
            }

            jWriter.tail = tail;
        }

        if (!jWriter.buffer.isEmpty()) {
            outputBuffer.append(jWriter.map());
            jWriter.clear();
        }

        return outputBuffer.toString();
    }
}

/*
 * $History: $
 */
