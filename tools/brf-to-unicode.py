#!/usr/bin/env python3

import argparse

# ASCII to Unicode braille mapping (ASCII 32-127)
BRAILLE_ASCII_TABLE = [
    '\u2800', '⠮', '⠐', '⠼', '⠫', '⠩', '⠯', '⠄', '⠷', '⠾', '⠡', '⠬', '⠠', '⠤', '⠨', '⠌',
    '⠴', '⠂', '⠆', '⠒', '⠲', '⠢', '⠖', '⠶', '⠦', '⠔', '⠱', '⠰', '⠣', '⠿', '⠜', '⠹',
    '⠈', '⠁', '⠃', '⠉', '⠙', '⠑', '⠋', '⠛', '⠓', '⠊', '⠚', '⠅', '⠇', '⠍', '⠝', '⠕',
    '⠏', '⠟', '⠗', '⠎', '⠞', '⠥', '⠧', '⠺', '⠭', '⠽', '⠵', '⠪', '⠳', '⠻', '⠘', '⠸',
    '⠈', '⠁', '⠃', '⠉', '⠙', '⠑', '⠋', '⠛', '⠓', '⠊', '⠚', '⠅', '⠇', '⠍', '⠝', '⠕',
    '⠏', '⠟', '⠗', '⠎', '⠞', '⠥', '⠧', '⠺', '⠭', '⠽', '⠵', '⠪', '⠳', '⠻', '⠘', '⠸'
]


# BSEファイルのヘッダー構成（独自分析・推測）
# 文字エンコーディング：Shift_JIS/CP932と思われる
# 合計サイズ：512バイト
# 項目詳細：
# ・ファイル保存日　　　　 10バイト（半角10文字：YYYY/MM/DD）
# ・書籍タイトル　　　　　 50バイト（全角25文字）
# ・サブタイトル　　　　　 50バイト（全角25文字）
# ・著者名　　　　　　　　 50バイト（全角25文字）
# ・出版社　　　　　　　　 50バイト（全角25文字）
# ・発行日（全角）　　　　 20バイト（全角10文字：ＹＹＹＹ／ＭＭ／ＤＤ）
# ・図書コード　　　　　　 36バイト（全角18文字）
# ・点訳者名　　　　　　　 36バイト（全角18文字）
# ・校正者名　　　　　　　 36バイト（全角18文字）
# ・作成日（全角）　　　　 20バイト（全角10文字：ＹＹＹＹ／ＭＭ／ＤＤ）
# ・備考欄　　　　　　　　100バイト（全角50文字）
# ・未定義エリア　　　　　 46バイト（全角24文字？）
# ・ページ数　　　　　　　  4バイト（半角数字：4桁固定ゼロパディング）
# ・１行あたり文字数　　　  2バイト（半角数字：2桁固定ゼロパディング）
# ・１ページあたりの行数　  2バイト（半角数字：2桁固定ゼロパディング）
def parse_bse_header(content):
    if len(content) < 512:
        raise ValueError("BSE header too short (<512 bytes)")
    try:
        header = content[0:512]
        fields = {
            "保　 存 　日 ": header[0:10].decode('ascii'),
            "署　　　　名 ": header[10:60].decode('shift_jis'),
            "サブタイトル ": header[60:110].decode('shift_jis'),
            "著　　　　者 ": header[110:160].decode('shift_jis'),
            "出　 版 　社 ": header[160:210].decode('shift_jis'),
            "発　 行 　日 ": header[210:230].decode('shift_jis'),
            "図 書 コード ": header[230:266].decode('shift_jis'),
            "点　　　　訳 ": header[266:302].decode('shift_jis'),
            "校　　　　正 ": header[302:338].decode('shift_jis'),
            "作　 成　 日 ": header[338:358].decode('shift_jis'),
            "備　　　　考 ": header[358:458].decode('shift_jis'),
            "未　 定　 義 ": header[458:504].decode('shift_jis'),
            "総　 頁　 数 ": int(header[504:508].decode('ascii')),
            "１行 の 字数 ": int(header[508:510].decode('ascii')),
            "１頁 の 行数 ": int(header[510:512].decode('ascii'))
        }
        return fields
    except Exception as e:
        raise ValueError(f"Invalid BSE header: {e}")


def brf_to_unicode_braille(content, is_bse=False):
    if is_bse:
        content = content[512:]
    result = []
    for code in content:
        if 32 <= code <= 127:
            result.append(BRAILLE_ASCII_TABLE[code - 32])
        else:
            result.append(chr(code))
    return ''.join(result)


