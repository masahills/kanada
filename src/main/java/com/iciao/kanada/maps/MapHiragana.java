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
public class MapHiragana extends JMapper {
    private static final String[] hiraganaToRomaji = {
            "ぁ", "a",
            "あ", "a",
            "ぃ", "i",
            "い", "i",
            "いぇ", "ye",
            "ぅ", "u",
            "う", "u",
            "ぇ", "e",
            "え", "e",
            "ぉ", "o",
            "お", "o",

            "か", "ka",
            "が", "ga",
            "き", "ki",
            "きぃ", "ki",
            "きぇ", "kye",
            "きゃ", "kya",
            "きゅ", "kyu",
            "きょ", "kyo",
            "ぎ", "gi",
            "ぎぃ", "gi",
            "ぎぇ", "gye",
            "ぎゃ", "gya",
            "ぎゅ", "gyu",
            "ぎょ", "gyo",
            "く", "ku",
            "くぁ", "kwa",
            "くぃ", "kwi",
            "くぅ", "ku",
            "くぇ", "kwe",
            "くぉ", "kwo",
            "ぐ", "gu",
            "ぐぁ", "gwa",
            "ぐぃ", "gwi",
            "ぐぅ", "gu",
            "ぐぇ", "gwe",
            "ぐぉ", "gwo",
            "け", "ke",
            "げ", "ge",
            "こ", "ko",
            "ご", "go",

            "さ", "sa",
            "ざ", "za",
            "し", "si",
            "しぃ", "si",
            "しぇ", "sye",
            "しゃ", "sya",
            "しゅ", "syu",
            "しょ", "syo",
            "じ", "zi",
            "じぃ", "zi",
            "じぇ", "zye",
            "じゃ", "zya",
            "じゅ", "zyu",
            "じょ", "zyo",
            "す", "su",
            "ず", "zu",
            "せ", "se",
            "ぜ", "ze",
            "そ", "so",
            "ぞ", "zo",

            "た", "ta",
            "だ", "da",
            "ち", "ti",
            "ちぃ", "ti",
            "ちぇ", "tye",
            "ちゃ", "tya",
            "ちゅ", "tyu",
            "ちょ", "tyo",
            "ぢ", "zi",
            "ぢぃ", "zi",
            "ぢぇ", "zye",
            "ぢゃ", "zya",
            "ぢゅ", "zyu",
            "ぢょ", "zyo",

            "っ", "tu",
            "っか", "kka",
            "っが", "gga",
            "っき", "kki",
            "っきぃ", "kki",
            "っきぇ", "kkye",
            "っきゃ", "kkya",
            "っきゅ", "kkyu",
            "っきょ", "kkyo",
            "っぎ", "ggi",
            "っぎぃ", "ggi",
            "っぎぇ", "ggye",
            "っぎゃ", "ggya",
            "っぎゅ", "ggyu",
            "っぎょ", "ggyo",
            "っく", "kku",
            "っぐ", "ggu",
            "っけ", "kke",
            "っげ", "gge",
            "っこ", "kko",
            "っご", "ggo",
            "っさ", "ssa",
            "っざ", "zza",
            "っし", "ssi",
            "っしぃ", "ssi",
            "っしぇ", "ssye",
            "っしゃ", "ssya",
            "っしゅ", "ssyu",
            "っしょ", "ssyo",
            "っじ", "zzi",
            "っじぃ", "zzi",
            "っじぇ", "zzye",
            "っじゃ", "zzya",
            "っじゅ", "zzyu",
            "っじょ", "zzyo",
            "っす", "ssu",
            "っず", "zzu",
            "っせ", "sse",
            "っぜ", "zze",
            "っそ", "sso",
            "っぞ", "zzo",
            "った", "tta",
            "っだ", "dda",
            "っち", "tti",
            "っちぃ", "tti",
            "っちぇ", "ttye",
            "っちゃ", "ttya",
            "っちゅ", "ttyu",
            "っちょ", "ttyo",
            "っぢ", "zzi",
            "っぢぃ", "zzi",
            "っぢぇ", "zzye",
            "っぢゃ", "zzya",
            "っぢゅ", "zzyu",
            "っぢょ", "zzyo",
            "っつ", "ttu",
            "っづ", "zzu",
            "って", "tte",
            "っで", "dde",
            "っと", "tto",
            "っど", "ddo",
            "っは", "hha",
            "っば", "bba",
            "っぱ", "ppa",
            "っひ", "hhi",
            "っひぃ", "hhi",
            "っひぇ", "hhye",
            "っひゃ", "hhya",
            "っひゅ", "hhyu",
            "っひょ", "hhyo",
            "っび", "bbi",
            "っびぃ", "bbi",
            "っびぇ", "bbye",
            "っびゃ", "bbya",
            "っびゅ", "bbyu",
            "っびょ", "bbyo",
            "っぴ", "ppi",
            "っぴぃ", "ppi",
            "っぴぇ", "ppye",
            "っぴゃ", "ppya",
            "っぴゅ", "ppyu",
            "っぴょ", "ppyo",
            "っふ", "hhu",
            "っふぁ", "hhwa",
            "っふぃ", "hhwi",
            "っふぅ", "hhu",
            "っふぇ", "hhwe",
            "っふぉ", "hhwo",
            "っぶ", "bbu",
            "っぷ", "ppu",
            "っへ", "hhe",
            "っべ", "bbe",
            "っぺ", "ppe",
            "っほ", "hho",
            "っぼ", "bbo",
            "っぽ", "ppo",
            "っや", "yya",
            "っゆ", "yyu",
            "っよ", "yyo",
            "っら", "rra",
            "っり", "rri",
            "っりゃ", "rrya",
            "っりゅ", "rryu",
            "っりょ", "rryo",
            "っる", "rru",
            "っれ", "rre",
            "っろ", "rro",

            "つ", "tu",
            "つぁ", "twa",
            "つぃ", "twi",
            "つぅ", "tu",
            "つぇ", "twe",
            "つぉ", "two",
            "づ", "zu",
            "て", "te",
            "てゅ", "twyu",
            "で", "de",
            "でゅ", "dwyu",
            "と", "to",
            "ど", "do",

            "な", "na",
            "に", "ni",
            "にぃ", "ni",
            "にぇ", "nye",
            "にゃ", "nya",
            "にゅ", "nyu",
            "にょ", "nyo",
            "ぬ", "nu",
            "ね", "ne",
            "の", "no",

            "は", "ha",
            "ば", "ba",
            "ぱ", "pa",
            "ひ", "hi",
            "ひぃ", "hi",
            "ひぇ", "hye",
            "ひゃ", "hya",
            "ひゅ", "hyu",
            "ひょ", "hyo",
            "び", "bi",
            "びぃ", "bi",
            "びぇ", "bye",
            "びゃ", "bya",
            "びゅ", "byu",
            "びょ", "byo",
            "ぴ", "pi",
            "ぴぃ", "pi",
            "ぴぇ", "pye",
            "ぴゃ", "pya",
            "ぴゅ", "pyu",
            "ぴょ", "pyo",
            "ふ", "hu",
            "ふぁ", "hwa",
            "ふぃ", "hwi",
            "ふぅ", "hu",
            "ふぇ", "hwe",
            "ふぉ", "hwo",
            "ふゅ", "hwyu",
            "ぶ", "bu",
            "ぷ", "pu",
            "へ", "he",
            "べ", "be",
            "ぺ", "pe",
            "ほ", "ho",
            "ぼ", "bo",
            "ぽ", "po",

            "ま", "ma",
            "み", "mi",
            "みぃ", "mi",
            "みぇ", "mye",
            "みゃ", "mya",
            "みゅ", "myu",
            "みょ", "myo",
            "む", "mu",
            "め", "me",
            "も", "mo",

            "ゃ", "ya",
            "や", "ya",
            "ゅ", "yu",
            "ゆ", "yu",
            "ょ", "yo",
            "よ", "yo",

            "ら", "ra",
            "り", "ri",
            "りぃ", "ri",
            "りぇ", "rye",
            "りゃ", "rya",
            "りゅ", "ryu",
            "りょ", "ryo",
            "る", "ru",
            "れ", "re",
            "ろ", "ro",

            "ゎ", "wa",
            "わ", "wa",
            "ゐ", "i",
            "ゑ", "e",
            "を", "o",
            "ん", "n",

            "んあ", "n'a",
            "んい", "n'i",
            "んう", "n'u",
            "んえ", "n'e",
            "んお", "n'o",

            "う゛", "vu", // う゛
            "う゛ぁ", "va",
            "う゛ぃ", "vi",
            "う゛ぇ", "ve",
            "う゛ぉ", "vo",
            "う゛ゃ", "vya",
            "う゛ゅ", "vyu",
            "う゛ょ", "vyo",

            "っう゛", "vvu",
            "っう゛ぁ", "vva",
            "っう゛ぃ", "vvi",
            "っう゛ぇ", "vve",
            "っう゛ぉ", "vvo",
            "っう゛ゃ", "vvya",
            "っう゛ゅ", "vvyu",
            "っう゛ょ", "vvyo",

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