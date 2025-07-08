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

/**
 * Convert katakana input to romaji using TSV-based mapping.
 * @author Masahiko Sato
 */
class MapKatakana extends JMapper {
    
    private static final KanaMapping kanaMapping = KanaMapping.getInstance();
    
    public MapKatakana() {
        this(null);
    }
    
    protected MapKatakana(String str) {
        super(str);
    }
    
    @Override
    protected void process(String str, int param) {
        StringBuilder out = new StringBuilder();
        int i = 0;
        
        while (i < str.length()) {
            String longest = null;
            int longestLength = 0;
            
            // Try longest match first
            for (int j = Math.min(i + 3, str.length()); j > i; j--) {
                String candidate = str.substring(i, j);
                String romaji = null;
                // Only process if input is actually katakana
                if (isKatakana(candidate)) {
                    romaji = kanaMapping.toRomaji(candidate, KanaMapping.RomanizationSystem.KUNREI);
                }
                
                if (romaji != null) {
                    longest = kanaMapping.removeMacrons(romaji);
                    longestLength = j - i;
                    break;
                }
            }
            
            if (longest != null) {
                out.append(longest);
                i += longestLength;
            } else {
                out.append(str.charAt(i));
                i++;
            }
        }
        
        setResult(out.toString());
    }
    
