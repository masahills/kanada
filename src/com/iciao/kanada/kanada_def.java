/**
 * Kanada (Kanji-Kana Transliteration Library for Java)
 * Copyright (C) 2002-2014 Masahiko Sato
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package com.iciao.kanada;

/**
 * parameters and conversion tables for non-Kanji character.<br>
 *
 * @author Masahiko Sato
 */
public class kanada_def {
    public static final int FLAG_ADD_SPACE = 0x00000001;
    public static final int FLAG_UC_FIRST = 0x00000002;
    public static final int FLAG_UC_ALL = 0x00000004;
    public static final int FLAG_FURIGANA = 0x00000008;
    public static final int FLAG_SHOW_ALL_YOMI = 0x00000010;
    public static final int FLAG_KUNREI_ROMAJI = 0x00000020;

    public static final int ENCODING_SJIS = 0;
    public static final int ENCODING_JIS = 1;
    public static final int ENCODING_EUC_JP = 2;
    public static final int ENCODING_AUTO_DETECT = 3;

    public static final int CONFIG_GET_AS_IS = -1;
    public static final int CONFIG_GET_ROMAJI = 0;
    public static final int CONFIG_GET_HIRAGANA = 1;
    public static final int CONFIG_GET_KATAKANA = 2;
    public static final int CONFIG_HALF_TO_WIDE_ALL = 3;
    public static final int CONFIG_HALF_TO_WIDE_KANA = 4;

    protected static final String JDK_ISO8859_1 = "ISO8859_1";
    protected static final String JDK_SJIS = "SJIS";
    protected static final String DEFAULT_ENCODING_NAME = JDK_SJIS;
    protected static final String JDK_JIS = "JIS";
    protected static final String JDK_EUC_JP = "EUC_JP";
    protected static final String JDK_JIS_AUTO_DETECT = "JISAutoDetect";

}

/*
 * $History: $
 */
