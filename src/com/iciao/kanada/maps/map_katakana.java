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

import java.util.HashMap;

/**
 * Remap non-kanji characters.<br>
 *
 * @author Masahiko Sato
 */
public class map_katakana extends j_mapper {
    private static final String katakana_to_romaji[] = {
            "\245\241", "a",
            "\245\242", "a",
            "\245\243", "i",
            "\245\244", "i",
            "\245\245", "u",
            "\245\246", "u",
            "\245\247", "e",
            "\245\250", "e",
            "\245\251", "o",
            "\245\252", "o",

            "\245\253", "ka",
            "\245\254", "ga",
            "\245\255", "ki",
            "\245\255\245\343", "kya",
            "\245\255\245\345", "kyu",
            "\245\255\245\347", "kyo",
            "\245\256", "gi",
            "\245\256\245\343", "gya",
            "\245\256\245\345", "gyu",
            "\245\256\245\347", "gyo",
            "\245\257", "ku",
            "\245\260", "gu",
            "\245\261", "ke",
            "\245\262", "ge",
            "\245\263", "ko",
            "\245\264", "go",

            "\245\265", "sa",
            "\245\266", "za",
            "\245\267", "shi",
            "\245\267\245\343", "sha",
            "\245\267\245\345", "shu",
            "\245\267\245\347", "sho",
            "\245\270", "ji",
            "\245\270\245\343", "ja",
            "\245\270\245\345", "ju",
            "\245\270\245\347", "jo",
            "\245\271", "su",
            "\245\272", "zu",
            "\245\273", "se",
            "\245\274", "ze",
            "\245\275", "so",
            "\245\276", "zo",

            "\245\277", "ta",
            "\245\300", "da",
            "\245\301", "chi",
            "\245\301\245\343", "cha",
            "\245\301\245\345", "chu",
            "\245\301\245\347", "cho",
            "\245\302", "di",
            "\245\302\245\343", "dya",
            "\245\302\245\345", "dyu",
            "\245\302\245\347", "dyo",

            "\245\303", "tsu",
            "\245\303\245\253", "kka",
            "\245\303\245\254", "gga",
            "\245\303\245\255", "kki",
            "\245\303\245\255\245\343", "kkya",
            "\245\303\245\255\245\345", "kkyu",
            "\245\303\245\255\245\347", "kkyo",
            "\245\303\245\256", "ggi",
            "\245\303\245\256\245\343", "ggya",
            "\245\303\245\256\245\345", "ggyu",
            "\245\303\245\256\245\347", "ggyo",
            "\245\303\245\257", "kku",
            "\245\303\245\260", "ggu",
            "\245\303\245\261", "kke",
            "\245\303\245\262", "gge",
            "\245\303\245\263", "kko",
            "\245\303\245\264", "ggo",
            "\245\303\245\265", "ssa",
            "\245\303\245\266", "zza",
            "\245\303\245\267", "sshi",
            "\245\303\245\267\245\343", "ssha",
            "\245\303\245\267\245\345", "sshu",
            "\245\303\245\267\245\347", "ssho",
            "\245\303\245\270", "jji",
            "\245\303\245\270\245\343", "jja",
            "\245\303\245\270\245\345", "jju",
            "\245\303\245\270\245\347", "jjo",
            "\245\303\245\271", "ssu",
            "\245\303\245\272", "zzu",
            "\245\303\245\273", "sse",
            "\245\303\245\274", "zze",
            "\245\303\245\275", "sso",
            "\245\303\245\276", "zzo",
            "\245\303\245\277", "tta",
            "\245\303\245\300", "dda",
            "\245\303\245\301", "cchi",
            "\245\303\245\301\245\343", "ccha",
            "\245\303\245\301\245\345", "cchu",
            "\245\303\245\301\245\347", "ccho",
            "\245\303\245\302", "ddi",
            "\245\303\245\302\245\343", "ddya",
            "\245\303\245\302\245\345", "ddyu",
            "\245\303\245\302\245\347", "ddyo",
            "\245\303\245\304", "ttsu",
            "\245\303\245\305", "ddu",
            "\245\303\245\306", "tte",
            "\245\303\245\307", "dde",
            "\245\303\245\310", "tto",
            "\245\303\245\311", "ddo",
            "\245\303\245\317", "hha",
            "\245\303\245\320", "bba",
            "\245\303\245\321", "ppa",
            "\245\303\245\322", "hhi",
            "\245\303\245\322\245\343", "hhya",
            "\245\303\245\322\245\345", "hhyu",
            "\245\303\245\322\245\347", "hhyo",
            "\245\303\245\323", "bbi",
            "\245\303\245\323\245\343", "bbya",
            "\245\303\245\323\245\345", "bbyu",
            "\245\303\245\323\245\347", "bbyo",
            "\245\303\245\324", "ppi",
            "\245\303\245\324\245\343", "ppya",
            "\245\303\245\324\245\345", "ppyu",
            "\245\303\245\324\245\347", "ppyo",
            "\245\303\245\325", "ffu",
            "\245\303\245\325\245\241", "ffa",
            "\245\303\245\325\245\243", "ffi",
            "\245\303\245\325\245\247", "ffe",
            "\245\303\245\325\245\251", "ffo",
            "\245\303\245\326", "bbu",
            "\245\303\245\327", "ppu",
            "\245\303\245\330", "hhe",
            "\245\303\245\331", "bbe",
            "\245\303\245\332", "ppe",
            "\245\303\245\333", "hho",
            "\245\303\245\334", "bbo",
            "\245\303\245\335", "ppo",
            "\245\303\245\344", "yya",
            "\245\303\245\346", "yyu",
            "\245\303\245\350", "yyo",
            "\245\303\245\351", "rra",
            "\245\303\245\352", "rri",
            "\245\303\245\352\245\343", "rrya",
            "\245\303\245\352\245\345", "rryu",
            "\245\303\245\352\245\347", "rryo",
            "\245\303\245\353", "rru",
            "\245\303\245\354", "rre",
            "\245\303\245\355", "rro",
            "\245\303\245\364", "vvu",
            "\245\303\245\364\245\241", "vva",
            "\245\303\245\364\245\243", "vvi",
            "\245\303\245\364\245\247", "vve",
            "\245\303\245\364\245\251", "vvo",

            "\245\304", "tsu",
            "\245\305", "du",
            "\245\306", "te",
            "\245\307", "de",
            "\245\310", "to",
            "\245\311", "do",

            "\245\312", "na",
            "\245\313", "ni",
            "\245\313\245\343", "nya",
            "\245\313\245\345", "nyu",
            "\245\313\245\347", "nyo",
            "\245\314", "nu",
            "\245\315", "ne",
            "\245\316", "no",

            "\245\317", "ha",
            "\245\320", "ba",
            "\245\321", "pa",
            "\245\322", "hi",
            "\245\322\245\343", "hya",
            "\245\322\245\345", "hyu",
            "\245\322\245\347", "hyo",
            "\245\323", "bi",
            "\245\323\245\343", "bya",
            "\245\323\245\345", "byu",
            "\245\323\245\347", "byo",
            "\245\324", "pi",
            "\245\324\245\343", "pya",
            "\245\324\245\345", "pyu",
            "\245\324\245\347", "pyo",
            "\245\325", "fu",
            "\245\325\245\241", "fa",
            "\245\325\245\243", "fi",
            "\245\325\245\247", "fe",
            "\245\325\245\251", "fo",
            "\245\326", "bu",
            "\245\327", "pu",
            "\245\330", "he",
            "\245\331", "be",
            "\245\332", "pe",
            "\245\333", "ho",
            "\245\334", "bo",
            "\245\335", "po",

            "\245\336", "ma",
            "\245\337", "mi",
            "\245\337\245\343", "mya",
            "\245\337\245\345", "myu",
            "\245\337\245\347", "myo",
            "\245\340", "mu",
            "\245\341", "me",
            "\245\342", "mo",

            "\245\343", "ya",
            "\245\344", "ya",
            "\245\345", "yu",
            "\245\346", "yu",
            "\245\347", "yo",
            "\245\350", "yo",

            "\245\351", "ra",
            "\245\352", "ri",
            "\245\352\245\343", "rya",
            "\245\352\245\345", "ryu",
            "\245\352\245\347", "ryo",
            "\245\353", "ru",
            "\245\354", "re",
            "\245\355", "ro",

            "\245\356", "wa",
            "\245\357", "wa",
            "\245\360", "i",
            "\245\361", "e",
            "\245\362", "wo",
            "\245\363", "n",
            "\245\363\245\242", "n'a",
            "\245\363\245\244", "n'i",
            "\245\363\245\246", "n'u",
            "\245\363\245\250", "n'e",
            "\245\363\245\252", "n'o",
            "\245\364", "vu",
            "\245\364\245\241", "va",
            "\245\364\245\243", "vi",
            "\245\364\245\247", "ve",
            "\245\364\245\251", "vo",
            "\245\365", "ka",
            "\245\366", "ke",
            "", ""};
    private static final HashMap<String, String> katakana_to_romaji_map;
    static {
        katakana_to_romaji_map = new HashMap<String, String>(katakana_to_romaji.length);
        for (int i = 0; i < katakana_to_romaji.length; i += 2) {
            String key = katakana_to_romaji[i];
            String value = katakana_to_romaji[i + 1];
            katakana_to_romaji_map.put(key, value);
        }
    }

