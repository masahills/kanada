package com.iciao.kanada.maps;

import com.iciao.kanada.JMapper;
import com.iciao.kanada.Kanada;

import java.util.Objects;

/**
 * Convert katakana input to romaji using TSV-based mapping.
 *
 * @author Masahiko Sato
 */
public class MapKatakana extends JMapper {

    private static final KanaMapping kanaMapping = KanaMapping.getInstance();

    public MapKatakana(Kanada kanada) {
        super(kanada);
    }

    @Override
    protected void process(String str, int param) {
        StringBuilder out = new StringBuilder();
        int thisChar = str.codePointAt(0);
        String kana = String.valueOf(Character.toChars(thisChar));

        switch (param) {
            case TO_HIRAGANA:
                out.appendCodePoint(thisChar - 0x60);
                break;
            case TO_HALF_KATAKANA:
                String halfKatakana = kanaMapping.toHalfWidthKana(kana);
                out.append(Objects.requireNonNullElse(halfKatakana, kana));
                break;
            case TO_ASCII:
            case TO_WIDE_ASCII:
                KanaTrie.MatchResult result = kanaMapping.toRomaji(str);
                String romaji = result != null ? result.values()[getRomanizationSystem().getColumnIndex() - 2] : null;
                if (romaji != null) {
                    out.append(modeMacron() ? romaji : kanaMapping.removeMacrons(romaji));
                    matchedLength = result.length();
                } else {
                    out.append(kana);
                }
                break;
            default:
                out.appendCodePoint(thisChar);
                break;
        }
        setString(out.toString());
    }
}