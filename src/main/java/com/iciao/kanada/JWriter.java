package com.iciao.kanada;

import com.iciao.kanada.maps.*;

import java.util.Locale;
import java.util.StringTokenizer;

/**
 * Main string buffer class.<br>
 *
 * @author Masahiko Sato
 */
public class JWriter {
    protected Kanada kanada;
    protected StringBuilder buffer = new StringBuilder();
    protected int tail;
    private boolean isTail;

    public JWriter(Kanada thisKanada) {
        this.clear();
        kanada = thisKanada;
        tail = ' ';
        isTail = false;
    }

    public StringBuilder append(int codePoint) {
        return buffer.appendCodePoint(codePoint);
    }

    public StringBuilder append(String str) {
        return buffer.append(str);
    }

    public void clear() {
        buffer.setLength(0);
    }

    public Kanada getKanada() {
        return kanada;
    }

    public StringBuilder map() {
        StringBuilder mappedStr = new StringBuilder();
        StringBuilder outStr = new StringBuilder();
        int totalLen = buffer.length();
        String workStr;

        for (int i = 0; i < totalLen; i++) {
            int thisChar = buffer.codePointAt(i);
            Character.UnicodeBlock block = Character.UnicodeBlock.of(thisChar);
            workStr = buffer.substring(i, buffer.length());
            JMapper mappedMapper = null;

            if (block == Character.UnicodeBlock.BASIC_LATIN) {
                if (kanada.optionAscii == JMapper.TO_WIDE_ASCII) {
                    JMapper ascii = new MapAscii(kanada);
                    ascii.process(workStr, kanada.optionAscii);
                    mappedMapper = ascii;
                } else {
                    mappedStr.appendCodePoint(thisChar);
                }
            } else if (block == Character.UnicodeBlock.LATIN_1_SUPPLEMENT) {
                if (kanada.optionAscii == JMapper.TO_WIDE_ASCII) {
                    JMapper halfSymbol = new MapHalfSymbol(kanada);
                    halfSymbol.process(workStr, kanada.optionAscii);
                    mappedMapper = halfSymbol;
                } else {
                    mappedStr.appendCodePoint(thisChar);
                }
            } else if (block == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS) {
                if (thisChar < 0xff61 || thisChar > 0xffdf) {
                    if (kanada.optionWideAscii == JMapper.TO_ASCII) {
                        JMapper wideAscii = new MapWideAscii(kanada);
                        wideAscii.process(workStr, kanada.optionWideAscii);
                        mappedMapper = wideAscii;
                    } else {
                        mappedStr.appendCodePoint(thisChar);
                    }
                } else if (thisChar < 0xffa0) {
                    switch (kanada.optionHalfKatakana) {
                        case JMapper.TO_WIDE_ASCII:
                        case JMapper.TO_ASCII:
                        case JMapper.TO_KATAKANA:
                        case JMapper.TO_HIRAGANA:
                            JMapper halfKatakana = new MapHalfKatakana(kanada);
                            halfKatakana.process(workStr, kanada.optionHalfKatakana);
                            mappedMapper = halfKatakana;
                            break;
                        default:
                            mappedStr.appendCodePoint(thisChar);
                            break;
                    }
                }
            } else if (block == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION) {
                switch (kanada.optionWideSymbol) {
                    case JMapper.TO_ASCII:
                    case JMapper.TO_HALF_SYMBOL:
                        JMapper wideSymbol = new MapWideSymbol(kanada);
                        wideSymbol.process(workStr, kanada.optionWideSymbol);
                        mappedMapper = wideSymbol;
                        break;
                    default:
                        mappedStr.appendCodePoint(thisChar);
                        break;
                }
            } else if (block == Character.UnicodeBlock.HIRAGANA) {
                switch (kanada.optionHiragana) {
                    case JMapper.TO_KATAKANA:
                    case JMapper.TO_HALF_KATAKANA:
                    case JMapper.TO_ASCII:
                    case JMapper.TO_WIDE_ASCII:
                        JMapper hiragana = new MapHiragana(kanada);
                        hiragana.process(workStr, kanada.optionHiragana);
                        mappedMapper = hiragana;
                        break;
                    default:
                        mappedStr.appendCodePoint(thisChar);
                        break;
                }
            } else if (block == Character.UnicodeBlock.KATAKANA
                    || block == Character.UnicodeBlock.KATAKANA_PHONETIC_EXTENSIONS) {
                switch (kanada.optionKatakana) {
                    case JMapper.TO_HIRAGANA:
                    case JMapper.TO_HALF_KATAKANA:
                    case JMapper.TO_ASCII:
                    case JMapper.TO_WIDE_ASCII:
                        JMapper katakana = new MapKatakana(kanada);
                        katakana.process(workStr, kanada.optionKatakana);
                        mappedMapper = katakana;
                        break;
                    default:
                        mappedStr.appendCodePoint(thisChar);
                        break;
                }
            } else {
                mappedStr.appendCodePoint(thisChar);
            }

            if (mappedMapper != null) {
                mappedStr.append(mappedMapper.getString());
                i = i + mappedMapper.getProcessedLength() - 1;
            }
            outStr.append(mappedStr);
            mappedStr.setLength(0);
        }

        if (kanada.modeUcFirst && !isTail) {
            StringBuilder sb = new StringBuilder();
            StringTokenizer token = new StringTokenizer(outStr.toString(), " \t\n\r\f", true);
            while (token.hasMoreTokens()) {
                String word = token.nextToken();
                sb.append(word.substring(0, 1).toUpperCase(Locale.ENGLISH)).append(word.substring(1));
            }
            outStr.setLength(0);
            outStr.append(sb);
        }

        isTail = tail != ' ';
        tail = ' ';

        return outStr;
    }
}

