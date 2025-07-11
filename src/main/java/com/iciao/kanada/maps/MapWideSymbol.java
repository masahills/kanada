package com.iciao.kanada.maps;

import com.iciao.kanada.JMapper;

/**
 * Remap non-kanji characters.<br>
 *
 * @author Masahiko Sato
 */
/*
U+300x	　	、	。	〃	〄	々	〆	〇	〈	〉	《	》	「	」	『	』
U+301x	【	】	〒	〓	〔	〕	〖	〗	〘	〙	〚	〛	〜	〝	〞	〟
U+302x	〠	〡	〢	〣	〤	〥	〦	〧	〨	〩	〪	〫	〬	〭	〮	〯
U+303x	〰	〱	〲	〳	〴	〵	〶	〷	〸	〹	〺	〻	〼	〽	 〾 	〿
 */
public class MapWideSymbol extends JMapper {
    private static final String[] CJK_SYMBOLS_AND_PUNCTUATION_TO_ASCII = {
            " ", ",", ".", "(repeat)", "(jis)", "(repeat)", "(closing mark)", "(circle)",
            "<", ">", "<<", ">>", "'", "'", "\"", "\"",

            "[", "]", "(postal mark)", "=", "[", "]", "{", "}",
            "[", "]", "[[", "]]", "~", "\"", "\"", "\"",

            "(postal mark face)", "1", "2", "3", "4", "5", "6", "7",
            "8", "9", "", "", "", "", "", "",

            "~", "(repeat)", "(repeat)", "(repeat)", "(repeat)", "(repeat)", "(postal mark circle)", "XX",
            "10", "20", "30", "(repeat)", "(masu mark)", "♪", " ", " "
    };

    @Override
    protected void process(String str, int param) {
        StringBuilder out = new StringBuilder();
        int thisChar = str.codePointAt(0);
        Character.UnicodeBlock block = Character.UnicodeBlock.of(thisChar);
        if (block == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION) {
            out.append(CJK_SYMBOLS_AND_PUNCTUATION_TO_ASCII[thisChar - 0x3000]);
        } else {
            out.appendCodePoint(thisChar);
        }
        setString(out.toString());
    }
}
