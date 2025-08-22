/*
 * MIT License
 *
 * Copyright (C) 2025 Masahiko Sato
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.iciao.kanada.maps;

import com.iciao.kanada.JMapper;
import com.iciao.kanada.Kanada;

/**
 * Map ASCII characters to fullwidth forms when appropriate.
 *
 * @author Masahiko Sato
 */
public class MapAscii extends JMapper {

    public MapAscii(Kanada kanada) {
        super(kanada);
    }

    @Override
    protected void process(String str, int param) {
        StringBuilder out = new StringBuilder();
        int thisChar = str.codePointAt(0);
        if (param == JMapper.TO_WIDE_ASCII && thisChar > 0x20 && thisChar < 0x7f) {
            out.appendCodePoint(thisChar + 0xfee0);
        } else if (param == JMapper.TO_KANA_BRAILLE) {
            if (thisChar >= '0' && thisChar <= '9') {
                out.append(numbersToBraille(str));
            } else if (thisChar >= 'A' && thisChar <= 'Z' || thisChar >= 'a' && thisChar <= 'z') {
                out.append(alphabetsToBraille(str));
            } else if (thisChar == ' ') {
                out.append('\u2800');
            } else {
                out.appendCodePoint(thisChar);
            }
        } else {
            out.appendCodePoint(thisChar);
        }
        setString(out.toString());
    }

    private String alphabetsToBraille(String str) {
        int count = 0;
        //TODO: 途中に空白が含まれる場合は引用なので、外字符ではなく外国語引用符をつかう
        //TODO: 大文字が連続する場合は二重大文字符をつかう
        //TODO: ハイフンが挟まる場合はつなぎ符をつかう
        //TODO: スラッシュは外字付の効果をキャンセルしない
        //TODO: 情報処理用点字とは？
        StringBuilder alphabets = new StringBuilder("⠦");
        for (char c : str.toCharArray()) {
            if (c >= 'A' && c <= 'Z' || c >= 'a' && c <= 'z' || c == '.' || c == ',' || c == ' ' || c == '/') {
                alphabets.append(BASIC_LATIN_TO_BRAILLE[c - 0x0020]);
                count++;
            } else {
                break;
            }
        }
        matchedLength = count;
        return alphabets.toString();
    }

    private String numbersToBraille(String str) {
        int count = 0;
        StringBuilder numbers = new StringBuilder("⠼");
        for (char c : str.toCharArray()) {
            if (c >= '0' && c <= '9' || c == '.' || c == ',' || c == ' ' || c == '\u2800') {
                //TODO: 次の文字がア行・ラ行以外のかな（つまり数字でなければ）であれば、つなぎ付は不要
                if (c == ' ' || c == '\u2800') {
                    numbers.append('⠤');
                } else {
                    numbers.append(BASIC_LATIN_TO_BRAILLE[c - 0x0020]);
                }
                count++;
            } else {
                break;
            }
        }
        matchedLength = count;
        return numbers.toString();
    }

    /*
    U+002x	SP 	!	"	#	$	%	&	'	(	)	*	+	,	-	.	/
    U+003x	0	1	2	3	4	5	6	7	8	9	:	;	<	=	>	?
    U+004x	@	A	B	C	D	E	F	G	H	I	J	K	L	M	N	O
    U+005x	P	Q	R	S	T	U	V	W	X	Y	Z	[	\	]	^	_
    U+006x	`	a	b	c	d	e	f	g	h	i	j	k	l	m	n	o
    U+007x	p	q	r	s	t	u	v	w	x	y	z	{	|	}	~	DEL
     */
    // TODO: Need to find out how to convert English symbols to Japanese braille.
    private static final String[] BASIC_LATIN_TO_BRAILLE = {
            "⠀", "⠖", "\"", "⠰⠩", "$", "⠰⠏", "⠰⠯", "'", "⠶", "⠶", "⠰⠡", "+", "⠠", "-", "⠲", "⠸⠌",
            "⠚", "⠁", "⠃", "⠉", "⠙", "⠑", "⠋", "⠛", "⠓", "⠊", "⠒", "⡠", "<", "=", ">", "⠢",
            "@", "⠰⠠⠁", "⠠⠃", "⠠⠉", "⠠⠙", "⠠⠑", "⠠⠋", "⠠⠛", "⠠⠓", "⠠⠊", "⠠⠚", "⠠⠅", "⠠⠇", "⠠⠍", "⠠⠝", "⠠⠕",
            "⠠⠏", "⠠⠟", "⠠⠗", "⠠⠎", "⠠⠞", "⠠⠥", "⠠⠧", "⠠⠺", "⠠⠭", "⠠⠽", "⠠⠵", "[", "", "]", "^", "_",
            "`", "⠁", "⠃", "⠉", "⠙", "⠑", "⠋", "⠛", "⠓", "⠊", "⠚", "⠅", "⠇", "⠍", "⠝", "⠕",
            "⠏", "⠟", "⠗", "⠎", "⠞", "⠥", "⠧", "⠺", "⠭", "⠽", "⠵", "{", "|", "}", "~"
    };
}
