package com.iciao.kanada.maps;

import com.iciao.kanada.JMapper;
import com.iciao.kanada.Kanada;

/**
 * Remap non-kanji characters.<br>
 *
 * @author Masahiko Sato
 */
public class MapHalfSymbol extends JMapper {

    public MapHalfSymbol(Kanada kanada) {
        super(kanada);
    }

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
        return switch (ch) {
            case '¢' -> '￠'; // CENT SIGN → FULLWIDTH CENT SIGN
            case '£' -> '￡'; // POUND SIGN → FULLWIDTH POUND SIGN
            case '¬' -> '￢'; // NOT SIGN → FULLWIDTH NOT SIGN
            case '¯' -> '￣'; // MACRON → FULLWIDTH MACRON
            case '¦' -> '￤'; // BROKEN BAR → FULLWIDTH BROKEN BAR
            case '¥' -> '￥'; // YEN SIGN → FULLWIDTH YEN SIGN
            case '₩' -> '￦'; // WON SIGN → FULLWIDTH WON SIGN
            default -> ch;
        };
    }
}
