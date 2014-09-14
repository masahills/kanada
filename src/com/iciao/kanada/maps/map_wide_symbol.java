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
public class map_wide_symbol extends j_mapper {
    public map_wide_symbol() {
        this(0, null);
    }

    protected map_wide_symbol(int count, String str) {
        super(count, str);
    }

    protected void process(String str, int param) {
        int i = 0;
        StringBuilder out = new StringBuilder();

        char first_char = str.charAt(0);

        try {
            char second_char = str.charAt(1);

            if (second_char < 0xa1) {
                // Out of range. Not a Japanese character.
                out.append(first_char);
                out.append(second_char);
                i = 2;
            }

            switch (first_char) {
                case 0xa1: {
                    out.append(wide_symbol_a1_to_ascii[second_char - 0xa1]);
                    i = 2;
                    break;
                }
                case 0xa2: {
                    out.append(wide_symbol_a2_to_ascii[second_char - 0xa1]);
                    i = 2;
                    break;
                }
                case 0xa3: {
                    if (second_char < 0x80) {
                        // Out of range. Not a Japanese character.
                        out.append(first_char);
                        out.append(second_char);
                        i = 2;
                    } else {
                        out.append((char) (second_char - 0x80));
                        i = 2;
                    }
                    break;
                }
                case 0xa6: {
                    out.append(wide_symbol_a6_to_ascii[second_char - 0xa1]);
                    i = 2;
                    break;
                }
                default: {
                    // You sould never get here.
                    out.append(first_char);
                    out.append(second_char);
                    i = 2;
                    break;
                }
            }
        }
        // ArrayIndexOutOfBoundsException or StringIndexOutOfBoundsException may occur
        // while processing non-Japanese double byte texts or other non-Ascii chars.
        catch (Exception e) {
            out.append(first_char);
            i = 1;
        }

        set_int(i);
        set_string(out.toString());
    }
}

/*
 * $History: $
 */
