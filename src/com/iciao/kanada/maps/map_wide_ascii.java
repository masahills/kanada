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
package com.iciao.kanada.maps;

import com.iciao.kanada.j_mapper;

/**
 * Remap non-kanji characters.<br>
 *
 * @author Masahiko Sato
 */
public class map_wide_ascii extends j_mapper {
    public map_wide_ascii() {
        this(0, null);
    }

    protected map_wide_ascii(int count, String str) {
        super(count, str);
    }

    protected void process(String str, int param) {
        int i = 0;
        StringBuffer out = new StringBuffer();

        out.append((char) (str.charAt(1) - 0x80));

        if (out.length() == 0) {
            out.append('?');
        }

        i = 2;

        set_int(i);
        set_string(out.toString());
    }
}

/*
 * $History: $
 */
