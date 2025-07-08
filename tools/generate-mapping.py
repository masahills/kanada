#!/usr/bin/env python3
"""
Generate KanaMapping.java from kanatable.tsv
"""
import csv
import os

def escape_java_string(s):
    """Escape string for Java code"""
    return s.replace('\\', '\\\\').replace('"', '\\"')

def generate_mapping():
    tsv_path = os.path.join(os.path.dirname(__file__), 'mappings', 'kanatable.tsv')
    java_path = os.path.join(os.path.dirname(__file__), '..', 'src', 'main', 'java', 'com', 'iciao', 'kanada', 'maps', 'KanaMapping.java')
    
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
 * TSV-based kana to romanization mapping with support for multiple romanization systems.
 * Generated from kanatable.tsv - DO NOT EDIT MANUALLY
 */
public class KanaMapping {{
    
    public enum RomanizationSystem {{
        MODIFIED_HEPBURN(2),    // 修正ヘボン式
        KUNREI(3),              // 訓令式
        GAIMUSHO_HEPBURN(4),    // 外務省ヘボン式
        NIHON(5),               // 日本式
        STATION_HEPBURN(6),     // 駅名標ヘボン式
        ROAD_SIGN_HEPBURN(7);   // 道路標識ヘボン式
        
        private final int columnIndex;
        
        RomanizationSystem(int columnIndex) {{
            this.columnIndex = columnIndex;
        }}
        
        public int getColumnIndex() {{
            return columnIndex;
        }}
    }}
    
    private final Map<String, String[]> hiraganaMap = new HashMap<>();
    private final Map<String, String[]> katakanaMap = new HashMap<>();
    
    private static KanaMapping instance;
    
    public static synchronized KanaMapping getInstance() {{
        if (instance == null) {{
            instance = new KanaMapping();
        }}
        return instance;
    }}
    
    private KanaMapping() {{
        initializeMappings();
    }}
    
    private void initializeMappings() {{
        // Generated from kanatable.tsv
{chr(10).join(hiragana_entries)}
        
{chr(10).join(katakana_entries)}
    }}
    
    public String toRomaji(String kana, RomanizationSystem system) {{
        String[] romanizations = hiraganaMap.get(kana);
        if (romanizations == null) {{
            romanizations = katakanaMap.get(kana);
        }}
        
        if (romanizations != null) {{
            String result = romanizations[system.getColumnIndex() - 2];
            return result.isEmpty() ? null : result;
        }}
        
        return null;
    }}
    
    public String removeMacrons(String text) {{
        return text.replace("ā", "aa")
                  .replace("ī", "ii")
                  .replace("ū", "uu")
                  .replace("ē", "ee")
                  .replace("ō", "oo");
    }}
}}'''
    
    with open(java_path, 'w', encoding='utf-8') as f:
        f.write(java_code)
    
    print(f"Generated {len(hiragana_entries)} hiragana and {len(katakana_entries)} katakana mappings")

if __name__ == '__main__':
    generate_mapping()