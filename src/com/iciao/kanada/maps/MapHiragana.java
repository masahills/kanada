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
package com.iciao.kanada.maps;

import com.iciao.kanada.JMapper;

import java.util.HashMap;

/**
 * Remap non-kanji characters.<br>
 *
 * @author Masahiko Sato
 */

/**
 0	1	2	3	4	5	6	7	8	9	A	B	C	D	E	F
 U+304x		ぁ	あ	ぃ	い	ぅ	う	ぇ	え	ぉ	お	か	が	き	ぎ	く
 U+305x	ぐ	け	げ	こ	ご	さ	ざ	し	じ	す	ず	せ	ぜ	そ	ぞ	た
 U+306x	だ	ち	ぢ	っ	つ	づ	て	で	と	ど	な	に	ぬ	ね	の	は
 U+307x	ば	ぱ	ひ	び	ぴ	ふ	ぶ	ぷ	へ	べ	ぺ	ほ	ぼ	ぽ	ま	み
 U+308x	む	め	も	ゃ	や	ゅ	ゆ	ょ	よ	ら	り	る	れ	ろ	ゎ	わ
 U+309x	ゐ	ゑ	を	ん	ゔ	ゕ	ゖ			゙	゚	゛	゜	ゝ	ゞ	ゟ 
 */
