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

import java.util.Locale;

/**
 * Kanji-to-Kana/Romaji Conversion Utility.<br>
 * Convert a given Japanese string into Hiragana, Katakana, or Romaji string.
 *
 * @author Masahiko Sato
 */
public class Kanada {
    public static final int FLAG_ADD_SPACE = 0x00000001;
    public static final int FLAG_UC_FIRST = 0x00000002;
    public static final int FLAG_UC_ALL = 0x00000004;
    public static final int FLAG_FURIGANA = 0x00000008;
    public static final int FLAG_SHOW_ALL_YOMI = 0x00000010;
    public static final int FLAG_KUNREI_ROMAJI = 0x00000020;

    public static final int CONFIG_GET_AS_IS = -1;
    public static final int CONFIG_GET_ROMAJI = 0;
    public static final int CONFIG_GET_HIRAGANA = 1;
    public static final int CONFIG_GET_KATAKANA = 2;
    public static final int CONFIG_HALF_TO_WIDE_ALL = 3;
    public static final int CONFIG_HALF_TO_WIDE_KANA = 4;

    protected int optionKanji;
    protected int optionHiragana;
    protected int optionKatakana;
    protected int optionWideAscii;
    protected int optionWideSymbol;
    protected int optionHalfKatakana;
    protected int optionAscii;
    protected int optionHalfSymbol;
    protected boolean modeShowAllYomi = false;
    protected boolean modeAddSpace = false;
    protected boolean modeFurigana = false;
    protected boolean modeUcFirst = false;
    protected boolean modeUcAll = false;
    protected boolean modeKunreiRomaji = false;
    protected boolean modeWakachiKaki = false;

    public Kanada() throws java.io.IOException {
        setParam(CONFIG_GET_AS_IS);
    }

    public Kanada(int config) throws java.io.IOException {
        setParam(config);
    }

    public Kanada(int paramKanji,
                  int paramHiragana,
                  int paramKatakana,
                  int paramWideAscii,
                  int paramWideSymbol,
                  int paramHalfKatakana,
                  int paramAscii,
                  int paramHalfSymbol) throws java.io.IOException {
        setParam(paramKanji,
                paramHiragana,
                paramKatakana,
                paramWideAscii,
                paramWideSymbol,
                paramAscii,
                paramHalfKatakana,
                paramHalfSymbol);
    }

    private void setParam(int config) {
        switch (config) {
            case CONFIG_GET_ROMAJI: {
                setParam(
                        JMapper.TO_ASCII,
                        JMapper.TO_ASCII,
                        JMapper.TO_ASCII,
                        JMapper.TO_ASCII,
                        JMapper.TO_ASCII,
                        JMapper.AS_IS,
                        JMapper.TO_ASCII,
                        JMapper.TO_ASCII);
                break;
            }
            case CONFIG_GET_HIRAGANA: {
                setParam(
                        JMapper.TO_HIRAGANA,
                        JMapper.AS_IS,
                        JMapper.TO_HIRAGANA,
                        JMapper.AS_IS,
                        JMapper.AS_IS,
                        JMapper.TO_HIRAGANA,
                        JMapper.AS_IS,
                        JMapper.AS_IS);
                break;
            }
            case CONFIG_GET_KATAKANA: {
                setParam(JMapper.TO_KATAKANA,
                        JMapper.TO_KATAKANA,
                        JMapper.AS_IS,
                        JMapper.AS_IS,
                        JMapper.AS_IS,
                        JMapper.TO_KATAKANA,
                        JMapper.AS_IS,
                        JMapper.AS_IS);
                break;
            }
            case CONFIG_HALF_TO_WIDE_ALL: {
                setParam(
                        JMapper.AS_IS,
                        JMapper.AS_IS,
                        JMapper.AS_IS,
                        JMapper.AS_IS,
                        JMapper.TO_HIRAGANA,
                        JMapper.TO_WIDE_ASCII,
                        JMapper.TO_KATAKANA,
                        JMapper.TO_WIDE_SYMBOL);
                break;
            }
            default: {
                setParam(
                        JMapper.AS_IS,
                        JMapper.AS_IS,
                        JMapper.AS_IS,
                        JMapper.AS_IS,
                        JMapper.AS_IS,
                        JMapper.AS_IS,
                        JMapper.AS_IS,
                        JMapper.AS_IS);
                break;
            }
        }
    }

    private void setParam(int paramKanji,
                          int paramHiragana,
                          int paramKatakana,
                          int paramWideAscii,
                          int paramWideSymbol,
                          int paramAscii,
                          int paramHalfKatakana,
                          int paramHalfSymbol) {
        optionKanji = paramKanji;
        optionHiragana = paramHiragana;
        optionKatakana = paramKatakana;
        optionWideAscii = paramWideAscii;
        optionWideSymbol = paramWideSymbol;
        optionAscii = paramAscii;
        optionHalfKatakana = paramHalfKatakana;
        optionHalfSymbol = paramHalfSymbol;
    }

    public void setMode(int mode) {
        modeShowAllYomi = false;
        modeAddSpace = false;
        modeUcFirst = false;
        modeUcAll = false;
        modeKunreiRomaji = false;

        if ((mode & FLAG_ADD_SPACE) != 0) {
            modeAddSpace = true;
        }

        if ((mode & FLAG_UC_FIRST) != 0) {
            modeUcFirst = true;
            modeUcAll = false;
        }

        if ((mode & FLAG_UC_ALL) != 0) {
            modeUcFirst = false;
            modeUcAll = true;
        }

        // TODO: To be implemented
        if ((mode & FLAG_FURIGANA) != 0) {
            modeUcAll = true;
        }

        if ((mode & FLAG_SHOW_ALL_YOMI) != 0) {
            modeShowAllYomi = true;
        }

        if ((mode & FLAG_KUNREI_ROMAJI) != 0) {
            modeKunreiRomaji = true;
        }
    }

    public String process(String str, int mode) {
        setMode(mode);
        return process(str);
    }

    public String process(String str, boolean addSpace) {
        boolean savedStatus = modeAddSpace;

        if (addSpace) {
            modeAddSpace = true;
        }

        String processed = process(str);

        if (addSpace) {
            modeAddSpace = savedStatus;
        }

        return processed;
    }

    public String process(String str) {
        if (str == null) {
            return str;
        }

        String parsedStr;
        try {
            JWriter writer = new JWriter(this);
            KanjiParser parser = new KanjiParser(writer);
            parsedStr = parser.parse(str);
            if (modeUcAll) {
                parsedStr = parsedStr.toUpperCase(Locale.ENGLISH);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return str;
        }

        return parsedStr;
    }
}
