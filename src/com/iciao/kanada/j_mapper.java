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
 * Remap non-kanji characters.<br>
 *
 * @author Masahiko Sato
 */
public abstract class j_mapper {
    public static final int AS_IS = -1;
    public static final int TO_HIRAGANA = 0;
    public static final int TO_KATAKANA = 1;
    public static final int TO_WIDE_ASCII = 2;
    public static final int TO_WIDE_SYMBOL = 3;
    public static final int TO_HALF_KATAKANA = 4;
    public static final int TO_ASCII = 5;
    public static final int TO_HALF_SYMBOL = 6;
    protected String out_str_mbr;

    protected j_mapper(String str) {
        out_str_mbr = str;
    }

    protected String get_string() {
        return out_str_mbr;
    }

    protected void set_string(String str) {
        out_str_mbr = str;
    }

    protected abstract void process(String str, int param);
}
