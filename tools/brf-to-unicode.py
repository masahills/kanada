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


def parse_bse_header(header):
    try:
        fields = [
            ("saved_date", 0, 10),
            ("book_title", 10, 60),
            ("subtitle", 60, 110),
            ("author", 110, 160),
            ("publisher", 160, 210),
            ("publication_date", 210, 230),
            ("book_code", 230, 266),
            ("braille_translation", 266, 302),
            ("proofreading", 302, 338),
            ("creation_date", 338, 358),
            ("remarks", 358, 458),
            ("undefined_area", 458, 504),
        ]
        result = {
            name: header[start:end].decode('shift_jis', errors='ignore').strip(' ')
            for name, start, end in fields
        }
        result["num_pages"] = int(header[504:508].decode('shift_jis', errors='ignore'))
        result["chars_per_line"] = int(header[508:510].decode('shift_jis', errors='ignore'))
        result["lines_per_page"] = int(header[510:512].decode('shift_jis', errors='ignore'))
        return result
    except Exception as e:
        raise ValueError(f"Invalid BSE header: {e}")


def to_unicode_braille(text):
    result = []
    for char in text:
        code = ord(char)
        if 32 <= code <= 127:
            result.append(BRAILLE_ASCII_TABLE[code - 32])
        else:
            result.append(char)
    return ''.join(result)


def main():
    parser = argparse.ArgumentParser(description='Convert Braille ASCII to Unicode braille')
    parser.add_argument('input_file', help='Input file name')
    parser.add_argument('output_file', help='Output file name')
    parser.add_argument('--bse', action='store_true', help='Parse BSE header')
    args = parser.parse_args()

    try:
        if args.bse:
            with open(args.input_file, 'rb') as f:
                header = f.readline()
                header_info = parse_bse_header(header)
                print(f"BSE Header Info:")
                labels = {
                    "saved_date": "Saved date",
                    "book_title": "Book title",
                    "subtitle": "Subtitle",
                    "author": "Author",
                    "publisher": "Publisher",
                    "publication_date": "Publication date",
                    "book_code": "Book code",
                    "braille_translation": "Braille Translation",
                    "proofreading": "Proofreading",
                    "creation_date": "Creation date",
                    "remarks": "Remarks",
                    "undefined_area": "Undefined area",
                    "num_pages": "Pages",
                    "chars_per_line": "Characters per line",
                    "lines_per_page": "Lines per page"
                }
                for key, value in header_info.items():
                    print(f"{labels[key]:>20}[{value}]")
                f.seek(len(header))
                content = f.read().decode('ascii', errors='ignore')
        else:
            with open(args.input_file, 'r', encoding='utf-8') as f:
                content = f.read()

        converted_content = to_unicode_braille(content)

        with open(args.output_file, 'w', encoding='utf-8', newline='') as f:
            f.write(converted_content)

        print(f"-> The output has been saved to: {args.output_file}")

    except FileNotFoundError:
        print(f"Error: {args.input_file} not found.")
    except Exception as e:
        print(f"Error: {e}")


if __name__ == "__main__":
    main()
