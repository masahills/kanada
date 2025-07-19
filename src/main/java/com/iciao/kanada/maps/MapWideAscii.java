/**
 * MIT License
 *
 * Copyright (C) 2025 Masahiko Sato
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
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
