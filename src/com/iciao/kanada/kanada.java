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

import java.util.Locale;

/**
 * Kanji-to-Kana/Romaji Conversion Utility.<br>
 * Convert a given Japanese string into Hiragana, Katakana, or Romaji string.
 *
 * @author Masahiko Sato
 */
public class kanada {
    public static final int FLAG_ADD_SPACE = 0x00000001;
    public static final int FLAG_UC_FIRST = 0x00000002;
    public static final int FLAG_UC_ALL = 0x00000004;
    public static final int FLAG_FURIGANA = 0x00000008;
    public static final int FLAG_SHOW_ALL_YOMI = 0x00000010;
    public static final int FLAG_KUNREI_ROMAJI = 0x00000020;

    public static final int CONFIG_GET_AS_IS = -1;
    public static final int CONFIG_GET_ROMAJI = 0;
    public static final int CONFIG_GET_HIRAGANA = 1;
    public static final int CONFIG_GET_KATAKANA = 2;
    public static final int CONFIG_HALF_TO_WIDE_ALL = 3;
    public static final int CONFIG_HALF_TO_WIDE_KANA = 4;

    protected int option_kanji_mbr;
    protected int option_hiragana_mbr;
    protected int option_katakana_mbr;
    protected int option_wide_ascii_mbr;
    protected int option_wide_symbol_mbr;
    protected int option_half_katakana_mbr;
    protected int option_ascii_mbr;
    protected int option_half_symbol_mbr;
    protected boolean mode_show_all_yomi_mbr = false;
    protected boolean mode_add_space_mbr = false;
    protected boolean mode_furigana_mbr = false;
    protected boolean mode_uc_first_mbr = false;
    protected boolean mode_uc_all_mbr = false;
    protected boolean mode_kunrei_romaji_mbr = false;
    protected boolean mode_wakachi_kaki_mbr = false;

    public kanada() throws java.io.IOException {
        set_param(CONFIG_GET_AS_IS);
    }

    public kanada(int config) throws java.io.IOException {
        set_param(config);
    }

    public kanada(int param_kanji,
                  int param_hiragana,
                  int param_katakana,
                  int param_wide_ascii,
                  int param_wide_symbol,
                  int param_half_katakana,
                  int param_ascii,
                  int param_half_symbol) throws java.io.IOException {
        set_param(param_kanji,
                param_hiragana,
                param_katakana,
                param_wide_ascii,
                param_wide_symbol,
                param_ascii,
                param_half_katakana,
                param_half_symbol);
    }

    private void set_param(int config) {
        switch (config) {
            case CONFIG_GET_ROMAJI: {
                set_param(
                        j_mapper.TO_ASCII,
                        j_mapper.TO_ASCII,
                        j_mapper.TO_ASCII,
                        j_mapper.TO_ASCII,
                        j_mapper.TO_ASCII,
                        j_mapper.AS_IS,
                        j_mapper.TO_ASCII,
                        j_mapper.TO_ASCII);
                break;
            }
            case CONFIG_GET_HIRAGANA: {
                set_param(
                        j_mapper.TO_HIRAGANA,
                        j_mapper.AS_IS,
                        j_mapper.TO_HIRAGANA,
                        j_mapper.AS_IS,
                        j_mapper.AS_IS,
                        j_mapper.TO_HIRAGANA,
                        j_mapper.AS_IS,
                        j_mapper.AS_IS);
                break;
            }
            case CONFIG_GET_KATAKANA: {
                set_param(j_mapper.TO_KATAKANA,
                        j_mapper.TO_KATAKANA,
                        j_mapper.AS_IS,
                        j_mapper.AS_IS,
                        j_mapper.AS_IS,
                        j_mapper.TO_KATAKANA,
                        j_mapper.AS_IS,
                        j_mapper.AS_IS);
                break;
            }
            case CONFIG_HALF_TO_WIDE_ALL: {
                set_param(
                        j_mapper.AS_IS,
                        j_mapper.AS_IS,
                        j_mapper.AS_IS,
                        j_mapper.AS_IS,
                        j_mapper.TO_HIRAGANA,
                        j_mapper.TO_WIDE_ASCII,
                        j_mapper.TO_KATAKANA,
                        j_mapper.TO_WIDE_SYMBOL);
                break;
            }
            default: {
                set_param(
                        j_mapper.AS_IS,
                        j_mapper.AS_IS,
                        j_mapper.AS_IS,
                        j_mapper.AS_IS,
                        j_mapper.AS_IS,
                        j_mapper.AS_IS,
                        j_mapper.AS_IS,
                        j_mapper.AS_IS);
                break;
            }
        }
    }

    private void set_param(int param_kanji,
                           int param_hiragana,
                           int param_katakana,
                           int param_wide_ascii,
                           int param_wide_symbol,
                           int param_ascii,
                           int param_half_katakana,
                           int param_half_symbol) {
        option_kanji_mbr = param_kanji;
        option_hiragana_mbr = param_hiragana;
        option_katakana_mbr = param_katakana;
        option_wide_ascii_mbr = param_wide_ascii;
        option_wide_symbol_mbr = param_wide_symbol;
        option_ascii_mbr = param_ascii;
        option_half_katakana_mbr = param_half_katakana;
        option_half_symbol_mbr = param_half_symbol;
    }

    public void set_mode(int mode) {
        mode_show_all_yomi_mbr = false;
        mode_add_space_mbr = false;
        mode_uc_first_mbr = false;
        mode_uc_all_mbr = false;
        mode_kunrei_romaji_mbr = false;

        if ((mode & FLAG_ADD_SPACE) != 0) {
            mode_add_space_mbr = true;
        }

        if ((mode & FLAG_UC_FIRST) != 0) {
            mode_uc_first_mbr = true;
            mode_uc_all_mbr = false;
        }

        if ((mode & FLAG_UC_ALL) != 0) {
            mode_uc_first_mbr = false;
            mode_uc_all_mbr = true;
        }

        // TODO: To be implemented
        if ((mode & FLAG_FURIGANA) != 0) {
            mode_uc_all_mbr = true;
        }

        if ((mode & FLAG_SHOW_ALL_YOMI) != 0) {
            mode_show_all_yomi_mbr = true;
        }

        if ((mode & FLAG_KUNREI_ROMAJI) != 0) {
            mode_kunrei_romaji_mbr = true;
        }
    }

    public String process(String str, int mode) {
        set_mode(mode);
        return process(str);
    }

    public String process(String str, boolean add_space) {
        boolean saved_status = mode_add_space_mbr;

        if (add_space) {
            mode_add_space_mbr = true;
        }

        String processed = process(str);

        if (add_space) {
            mode_add_space_mbr = saved_status;
        }

        return processed;
    }

    public String process(String str) {
        if (str == null) {
            return str;
        }

        String parsed_str;
        try {
            j_writer writer = new j_writer(this);
            kanji_parser parser = new kanji_parser(writer);
            parsed_str = parser.parse(str);
            if (mode_uc_all_mbr) {
                parsed_str = parsed_str.toUpperCase(Locale.ENGLISH);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return str;
        }

        return parsed_str;
    }
}
