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

/**
 * Convert hiragana input to romaji using TSV-based mapping.
 * @author Masahiko Sato
 */
public class MapHiragana extends JMapper {
    
    private static final KanaMapping kanaMapping = KanaMapping.getInstance();
    
    public MapHiragana() {
        this(null);
    }
    
    protected MapHiragana(String str) {
        super(str);
    }
    
    @Override
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
                if (isHiragana(kana)) {
                    String romaji = kanaMapping.toRomaji(kana, KanaMapping.RomanizationSystem.KUNREI);
                    if (romaji != null) {
                        out.append(kanaMapping.removeMacrons(romaji));
                    } else {
                        out.append(kana);
                    }
                } else {
                    out.append(kana);
                }
                break;
            default:
                out.appendCodePoint(thisChar);
                break;
        }
        setString(out.toString());
    }
    
    private boolean isHiragana(String str) {
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (!(c >= '\u3041' && c <= '\u3096')) {
                return false;
            }
        }
        return true;
    }

}