package com.iciao.kanada.maps;

import com.iciao.kanada.JMapper;

import java.util.Objects;

/**
 * Remap non-kanji characters.<br>
 *
 * @author Masahiko Sato
 */
/*
U+FF6x	 	｡	｢	｣	､	･	ｦ	ｧ	ｨ	ｩ	ｪ	ｫ	ｬ	ｭ	ｮ	ｯ
U+FF7x	ｰ	ｱ	ｲ	ｳ	ｴ	ｵ	ｶ	ｷ	ｸ	ｹ	ｺ	ｻ	ｼ	ｽ	ｾ	ｿ
U+FF8x	ﾀ	ﾁ	ﾂ	ﾃ	ﾄ	ﾅ	ﾆ	ﾇ	ﾈ	ﾉ	ﾊ	ﾋ	ﾌ	ﾍ	ﾎ	ﾏ
U+FF9x	ﾐ	ﾑ	ﾒ	ﾓ	ﾔ	ﾕ	ﾖ	ﾗ	ﾘ	ﾙ	ﾚ	ﾛ	ﾜ	ﾝ	ﾞ	ﾟ
 */
public class MapHalfKatakana extends JMapper {

    private static final KanaMapping kanaMapping = KanaMapping.getInstance();

    @Override
    protected void process(String str, int param) {
        StringBuilder out = new StringBuilder();

        for (int i = 0; i < str.length(); i++) {
            char thisChar = str.charAt(i);
            // Check for dakuten/handakuten combination
            if (i + 1 < str.length()) {
                char next = str.charAt(i + 1);
                if (next == 'ﾞ' || next == 'ﾟ') {
                    String combined = combineDakuten(thisChar, next);
                    if (combined != null) {
                        out.append(combined);
                        i++; // skip next character
                        continue;
                    }
                }
            }

            String converted = convertChar(thisChar, param);
            if (param == TO_ASCII || param == TO_WIDE_ASCII) {
                String romaji = kanaMapping.toRomaji(converted, getRomanizationSystem());
                if (romaji != null) {
                    out.append(kanaMapping.removeMacrons(romaji));
                } else {
                    out.append(converted);
                }
            } else {
                out.append(Objects.requireNonNullElse(converted, thisChar));
            }
        }

        setString(out.toString());
    }

    private String combineDakuten(char base, char mark) {
        if (mark == 'ﾞ') { // dakuten
            if ((base >= 'ｶ' && base <= 'ｺ') || // ka-ko
                    (base >= 'ｻ' && base <= 'ｿ') || // sa-so
                    (base >= 'ﾀ' && base <= 'ﾄ') || // ta-to
                    (base >= 'ﾊ' && base <= 'ﾎ')) { // ha-ho
                return String.valueOf((char) (base - 0xFF71 + 0x30AC)); // to dakuten katakana
            }
        } else if (mark == 'ﾟ') { // handakuten
            if (base >= 'ﾊ' && base <= 'ﾎ') { // ha-ho
                return String.valueOf((char) (base - 0xFF8A + 0x30D1)); // to handakuten katakana
            }
        }
        return null;
    }

    private String convertChar(char c, int param) {
        // Punctuation conversion
        switch (c) {
            case '｡':
                return "。"; // period
            case '｢':
                return "「"; // left bracket
            case '｣':
                return "」"; // right bracket
            case '､':
                return "、"; // comma
            case '･':
                return "・"; // middle dot
            case 'ｰ':
                return "ー"; // long vowel mark
        }

        // Determine target base
        int smallBase, normalBase;
        switch (param) {
            case TO_KATAKANA:
            case TO_ASCII:
            case TO_WIDE_ASCII:
                smallBase = 0x30A1;
                normalBase = 0x30A2;
                break;
            case TO_HIRAGANA:
                smallBase = 0x3041;
                normalBase = 0x3042;
                break;
            default:
                return null;
        }

        // Convert half-width katakana
        if (c >= 0xFF67 && c <= 0xFF6F) {
            return String.valueOf((char) (c - 0xFF67 + smallBase));
        }
        if (c >= 0xFF71 && c <= 0xFF9D) {
            return String.valueOf((char) (c - 0xFF71 + normalBase));
        }

        return null;
    }
}
