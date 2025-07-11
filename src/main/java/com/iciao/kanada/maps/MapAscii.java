package com.iciao.kanada.maps;

import com.iciao.kanada.JMapper;

/**
 * Map ASCII characters to fullwidth forms when appropriate.
 *
 * @author Masahiko Sato
 */
public class MapAscii extends JMapper {

    @Override
    protected void process(String str, int param) {
        StringBuilder out = new StringBuilder();
        int thisChar = str.codePointAt(0);
        if (param == JMapper.TO_WIDE_ASCII && thisChar > 0x20 && thisChar < 0x7f) {
            out.appendCodePoint(thisChar + 0xfee0);
        } else {
            out.appendCodePoint(thisChar);
        }
        setString(out.toString());
    }
}
