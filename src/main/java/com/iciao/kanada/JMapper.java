package com.iciao.kanada;

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
    protected String outStr;

    protected JMapper(String str) {
        outStr = str;
    }

    protected String getString() {
        return outStr;
    }

    protected void setString(String str) {
        outStr = str;
    }

    protected abstract void process(String str, int param);
}
