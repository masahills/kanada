package com.iciao.kanada.maps;

/**
 * Kana mapping utilities with romanization and half-width conversion support.
 */
public class KanaMapping {

    public enum RomanizationSystem {
        MODIFIED_HEPBURN(2),    // 修正ヘボン式
        KUNREI(3),              // 訓令式
        GAIMUSHO_HEPBURN(4),    // 外務省ヘボン式
        NIHON(5),               // 日本式
        STATION_HEPBURN(6),     // 駅名標ヘボン式
        ROAD_SIGN_HEPBURN(7);   // 道路標識ヘボン式

        private final int columnIndex;

        RomanizationSystem(int columnIndex) {
            this.columnIndex = columnIndex;
        }

        public int getColumnIndex() {
            return columnIndex;
        }
    }

    private final KanaMappingData mappingData = new KanaMappingData();

    private static KanaMapping instance;

    public static synchronized KanaMapping getInstance() {
        if (instance == null) {
            instance = new KanaMapping();
        }
        return instance;
    }

    private KanaMapping() {
    }

    public KanaTrie.MatchResult toRomaji(String str) {
        return mappingData.getRomanizations(str);
    }

    public String removeMacrons(String text) {
        return text.replace("ā", "aa")
                .replace("ī", "ii")
                .replace("ū", "uu")
                .replace("ē", "ee")
                .replace("ō", "oo");
    }

    public String toHalfWidthKana(String text) {
        if (text == null || text.isEmpty()) return text;

        StringBuilder result = new StringBuilder();
        for (char c : text.toCharArray()) {
            Character.UnicodeBlock block = Character.UnicodeBlock.of(c);
            if (block == Character.UnicodeBlock.KATAKANA) {
                int index = c - 0x30A0;
                if (index < KATAKANA_FULL_TO_HALF_KATAKANA_ARRAY.length) {
                    result.append(KATAKANA_FULL_TO_HALF_KATAKANA_ARRAY[index]);
                } else {
                    result.append(c);
                }
            } else if (block == Character.UnicodeBlock.HIRAGANA) {
                int index = c - 0x3040;
                if (index < HIRAGANA_FULL_TO_HALF_KATAKANA_ARRAY.length) {
                    result.append(HIRAGANA_FULL_TO_HALF_KATAKANA_ARRAY[index]);
                } else {
                    result.append(c);
                }
            } else {
                String special = getHalfWidthKanaSpecial(c);
                result.append(special);
            }
        }
        return result.toString();
    }

    private String getHalfWidthKanaSpecial(char c) {
        return switch (c) {
            case '。' -> "｡";
            case '、' -> "､";
            case '「' -> "｢";
            case '」' -> "｣";
            default -> String.valueOf(c);
        };
    }

    /*
    U+30Ax	゠	ァ	ア	ィ	イ	ゥ	ウ	ェ	エ	ォ	オ	カ	ガ	キ	ギ	ク
    U+30Bx	グ	ケ	ゲ	コ	ゴ	サ	ザ	シ	ジ	ス	ズ	セ	ゼ	ソ	ゾ	タ
    U+30Cx	ダ	チ	ヂ	ッ	ツ	ヅ	テ	デ	ト	ド	ナ	ニ	ヌ	ネ	ノ	ハ
    U+30Dx	バ	パ	ヒ	ビ	ピ	フ	ブ	プ	ヘ	ベ	ペ	ホ	ボ	ポ	マ	ミ
    U+30Ex	ム	メ	モ	ャ	ヤ	ュ	ユ	ョ	ヨ	ラ	リ	ル	レ	ロ	ヮ	ワ
    U+30Fx	ヰ	ヱ	ヲ	ン	ヴ	ヵ	ヶ	ヷ	ヸ	ヹ	ヺ	・	ー	ヽ	ヾ	ヿ
    */
    private final String[] KATAKANA_FULL_TO_HALF_KATAKANA_ARRAY = {
            "⹀", "ｧ", "ｱ", "ｨ", "ｲ", "ｩ", "ｳ", "ｪ", "ｴ", "ｫ", "ｵ", "ｶ", "ｶﾞ", "ｷ", "ｷﾞ", "ｸ",
            "ｸﾞ", "ｹ", "ｹﾞ", "ｺ", "ｺﾞ", "ｻ", "ｻﾞ", "ｼ", "ｼﾞ", "ｽ", "ｽﾞ", "ｾ", "ｾﾞ", "ｿ", "ｿﾞ", "ﾀ",
            "ﾀﾞ", "ﾁ", "ﾁﾞ", "ｯ", "ﾂ", "ﾂﾞ", "ﾃ", "ﾃﾞ", "ﾄ", "ﾄﾞ", "ﾅ", "ﾆ", "ﾇ", "ﾈ", "ﾉ", "ﾊ", "ﾊﾞ",
            "ﾊﾟ", "ﾋ", "ﾋﾞ", "ﾋﾟ", "ﾌ", "ﾌﾞ", "ﾌﾟ", "ﾍ", "ﾍﾞ", "ﾍﾟ", "ﾎ", "ﾎﾞ", "ﾎﾟ", "ﾏ", "ﾐ",
            "ﾑ", "ﾒ", "ﾓ", "ｬ", "ﾔ", "ｭ", "ﾕ", "ｮ", "ﾖ", "ﾗ", "ﾘ", "ﾙ", "ﾚ", "ﾛ", "ヮ", "ﾜ",
            "ｲ", "ｴ", "ｦ", "ﾝ", "ｳﾞ", "ｶ", "ｹ", "ﾜﾞ", "ｲﾞ", "ｴﾞ", "ｦﾞ", "･", "ｰ", "ヽ", "ヽ", "ｺﾄ"
    };