    private boolean isKatakana(String str) {
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (!(c >= '\u30A1' && c <= '\u30F6')) {
                return false;
            }
        }
        return true;
    }
    
    // Legacy array - no longer used
    @SuppressWarnings("unused")
    private static final String[] katakanaToRomaji = {
            "ァ", "a",
            "ア", "a",
            "ィ", "i",
            "イ", "i",
            "イェ", "ye",
            "ゥ", "u",
            "ウ", "u",
            "ェ", "e",
            "エ", "e",
            "ォ", "o",
            "オ", "o",

            "カ", "ka",
            "ガ", "ga",
            "キ", "ki",
            "キィ", "ki",
            "キェ", "kye",
            "キャ", "kya",
            "キュ", "kyu",
            "キョ", "kyo",
            "ギ", "gi",
            "ギィ", "gi",
            "ギェ", "gye",
            "ギャ", "gya",
            "ギュ", "gyu",
            "ギョ", "gyo",
            "ク", "ku",
            "クァ", "kwa",
            "クィ", "kwi",
            "クゥ", "ku",
            "クェ", "kwe",
            "クォ", "kwo",
            "グ", "gu",
            "グァ", "gwa",
            "グィ", "gwi",
            "グゥ", "gu",
            "グェ", "gwe",
            "グォ", "gwo",
            "ケ", "ke",
            "ゲ", "ge",
            "コ", "ko",
            "ゴ", "go",

            "サ", "sa",
            "ザ", "za",
            "シ", "si",
            "シィ", "si",
            "シェ", "sye",
            "シャ", "sya",
            "シュ", "syu",
            "ショ", "syo",
            "ジ", "zi",
            "ジィ", "zi",
            "ジェ", "zye",
            "ジャ", "zya",
            "ジュ", "zyu",
            "ジョ", "zyo",
            "ス", "su",
            "ズ", "zu",
            "セ", "se",
            "ゼ", "ze",
            "ソ", "so",
            "ゾ", "zo",

            "タ", "ta",
            "ダ", "da",
            "チ", "ti",
            "チィ", "ti",
            "チェ", "tye",
            "チャ", "tya",
            "チュ", "tyu",
            "チョ", "tyo",
            "ヂ", "zi",
            "ヂィ", "zi",
            "ヂェ", "zye",
            "ヂャ", "zya",
            "ヂュ", "zyu",
            "ヂョ", "zyo",

            "ッ", "tu",
            "ッカ", "kka",
            "ッガ", "gga",
            "ッキ", "kki",
            "ッキィ", "kki",
            "ッキェ", "kkye",
            "ッキャ", "kkya",
            "ッキュ", "kkyu",
            "ッキョ", "kkyo",
            "ッギ", "ggi",
            "ッギィ", "ggi",
            "ッギェ", "ggye",
            "ッギャ", "ggya",
            "ッギュ", "ggyu",
            "ッギョ", "ggyo",
            "ック", "kku",
            "ッグ", "ggu",
            "ッケ", "kke",
            "ッゲ", "gge",
            "ッコ", "kko",
            "ッゴ", "ggo",
            "ッサ", "ssa",
            "ッザ", "zza",
            "ッシ", "ssi",
            "ッシィ", "ssi",
            "ッシェ", "ssye",
            "ッシャ", "ssya",
            "ッシュ", "ssyu",
            "ッショ", "ssyo",
            "ッジ", "zzi",
            "ッジィ", "zzi",
            "ッジェ", "zzye",
            "ッジャ", "zzya",
            "ッジュ", "zzyu",
            "ッジョ", "zzyo",
            "ッス", "ssu",
            "ッズ", "zzu",
            "ッセ", "sse",
            "ッゼ", "zze",
            "ッソ", "sso",
            "ッゾ", "zzo",
            "ッタ", "tta",
            "ッダ", "dda",
            "ッチ", "tti",
            "ッチィ", "tti",
            "ッチェ", "ttye",
            "ッチャ", "ttya",
            "ッチュ", "ttyu",
            "ッチョ", "ttyo",
            "ッヂ", "zzi",
            "ッヂィ", "zzi",
            "ッヂェ", "zzye",
            "ッヂャ", "zzya",
            "ッヂュ", "zzyu",
            "ッヂョ", "zzyo",
            "ッツ", "ttu",
            "ッヅ", "zzu",
            "ッテ", "tte",
            "ッデ", "dde",
            "ット", "tto",
            "ッド", "ddo",
            "ッハ", "hha",
            "ッバ", "bba",
            "ッパ", "ppa",
            "ッヒ", "hhi",
            "ッヒィ", "hhi",
            "ッヒェ", "hhye",
            "ッヒャ", "hhya",
            "ッヒュ", "hhyu",
            "ッヒョ", "hhyo",
            "ッビ", "bbi",
            "ッビィ", "bbi",
            "ッビェ", "bbye",
            "ッビャ", "bbya",
            "ッビュ", "bbyu",
            "ッビョ", "bbyo",
            "ッピ", "ppi",
            "ッピィ", "ppi",
            "ッピェ", "ppye",
            "ッピャ", "ppya",
            "ッピュ", "ppyu",
            "ッピョ", "ppyo",
            "ッフ", "hhu",
            "ッファ", "hhwa",
            "ッフィ", "hhwi",
            "ッフゥ", "hhu",
            "ッフェ", "hhwe",
            "ッフォ", "hhwo",
            "ッブ", "bbu",
            "ップ", "ppu",
            "ッヘ", "hhe",
            "ッベ", "bbe",
            "ッペ", "ppe",
            "ッホ", "hho",
            "ッボ", "bbo",
            "ッポ", "ppo",
            "ッヤ", "yya",
            "ッユ", "yyu",
            "ッヨ", "yyo",
            "ッラ", "rra",
            "ッリ", "rri",
            "ッリャ", "rrya",
            "ッリュ", "rryu",
            "ッリョ", "rryo",
            "ッル", "rru",
            "ッレ", "rre",
            "ッロ", "rro",
            "ッヴ", "vvu",
            "ッヴァ", "vva",
            "ッヴィ", "vvi",
            "ッヴェ", "vve",
            "ッヴォ", "vvo",

            "ツ", "tu",
            "ツァ", "twa",
            "ツィ", "twi",
            "ツゥ", "tu",
            "ツェ", "twe",
            "ツォ", "two",
            "ヅ", "zu",
            "テ", "te",
            "テュ", "twyu",
            "デ", "de",
            "デュ", "dwyu",
            "ト", "to",
            "ド", "do",

            "ナ", "na",
            "ニ", "ni",
            "ニィ", "ni",
            "ニェ", "nye",
            "ニャ", "nya",
            "ニュ", "nyu",
            "ニョ", "nyo",
            "ヌ", "nu",
            "ネ", "ne",
            "ノ", "no",

            "ハ", "ha",
            "バ", "ba",
            "パ", "pa",
            "ヒ", "hi",
            "ヒィ", "hi",
            "ヒェ", "hye",
            "ヒャ", "hya",
            "ヒュ", "hyu",
            "ヒョ", "hyo",
            "ビ", "bi",
            "ビィ", "bi",
            "ビェ", "bye",
            "ビャ", "bya",
            "ビュ", "byu",
            "ビョ", "byo",
            "ピ", "pi",
            "ピィ", "pi",
            "ピェ", "pye",
            "ピャ", "pya",
            "ピュ", "pyu",
            "ピョ", "pyo",
            "フ", "hu",
            "ファ", "hwa",
            "フィ", "hwi",
            "フゥ", "hu",
            "フェ", "hwe",
            "フォ", "hwo",
            "フュ", "hwyu",
            "ブ", "bu",
            "プ", "pu",
            "ヘ", "he",
            "ベ", "be",
            "ペ", "pe",
            "ホ", "ho",
            "ボ", "bo",
            "ポ", "po",

            "マ", "ma",
            "ミ", "mi",
            "ミィ", "mi",
            "ミェ", "mye",
            "ミャ", "mya",
            "ミュ", "myu",
            "ミョ", "myo",
            "ム", "mu",
            "メ", "me",
            "モ", "mo",

            "ャ", "ya",
            "ヤ", "ya",
            "ュ", "yu",
            "ユ", "yu",
            "ョ", "yo",
            "ヨ", "yo",

            "ラ", "ra",
            "リ", "ri",
            "リィ", "ri",
            "リェ", "rye",
            "リャ", "rya",
            "リュ", "ryu",
            "リョ", "ryo",
            "ル", "ru",
            "レ", "re",
            "ロ", "ro",

            "ヮ", "wa",
            "ワ", "wa",
            "ヰ", "i",
            "ヱ", "e",
            "ヲ", "o",
            "ン", "n",

            "ンア", "n'a",
            "ンイ", "n'i",
            "ンウ", "n'u",
            "ンエ", "n'e",
            "ンオ", "n'o",

            "ヴ", "vu",
            "ヴァ", "va",
            "ヴィ", "vi",
            "ヴェ", "ve",
            "ヴォ", "vo",
            "ヴャ", "vya",
            "ヴュ", "vyu",
            "ヴョ", "vyo",

            "ヵ", "ka",
            "ヶ", "ke",

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