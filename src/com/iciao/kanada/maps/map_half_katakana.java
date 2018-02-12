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
package com.iciao.kanada.maps;

import com.iciao.kanada.j_mapper;

/**
 * Remap non-kanji characters.<br>
 *
 * @author Masahiko Sato
 */
public class map_half_katakana extends j_mapper {
    public map_half_katakana() {
        this(null);
    }

    protected map_half_katakana(String str) {
        super(str);
    }

    protected void process(String str, int param) {
        StringBuilder out = new StringBuilder();
        int this_char = str.codePointAt(0);
        out.appendCodePoint(this_char);
        set_string(out.toString());
    }
}