# BESファイルのヘッダー構成（独自分析・推測）
# 文字エンコーディング：iso-8859-1（一部は点字データ）
# 合計サイズ：1024バイト
# 項目詳細：
# ・書式バージョン　  8バイト（半角8文字：'%BET400%'）
# ・ファイル保存日　 10バイト（半角10文字：YYYY/MM/DD）
# ・不明なエリア　　  4バイト（半角空白,半角空白,'E',半角数字）
# ・１行の文字数　　  2バイト（半角数字：2桁）
# ・１ページの行数　  2バイト（半角数字：2桁）
# ・不明なエリア　　  5バイト（半角空白）
# ・ページ総数　　　 14バイト（半角数字：最大4桁左寄せ）
# ・不明なエリア　　  2バイト（半角空白）
# ・原文タイトル　　 50バイト（点字入力）
# ・著者　　　　　　 20バイト（点字入力）
# ・共著訳者等　　　 20バイト（点字入力）
# ・出版社　　　　　 20バイト（点字入力）
# ・出版年　　　　　  3バイト（点字入力）
# ・原本開始ページ　  5バイト（点字入力）
# ・分冊番号　　　　  4バイト（点字入力）
# ・不明なエリア　　  3バイト（半角空白）
# ・点訳者　　　　　 20バイト（点字入力）
# ・備考　　　　　　 50バイト（点字入力）
# ・不明なエリア　　270バイト（半角空白）
# ・不明なエリア　　  4バイト（0xFF,0xFF,0xFF,不明な値）
# ・不明なエリア　　508バイト（半角空白）
def parse_bes_header(content):
    if len(content) < 1024:
        raise ValueError("BES header too short (<1024 bytes)")

    # A helper function to decode BES header field text
    def decode_bes_header_field(text):
        result = []
        for char in text:
            code = ord(char)
            if 32 <= code <= 127:
                result.append(chr(code + 0x2800 - 32))
            else:
                result.append(char)
        return ''.join(result)

    try:
        header = content[0:1024].decode('iso-8859-1')
        fields = {
            "書式バージョン ": header[0:8],
            "ファイル保存日 ": header[8:18],
            "不明なエリア１ ": header[18:22],
            "１行の文字数　 ": int(header[22:24]),
            "ページの行数　 ": int(header[24:26]),
            "不明なエリア２ ": header[26:31],
            "ページ総数　　 ": int(header[31:45]),
            "不明なエリア３ ": header[45:47],
            "原文タイトル　 ": decode_bes_header_field(header[47:97]),
            "著者　　　　　 ": decode_bes_header_field(header[97:117]),
            "共著訳者等　　 ": decode_bes_header_field(header[117:137]),
            "出版社　　　　 ": decode_bes_header_field(header[137:157]),
            "出版年　　　　 ": decode_bes_header_field(header[157:160]),
            "原本開始ページ ": decode_bes_header_field(header[160:165]),
            "分冊番号　　　 ": decode_bes_header_field(header[165:169]),
            "不明なエリア４ ": header[169:172],
            "点訳者　　　　 ": decode_bes_header_field(header[172:192]),
            "備考　　　　　 ": decode_bes_header_field(header[192:242]),
            "不明なエリア５ ": header[242:512],
            "不明なエリア６ ": header[512:1024]
        }
        return fields
    except Exception as e:
        raise ValueError(f"Invalid BES header: {e}")


def bes_to_unicode_braille(content):
    if len(content) < 1024:
        raise ValueError("BES content too short (<1024 bytes)")
    result = []
    i = 1024
    n = len(content)
    while i < n:
        code = content[i]
        next_byte = content[i + 1] if i + 1 < n else None

        if next_byte == 0x01:
            result.append(f'[{code}]')
            i += 2
            continue

        if 160 <= code <= 223:
            result.append(chr(code + 0x2800 - 160))
        elif 224 <= code <= 252:
            result.append(chr(code))
        elif code == 253:
            result.append('\f')  # form feed
        elif code == 254:
            result.append('\n')  # line feed
        elif code == 255:
            result.append('\0')  # null
        else:
            result.append(chr(code))
        i += 1
    return ''.join(result)


def main():
    parser = argparse.ArgumentParser(description='Convert Braille ASCII to Unicode braille')
    parser.add_argument('input_file', help='Input file name')
    parser.add_argument('output_file', nargs='?', help='Output file name (default: <input_file>_unicode.txt)')
    parser.add_argument('--bse', action='store_true', help='Parse BSE header')
    args = parser.parse_args()
    if args.output_file is None:
        args.output_file = args.input_file + '_unicode.txt'

    try:
        ext = args.input_file[-4:].lower()

        with open(args.input_file, 'rb') as f:
            content = f.read()

        header_info = None
        if ext == '.bse' or args.bse:
            header_info = parse_bse_header(content)
            converted_content = brf_to_unicode_braille(content, is_bse=True)
        elif ext == '.bes' or content.startswith(b'%BET'):
            header_info = parse_bes_header(content)
            converted_content = bes_to_unicode_braille(content)
        else:
            converted_content = brf_to_unicode_braille(content)

        if header_info:
            print(f"{args.input_file} Header Info:")
            for key, value in header_info.items():
                print(f"{key}[{value.strip(' ') if isinstance(value, str) else value}]")

        with open(args.output_file, 'w', encoding='utf-8', newline=None) as f:
            f.write(converted_content)

        print(f"-> The output has been saved to: {args.output_file}")

    except FileNotFoundError:
        print(f"Error: {args.input_file} not found.")
    except Exception as e:
        print(f"Error: {e}")


if __name__ == "__main__":
    main()
