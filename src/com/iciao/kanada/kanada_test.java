/*
 * Created by IntelliJ IDEA.
 * User: masahikos
 * Date: Nov 23, 2001
 * Time: 9:29:02 PM
 * To change template for new class use
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package com.iciao.kanada;

import java.io.IOException;
import java.util.Calendar;

public class kanada_test {

    private static final String TEST_TEXT =
            "第二次安倍改造内閣は3日夕方、皇居での認証式を経て正式に発足した。\n" +
                    "安倍晋三首相はこの後、首相官邸で記者会見し、改造内閣を「実行実現内閣」と命名するとともに、\n" +
                    "「引き続き経済最優先でデフレからの脱却を目指し、成長戦略の実行に全力を尽くす」と表明。\n" +
                    "地方創生、安全保障法制の整備についても重点的に取り組む方針を打ち出した。(時事通信) 完\n\n";

    private static final String TEST_ROMAJI =
            "あ い う え お\nか き く け こ\nさ し す せ そ\nた ち つ て と\nな に ぬ ね の\n" +
                    "は ひ ふ へ ほ\nま み む め も\nや い ゆ いぇ よ\nら り る れ ろ\nわ ゐ う ゑ を\n" +
                    "が ぎ ぐ げ ご\nざ じ ず ぜ ぞ\nだ ぢ づ で ど\nば び ぶ べ ぼ\nぱ ぴ ぷ ぺ ぽ\nう゛ぁ う゛ぃ う゛ う゛ぇ う゛ぉ\nん\n" +
                    "きゃ きぃ きゅ きぇ きょ\nしゃ しぃ しゅ しぇ しょ\nちゃ ちぃ ちゅ ちぇ ちょ\nにゃ にぃ にゅ にぇ にょ\n" +
                    "ひゃ ひぃ ひゅ ひぇ ひょ\nみゃ みぃ みゅ みぇ みょ\nりゃ りぃ りゅ りぇ りょ\n" +
                    "ぎゃ ぎぃ ぎゅ ぎぇ ぎょ\nじゃ じぃ じゅ じぇ じょ\nぢゃ ぢぃ ぢゅ ぢぇ ぢょ\n" +
                    "びゃ びぃ びゅ びぇ びょ\nぴゃ ぴぃ ぴゅ ぴぇ ぴょ\nう゛ゃ う゛ぃ う゛ゅ う゛ぇ う゛ょ\n" +
                    "くぁ くぃ くぅ くぇ くぉ\nつぁ つぃ つぅ つぇ つぉ\nふぁ ふぃ ふぅ ふぇ ふぉ\n" +
                    "ぐぁ ぐぃ ぐぅ ぐぇ ぐぉ\nてゅ ふゅ でゅ\n\n" +
                    "ア イ ウ エ オ\nカ キ ク ケ コ\nサ シ ス セ ソ\nタ チ ツ テ ト\nナ ニ ヌ ネ ノ\n" +
                    "ハ ヒ フ ヘ ホ\nマ ミ ム メ モ\nヤ イ ユ イェ ヨ\nラ リ ル レ ロ\nワ ヰ ウ ヱ ヲ\n" +
                    "ガ ギ グ ゲ ゴ\nザ ジ ズ ゼ ゾ\nダ ヂ ヅ デ ド\nバ ビ ブ ベ ボ\nパ ピ プ ペ ポ\nヴァ ヴィ ヴ ヴェ ヴォ\nン\n" +
                    "キャ キィ キュ キェ キョ\nシャ シィ シュ シェ ショ\nチャ チィ チュ チェ チョ\nニャ ニィ ニュ ニェ ニョ\n" +
                    "ヒャ ヒィ ヒュ ヒェ ヒョ\nミャ ミィ ミュ ミェ ミョ\nリャ リィ リュ リェ リョ\n" +
                    "ギャ ギィ ギュ ギェ ギョ\nジャ ジィ ジュ ジェ ジョ\nヂャ ヂィ ヂュ ヂェ ヂョ\n" +
                    "ビャ ビィ ビュ ビェ ビョ\nピャ ピィ ピュ ピェ ピョ\nヴャ ヴィ ヴュ ヴェ ヴョ\n" +
                    "クァ クィ クゥ クェ クォ\nツァ ツィ ツゥ ツェ ツォ\nファ フィ フゥ フェ フォ\n" +
                    "グァ グィ グゥ グェ グォ\nテュ フュ デュ\n" +
                    "ソニー かあさん 東京放送 きいろ おもう ＡＢＣ";

    private static final String TEST_TEXT_TO_WIDE =
            "“Organize the world’s information and make it universally accessible and useful.”\n" +
                    "Since the beginning, our goal has been to develop services that significantly improve the lives of as many people as possible.\n" +
                    "\n" +
                    "Not just for some. For everyone.";

    private static final String TEST_CHINESE =
            "\u008ex\u00a1x\u00a2x\u00a3x\u00a4x\u00a5x\u00a6x\u00a7x\u00a8x\u00adx";

    public static void main(String[] args) throws Exception {

        kanada roomaji = null;
        kanada wakatigaki = null;
        kanada hiragana = null;
        kanada katakana = null;
        kanada fullwidth = null;

        try {
            roomaji = new kanada(kanada.CONFIG_GET_ROMAJI);
            wakatigaki = new kanada(kanada.CONFIG_GET_AS_IS);
            hiragana = new kanada(kanada.CONFIG_GET_HIRAGANA);
            katakana = new kanada(kanada.CONFIG_GET_KATAKANA);
            fullwidth = new kanada(kanada.CONFIG_HALF_TO_WIDE_ALL);
        } catch (IOException e) {
            System.out.println(e);
        }

        roomaji.set_mode(kanada.FLAG_UC_FIRST);

        System.out.println(TEST_TEXT);

        process(wakatigaki, TEST_TEXT);
        process(hiragana, TEST_TEXT);
        process(katakana, TEST_TEXT);
        process(roomaji, TEST_TEXT);
        process(roomaji, TEST_ROMAJI);
        process(fullwidth, TEST_TEXT_TO_WIDE);
    }

    private static void process(kanada obj, String str) {
        Calendar t0 = Calendar.getInstance();
        String result = obj.process(str, true);
        Calendar t1 = Calendar.getInstance();
        int lap_time = (int) Math.ceil(t1.getTime().getTime() - t0.getTime().getTime());
        System.out.println("" + lap_time + " ms: " + "'" + result + "'");
    }

}
