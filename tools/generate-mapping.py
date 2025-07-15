#!/usr/bin/env python3
"""
Generate KanaMappingData.java from kanatable.tsv
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
            if len(row) >= 8:
                hiragana, katakana = row[0], row[1]
                romanizations = row[2:8]  # 6 romanization systems

                # Create array string
                array_str = ', '.join(f'"{escape_java_string(r)}"' for r in romanizations)

                hiragana_entries.append(
                    f'        hiraganaMap.put("{escape_java_string(hiragana)}", new String[]{{{array_str}}});')
                katakana_entries.append(
                    f'        katakanaMap.put("{escape_java_string(katakana)}", new String[]{{{array_str}}});')

    # Generate Java code
    java_code = f'''package com.iciao.kanada.maps;

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
    
    KanaTrie.MatchResult getRomanizations(String str) {{
        return trie.searchLongest(str);
    }}
    
}}'''

    with open(java_path, 'w', encoding='utf-8') as f:
        f.write(java_code)

    print(f"Generated {len(hiragana_entries)} hiragana and {len(katakana_entries)} katakana mappings")


if __name__ == '__main__':
    generate_mapping()
