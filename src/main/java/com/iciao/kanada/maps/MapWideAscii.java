package com.iciao.kanada.maps;

import com.iciao.kanada.JMapper;

/**
 * Remap non-kanji characters.<br>
 *
 * @author Masahiko Sato
 */
public class MapWideAscii extends JMapper {

    @Override
    protected void process(String str, int param) {
        StringBuilder out = new StringBuilder();
        int thisChar = str.codePointAt(0);
        if (thisChar >= 0xff00 && thisChar <= 0xff5e) {
            out.appendCodePoint(thisChar - 0xfee0);
        } else {
            out.appendCodePoint(thisChar);
        }
        setString(out.toString());
    }
}
