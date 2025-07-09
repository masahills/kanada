package com.iciao.kanada;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class KanadaTest {

    private Kanada romaji;
    private Kanada hiragana;
    private Kanada katakana;

    @Before
    public void setUp() throws Exception {
        romaji = new Kanada().toRomaji();
        hiragana = new Kanada().toHiragana();
        katakana = new Kanada().toKatakana();
    }

    @Test
    public void testBasicHiraganaConversion() throws Exception {
        String result = hiragana.process("漢字");
        assertNotNull(result);
        assertEquals("かんじ", result);
    }

    @Test
    public void testBasicKatakanaConversion() throws Exception {
        String result = katakana.process("漢字");
        assertNotNull(result);
        assertEquals("カンジ", result);
    }

    @Test
    public void testBasicRomajiConversion() throws Exception {
        String result = romaji.process("漢字");
        assertNotNull(result);
        assertEquals("kanzi", result);
    }

    @Test
    public void testNullInput() throws Exception {
        assertNull(romaji.process(null));
    }

    @Test
    public void testEmptyInput() throws Exception {
        assertEquals("", romaji.process(""));
    }
}