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

import com.iciao.kanada.JMapper;

import java.util.HashMap;

/**
 * Remap non-kanji characters.<br>
 *
 * @author Masahiko Sato
 */

/**
        0	1	2	3	4	5	6	7	8	9	A	B	C	D	E	F
 U+30Ax	゠	ァ	ア	ィ	イ	ゥ	ウ	ェ	エ	ォ	オ	カ	ガ	キ	ギ	ク
 U+30Bx	グ	ケ	ゲ	コ	ゴ	サ	ザ	シ	ジ	ス	ズ	セ	ゼ	ソ	ゾ	タ
 U+30Cx	ダ	チ	ヂ	ッ	ツ	ヅ	テ	デ	ト	ド	ナ	ニ	ヌ	ネ	ノ	ハ
 U+30Dx	バ	パ	ヒ	ビ	ピ	フ	ブ	プ	ヘ	ベ	ペ	ホ	ボ	ポ	マ	ミ
 U+30Ex	ム	メ	モ	ャ	ヤ	ュ	ユ	ョ	ヨ	ラ	リ	ル	レ	ロ	ヮ	ワ
 U+30Fx	ヰ	ヱ	ヲ	ン	ヴ	ヵ	ヶ	ヷ	ヸ	ヹ	ヺ	・	ー	ヽ	ヾ	ヿ
 */
