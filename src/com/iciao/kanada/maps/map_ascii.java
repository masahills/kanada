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
public class map_ascii extends j_mapper {
    private static final String ascii_to_wide_ascii[] = {
            "\241\241", "\241\252", "\241\311", "\241\364", "\241\360", "\241\363", "\241\365", "\241\307",
            "\241\312", "\241\313", "\241\366", "\241\334", "\241\244", "\241\335", "\241\245", "\241\277",
            "\243\260", "\243\261", "\243\262", "\243\263", "\243\264", "\243\265", "\243\266", "\243\267",
            "\243\270", "\243\271", "\241\247", "\241\250", "\241\343", "\241\341", "\241\344", "\241\251",
            "\241\367", "\243\301", "\243\302", "\243\303", "\243\304", "\243\305", "\243\306", "\243\307",
            "\243\310", "\243\311", "\243\312", "\243\313", "\243\314", "\243\315", "\243\316", "\243\317",
            "\243\320", "\243\321", "\243\322", "\243\323", "\243\324", "\243\325", "\243\326", "\243\327",
            "\243\330", "\243\331", "\243\332", "\241\316", "\241\300", "\241\317", "\241\260", "\241\262",
            "\241\256", "\243\341", "\243\342", "\243\343", "\243\344", "\243\345", "\243\346", "\243\347",
            "\243\350", "\243\351", "\243\352", "\243\353", "\243\354", "\243\355", "\243\356", "\243\357",
            "\243\360", "\243\361", "\243\362", "\243\363", "\243\364", "\243\365", "\243\366", "\243\367",
            "\243\370", "\243\371", "\243\372", "\241\320", "\241\303", "\241\321", "\241\301"};

    public map_ascii() {
        this(0, null);
    }

    protected map_ascii(int count, String str) {
        super(count, str);
    }

    protected void process(String str, int param) {
        int i = 0;
        StringBuilder out = new StringBuilder();

        char first_char = str.charAt(0);
        out.append(first_char);
        i = 1;

        set_int(i);
        set_string(out.toString());
    }

}
