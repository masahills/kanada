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

import com.iciao.kanada.maps.*;

import java.util.Locale;
import java.util.StringTokenizer;

/**
 * Main string buffer class.<br>
 *
 * @author Masahiko Sato
 */
public class j_writer {
    protected kanada kanada_mbr;
    protected StringBuffer buffer_mbr = new StringBuffer();
    protected char tail_mbr;

    public j_writer(kanada this_kanada) throws java.io.IOException {
        this.clear();
        kanada_mbr = this_kanada;
        tail_mbr = ' ';
    }

    public j_writer(kanada this_kanada, StringBuffer input) throws java.io.IOException {
        this(this_kanada);
        buffer_mbr = input;
        tail_mbr = ' ';
    }

    public StringBuffer append(char ch) {
        return buffer_mbr.append(ch);
    }

    public StringBuffer append(String str) {
        return buffer_mbr.append(str);
    }

    public void clear() {
        buffer_mbr.setLength(0);
    }

    public kanada get_kanada() {
        return kanada_mbr;
    }

    public StringBuffer map() {
        String temp_str;
        StringBuffer mapped_str = new StringBuffer();
        StringBuffer out_str = new StringBuffer();

        int i = 0;
        int last_type = 0;

        for (; ; ) {
            if (i > buffer_mbr.length() - 1) {
                break;
            }

            char first_char = buffer_mbr.charAt(i);

            temp_str = buffer_mbr.substring(i, buffer_mbr.length());

            j_mapper mapped = null;

            if (first_char < 0xa0 && first_char != 0x8e && first_char != 0x8f) {
                switch (kanada_mbr.option_ascii_mbr) {
                    case kanada_def.TO_WIDE_ASCII: {
                        j_mapper ascii = new map_ascii();
                        ascii.process(temp_str, kanada_mbr.option_ascii_mbr);
                        mapped = ascii;
                        break;
                    }
                    default: {
                        mapped_str.append(first_char);
                        break;
                    }
                }

                if (kanada_mbr.mode_add_space_mbr && out_str.length() > 0 && mapped_str.length() > 0) {
                    char last_char = out_str.charAt(out_str.length() - 1);
                    char next_char = mapped_str.charAt(0);

                    if (last_type > 0xa0 && last_type != first_char
                            && last_char != ' ' && last_char != '\t' && last_char != '\n' && last_char != '-' && last_char != '/'
                            && next_char != ' ' && next_char != ',' && next_char != '.' && next_char != '-' && next_char != '/') {
                        out_str.append(' ');
                    }
                }

                i = i + 1;
                last_type = first_char;

                out_str.append(mapped_str);
                mapped_str.setLength(0);
                continue;
            }

            switch (first_char) {
                // Half Katakana
                case 0x8e: {
                    switch (kanada_mbr.option_half_katakana_mbr) {
                        case kanada_def.TO_WIDE_ASCII:
                        case kanada_def.TO_ASCII:
                        case kanada_def.TO_KATAKANA:
                        case kanada_def.TO_HIRAGANA: {
                            j_mapper half_katakana = new map_half_katakana();
                            half_katakana.process(temp_str, kanada_mbr.option_half_katakana_mbr);
                            mapped = half_katakana;
                            break;
                        }
                        default: {
                            mapped_str.append(first_char);
                            break;
                        }
                    }
                    break;
                }
                // Wide Symbols
                case 0xa1:
                case 0xa2:
                case 0xa6:
                case 0xa7:
                case 0xa8:
                case 0xad: {
                    switch (kanada_mbr.option_wide_symbol_mbr) {
                        case kanada_def.TO_ASCII:
                        case kanada_def.TO_HALF_SYMBOL: {
                            j_mapper wide_symbol = new map_wide_symbol();
                            wide_symbol.process(temp_str, kanada_mbr.option_wide_symbol_mbr);
                            mapped = wide_symbol;
                            break;
                        }
                        default: {
                            mapped_str.append(first_char).append(buffer_mbr.charAt(i + 1));
                            break;
                        }
                    }
                    break;
                }
                // Wide Ascii
                case 0xa3: {
                    switch (kanada_mbr.option_wide_ascii_mbr) {
                        case kanada_def.TO_ASCII: {
                            j_mapper wide_ascii = new map_wide_ascii();
                            wide_ascii.process(temp_str, kanada_mbr.option_wide_ascii_mbr);
                            mapped = wide_ascii;
                            break;
                        }
                        default: {
                            mapped_str.append(first_char).append(buffer_mbr.charAt(i + 1));
                            break;
                        }
                    }
                    break;
                }
                // Hiragana
                case 0xa4: {
                    switch (kanada_mbr.option_hiragana_mbr) {
                        case kanada_def.TO_KATAKANA:
                        case kanada_def.TO_HALF_KATAKANA:
                        case kanada_def.TO_ASCII:
                        case kanada_def.TO_WIDE_ASCII: {
                            j_mapper hiragana = new map_hiragana();
                            hiragana.process(temp_str, kanada_mbr.option_hiragana_mbr);
                            mapped = hiragana;
                            break;
                        }
                        default: {
                            mapped_str.append(first_char).append(buffer_mbr.charAt(i + 1));
                            break;
                        }
                    }
                    break;
                }
                // Katakana
                case 0xa5: {
                    switch (kanada_mbr.option_katakana_mbr) {
                        case kanada_def.TO_HIRAGANA:
                        case kanada_def.TO_HALF_KATAKANA: {
                            j_mapper katakana = new map_katakana();
                            katakana.process(temp_str, kanada_mbr.option_katakana_mbr);
                            mapped = katakana;
                            break;
                        }
                        case kanada_def.TO_ASCII:
                        case kanada_def.TO_WIDE_ASCII: {
                            j_mapper katakana = new map_katakana();
                            katakana.process(temp_str, kanada_mbr.option_katakana_mbr);
                            mapped = katakana;
                            break;
                        }
                        default: {
                            mapped_str.append(first_char).append(buffer_mbr.charAt(i + 1));
                            break;
                        }
                    }
                    break;
                }
                default: {
                    mapped_str.append(first_char);
                    break;
                }

            }

            if (mapped == null) {
                i = mapped_str.length() == 0 ? i + 1 : i + mapped_str.length();
            } else {
                mapped_str.append(mapped.get_string());
                i = mapped.get_int() < 1 ? i + 1 : i + mapped.get_int();
            }

            if (out_str.length() > 0 && mapped_str.length() > 0) {
                char before_last_char = out_str.length() > 1 ? out_str.charAt(out_str.length() - 2) : ' ';
                char last_char = out_str.charAt(out_str.length() - 1);
                char next_char = mapped_str.charAt(0);
                char after_next_char = mapped_str.length() > 1 ? mapped_str.charAt(1) : (char) 0;

                if (last_type != first_char
                        && kanada_mbr.mode_add_space_mbr
                        && !(before_last_char == 0xa1 && last_char == 0xbc)
                        && !(next_char == 0xa1 && after_next_char == 0xbc)
                        && last_char != ' ' && last_char != '\t' && last_char != '\n'
                        && last_char != '-' && last_char != '/' && last_char != '\'' && last_char != '\"'
                        && next_char != ' ' && next_char != '\t' && next_char != '\n'
                        && next_char != '-' && next_char != '/' && next_char != '\'' && next_char != '\"'
                        && next_char != '.' && next_char != ',') {
                    out_str.append(' ');
                }

            }

            last_type = first_char;

            out_str.append(mapped_str);
            mapped_str.setLength(0);
        }

        if (kanada_mbr.mode_uc_first_mbr && tail_mbr == ' ') {
            StringBuffer sb = new StringBuffer();
            StringTokenizer token = new StringTokenizer(out_str.toString(), " ", true);
            while (token.hasMoreTokens()) {
                String word = token.nextToken();
                if (word.length() == 1) {
                    sb.append(word.substring(0, 1).toUpperCase(Locale.ENGLISH));
                } else if (word.length() > 1) {
                    if (word.charAt(0) == ' ') {
                        sb.append(' ');
                        word = word.substring(1);
                    }
                    sb.append(word.substring(0, 1).toUpperCase()).append(word.substring(1));
                }
            }
            out_str.setLength(0);
            out_str.append(sb.toString());
        }

        tail_mbr = ' ';

        return out_str;
    }
}

/*
 * $History: $
 */

