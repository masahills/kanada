package com.iciao.kanada.maps;

import com.iciao.kanada.JMapper;

/**
 * Remap non-kanji characters.<br>
 *
 * @author Masahiko Sato
 */
public class MapHalfSymbol extends JMapper {

    @Override
    protected void process(String str, int param) {
        StringBuilder out = new StringBuilder();
        char thisChar = str.charAt(0);
        if (param == JMapper.TO_WIDE_SYMBOL) {
            out.append(halfwidthSymbolToFull(thisChar));
        } else {
            out.append(thisChar);
        }
        setString(out.toString());
    }

    private char halfwidthSymbolToFull(char ch) {
        switch (ch) {
            case '¢':
                return '￠'; // CENT SIGN → FULLWIDTH CENT SIGN
            case '£':
                return '￡'; // POUND SIGN → FULLWIDTH POUND SIGN
            case '¬':
                return '￢'; // NOT SIGN → FULLWIDTH NOT SIGN
            case '¯':
                return '￣'; // MACRON → FULLWIDTH MACRON
            case '¦':
                return '￤'; // BROKEN BAR → FULLWIDTH BROKEN BAR
            case '¥':
                return '￥'; // YEN SIGN → FULLWIDTH YEN SIGN
            case '₩':
                return '￦'; // WON SIGN → FULLWIDTH WON SIGN
            default:
                return ch;
        }
    }
}