    public map_katakana() {
        this(0, null);
    }

    protected map_katakana(int count, String str) {
        super(count, str);
    }

    protected void process(String str, int param) {
        int i = 0;
        StringBuilder out = new StringBuilder();

        switch (param) {
            case TO_HIRAGANA: {
                char first_char = (char) (str.charAt(0) - 0x01);
                char second_char = str.charAt(1);

                if (first_char == 0xa5 && second_char > 0xf3) {
                    switch (second_char) {
                        case 0xf4: {
                            second_char = 0xd6;
                            break;
                        }
                        case 0xf5: {
                            second_char = 0xab;
                            break;
                        }
                        case 0xf6: {
                            second_char = 0xb1;
                            break;
                        }
                    }
                }

                out.append(first_char).append(second_char);
                i = 2;
                break;
            }
            case TO_HALF_KATAKANA: {
                char first_char = str.charAt(0);
                char second_char = str.charAt(1);
                out.append(first_char).append(second_char);
                i = 2;
                break;
            }

            case TO_ASCII:
            case TO_WIDE_ASCII: {
//				Iterator	iterater	= katakana_to_romaji_map.keySet().iterator();
//				String	romaji	= "?";
//				int matched_len = 0;
//				while ( iterater.hasNext() )
//				{
//					String	key		= (String)iterater.next();
//					int		key_len	= key.length();
//					if ( key_len > str.length() )
//					{
//						continue;
//					}
//					String	kana	= str.substring(0, key_len);
//
//					if ( key.equals(kana) && matched_len < key_len )
//					{
//						romaji	= (String)kanada_def.katakana_to_romaji_map.get(key);
//						matched_len		= key_len;
//					}
//				}
//				i = matched_len > 0 ? matched_len : 2;

                i = str.length() > 6 ? 6 : str.length();
                if (i % 2 == 1) {
                    --i;
                }
                String romaji = "";
                while (i > 1) {
                    String kana = str.substring(0, i);
                    if (katakana_to_romaji_map.containsKey(kana)) {
                        romaji = katakana_to_romaji_map.get(kana);
                        break;
                    }
                    i = i - 2;
                }
                out.append(romaji);
                break;
            }
            default: {
                char first_char = str.charAt(0);
                char second_char = str.charAt(1);
                out.append(first_char).append(second_char);
                i = 2;
                break;
            }
        }

        set_int(i);
        set_string(out.toString());
    }

}
