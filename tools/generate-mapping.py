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
    java_path = os.path.join(os.path.dirname(__file__), '..', 'src', 'main', 'java', 'com', 'iciao', 'kanada', 'maps', 'KanaMappingData.java')
    
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
                
                hiragana_entries.append(f'        hiraganaMap.put("{escape_java_string(hiragana)}", new String[]{{{array_str}}});')
                katakana_entries.append(f'        katakanaMap.put("{escape_java_string(katakana)}", new String[]{{{array_str}}});')
    
    # Generate Java code
    java_code = f'''/**
 * Kanada (Kanji-Kana Transliteration Library for Java)
 * Copyright (C) 2002-2014 Masahiko Sato
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 */
package com.iciao.kanada.maps;

import java.util.HashMap;
import java.util.Map;

/**
 * Kana mapping data including romanization and half-width conversion.
 * Generated from kanatable.tsv - DO NOT EDIT MANUALLY
 */
class KanaMappingData {{
    

    
    private final Map<String, String[]> hiraganaMap = new HashMap<>();
    private final Map<String, String[]> katakanaMap = new HashMap<>();
    private final Map<Character, String> halfWidthMap = new HashMap<>();
    
    KanaMappingData() {{
        initializeMappings();
        initializeHalfWidthMapping();
    }}
    
    private void initializeMappings() {{
        // Generated from kanatable.tsv
{chr(10).join(hiragana_entries)}
        
{chr(10).join(katakana_entries)}
    }}
    
    String[] getRomanizations(String kana) {{
        String[] romanizations = hiraganaMap.get(kana);
        if (romanizations == null) {{
            romanizations = katakanaMap.get(kana);
        }}
        return romanizations;
    }}
    
    String getHalfWidthKana(char c) {{
        return halfWidthMap.get(c);
    }}
    
    private void initializeHalfWidthMapping() {{
halfWidthMap.put('ガ', "ｶﾞ");
        halfWidthMap.put('ギ', "ｷﾞ");
        halfWidthMap.put('グ', "ｸﾞ");
        halfWidthMap.put('ゲ', "ｹﾞ");
        halfWidthMap.put('ゴ', "ｺﾞ");
        halfWidthMap.put('ザ', "ｻﾞ");
        halfWidthMap.put('ジ', "ｼﾞ");
        halfWidthMap.put('ズ', "ｽﾞ");
        halfWidthMap.put('ゼ', "ｾﾞ");
        halfWidthMap.put('ゾ', "ｿﾞ");
        halfWidthMap.put('ダ', "ﾀﾞ");
        halfWidthMap.put('ヂ', "ﾁﾞ");
        halfWidthMap.put('ヅ', "ﾂﾞ");
        halfWidthMap.put('デ', "ﾃﾞ");
        halfWidthMap.put('ド', "ﾄﾞ");
        halfWidthMap.put('バ', "ﾊﾞ");
        halfWidthMap.put('ビ', "ﾋﾞ");
        halfWidthMap.put('ブ', "ﾌﾞ");
        halfWidthMap.put('ベ', "ﾍﾞ");
        halfWidthMap.put('ボ', "ﾎﾞ");
        halfWidthMap.put('パ', "ﾊﾟ");
        halfWidthMap.put('ピ', "ﾋﾟ");
        halfWidthMap.put('プ', "ﾌﾟ");
        halfWidthMap.put('ペ', "ﾍﾟ");
        halfWidthMap.put('ポ', "ﾎﾟ");
        halfWidthMap.put('ヴ', "ｳﾞ");
        halfWidthMap.put('が', "ｶﾞ");
        halfWidthMap.put('ぎ', "ｷﾞ");
        halfWidthMap.put('ぐ', "ｸﾞ");
        halfWidthMap.put('げ', "ｹﾞ");
        halfWidthMap.put('ご', "ｺﾞ");
        halfWidthMap.put('ざ', "ｻﾞ");
        halfWidthMap.put('じ', "ｼﾞ");
        halfWidthMap.put('ず', "ｽﾞ");
        halfWidthMap.put('ぜ', "ｾﾞ");
        halfWidthMap.put('ぞ', "ｿﾞ");
        halfWidthMap.put('だ', "ﾀﾞ");
        halfWidthMap.put('ぢ', "ﾁﾞ");
        halfWidthMap.put('づ', "ﾂﾞ");
        halfWidthMap.put('で', "ﾃﾞ");
        halfWidthMap.put('ど', "ﾄﾞ");
        halfWidthMap.put('ば', "ﾊﾞ");
        halfWidthMap.put('び', "ﾋﾞ");
        halfWidthMap.put('ぶ', "ﾌﾞ");
        halfWidthMap.put('べ', "ﾍﾞ");
        halfWidthMap.put('ぼ', "ﾎﾞ");
        halfWidthMap.put('ぱ', "ﾊﾟ");
        halfWidthMap.put('ぴ', "ﾋﾟ");
        halfWidthMap.put('ぷ', "ﾌﾟ");
        halfWidthMap.put('ぺ', "ﾍﾟ");
        halfWidthMap.put('ぽ', "ﾎﾟ");
        halfWidthMap.put('ゔ', "ｳﾞ");
        halfWidthMap.put('。', "｡");
        halfWidthMap.put('、', "､");
        halfWidthMap.put('「', "｢");
        halfWidthMap.put('」', "｣");
        halfWidthMap.put('・', "･");
        halfWidthMap.put('ー', "ｰ");    }}
}}'''
    
    with open(java_path, 'w', encoding='utf-8') as f:
        f.write(java_code)
    
    print(f"Generated {len(hiragana_entries)} hiragana and {len(katakana_entries)} katakana mappings")

if __name__ == '__main__':
    generate_mapping()