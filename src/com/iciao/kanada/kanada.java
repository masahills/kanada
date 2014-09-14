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

import java.util.Locale;

/**
 * Kanji-to-Kana/Romaji Conversion Utility.<br>
 * Convert a given Japanese string into Hiragana, Katakana, or Romaji string.
 *
 * @author Masahiko Sato
 */
public class kanada extends kanada_def {
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
    private boolean massage_string_mbr = false;
    private String encoding_in_mbr = DEFAULT_ENCODING_NAME;
    private String encoding_out_mbr = DEFAULT_ENCODING_NAME;

    public kanada() throws java.io.IOException {
        set_param(CONFIG_GET_AS_IS);
    }

    public kanada(int config) throws java.io.IOException {
        this(config, false);
    }

    public kanada(int config, boolean massage_string) throws java.io.IOException {
        set_param(config);
        massage_string_mbr = massage_string;
    }

    public kanada(int param_kanji,
                  int param_hiragana,
                  int param_katakana,
                  int param_wide_ascii,
                  int param_wide_symbol,
                  int param_half_katakana,
                  int param_ascii,
                  int param_half_symbol,
                  boolean massage_string) throws java.io.IOException {
        set_param(param_kanji,
                param_hiragana,
                param_katakana,
                param_wide_ascii,
                param_wide_symbol,
                param_ascii,
                param_half_katakana,
                param_half_symbol);
        massage_string_mbr = massage_string;
    }

    // If two consecutive char >= 0xa0 are found,
    // it is (most likely) a Japanese text.
    private static boolean is_japanese(String euc_str) {
        boolean maybe = false;

        for (int i = 0; i < euc_str.length(); ++i) {
            int this_char = (int) euc_str.charAt(i);

            if (this_char >= 0xa0) {
                if (maybe)
                    return true;
                else
                    maybe = true;
            } else {
                maybe = false;
            }
        }

        return false;
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
                           int param_half_katakana,
                           int param_ascii,
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

        // Followings are to be implemented in the future.
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

    public void set_encoding(int enc) {
        set_encoding(enc, enc);
    }

    public void set_encoding(int enc_in, int enc_out) {
        switch (enc_in) {
            case ENCODING_SJIS: {
                encoding_in_mbr = JDK_SJIS;
                break;
            }
            case ENCODING_JIS: {
                encoding_in_mbr = JDK_JIS;
                break;
            }
            case ENCODING_EUC_JP: {
                encoding_in_mbr = JDK_EUC_JP;
                break;
            }
            case ENCODING_AUTO_DETECT: {
                encoding_in_mbr = JDK_JIS_AUTO_DETECT;
                break;
            }
            default: {
                encoding_in_mbr = DEFAULT_ENCODING_NAME;
                break;
            }
        }

        switch (enc_out) {
            case ENCODING_SJIS: {
                encoding_out_mbr = JDK_SJIS;
                break;
            }
            case ENCODING_JIS: {
                encoding_out_mbr = JDK_JIS;
                break;
            }
            case ENCODING_EUC_JP: {
                encoding_out_mbr = JDK_EUC_JP;
                break;
            }
            case ENCODING_AUTO_DETECT: {
                encoding_out_mbr = JDK_JIS_AUTO_DETECT;
                break;
            }
            default: {
                encoding_out_mbr = DEFAULT_ENCODING_NAME;
                break;
            }
        }
    }

    public String process(String str, int mode) {
        set_mode(mode);
        return process(str);
    }

    public String process(String str, boolean add_space) {
        boolean saved_status = mode_add_space_mbr;

        if (add_space)
            mode_add_space_mbr = true;

        String processed = process(str);

        if (add_space)
            mode_add_space_mbr = saved_status;

        return processed;
    }

    public String process(String str) {
        if (str == null || str.length() < 1) {
            return str;
        }

        String parsed_str;

        try {
            j_writer writer = new j_writer(this);
            kanji_parser parser = new kanji_parser(writer);

            String temp_str;

            if (massage_string_mbr) {
                temp_str = new String(str.getBytes(JDK_ISO8859_1), encoding_in_mbr);
            } else {
                temp_str = str;
            }

            temp_str = new String(temp_str.getBytes(JDK_EUC_JP), JDK_ISO8859_1);

            if (is_japanese(temp_str)) {
                str = temp_str;
            } else {
                // Probably contains non-Japanese extended characters.
                // Assume this is not a Japanese text and return it as-is.
                return str;
            }

            parsed_str = parser.parse(str);
            parsed_str = new String(parsed_str.getBytes(JDK_ISO8859_1), JDK_EUC_JP);

            if (massage_string_mbr) {
                parsed_str = new String(parsed_str.getBytes(encoding_out_mbr), JDK_ISO8859_1);
            }

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