public class MapHiragana extends JMapper {
    private static final String[] hiraganaToRomaji = {
            "\u3041", "a",
            "\u3042", "a",
            "\u3043", "i",
            "\u3044", "i",
            "\u3044\u3047", "ye",
            "\u3045", "u",
            "\u3046", "u",
            "\u3047", "e",
            "\u3048", "e",
            "\u3049", "o",
            "\u304a", "o",

            "\u304b", "ka",
            "\u304c", "ga",
            "\u304d", "ki",
            "\u304d\u3043", "ki",
            "\u304d\u3047", "kye",
            "\u304d\u3083", "kya",
            "\u304d\u3085", "kyu",
            "\u304d\u3087", "kyo",
            "\u304e", "gi",
            "\u304e\u3043", "gi",
            "\u304e\u3047", "gye",
            "\u304e\u3083", "gya",
            "\u304e\u3085", "gyu",
            "\u304e\u3087", "gyo",
            "\u304f", "ku",
            "\u304f\u3041", "kwa",
            "\u304f\u3043", "kwi",
            "\u304f\u3045", "ku",
            "\u304f\u3047", "kwe",
            "\u304f\u3049", "kwo",
            "\u3050", "gu",
            "\u3050\u3041", "gwa",
            "\u3050\u3043", "gwi",
            "\u3050\u3045", "gu",
            "\u3050\u3047", "gwe",
            "\u3050\u3049", "gwo",
            "\u3051", "ke",
            "\u3052", "ge",
            "\u3053", "ko",
            "\u3054", "go",

            "\u3055", "sa",
            "\u3056", "za",
            "\u3057", "si",
            "\u3057\u3043", "si",
            "\u3057\u3047", "sye",
            "\u3057\u3083", "sya",
            "\u3057\u3085", "syu",
            "\u3057\u3087", "syo",
            "\u3058", "zi",
            "\u3058\u3043", "zi",
            "\u3058\u3047", "zye",
            "\u3058\u3083", "zya",
            "\u3058\u3085", "zyu",
            "\u3058\u3087", "zyo",
            "\u3059", "su",
            "\u305a", "zu",
            "\u305b", "se",
            "\u305c", "ze",
            "\u305d", "so",
            "\u305e", "zo",

            "\u305f", "ta",
            "\u3060", "da",
            "\u3061", "ti",
            "\u3061\u3043", "ti",
            "\u3061\u3047", "tye",
            "\u3061\u3083", "tya",
            "\u3061\u3085", "tyu",
            "\u3061\u3087", "tyo",
            "\u3062", "zi",
            "\u3062\u3043", "zi",
            "\u3062\u3047", "zye",
            "\u3062\u3083", "zya",
            "\u3062\u3085", "zyu",
            "\u3062\u3087", "zyo",

            "\u3063", "tu",
            "\u3063\u304b", "kka",
            "\u3063\u304c", "gga",
            "\u3063\u304d", "kki",
            "\u3063\u304d\u3043", "kki",
            "\u3063\u304d\u3047", "kkye",
            "\u3063\u304d\u3083", "kkya",
            "\u3063\u304d\u3085", "kkyu",
            "\u3063\u304d\u3087", "kkyo",
            "\u3063\u304e", "ggi",
            "\u3063\u304e\u3043", "ggi",
            "\u3063\u304e\u3047", "ggye",
            "\u3063\u304e\u3083", "ggya",
            "\u3063\u304e\u3085", "ggyu",
            "\u3063\u304e\u3087", "ggyo",
            "\u3063\u304f", "kku",
            "\u3063\u3050", "ggu",
            "\u3063\u3051", "kke",
            "\u3063\u3052", "gge",
            "\u3063\u3053", "kko",
            "\u3063\u3054", "ggo",
            "\u3063\u3055", "ssa",
            "\u3063\u3056", "zza",
            "\u3063\u3057", "ssi",
            "\u3063\u3057\u3043", "ssi",
            "\u3063\u3057\u3047", "ssye",
            "\u3063\u3057\u3083", "ssya",
            "\u3063\u3057\u3085", "ssyu",
            "\u3063\u3057\u3087", "ssyo",
            "\u3063\u3058", "zzi",
            "\u3063\u3058\u3043", "zzi",
            "\u3063\u3058\u3047", "zzye",
            "\u3063\u3058\u3083", "zzya",
            "\u3063\u3058\u3085", "zzyu",
            "\u3063\u3058\u3087", "zzyo",
            "\u3063\u3059", "ssu",
            "\u3063\u305a", "zzu",
            "\u3063\u305b", "sse",
            "\u3063\u305c", "zze",
            "\u3063\u305d", "sso",
            "\u3063\u305e", "zzo",
            "\u3063\u305f", "tta",
            "\u3063\u3060", "dda",
            "\u3063\u3061", "tti",
            "\u3063\u3061\u3043", "tti",
            "\u3063\u3061\u3047", "ttye",
            "\u3063\u3061\u3083", "ttya",
            "\u3063\u3061\u3085", "ttyu",
            "\u3063\u3061\u3087", "ttyo",
            "\u3063\u3062", "zzi",
            "\u3063\u3062\u3043", "zzi",
            "\u3063\u3062\u3047", "zzye",
            "\u3063\u3062\u3083", "zzya",
            "\u3063\u3062\u3085", "zzyu",
            "\u3063\u3062\u3087", "zzyo",
            "\u3063\u3064", "ttu",
            "\u3063\u3065", "zzu",
            "\u3063\u3066", "tte",
            "\u3063\u3067", "dde",
            "\u3063\u3068", "tto",
            "\u3063\u3069", "ddo",
            "\u3063\u306f", "hha",
            "\u3063\u3070", "bba",
            "\u3063\u3071", "ppa",
            "\u3063\u3072", "hhi",
            "\u3063\u3072\u3043", "hhi",
            "\u3063\u3072\u3047", "hhye",
            "\u3063\u3072\u3083", "hhya",
            "\u3063\u3072\u3085", "hhyu",
            "\u3063\u3072\u3087", "hhyo",
            "\u3063\u3073", "bbi",
            "\u3063\u3073\u3043", "bbi",
            "\u3063\u3073\u3047", "bbye",
            "\u3063\u3073\u3083", "bbya",
            "\u3063\u3073\u3085", "bbyu",
            "\u3063\u3073\u3087", "bbyo",
            "\u3063\u3074", "ppi",
            "\u3063\u3074\u3043", "ppi",
            "\u3063\u3074\u3047", "ppye",
            "\u3063\u3074\u3083", "ppya",
            "\u3063\u3074\u3085", "ppyu",
            "\u3063\u3074\u3087", "ppyo",
            "\u3063\u3075", "hhu",
            "\u3063\u3075\u3041", "hhwa",
            "\u3063\u3075\u3043", "hhwi",
            "\u3063\u3075\u3045", "hhu",
            "\u3063\u3075\u3047", "hhwe",
            "\u3063\u3075\u3049", "hhwo",
            "\u3063\u3076", "bbu",
            "\u3063\u3077", "ppu",
            "\u3063\u3078", "hhe",
            "\u3063\u3079", "bbe",
            "\u3063\u307a", "ppe",
            "\u3063\u307b", "hho",
            "\u3063\u307c", "bbo",
            "\u3063\u307d", "ppo",
            "\u3063\u3084", "yya",
            "\u3063\u3086", "yyu",
            "\u3063\u3088", "yyo",
            "\u3063\u3089", "rra",
            "\u3063\u308a", "rri",
            "\u3063\u308a\u3083", "rrya",
            "\u3063\u308a\u3085", "rryu",
            "\u3063\u308a\u3087", "rryo",
            "\u3063\u308b", "rru",
            "\u3063\u308c", "rre",
            "\u3063\u308d", "rro",

            "\u3064", "tu",
            "\u3064\u3041", "twa",
            "\u3064\u3043", "twi",
            "\u3064\u3045", "tu",
            "\u3064\u3047", "twe",
            "\u3064\u3049", "two",
            "\u3065", "zu",
            "\u3066", "te",
            "\u3066\u3085", "twyu",
            "\u3067", "de",
            "\u3067\u3085", "dwyu",
            "\u3068", "to",
            "\u3069", "do",

            "\u306a", "na",
            "\u306b", "ni",
            "\u306b\u3043", "ni",
            "\u306b\u3047", "nye",
            "\u306b\u3083", "nya",
            "\u306b\u3085", "nyu",
            "\u306b\u3087", "nyo",
            "\u306c", "nu",
            "\u306d", "ne",
            "\u306e", "no",

            "\u306f", "ha",
            "\u3070", "ba",
            "\u3071", "pa",
            "\u3072", "hi",
            "\u3072\u3043", "hi",
            "\u3072\u3047", "hye",
            "\u3072\u3083", "hya",
            "\u3072\u3085", "hyu",
            "\u3072\u3087", "hyo",
            "\u3073", "bi",
            "\u3073\u3043", "bi",
            "\u3073\u3047", "bye",
            "\u3073\u3083", "bya",
            "\u3073\u3085", "byu",
            "\u3073\u3087", "byo",
            "\u3074", "pi",
            "\u3074\u3043", "pi",
            "\u3074\u3047", "pye",
            "\u3074\u3083", "pya",
            "\u3074\u3085", "pyu",
            "\u3074\u3087", "pyo",
            "\u3075", "hu",
            "\u3075\u3041", "hwa",
            "\u3075\u3043", "hwi",
            "\u3075\u3045", "hu",
            "\u3075\u3047", "hwe",
            "\u3075\u3049", "hwo",
            "\u3075\u3085", "hwyu",
            "\u3076", "bu",
            "\u3077", "pu",
            "\u3078", "he",
            "\u3079", "be",
            "\u307a", "pe",
            "\u307b", "ho",
            "\u307c", "bo",
            "\u307d", "po",

            "\u307e", "ma",
            "\u307f", "mi",
            "\u307f\u3043", "mi",
            "\u307f\u3047", "mye",
            "\u307f\u3083", "mya",
            "\u307f\u3085", "myu",
            "\u307f\u3087", "myo",
            "\u3080", "mu",
            "\u3081", "me",
            "\u3082", "mo",

            "\u3083", "ya",
            "\u3084", "ya",
            "\u3085", "yu",
            "\u3086", "yu",
            "\u3087", "yo",
            "\u3088", "yo",

            "\u3089", "ra",
            "\u308a", "ri",
            "\u308a\u3043", "ri",
            "\u308a\u3047", "rye",
            "\u308a\u3083", "rya",
            "\u308a\u3085", "ryu",
            "\u308a\u3087", "ryo",
            "\u308b", "ru",
            "\u308c", "re",
            "\u308d", "ro",

            "\u308e", "wa",
            "\u308f", "wa",
            "\u3090", "i",
            "\u3091", "e",
            "\u3092", "o",
            "\u3093", "n",

            "\u3093\u3042", "n'a",
            "\u3093\u3044", "n'i",
            "\u3093\u3046", "n'u",
            "\u3093\u3048", "n'e",
            "\u3093\u304a", "n'o",

            "\u3046\u3099", "vu", // う゛
            "\u3046\u3099\u3041", "va",
            "\u3046\u3099\u3043", "vi",
            "\u3046\u3099\u3047", "ve",
            "\u3046\u3099\u3049", "vo",
            "\u3046\u3099\u3083", "vya",
            "\u3046\u3099\u3085", "vyu",
            "\u3046\u3099\u3087", "vyo",

            "\u3063\u3046\u3099", "vvu",
            "\u3063\u3046\u3099\u3041", "vva",
            "\u3063\u3046\u3099\u3043", "vvi",
            "\u3063\u3046\u3099\u3047", "vve",
            "\u3063\u3046\u3099\u3049", "vvo",
            "\u3063\u3046\u3099\u3083", "vvya",
            "\u3063\u3046\u3099\u3085", "vvyu",
            "\u3063\u3046\u3099\u3087", "vvyo",

            "", ""};