public class MapKatakana extends JMapper {
    private static final String[] katakanaToRomaji = {
            "\u30a1", "a",
            "\u30a2", "a",
            "\u30a3", "i",
            "\u30a4", "i",
            "\u30a4\u30a7", "ye",
            "\u30a5", "u",
            "\u30a6", "u",
            "\u30a7", "e",
            "\u30a8", "e",
            "\u30a9", "o",
            "\u30aa", "o",

            "\u30ab", "ka",
            "\u30ac", "ga",
            "\u30ad", "ki",
            "\u30ad\u30a3", "ki",
            "\u30ad\u30a7", "kye",
            "\u30ad\u30e3", "kya",
            "\u30ad\u30e5", "kyu",
            "\u30ad\u30e7", "kyo",
            "\u30ae", "gi",
            "\u30ae\u30a3", "gi",
            "\u30ae\u30a7", "gye",
            "\u30ae\u30e3", "gya",
            "\u30ae\u30e5", "gyu",
            "\u30ae\u30e7", "gyo",
            "\u30af", "ku",
            "\u30af\u30a1", "kwa",
            "\u30af\u30a3", "kwi",
            "\u30af\u30a5", "ku",
            "\u30af\u30a7", "kwe",
            "\u30af\u30a9", "kwo",
            "\u30b0", "gu",
            "\u30b0\u30a1", "gwa",
            "\u30b0\u30a3", "gwi",
            "\u30b0\u30a5", "gu",
            "\u30b0\u30a7", "gwe",
            "\u30b0\u30a9", "gwo",
            "\u30b1", "ke",
            "\u30b2", "ge",
            "\u30b3", "ko",
            "\u30b4", "go",

            "\u30b5", "sa",
            "\u30b6", "za",
            "\u30b7", "si",
            "\u30b7\u30a3", "si",
            "\u30b7\u30a7", "sye",
            "\u30b7\u30e3", "sya",
            "\u30b7\u30e5", "syu",
            "\u30b7\u30e7", "syo",
            "\u30b8", "zi",
            "\u30b8\u30a3", "zi",
            "\u30b8\u30a7", "zye",
            "\u30b8\u30e3", "zya",
            "\u30b8\u30e5", "zyu",
            "\u30b8\u30e7", "zyo",
            "\u30b9", "su",
            "\u30ba", "zu",
            "\u30bb", "se",
            "\u30bc", "ze",
            "\u30bd", "so",
            "\u30be", "zo",

            "\u30bf", "ta",
            "\u30c0", "da",
            "\u30c1", "ti",
            "\u30c1\u30a3", "ti",
            "\u30c1\u30a7", "tye",
            "\u30c1\u30e3", "tya",
            "\u30c1\u30e5", "tyu",
            "\u30c1\u30e7", "tyo",
            "\u30c2", "zi",
            "\u30c2\u30a3", "zi",
            "\u30c2\u30a7", "zye",
            "\u30c2\u30e3", "zya",
            "\u30c2\u30e5", "zyu",
            "\u30c2\u30e7", "zyo",

            "\u30c3", "tu",
            "\u30c3\u30ab", "kka",
            "\u30c3\u30ac", "gga",
            "\u30c3\u30ad", "kki",
            "\u30c3\u30ad\u30a3", "kki",
            "\u30c3\u30ad\u30a7", "kkye",
            "\u30c3\u30ad\u30e3", "kkya",
            "\u30c3\u30ad\u30e5", "kkyu",
            "\u30c3\u30ad\u30e7", "kkyo",
            "\u30c3\u30ae", "ggi",
            "\u30c3\u30ae\u30a3", "ggi",
            "\u30c3\u30ae\u30a7", "ggye",
            "\u30c3\u30ae\u30e3", "ggya",
            "\u30c3\u30ae\u30e5", "ggyu",
            "\u30c3\u30ae\u30e7", "ggyo",
            "\u30c3\u30af", "kku",
            "\u30c3\u30b0", "ggu",
            "\u30c3\u30b1", "kke",
            "\u30c3\u30b2", "gge",
            "\u30c3\u30b3", "kko",
            "\u30c3\u30b4", "ggo",
            "\u30c3\u30b5", "ssa",
            "\u30c3\u30b6", "zza",
            "\u30c3\u30b7", "ssi",
            "\u30c3\u30b7\u30a3", "ssi",
            "\u30c3\u30b7\u30a7", "ssye",
            "\u30c3\u30b7\u30e3", "ssya",
            "\u30c3\u30b7\u30e5", "ssyu",
            "\u30c3\u30b7\u30e7", "ssyo",
            "\u30c3\u30b8", "zzi",
            "\u30c3\u30b8\u30a3", "zzi",
            "\u30c3\u30b8\u30a7", "zzye",
            "\u30c3\u30b8\u30e3", "zzya",
            "\u30c3\u30b8\u30e5", "zzyu",
            "\u30c3\u30b8\u30e7", "zzyo",
            "\u30c3\u30b9", "ssu",
            "\u30c3\u30ba", "zzu",
            "\u30c3\u30bb", "sse",
            "\u30c3\u30bc", "zze",
            "\u30c3\u30bd", "sso",
            "\u30c3\u30be", "zzo",
            "\u30c3\u30bf", "tta",
            "\u30c3\u30c0", "dda",
            "\u30c3\u30c1", "tti",
            "\u30c3\u30c1\u30a3", "tti",
            "\u30c3\u30c1\u30a7", "ttye",
            "\u30c3\u30c1\u30e3", "ttya",
            "\u30c3\u30c1\u30e5", "ttyu",
            "\u30c3\u30c1\u30e7", "ttyo",
            "\u30c3\u30c2", "zzi",
            "\u30c3\u30c2\u30a3", "zzi",
            "\u30c3\u30c2\u30a7", "zzye",
            "\u30c3\u30c2\u30e3", "zzya",
            "\u30c3\u30c2\u30e5", "zzyu",
            "\u30c3\u30c2\u30e7", "zzyo",
            "\u30c3\u30c4", "ttu",
            "\u30c3\u30c5", "zzu",
            "\u30c3\u30c6", "tte",
            "\u30c3\u30c7", "dde",
            "\u30c3\u30c8", "tto",
            "\u30c3\u30c9", "ddo",
            "\u30c3\u30cf", "hha",
            "\u30c3\u30d0", "bba",
            "\u30c3\u30d1", "ppa",
            "\u30c3\u30d2", "hhi",
            "\u30c3\u30d2\u30a3", "hhi",
            "\u30c3\u30d2\u30a7", "hhye",
            "\u30c3\u30d2\u30e3", "hhya",
            "\u30c3\u30d2\u30e5", "hhyu",
            "\u30c3\u30d2\u30e7", "hhyo",
            "\u30c3\u30d3", "bbi",
            "\u30c3\u30d3\u30a3", "bbi",
            "\u30c3\u30d3\u30a7", "bbye",
            "\u30c3\u30d3\u30e3", "bbya",
            "\u30c3\u30d3\u30e5", "bbyu",
            "\u30c3\u30d3\u30e7", "bbyo",
            "\u30c3\u30d4", "ppi",
            "\u30c3\u30d4\u30a3", "ppi",
            "\u30c3\u30d4\u30a7", "ppye",
            "\u30c3\u30d4\u30e3", "ppya",
            "\u30c3\u30d4\u30e5", "ppyu",
            "\u30c3\u30d4\u30e7", "ppyo",
            "\u30c3\u30d5", "hhu",
            "\u30c3\u30d5\u30a1", "hhwa",
            "\u30c3\u30d5\u30a3", "hhwi",
            "\u30c3\u30d5\u30a5", "hhu",
            "\u30c3\u30d5\u30a7", "hhwe",
            "\u30c3\u30d5\u30a9", "hhwo",
            "\u30c3\u30d6", "bbu",
            "\u30c3\u30d7", "ppu",
            "\u30c3\u30d8", "hhe",
            "\u30c3\u30d9", "bbe",
            "\u30c3\u30da", "ppe",
            "\u30c3\u30db", "hho",
            "\u30c3\u30dc", "bbo",
            "\u30c3\u30dd", "ppo",
            "\u30c3\u30e4", "yya",
            "\u30c3\u30e6", "yyu",
            "\u30c3\u30e8", "yyo",
            "\u30c3\u30e9", "rra",
            "\u30c3\u30ea", "rri",
            "\u30c3\u30ea\u30e3", "rrya",
            "\u30c3\u30ea\u30e5", "rryu",
            "\u30c3\u30ea\u30e7", "rryo",
            "\u30c3\u30eb", "rru",
            "\u30c3\u30ec", "rre",
            "\u30c3\u30ed", "rro",
            "\u30c3\u30f4", "vvu",
            "\u30c3\u30f4\u30a1", "vva",
            "\u30c3\u30f4\u30a3", "vvi",
            "\u30c3\u30f4\u30a7", "vve",
            "\u30c3\u30f4\u30a9", "vvo",

            "\u30c4", "tu",
            "\u30c4\u30a1", "twa",
            "\u30c4\u30a3", "twi",
            "\u30c4\u30a5", "tu",
            "\u30c4\u30a7", "twe",
            "\u30c4\u30a9", "two",
            "\u30c5", "zu",
            "\u30c6", "te",
            "\u30c6\u30e5", "twyu",
            "\u30c7", "de",
            "\u30c7\u30e5", "dwyu",
            "\u30c8", "to",
            "\u30c9", "do",

            "\u30ca", "na",
            "\u30cb", "ni",
            "\u30cb\u30a3", "ni",
            "\u30cb\u30a7", "nye",
            "\u30cb\u30e3", "nya",
            "\u30cb\u30e5", "nyu",
            "\u30cb\u30e7", "nyo",
            "\u30cc", "nu",
            "\u30cd", "ne",
            "\u30ce", "no",

            "\u30cf", "ha",
            "\u30d0", "ba",
            "\u30d1", "pa",
            "\u30d2", "hi",
            "\u30d2\u30a3", "hi",
            "\u30d2\u30a7", "hye",
            "\u30d2\u30e3", "hya",
            "\u30d2\u30e5", "hyu",
            "\u30d2\u30e7", "hyo",
            "\u30d3", "bi",
            "\u30d3\u30a3", "bi",
            "\u30d3\u30a7", "bye",
            "\u30d3\u30e3", "bya",
            "\u30d3\u30e5", "byu",
            "\u30d3\u30e7", "byo",
            "\u30d4", "pi",
            "\u30d4\u30a3", "pi",
            "\u30d4\u30a7", "pye",
            "\u30d4\u30e3", "pya",
            "\u30d4\u30e5", "pyu",
            "\u30d4\u30e7", "pyo",
            "\u30d5", "hu",
            "\u30d5\u30a1", "hwa",
            "\u30d5\u30a3", "hwi",
            "\u30d5\u30a5", "hu",
            "\u30d5\u30a7", "hwe",
            "\u30d5\u30a9", "hwo",
            "\u30d5\u30e5", "hwyu",
            "\u30d6", "bu",
            "\u30d7", "pu",
            "\u30d8", "he",
            "\u30d9", "be",
            "\u30da", "pe",
            "\u30db", "ho",
            "\u30dc", "bo",
            "\u30dd", "po",

            "\u30de", "ma",
            "\u30df", "mi",
            "\u30df\u30a3", "mi",
            "\u30df\u30a7", "mye",
            "\u30df\u30e3", "mya",
            "\u30df\u30e5", "myu",
            "\u30df\u30e7", "myo",
            "\u30e0", "mu",
            "\u30e1", "me",
            "\u30e2", "mo",

            "\u30e3", "ya",
            "\u30e4", "ya",
            "\u30e5", "yu",
            "\u30e6", "yu",
            "\u30e7", "yo",
            "\u30e8", "yo",

            "\u30e9", "ra",
            "\u30ea", "ri",
            "\u30ea\u30a3", "ri",
            "\u30ea\u30a7", "rye",
            "\u30ea\u30e3", "rya",
            "\u30ea\u30e5", "ryu",
            "\u30ea\u30e7", "ryo",
            "\u30eb", "ru",
            "\u30ec", "re",
            "\u30ed", "ro",

            "\u30ee", "wa",
            "\u30ef", "wa",
            "\u30f0", "i",
            "\u30f1", "e",
            "\u30f2", "o",
            "\u30f3", "n",

            "\u30f3\u30a2", "n'a",
            "\u30f3\u30a4", "n'i",
            "\u30f3\u30a6", "n'u",
            "\u30f3\u30a8", "n'e",
            "\u30f3\u30aa", "n'o",

            "\u30f4", "vu",
            "\u30f4\u30a1", "va",
            "\u30f4\u30a3", "vi",
            "\u30f4\u30a7", "ve",
            "\u30f4\u30a9", "vo",
            "\u30f4\u30e3", "vya",
            "\u30f4\u30e5", "vyu",
            "\u30f4\u30e7", "vyo",

            "\u30f5", "ka",
            "\u30f6", "ke",

            "", ""};

    private static final HashMap<String, String> katakanaToRomajiMap;

    static {
        katakanaToRomajiMap = new HashMap<String, String>(katakanaToRomaji.length);
        for (int i = 0; i < katakanaToRomaji.length; i += 2) {
            String key = katakanaToRomaji[i];
            String value = katakanaToRomaji[i + 1];
            katakanaToRomajiMap.put(key, value);
        }
    }

    public MapKatakana() {
        this(null);
    }

    protected MapKatakana(String str) {
        super(str);
    }

    protected void process(String str, int param) {
        StringBuilder out = new StringBuilder();
        int thisChar = str.codePointAt(0);

        switch (param) {
            case TO_HIRAGANA:
                out.appendCodePoint(thisChar - 0x60);
                break;
            case TO_HALF_KATAKANA:
                // TODO: need implementation
                out.appendCodePoint(thisChar);
                break;
            case TO_ASCII:
            case TO_WIDE_ASCII:
                String kana = String.valueOf(Character.toChars(thisChar));
                if (katakanaToRomajiMap.containsKey(kana)) {
                    out.append(katakanaToRomajiMap.get(kana));
                }
                break;
            default:
                out.appendCodePoint(thisChar);
                break;
        }

        setString(out.toString());
    }
}
