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
public abstract class j_mapper extends kanada_def {
    protected int count_mbr;
    protected String out_str_mbr;

    protected j_mapper(int count, String str) {
        count_mbr = count;
        out_str_mbr = str;
    }

    protected int get_int() {
        return count_mbr;
    }

    protected void set_int(int count) {
        count_mbr = count;
    }

    protected String get_string() {
        return out_str_mbr;
    }

    protected void set_string(String str) {
        out_str_mbr = str;
    }

    protected abstract void process(String str, int param);
}

/*
 * $History: $
 */
