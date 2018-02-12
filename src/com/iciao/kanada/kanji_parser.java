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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

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
        int str_len = input_string.length();
        output_buffer.ensureCapacity(str_len);

        for (int i = 0; i < str_len; i++) {
            int this_char = input_string.codePointAt(i);

            if (!Pattern.matches("[\\p{IsHiragana}\\p{IsKatakana}\\p{IsHan}]",
                    String.valueOf(Character.toChars(this_char)))) {
                j_writer_mbr.append(this_char);
                continue;
            }

            kanwadict.kanwa_key key = kanwa.get_key(this_char);
            List value_list = new ArrayList();

            if (kanwa.search_key(key)) {
                value_list = kanwa.get_value(key);
            }

            if (value_list.isEmpty()) {
                j_writer_mbr.append(this_char);
                continue;
            }

            if (j_writer_mbr.buffer_mbr.length() > 0) {
                if (kanada_mbr.mode_add_space_mbr && output_buffer.length() > 0) {
                    int next_char = 0;
                    if (i < str_len - 1) {
                        next_char = input_string.codePointAt(i);
                    }
                    if (!Pattern.matches("[\\p{Cntrl}\\p{IsCommon}]", String.valueOf(Character.toChars(next_char)))
                            && !Pattern.matches("(?s).*?[\\p{IsCommon}]$", j_writer_mbr.buffer_mbr.toString())) {
                        j_writer_mbr.append(' ');
                    }
//                    System.out.println("### '" + j_writer_mbr.buffer_mbr.toString()
//                            + "':'" + String.valueOf(Character.toChars(next_char)) + "'");
                }
                StringBuilder non_dic_str = j_writer_mbr.map();
                output_buffer.append(non_dic_str);
                j_writer_mbr.clear();
            }

            Iterator dic_iterator = value_list.iterator();

            int matched_len = 0;
            String yomi = "";
            String kanji = "";
            int tail = ' ';

            while (dic_iterator.hasNext()) {
                kanwadict.yomi_kanji_data term = (kanwadict.yomi_kanji_data) dic_iterator.next();

                int search_len = term.get_length();
                if ((i + search_len) > input_string.length() || search_len <= matched_len) {
                    continue;
                }

                String search_word = input_string.substring(i, i + search_len);

                if (search_word.equals(term.get_kanji())) {
                    if (term.get_tail() == ' ') {
                        kanji = term.get_kanji();
                        yomi = term.get_yomi();
                        tail = term.get_tail();
                        matched_len = search_len;
                    } else if (input_string.length() > search_word.length()) {
                        kanji = term.get_kanji();
                        yomi = term.get_yomi();
                        tail = term.get_tail();
                        matched_len = search_len;
                    }
                }
            }

            if (matched_len == 0 || yomi.length() == 0) {
                output_buffer.appendCodePoint(this_char);
            } else {
                if (kanada_mbr.option_kanji_mbr == kanada.CONFIG_GET_AS_IS) {
                    j_writer_mbr.append(kanji);
                } else {
                    j_writer_mbr.append(yomi);
                }
                if (j_writer_mbr.buffer_mbr.length() > 0) {
                    int next_char = 0;
                    if (kanada_mbr.mode_add_space_mbr && tail == ' ') {
                        if (i < str_len - matched_len - 1) {
                            next_char = input_string.codePointAt(i + matched_len);
                        }
                        if (!Pattern.matches("[\\p{Cntrl}\\p{IsCommon}]", String.valueOf(Character.toChars(next_char)))) {
                            j_writer_mbr.append(' ');
                        }
                    }
//                    System.out.println(">>> '" + kanji
//                            + "':'" + String.valueOf(Character.toChars(tail))
//                            + "':'" + j_writer_mbr.buffer_mbr.toString()
//                            + "':'" + String.valueOf(Character.toChars(next_char)) + "'");
                }
                StringBuilder dic_str = j_writer_mbr.map();
//                System.out.println("# '" + j_writer_mbr.buffer_mbr.toString() + "':'" + dic_str +"'");
                output_buffer.append(dic_str);
                j_writer_mbr.clear();
                i = i + matched_len - 1;
            }

            j_writer_mbr.tail_mbr = tail;
        }

        if (j_writer_mbr.buffer_mbr.length() > 0) {
            output_buffer.append(j_writer_mbr.map());
            j_writer_mbr.clear();
        }

        return output_buffer.toString();
    }
}

/*
 * $History: $
 */
