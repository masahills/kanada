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
