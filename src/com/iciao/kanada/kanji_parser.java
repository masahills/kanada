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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Parse strings and look up Kanji dictionary.<br>
 *
 * @author Masahiko Sato
 */
public class kanji_parser {
    private static kanwadict kanwa = kanwadict.get_kanwa();
    private kanada kanada_mbr;
    private j_writer j_writer_mbr;
    private StringBuilder output_buffer;
    public kanji_parser(j_writer writer) {
        kanada_mbr = writer.get_kanada();
        j_writer_mbr = writer;
        output_buffer = new StringBuilder();
    }

    public String parse(String input_string) throws Exception {
        if (input_string.length() < 2) {
            return input_string;
        }

        output_buffer.ensureCapacity(input_string.length());

        int i = 0;

        for (; ; ) {
            if (i > input_string.length() - 1) {
                if (j_writer_mbr.buffer_mbr.length() > 0) {
                    output_buffer.append(j_writer_mbr.map());
                    j_writer_mbr.clear();
                }
                break;
            }

            char first_char = input_string.charAt(i);

            // Single byte characters
            if (first_char < 0x80) {
                j_writer_mbr.append(first_char);
                i = i + 1;
                continue;
            }
            // Half_width Katakana or Non-Kanji Wide Chars
            else if ((first_char == 0x8e || first_char < 0xb0) && first_char != 0x8f) {
                try {
                    j_writer_mbr.append(first_char).append(input_string.charAt(i + 1));
                    i = i + 2;
                } catch (StringIndexOutOfBoundsException e) {
                    continue;
                }

                continue;
            }

            // output non-kanji string to buffer
            StringBuilder non_kanji_str = j_writer_mbr.map();
            output_buffer.append(non_kanji_str);
            j_writer_mbr.clear();

            if (kanada_mbr.mode_add_space_mbr && output_buffer.length() > 0) {
                char last_char = output_buffer.charAt(output_buffer.length() - 1);

                if (!Character.isWhitespace(last_char)) {
                    output_buffer.append(' ');
                }
            }

            // Kanji
            char second_char = input_string.charAt(i + 1);

            if (second_char < 0xa0) {
                // Not a kanji
                output_buffer.append(first_char);
                output_buffer.append(second_char);
                i = i + 2;
                continue;
            }

            kanwadict.kanwa_key key = kanwa.get_key(first_char, second_char);
            List value_list = new ArrayList();

            if (kanwa.search_key(key)) {
                value_list = (List) kanwa.get_value(key);
            }

            if (value_list == null) {
                // Could not find a value_list for the kanwa_key.
                // Do nothing and move to the next letter.
                output_buffer.append(first_char);
                output_buffer.append(second_char);
                i = i + 2;
                continue;
            }

            Iterator dic_iterator = value_list.iterator();

            int matched_len = 0;
            String yomi = "";
            String kanji = "";
            char tail = ' ';

            while (dic_iterator.hasNext()) {
                kanwadict.yomi_kanji_data term = (kanwadict.yomi_kanji_data) dic_iterator.next();

                int search_len = term.get_length();
                if (i + search_len > input_string.length() || search_len <= matched_len) {
                    continue;
                }
                String search_word = input_string.substring(i, i + search_len);

                if (search_word.equals(term.get_kanji())) {
                    if (term.get_tail() == ' ') {
                        kanji = term.get_kanji();
                        yomi = term.get_yomi();
                        tail = term.get_tail();
                        matched_len = search_len;
                    } else if (search_len > 2 || input_string.length() > search_word.length()) {
                        kanji = term.get_kanji();
                        yomi = term.get_yomi();
                        tail = term.get_tail();
                        matched_len = search_len;
                    }
                }
            }

            if (matched_len == 0 || yomi.length() == 0) {
                // Could not find a value_list for the kanwa_key.
                // Do nothing and move to the next letter.
                output_buffer.append(first_char);
                output_buffer.append(second_char);
                i = i + 2;
            } else {
                if (kanada_mbr.option_kanji_mbr == kanada_def.NO_OP) {
                    output_buffer.append(kanji);
                } else {
                    j_writer_mbr.append(yomi);
                    output_buffer.append(j_writer_mbr.map());
                }
                j_writer_mbr.clear();
                i = i + matched_len;

                if (kanada_mbr.mode_add_space_mbr && tail == ' ' && output_buffer.length() > 0 && i < input_string.length()) {
                    output_buffer.append(' ');
                }
            }

            j_writer_mbr.tail_mbr = tail;
        }

        return output_buffer.toString();
    }
}

/*
 * $History: $
 */
