#!/usr/bin/env python3
"""
Generate KanaMappingData.java from kanatable.tsv

Copyright (C) 2025 Masahiko Sato
Licensed under the MIT License - see MIT-LICENSE file for details.
"""
import csv
import os


def escape_java_string(s):
    """Escape string for Java code"""
    return s.replace('\\', '\\\\').replace('"', '\\"')


def generate_mapping():
    tsv_path = os.path.join(os.path.dirname(__file__), 'mappings', 'kanatable.tsv')
    java_path = os.path.join(os.path.dirname(__file__), '..', 'src', 'main', 'java', 'com', 'iciao', 'kanada', 'maps',
                             'KanaMappingData.java')

    hiragana_entries = []
    katakana_entries = []

    with open(tsv_path, 'r', encoding='utf-8') as f:
        reader = csv.reader(f, delimiter='\t')
        next(reader)  # Skip header

        for row in reader:
            if len(row) >= 9:
                hiragana, katakana = row[0], row[1]
                values = row[2:9]  # 6 romanization and 1 braille systems

                # Create array string
                array_str = ', '.join(f'"{escape_java_string(r)}"' for r in values)

                hiragana_entries.append(
                    f'        hiraganaMap.put("{escape_java_string(hiragana)}", new String[]{{{array_str}}});')
                katakana_entries.append(
                    f'        katakanaMap.put("{escape_java_string(katakana)}", new String[]{{{array_str}}});')

    # Generate Java code
    java_code = f'''/**
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

import java.util.HashMap;
import java.util.Map;

/**
 * Contains kana mapping data for multiple romanization systems.
 * Auto-generated from kanatable.tsv - DO NOT EDIT MANUALLY
 */
class KanaMappingData {{

    private final Map<String, String[]> hiraganaMap = new HashMap<>();
    private final Map<String, String[]> katakanaMap = new HashMap<>();
    private final KanaTrie trie = new KanaTrie();

    KanaMappingData() {{
        initializeMappings();
        initializeTrie();    
    }}
    
    private void initializeTrie() {{
        for (Map.Entry<String, String[]> entry : hiraganaMap.entrySet()) {{
            trie.insert(entry.getKey(), entry.getValue());
        }}
        for (Map.Entry<String, String[]> entry : katakanaMap.entrySet()) {{
            trie.insert(entry.getKey(), entry.getValue());
        }}
    }}

    private void initializeMappings() {{
        // Generated from kanatable.tsv
{chr(10).join(hiragana_entries)}
        
{chr(10).join(katakana_entries)}
    }}
    
    KanaTrie.MatchResult getTransliterations(String str) {{
        return trie.searchLongest(str);
    }}
    
}}'''

    with open(java_path, 'w', encoding='utf-8') as f:
        f.write(java_code)

    print(f"Generated {len(hiragana_entries)} hiragana and {len(katakana_entries)} katakana mappings")


if __name__ == '__main__':
    generate_mapping()
