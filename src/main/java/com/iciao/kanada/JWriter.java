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
package com.iciao.kanada;

import com.iciao.kanada.maps.*;

import java.io.IOException;
import java.io.Writer;
import java.util.Locale;

/**
 * Main string buffer class.<br>
 *
 * @author Masahiko Sato
 */
class JWriter {
    protected Kanada kanada;
    private final JMapper ascii;
    private final JMapper braille;
    private final JMapper halfKatakana;
    private final JMapper halfSymbol;
    private final JMapper hiragana;
    private final JMapper katakana;
    private final JMapper wideAscii;
    private final JMapper wideSymbol;
    protected StringBuilder buffer = new StringBuilder();
    protected int tail;
    private boolean isTail;

    protected JWriter(Kanada kanada) {
        this.clear();
        this.kanada = kanada;
        this.ascii = new MapAscii(kanada);
        this.braille = new MapBraille(kanada);
        this.halfKatakana = new MapHalfKatakana(kanada);
        this.halfSymbol = new MapHalfSymbol(kanada);
        this.hiragana = new MapHiragana(kanada);
        this.katakana = new MapKatakana(kanada);
        this.wideAscii = new MapWideAscii(kanada);
        this.wideSymbol = new MapWideSymbol(kanada);
        tail = ' ';
        isTail = false;
    }

    protected StringBuilder append(int codePoint) {
        return buffer.appendCodePoint(codePoint);
    }

    protected StringBuilder append(char c) {
        return buffer.append(c);
    }

    protected StringBuilder append(String str) {
        return buffer.append(str);
    }

    protected void flushBuffer(Writer writer) throws IOException {
        if (writer == null) {
            throw new IllegalArgumentException("Writer must not be null");
        }
        if (buffer.isEmpty()) {
            return;
        }
        String converted = map().toString();
        writer.write(converted);
        writer.flush();
        clear();
    }

    protected void clear() {
        buffer.setLength(0);
    }

    protected Kanada getKanada() {
        return kanada;
    }

    private StringBuilder map() {
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
                if (kanada.optionAscii == JMapper.TO_WIDE_ASCII || kanada.optionAscii == JMapper.TO_KANA_BRAILLE) {
                    ascii.process(workStr, kanada.optionAscii);
                    mappedMapper = ascii;
                } else {
                    mappedStr.appendCodePoint(thisChar);
                }
            } else if (block == Character.UnicodeBlock.LATIN_1_SUPPLEMENT) {
                if (kanada.optionAscii == JMapper.TO_WIDE_ASCII) {
                    halfSymbol.process(workStr, kanada.optionAscii);
                    mappedMapper = halfSymbol;
                } else {
                    mappedStr.appendCodePoint(thisChar);
                }
            } else if (block == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS) {
                if (thisChar < 0xff61 || thisChar > 0xffdf) {
                    if (kanada.optionWideAscii == JMapper.TO_ASCII) {
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
                    case JMapper.TO_KANA_BRAILLE:
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
                    case JMapper.TO_KANA_BRAILLE:
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
                    case JMapper.TO_KANA_BRAILLE:
                        katakana.process(workStr, kanada.optionKatakana);
                        mappedMapper = katakana;
                        break;
                    default:
                        mappedStr.appendCodePoint(thisChar);
                        break;
                }
            } else if (block == Character.UnicodeBlock.BRAILLE_PATTERNS) {
                if (thisChar < 0x2840) {
                    braille.process(workStr, kanada.optionBraille);
                    mappedMapper = braille;
                } else {
                    mappedStr.appendCodePoint(thisChar);
                }
            } else {
                mappedStr.appendCodePoint(thisChar);
            }

            if (mappedMapper != null) {
                if (kanada.modeUcFirst && !isTail && i == 0) {
                    mappedStr.append(mappedMapper.getStringCapitalized());
                } else if (kanada.modeUcAll) {
                    mappedStr.append(mappedMapper.getStringUppercased());
                } else {
                    mappedStr.append(mappedMapper.getString());
                }
                i = i + mappedMapper.getProcessedLength() - 1;
            } else if (!kanada.modeUcRomajiOnly && (kanada.modeUcFirst || kanada.modeUcAll)) {
                if (kanada.modeUcFirst && !isTail && i == 0) {
                    mappedStr.setCharAt(0, Character.toUpperCase(mappedStr.charAt(0)));
                } else if (kanada.modeUcAll) {
                    String upperCased = mappedStr.toString().toUpperCase(Locale.ENGLISH);
                    mappedStr.setLength(0);
                    mappedStr.append(upperCased);
                }
            }
            outStr.append(mappedStr);
            mappedStr.setLength(0);
        }

        isTail = tail != ' ';
        tail = ' ';

        return outStr;
    }
}

