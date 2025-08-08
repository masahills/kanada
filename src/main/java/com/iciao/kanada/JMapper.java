/*
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
package com.iciao.kanada;

import com.iciao.kanada.maps.KanaMapping;

/**
 * Remap non-kanji characters.<br>
 *
 * @author Masahiko Sato
 */
public abstract class JMapper {
    public static final int AS_IS = -1;
    public static final int TO_HIRAGANA = 0;
    public static final int TO_KATAKANA = 1;
    public static final int TO_WIDE_ASCII = 2;
    public static final int TO_WIDE_SYMBOL = 3;
    public static final int TO_HALF_KATAKANA = 4;
    public static final int TO_ASCII = 5;
    public static final int TO_HALF_SYMBOL = 6;
    public static final int TO_KANA_BRAILLE = 7;

    protected Kanada kanada;
    protected String outStr;
    protected int matchedLength = 1;

    public JMapper(Kanada kanada) {
        this.kanada = kanada;
    }

    protected KanaMapping.ConversionSystem getConversionSystem() {
        return kanada.conversionSystem;
    }

    protected boolean modeMacron() {
        return kanada.modeMacron;
    }

    protected String getString() {
        return outStr;
    }

    protected int getProcessedLength() {
        return matchedLength;
    }

    protected void setString(String str) {
        outStr = str;
    }

    protected abstract void process(String str, int param);
}
