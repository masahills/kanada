package com.iciao.kanada.maps;

import com.iciao.kanada.JMapper;

/**
 * Convert hiragana input to romaji using TSV-based mapping.
 *
 * @author Masahiko Sato
 */
public class MapHiragana extends JMapper {

    private static final KanaMapping kanaMapping = KanaMapping.getInstance();

    public MapHiragana() {
        this(null);
    }

    protected MapHiragana(String str) {
        super(str);
    }

    @Override
    protected void process(String str, int param) {
        StringBuilder out = new StringBuilder();
        int thisChar = str.codePointAt(0);

        switch (param) {
            case TO_KATAKANA:
                out.appendCodePoint(thisChar + 0x60);
                break;
            case TO_HALF_KATAKANA:
                // TODO: need implementation
                out.append(String.valueOf(Character.toChars(thisChar)));
                break;
            case TO_ASCII:
            case TO_WIDE_ASCII:
                String kana = String.valueOf(Character.toChars(thisChar));
                if (isHiragana(kana)) {
                    String romaji = kanaMapping.toRomaji(kana, KanaMapping.RomanizationSystem.MODIFIED_HEPBURN);
                    if (romaji != null) {
                        out.append(kanaMapping.removeMacrons(romaji));
                    } else {
                        out.append(kana);
                    }
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

    private boolean isHiragana(String str) {
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (!(c >= '\u3041' && c <= '\u3096')) {
                return false;
            }
        }
        return true;
    }

}