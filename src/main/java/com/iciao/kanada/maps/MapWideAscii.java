package com.iciao.kanada.maps;

import com.iciao.kanada.JMapper;
import com.iciao.kanada.Kanada;

/**
 * Map fullwidth ASCII characters to halfwidth forms when appropriate.
 *
 * @author Masahiko Sato
 */
public class MapWideAscii extends JMapper {

    public MapWideAscii(Kanada kanada) {
        super(kanada);
    }

    @Override
    protected void process(String str, int param) {
        StringBuilder out = new StringBuilder();
        int thisChar = str.codePointAt(0);
        if (param == JMapper.TO_ASCII) {
            if (thisChar >= 0xff00 && thisChar <= 0xff5e) {
                out.appendCodePoint(thisChar - 0xfee0);
            } else {
                out.append(fullwidthSymbolToHalf(str.charAt(0)));
            }
        } else {
            out.appendCodePoint(thisChar);
        }
        setString(out.toString());
    }

    private char fullwidthSymbolToHalf(char ch) {
        return switch (ch) {
            case '￠' -> '¢'; // FULLWIDTH CENT SIGN → CENT SIGN
            case '￡' -> '£'; // FULLWIDTH POUND SIGN → POUND SIGN
            case '￢' -> '¬'; // FULLWIDTH NOT SIGN → NOT SIGN
            case '￣' -> '¯'; // FULLWIDTH MACRON → MACRON
            case '￤' -> '¦'; // FULLWIDTH BROKEN BAR → BROKEN BAR
            case '￥' -> '¥'; // FULLWIDTH YEN SIGN → YEN SIGN
            case '￦' -> '₩'; // FULLWIDTH WON SIGN → WON SIGN
            default -> ch;
        };
    }
}
