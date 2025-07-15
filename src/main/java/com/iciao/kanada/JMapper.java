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

    protected Kanada kanada;
    protected String outStr;
    protected int matchedLength = 1;

    public JMapper(Kanada kanada) {
        this.kanada = kanada;
    }

    protected KanaMapping.RomanizationSystem getRomanizationSystem() {
        return kanada.romanizationSystem;
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