    private static final HashMap<String, String> hiraganaToRomajiMap;

    static {
        hiraganaToRomajiMap = new HashMap<String, String>(hiraganaToRomaji.length);
        for (int i = 0; i < hiraganaToRomaji.length; i += 2) {
            String key = hiraganaToRomaji[i];
            String value = hiraganaToRomaji[i + 1];
            hiraganaToRomajiMap.put(key, value);
        }
    }

    public MapHiragana() {
        this(null);
    }

    protected MapHiragana(String str) {
        super(str);
    }

    protected void process(String str, int param) {
        StringBuilder out = new StringBuilder();
        int thisChar = str.codePointAt(0);
        switch (param) {
            case TO_KATAKANA:
                out.appendCodePoint(thisChar + 0x60);
                break;
            case TO_HALF_KATAKANA:
                // TODO: need implementation
                out.append(String.valueOf(Character.toChars(thisChar)));
                break;
            case TO_ASCII:
            case TO_WIDE_ASCII:
                String kana = String.valueOf(Character.toChars(thisChar));
                if (hiraganaToRomajiMap.containsKey(kana)) {
                    out.append(hiraganaToRomajiMap.get(kana));
                }
                break;
            default: {
                out.appendCodePoint(thisChar);
                break;
            }
        }
        setString(out.toString());
    }
}
