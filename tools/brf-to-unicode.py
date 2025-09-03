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
    header = content[0:512].decode('shift_jis')
    try:
        fields = {
            "Saved date": header[0:10],
            "Book title": header[10:60],
            "Subtitle": header[60:110],
            "Author": header[110:160],
            "Publisher": header[160:210],
            "Publication date": header[210:230],
            "Book code": header[230:266],
            "Braille translation": header[266:302],
            "Proofreading": header[302:338],
            "Creation date": header[338:358],
            "Remarks": header[358:458],
            "Undefined area": header[458:504],
            "Pages": int(header[504:508]),
            "Characters per line": int(header[508:510]),
            "Lines per page": int(header[510:512])
        }
        return fields
    except Exception as e:
        raise ValueError(f"Invalid BSE header: {e}")


def brf_to_unicode_braille(content, is_bse=False):
    if is_bse:
        content = content[512:]
    result = []
    for code in content:
        char = chr(code)
        if 32 <= code <= 127:
            result.append(BRAILLE_ASCII_TABLE[code - 32])
        else:
            result.append(char)
    return ''.join(result)


# BESファイルのヘッダー構成（独自分析・推測）
# 文字エンコーディング：Shift_JIS/CP932と思われる
# 合計サイズ：1024バイト
# 項目詳細：
# ・書式バージョン　　　　 10バイト（半角8文字：'%BET400%'）
# ・ファイル保存日　　　　 10バイト（半角10文字：YYYY/MM/DD）
# ・不明なエリア　　　　 　 4バイト（半角空白,半角空白,'E',半角数字）
# ・１行あたり文字数　　　  2バイト（半角数字：2桁）
# ・１ページあたりの行数　  2バイト（半角数字：2桁）
# ・ページ数　　　　　　　  3バイト（半角数字：左寄せ、最大桁数不明）
# ・不明なエリア　　　　　480バイト（半角空白）
# ・不明なエリア　　　　　  4バイト（0xFF,0xFF,0xFF,不明な値）
# ・不明なエリア　　　　　508バイト（半角空白）
def parse_bes_header(content):
    header = content[0:512].decode('shift_jis')
    header += content[512:516].decode('iso-8859-1')
    header += content[516:1024].decode('shift_jis')
    try:
        fields = {
            "Format Version": header[0:8],
            "Saved sate": header[8:18],
            "Unknown area 1": header[18:22],
            "Characters per line": int(header[22:24]),
            "Lines per page": int(header[24:26]),
            "Unknown area 2": header[26:31],
            "Pages": int(header[31:35]),
            "Unknown area 3": header[35:512],
            "Unknown area 4": header[512:516],
            "Unknown area 5": header[516:1024]
        }
        return fields
    except Exception as e:
        raise ValueError(f"Invalid BES header: {e}")


def bes_to_unicode_braille(content):
    result = []
    for i in range(1024, len(content)):
        code = content[i]
        char = chr(code)
        next_char = content[i + 1] if i < len(content) - 1 else None
        if next_char == '\01':
            result.append('[' + str(code) + ']')
            i += 1
            continue
        elif 160 <= code <= 223:
            result.append(chr(code - 160 + 0x2800))
        elif 224 <= code <= 252:
            result.append(char)
        elif code == 253:
            result.append('\f')  # form feed
        elif code == 254:
            result.append('\n')  # line feed
        elif code == 255:
            result.append('\0')  # null
        else:
            result.append(char)
    return ''.join(result)


def main():
    parser = argparse.ArgumentParser(description='Convert Braille ASCII to Unicode braille')
    parser.add_argument('input_file', help='Input file name')
    parser.add_argument('output_file', nargs='?', help='Output file name (default: <input_file>_unicode.txt)')
    parser.add_argument('--bse', action='store_true', help='Parse BSE header')
    args = parser.parse_args()
    if args.output_file is None:
        args.output_file = args.input_file + '_unicode.txt'

    ext = args.input_file[-4:].lower()

    try:
        with open(args.input_file, 'rb') as f:
            content = f.read()

        header_info = None
        if ext == '.bse' or args.bse:
            header_info = parse_bse_header(content)
            converted_content = brf_to_unicode_braille(content, is_bse=True)
        elif ext == '.bes' or content.startswith('%BET'):
            header_info = parse_bes_header(content)
            converted_content = bes_to_unicode_braille(content)
        else:
            converted_content = brf_to_unicode_braille(content)

        if header_info:
            print(f"{args.input_file} Header Info:")
            for key, value in header_info.items():
                print(f"{key:>20}[{value.strip(' ') if isinstance(value, str) else value}]")

        with open(args.output_file, 'w', encoding='utf-8', newline=None) as f:
            f.write(converted_content)

        print(f"-> The output has been saved to: {args.output_file}")

    except FileNotFoundError:
        print(f"Error: {args.input_file} not found.")
    except Exception as e:
        print(f"Error: {e}")


if __name__ == "__main__":
    main()
