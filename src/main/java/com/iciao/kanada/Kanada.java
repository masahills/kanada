package com.iciao.kanada;

import com.iciao.kanada.maps.KanaMapping;

import java.util.Locale;

/**
 * Japanese text transliteration library for converting between Kanji, Hiragana, Katakana, and Romaji.
 *
 * <h3>Simple Usage:</h3>
 * <pre>{@code
 * String romaji = Kanada.toRomaji("日本語");     // "nihongo"
 * String hiragana = Kanada.toHiragana("日本語"); // "にほんご"
 * String katakana = Kanada.toKatakana("日本語");  // "ニホンゴ"
 * }</pre>
 *
 * <h3>Advanced Usage:</h3>
 * <pre>{@code
 * Kanada converter = Kanada.create()
 *     .toRomaji()
 *     .withSpaces()
 *     .upperCaseFirst();
 * String result = converter.process("東京都"); // "Tokyo To"
 * }</pre>
 *
 * @author Masahiko Sato
 */
public class Kanada {

    protected int optionKanji;
    protected int optionHiragana;
    protected int optionKatakana;
    protected int optionWideAscii;
    protected int optionWideSymbol;
    protected int optionHalfKatakana;
    protected int optionAscii;
    protected int optionHalfSymbol;

    protected boolean modeAddSpace = false;
    protected boolean modeUcFirst = false;
    protected boolean modeUcAll = false;
    protected KanaMapping.RomanizationSystem romanizationSystem = KanaMapping.RomanizationSystem.MODIFIED_HEPBURN;
    protected boolean modeShowAllYomi = false;
    protected boolean modeFurigana = false;

    public Kanada() throws java.io.IOException {
        setParam(
                JMapper.AS_IS,
                JMapper.AS_IS,
                JMapper.AS_IS,
                JMapper.AS_IS,
                JMapper.AS_IS,
                JMapper.AS_IS,
                JMapper.AS_IS,
                JMapper.AS_IS);
    }

    // Convenience static methods
    public static Kanada create() {
        try {
            return new Kanada();
        } catch (java.io.IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String toRomaji(String text) {
        try {
            return new Kanada().toRomaji().withSpaces().process(text);
        } catch (java.io.IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String toHiragana(String text) {
        try {
            return new Kanada().toHiragana().process(text);
        } catch (java.io.IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String toKatakana(String text) {
        try {
            return new Kanada().toKatakana().process(text);
        } catch (java.io.IOException e) {
            throw new RuntimeException(e);
        }
    }

    // Builder pattern methods
    public Kanada toRomaji() {
        setParam(
                JMapper.TO_ASCII,
                JMapper.TO_ASCII,
                JMapper.TO_ASCII,
                JMapper.TO_ASCII,
                JMapper.TO_ASCII,
                JMapper.AS_IS,
                JMapper.TO_ASCII,
                JMapper.TO_ASCII);
        return this;
    }

    public Kanada toHiragana() {
        setParam(
                JMapper.TO_HIRAGANA,
                JMapper.AS_IS,
                JMapper.TO_HIRAGANA,
                JMapper.AS_IS,
                JMapper.AS_IS,
                JMapper.TO_HIRAGANA,
                JMapper.AS_IS,
                JMapper.AS_IS);
        return this;
    }

    public Kanada toKatakana() {
        setParam(
                JMapper.TO_KATAKANA,
                JMapper.TO_KATAKANA,
                JMapper.AS_IS,
                JMapper.AS_IS,
                JMapper.AS_IS,
                JMapper.TO_KATAKANA,
                JMapper.AS_IS,
                JMapper.AS_IS);
        return this;
    }

    public Kanada toFullWidthKana() {
        setParam(
                JMapper.AS_IS,
                JMapper.AS_IS,
                JMapper.AS_IS,
                JMapper.AS_IS,
                JMapper.TO_HIRAGANA,
                JMapper.AS_IS,
                JMapper.TO_KATAKANA,
                JMapper.TO_WIDE_SYMBOL);
        return this;
    }

    public Kanada toFullWidthAll() {
        setParam(
                JMapper.AS_IS,
                JMapper.AS_IS,
                JMapper.AS_IS,
                JMapper.AS_IS,
                JMapper.TO_HIRAGANA,
                JMapper.TO_WIDE_ASCII,
                JMapper.TO_KATAKANA,
                JMapper.TO_WIDE_SYMBOL);
        return this;
    }

    public Kanada withSpaces() {
        modeAddSpace = true;
        return this;
    }

    public Kanada upperCaseFirst() {
        modeUcFirst = true;
        modeUcAll = false;
        return this;
    }

    public Kanada upperCaseAll() {
        modeUcAll = true;
        modeUcFirst = false;
        return this;
    }

    public Kanada kunreiRomaji() {
        romanizationSystem = KanaMapping.RomanizationSystem.KUNREI;
        return this;
    }

    public Kanada hepburnRomaji() {
        romanizationSystem = KanaMapping.RomanizationSystem.MODIFIED_HEPBURN;
        return this;
    }

    public Kanada romanizationSystem(KanaMapping.RomanizationSystem system) {
        this.romanizationSystem = system;
        return this;
    }

    private void setParam(int paramKanji,
                          int paramHiragana,
                          int paramKatakana,
                          int paramWideAscii,
                          int paramWideSymbol,
                          int paramAscii,
                          int paramHalfKatakana,
                          int paramHalfSymbol) {
        optionKanji = paramKanji;
        optionHiragana = paramHiragana;
        optionKatakana = paramKatakana;
        optionWideAscii = paramWideAscii;
        optionWideSymbol = paramWideSymbol;
        optionAscii = paramAscii;
        optionHalfKatakana = paramHalfKatakana;
        optionHalfSymbol = paramHalfSymbol;
    }

    public String process(String str) {
        if (str == null) {
            return str;
        }

        String parsedStr;
        try {
            JWriter writer = new JWriter(this);
            KanjiParser parser = new KanjiParser(writer);
            parsedStr = parser.parse(str);
            if (modeUcAll) {
                parsedStr = parsedStr.toUpperCase(Locale.ENGLISH);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return str;
        }

        return parsedStr;
    }
}
