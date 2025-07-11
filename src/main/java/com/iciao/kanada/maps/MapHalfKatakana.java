package com.iciao.kanada.maps;

import com.iciao.kanada.JMapper;

/**
 * Remap non-kanji characters.<br>
 *
 * @author Masahiko Sato
 */
public class MapHalfKatakana extends JMapper {
    public MapHalfKatakana() {
        this(null);
    }

    protected MapHalfKatakana(String str) {
        super(str);
    }

    protected void process(String str, int param) {
        StringBuilder out = new StringBuilder();
        int thisChar = str.codePointAt(0);
        out.appendCodePoint(thisChar);
        setString(out.toString());
    }
}
