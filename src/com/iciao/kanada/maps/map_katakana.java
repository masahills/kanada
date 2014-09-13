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
import com.iciao.kanada.kanada_def;

/**
 * Remap non-kanji characters.<br>
 *
 * @author Masahiko Sato
 */
public class map_katakana extends j_mapper {
    public map_katakana() {
        this(0, null);
    }

    protected map_katakana(int count, String str) {
        super(count, str);
    }

    protected void process(String str, int param) {
        int i = 0;
        StringBuffer out = new StringBuffer();

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
                    if (kanada_def.katakana_to_romaji_map.containsKey(kana)) {
                        romaji = (String) kanada_def.katakana_to_romaji_map.get(kana);
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

/*
 * $History: $
 */