    /*
    U+304x		ぁ	あ	ぃ	い	ぅ	う	ぇ	え	ぉ	お	か	が	き	ぎ	く
    U+305x	ぐ	け	げ	こ	ご	さ	ざ	し	じ	す	ず	せ	ぜ	そ	ぞ	た
    U+306x	だ	ち	ぢ	っ	つ	づ	て	で	と	ど	な	に	ぬ	ね	の	は
    U+307x	ば	ぱ	ひ	び	ぴ	ふ	ぶ	ぷ	へ	べ	ぺ	ほ	ぼ	ぽ	ま	み
    U+308x	む	め	も	ゃ	や	ゅ	ゆ	ょ	よ	ら	り	る	れ	ろ	ゎ	わ
    U+309x	ゐ	ゑ	を	ん	ゔ	ゕ	ゖ			゙	゚	゛	゜	ゝ	ゞ	ゟ
     */
    private final String[] HIRAGANA_FULL_TO_HALF_KATAKANA_ARRAY = {
            "", "ｧ", "ｱ", "ｨ", "ｲ", "ｩ", "ｳ", "ｪ", "ｴ", "ｫ", "ｵ", "ｶ", "ｶﾞ", "ｷ", "ｷﾞ", "ｸ",
            "ｸﾞ", "ｹ", "ｹﾞ", "ｺ", "ｺﾞ", "ｻ", "ｻﾞ", "ｼ", "ｼﾞ", "ｽ", "ｽﾞ", "ｾ", "ｾﾞ", "ｿ", "ｿﾞ", "ﾀ",
            "ﾀﾞ", "ﾁ", "ﾁﾞ", "ｯ", "ﾂ", "ﾂﾞ", "ﾃ", "ﾃﾞ", "ﾄ", "ﾄﾞ", "ﾅ", "ﾆ", "ﾇ", "ﾈ", "ﾉ", "ﾊ",
            "ﾊﾞ", "ﾊﾟ", "ﾋ", "ﾋﾞ", "ﾋﾟ", "ﾌ", "ﾌﾞ", "ﾌﾟ", "ﾍ", "ﾍﾞ", "ﾍﾟ", "ﾎ", "ﾎﾞ", "ﾎﾟ", "ﾏ", "ﾐ",
            "ﾑ", "ﾒ", "ﾓ", "ｬ", "ﾔ", "ｭ", "ﾕ", "ｮ", "ﾖ", "ﾗ", "ﾘ", "ﾙ", "ﾚ", "ﾛ", "ﾜ", "ﾜ",
            "ｲ", "ｴ", "ｦ", "ﾝ", "ｳﾞ", "ｶ", "ｹ", "", "", "ﾞ", "ﾟ", "ﾞ", "ﾟ", "ゝ", "ゞ", "ﾖﾘ"
    };
}
