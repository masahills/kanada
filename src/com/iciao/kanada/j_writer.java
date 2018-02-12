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
    protected StringBuilder buffer_mbr = new StringBuilder();
    protected int tail_mbr;


    public j_writer(kanada this_kanada) {
        this.clear();
        kanada_mbr = this_kanada;
        tail_mbr = ' ';
    }

    public StringBuilder append(int code_point) {
        return buffer_mbr.appendCodePoint(code_point);
    }

    public StringBuilder append(String str) {
        return buffer_mbr.append(str);
    }

    public void clear() {
        buffer_mbr.setLength(0);
    }

    public kanada get_kanada() {
        return kanada_mbr;
    }

    public StringBuilder map() {
        StringBuilder mapped_str = new StringBuilder();
        StringBuilder out_str = new StringBuilder();
        int total_len = buffer_mbr.length();
        String work_str;

        for (int i = 0; i < total_len; i++) {
            int this_char = buffer_mbr.codePointAt(i);
            Character.UnicodeBlock block = Character.UnicodeBlock.of(this_char);
            work_str = buffer_mbr.substring(i, buffer_mbr.length());
            j_mapper mapped_mapper = null;

            if (block == Character.UnicodeBlock.BASIC_LATIN) {
                switch (kanada_mbr.option_ascii_mbr) {
                    case j_mapper.TO_WIDE_ASCII:
                        j_mapper ascii = new map_ascii();
                        ascii.process(work_str, kanada_mbr.option_ascii_mbr);
                        mapped_mapper = ascii;
                        break;
                    default:
                        mapped_str.appendCodePoint(this_char);
                        break;
                }
            } else if (block == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS) {
                if (this_char < 0xff5f) {
                    switch (kanada_mbr.option_wide_ascii_mbr) {
                        case j_mapper.TO_ASCII:
                            j_mapper wide_ascii = new map_wide_ascii();
                            wide_ascii.process(work_str, kanada_mbr.option_wide_ascii_mbr);
                            mapped_mapper = wide_ascii;
                            break;
                        default:
                            mapped_str.appendCodePoint(this_char);
                            break;
                    }
                } else {
                    switch (kanada_mbr.option_half_katakana_mbr) {
                        case j_mapper.TO_WIDE_ASCII:
                        case j_mapper.TO_ASCII:
                        case j_mapper.TO_KATAKANA:
                        case j_mapper.TO_HIRAGANA:
                            j_mapper half_katakana = new map_half_katakana();
                            half_katakana.process(work_str, kanada_mbr.option_half_katakana_mbr);
                            mapped_mapper = half_katakana;
                            break;
                        default:
                            mapped_str.appendCodePoint(this_char);
                            break;
                    }
                }
            } else if (block == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION) {
                switch (kanada_mbr.option_wide_symbol_mbr) {
                    case j_mapper.TO_ASCII:
                    case j_mapper.TO_HALF_SYMBOL:
                        j_mapper wide_symbol = new map_wide_symbol();
                        wide_symbol.process(work_str, kanada_mbr.option_wide_symbol_mbr);
                        mapped_mapper = wide_symbol;
                        break;
                    default:
                        mapped_str.appendCodePoint(this_char);
                        break;
                }
            } else if (block == Character.UnicodeBlock.HIRAGANA) {
                switch (kanada_mbr.option_hiragana_mbr) {
                    case j_mapper.TO_KATAKANA:
                    case j_mapper.TO_HALF_KATAKANA:
                    case j_mapper.TO_ASCII:
                    case j_mapper.TO_WIDE_ASCII:
                        j_mapper hiragana = new map_hiragana();
                        hiragana.process(work_str, kanada_mbr.option_hiragana_mbr);
                        mapped_mapper = hiragana;
                        break;
                    default:
                        mapped_str.appendCodePoint(this_char);
                        break;
                }
            } else if (block == Character.UnicodeBlock.KATAKANA
                    || block == Character.UnicodeBlock.KATAKANA_PHONETIC_EXTENSIONS) {
                switch (kanada_mbr.option_katakana_mbr) {
                    case j_mapper.TO_HIRAGANA:
                    case j_mapper.TO_HALF_KATAKANA:
                    case j_mapper.TO_ASCII:
                    case j_mapper.TO_WIDE_ASCII:
                        j_mapper katakana = new map_katakana();
                        katakana.process(work_str, kanada_mbr.option_katakana_mbr);
                        mapped_mapper = katakana;
                        break;
                    default:
                        mapped_str.appendCodePoint(this_char);
                        break;
                }
            } else {
                mapped_str.appendCodePoint(this_char);
            }

            if (mapped_mapper != null) {
                mapped_str.append(mapped_mapper.get_string());
            }
            out_str.append(mapped_str);
            mapped_str.setLength(0);
        }

        if (kanada_mbr.mode_uc_first_mbr && tail_mbr == ' ') {
            StringBuilder sb = new StringBuilder();
            StringTokenizer token = new StringTokenizer(out_str.toString(), " \t\n\r\f", true);
            while (token.hasMoreTokens()) {
                String word = token.nextToken();
                sb.append(word.substring(0, 1).toUpperCase(Locale.ENGLISH)).append(word.substring(1));
            }
            out_str.setLength(0);
            out_str.append(sb.toString());
        }

        tail_mbr = ' ';

        return out_str;
    }
}

