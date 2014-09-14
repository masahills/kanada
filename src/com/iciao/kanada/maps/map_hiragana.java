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
public class map_hiragana extends j_mapper {
    private static final String hiragana_to_romaji[] = {
            "\244\241", "a",
            "\244\242", "a",
            "\244\243", "i",
            "\244\244", "i",
            "\244\245", "u",
            "\244\246", "u",
            "\244\246\241\253", "vu",
            "\244\246\241\253\244\241", "va",
            "\244\246\241\253\244\243", "vi",
            "\244\246\241\253\244\247", "ve",
            "\244\246\241\253\244\251", "vo",
            "\244\247", "e",
            "\244\250", "e",
            "\244\251", "o",
            "\244\252", "o",

            "\244\253", "ka",
            "\244\254", "ga",
            "\244\255", "ki",
            "\244\255\244\343", "kya",
            "\244\255\244\345", "kyu",
            "\244\255\244\347", "kyo",
            "\244\256", "gi",
            "\244\256\244\343", "gya",
            "\244\256\244\345", "gyu",
            "\244\256\244\347", "gyo",
            "\244\257", "ku",
            "\244\260", "gu",
            "\244\261", "ke",
            "\244\262", "ge",
            "\244\263", "ko",
            "\244\264", "go",

            "\244\265", "sa",
            "\244\266", "za",
            "\244\267", "shi",
            "\244\267\244\343", "sha",
            "\244\267\244\345", "shu",
            "\244\267\244\347", "sho",
            "\244\270", "ji",
            "\244\270\244\343", "ja",
            "\244\270\244\345", "ju",
            "\244\270\244\347", "jo",
            "\244\271", "su",
            "\244\272", "zu",
            "\244\273", "se",
            "\244\274", "ze",
            "\244\275", "so",
            "\244\276", "zo",

            "\244\277", "ta",
            "\244\300", "da",
            "\244\301", "chi",
            "\244\301\244\343", "cha",
            "\244\301\244\345", "chu",
            "\244\301\244\347", "cho",
            "\244\302", "di",
            "\244\302\244\343", "dya",
            "\244\302\244\345", "dyu",
            "\244\302\244\347", "dyo",

            "\244\303", "tsu",
            "\244\303\244\246\241\253", "vvu",
            "\244\303\244\246\241\253\244\241", "vva",
            "\244\303\244\246\241\253\244\243", "vvi",
            "\244\303\244\246\241\253\244\247", "vve",
            "\244\303\244\246\241\253\244\251", "vvo",
            "\244\303\244\253", "kka",
            "\244\303\244\254", "gga",
            "\244\303\244\255", "kki",
            "\244\303\244\255\244\343", "kkya",
            "\244\303\244\255\244\345", "kkyu",
            "\244\303\244\255\244\347", "kkyo",
            "\244\303\244\256", "ggi",
            "\244\303\244\256\244\343", "ggya",
            "\244\303\244\256\244\345", "ggyu",
            "\244\303\244\256\244\347", "ggyo",
            "\244\303\244\257", "kku",
            "\244\303\244\260", "ggu",
            "\244\303\244\261", "kke",
            "\244\303\244\262", "gge",
            "\244\303\244\263", "kko",
            "\244\303\244\264", "ggo",
            "\244\303\244\265", "ssa",
            "\244\303\244\266", "zza",
            "\244\303\244\267", "sshi",
            "\244\303\244\267\244\343", "ssha",
            "\244\303\244\267\244\345", "sshu",
            "\244\303\244\267\244\347", "ssho",
            "\244\303\244\270", "jji",
            "\244\303\244\270\244\343", "jja",
            "\244\303\244\270\244\345", "jju",
            "\244\303\244\270\244\347", "jjo",
            "\244\303\244\271", "ssu",
            "\244\303\244\272", "zzu",
            "\244\303\244\273", "sse",
            "\244\303\244\274", "zze",
            "\244\303\244\275", "sso",
            "\244\303\244\276", "zzo",
            "\244\303\244\277", "tta",
            "\244\303\244\300", "dda",
            "\244\303\244\301", "cchi",
            "\244\303\244\301\244\343", "ccha",
            "\244\303\244\301\244\345", "cchu",
            "\244\303\244\301\244\347", "ccho",
            "\244\303\244\302", "ddi",
            "\244\303\244\302\244\343", "ddya",
            "\244\303\244\302\244\345", "ddyu",
            "\244\303\244\302\244\347", "ddyo",
            "\244\303\244\304", "ttsu",
            "\244\303\244\305", "ddu",
            "\244\303\244\306", "tte",
            "\244\303\244\307", "dde",
            "\244\303\244\310", "tto",
            "\244\303\244\311", "ddo",
            "\244\303\244\317", "hha",
            "\244\303\244\320", "bba",
            "\244\303\244\321", "ppa",
            "\244\303\244\322", "hhi",
            "\244\303\244\322\244\343", "hhya",
            "\244\303\244\322\244\345", "hhyu",
            "\244\303\244\322\244\347", "hhyo",
            "\244\303\244\323", "bbi",
            "\244\303\244\323\244\343", "bbya",
            "\244\303\244\323\244\345", "bbyu",
            "\244\303\244\323\244\347", "bbyo",
            "\244\303\244\324", "ppi",
            "\244\303\244\324\244\343", "ppya",
            "\244\303\244\324\244\345", "ppyu",
            "\244\303\244\324\244\347", "ppyo",
            "\244\303\244\325", "ffu",
            "\244\303\244\325\244\241", "ffa",
            "\244\303\244\325\244\243", "ffi",
            "\244\303\244\325\244\247", "ffe",
            "\244\303\244\325\244\251", "ffo",
            "\244\303\244\326", "bbu",
            "\244\303\244\327", "ppu",
            "\244\303\244\330", "hhe",
            "\244\303\244\331", "bbe",
            "\244\303\244\332", "ppe",
            "\244\303\244\333", "hho",
            "\244\303\244\334", "bbo",
            "\244\303\244\335", "ppo",
            "\244\303\244\344", "yya",
            "\244\303\244\346", "yyu",
            "\244\303\244\350", "yyo",
            "\244\303\244\351", "rra",
            "\244\303\244\352", "rri",
            "\244\303\244\352\244\343", "rrya",
            "\244\303\244\352\244\345", "rryu",
            "\244\303\244\352\244\347", "rryo",
            "\244\303\244\353", "rru",
            "\244\303\244\354", "rre",
            "\244\303\244\355", "rro",

            "\244\304", "tsu",
            "\244\305", "du",
            "\244\306", "te",
            "\244\307", "de",
            "\244\310", "to",
            "\244\311", "do",

            "\244\312", "na",
            "\244\313", "ni",
            "\244\313\244\343", "nya",
            "\244\313\244\345", "nyu",
            "\244\313\244\347", "nyo",
            "\244\314", "nu",
            "\244\315", "ne",
            "\244\316", "no",

            "\244\317", "ha",
            "\244\320", "ba",
            "\244\321", "pa",
            "\244\322", "hi",
            "\244\322\244\343", "hya",
            "\244\322\244\345", "hyu",
            "\244\322\244\347", "hyo",
            "\244\323", "bi",
            "\244\323\244\343", "bya",
            "\244\323\244\345", "byu",
            "\244\323\244\347", "byo",
            "\244\324", "pi",
            "\244\324\244\343", "pya",
            "\244\324\244\345", "pyu",
            "\244\324\244\347", "pyo",
            "\244\325", "fu",
            "\244\325\244\241", "fa",
            "\244\325\244\243", "fi",
            "\244\325\244\247", "fe",
            "\244\325\244\251", "fo",
            "\244\326", "bu",
            "\244\327", "pu",
            "\244\330", "he",
            "\244\331", "be",
            "\244\332", "pe",
            "\244\333", "ho",
            "\244\334", "bo",
            "\244\335", "po",

            "\244\336", "ma",
            "\244\337", "mi",
            "\244\337\244\343", "mya",
            "\244\337\244\345", "myu",
            "\244\337\244\347", "myo",
            "\244\340", "mu",
            "\244\341", "me",
            "\244\342", "mo",

            "\244\343", "ya",
            "\244\344", "ya",
            "\244\345", "yu",
            "\244\346", "yu",
            "\244\347", "yo",
            "\244\350", "yo",

            "\244\351", "ra",
            "\244\352", "ri",
            "\244\352\244\343", "rya",
            "\244\352\244\345", "ryu",
            "\244\352\244\347", "ryo",
            "\244\353", "ru",
            "\244\354", "re",
            "\244\355", "ro",

            "\244\356", "wa",
            "\244\357", "wa",
            "\244\360", "i",
            "\244\361", "e",
            "\244\362", "wo",
            "\244\363", "n",

            "\244\363\244\242", "n'a",
            "\244\363\244\244", "n'i",
            "\244\363\244\246", "n'u",
            "\244\363\244\250", "n'e",
            "\244\363\244\252", "n'o",
            "", ""};
    private static final HashMap<String, String> hiragana_to_romaji_map;
    static {
        hiragana_to_romaji_map = new HashMap<String, String>(hiragana_to_romaji.length);
        for (int i = 0; i < hiragana_to_romaji.length; i += 2) {
            String key = hiragana_to_romaji[i];
            String value = hiragana_to_romaji[i + 1];
            hiragana_to_romaji_map.put(key, value);
        }


    }

    public map_hiragana() {
        this(0, null);
    }

    protected map_hiragana(int count, String str) {
        super(count, str);
    }

    protected void process(String str, int param) {
        int i = 0;
        StringBuilder out = new StringBuilder();

        switch (param) {
            case TO_KATAKANA: {
                char first_char = (char) (str.charAt(0) + 0x01);
                char second_char = str.charAt(1);
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
//				Iterator	iterater	= hiragana_to_romaji_map.keySet().iterator();
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
//						romaji	= (String)kanada_def.hiragana_to_romaji_map.get(key);
//						matched_len		= key_len;
//					}
//				}
//				i = matched_len > 0 ? matched_len : 2;

                i = str.length() > 8 ? 8 : str.length();
                if (i % 2 == 1) {
                    --i;
                }
                String romaji = "";
                while (i > 1) {
                    String kana = str.substring(0, i);
                    if (hiragana_to_romaji_map.containsKey(kana)) {
                        romaji = hiragana_to_romaji_map.get(kana);
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
