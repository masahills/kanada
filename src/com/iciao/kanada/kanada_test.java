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
            "[test]\n第二次安倍改造内閣は3日夕方、皇居での認証式を経て正式に発足した。\n" +
                    "安倍晋三首相はこの後、首相官邸で記者会見し、改造内閣を「実行実現内閣」と命名するとともに、\n" +
                    "「引き続き経済最優先でデフレからの脱却を目指し、成長戦略の実行に全力を尽くす」と表明。\n" +
                    "地方創生、安全保障法制の整備についても重点的に取り組む方針を打ち出した。(時事通信)\n\n" +
                    "あ い う え お\nか き く け こ きゃ きゅ きょ\nさ し す せ そ しゃ しゅ しょ\nた ち つ て と ちゃ ちゅ ちょ\nな に ぬ ね の にゃ にゅ にょ\n" +
                    "は ひ ふ へ ほ ひゃ ひゅ ひょ\nま み む め も みゃ みゅ みょ\nや い ゆ え よ\nら り る れ ろ りゃ りゅ りょ\nわ ゐ う ゑ を\nん\n" +
                    "が ぎ ぐ げ ご ぎゃ ぎゅ ぎょ\nざ じ ず ぜ ぞ じゃ じゅ じょ\nだ ぢ づ で ど ぢゃ ぢゅ ぢょ\n" +
                    "ば び ぶ べ ぼ びゃ びゅ びょ\nぱ ぴ ぷ ぺ ぽ ぴゃ ぴゅ ぴょ\nくゎ ぐゎ\n" +
                    "テスト";
    private static final String TEST_TEXT_WIDE_ASCII =
            "Que te pasen bien las navidades, y feliz a\u00f1o nuevo. Saludos a Lito y Nancy, y \u00a1Feliz Navidad! Jenny";
    private static final String TEST_CHINESE =
            "\u008ex\u00a1x\u00a2x\u00a3x\u00a4x\u00a5x\u00a6x\u00a7x\u00a8x\u00adx";

    public static void main(String[] args) throws Exception {
        kanada romanizer = null;

        try {
            romanizer = new kanada(kanada.CONFIG_GET_ROMAJI);
        } catch (IOException e) {
            System.out.println(e);
        }

        romanizer.set_mode(kanada.FLAG_ADD_SPACE | kanada.FLAG_UC_FIRST);


        String str = TEST_TEXT;
        System.out.println("0: " + str);

        for (int i = 1; i <= 1; i++) {
            Calendar lap = Calendar.getInstance();
            String result = romanizer.process(str);
            Calendar now = Calendar.getInstance();
            int lap_time = (int) Math.ceil(now.getTime().getTime() - lap.getTime().getTime());
            System.out.println(i + " (" + lap_time + " ms): " + result);
        }
    }

}
