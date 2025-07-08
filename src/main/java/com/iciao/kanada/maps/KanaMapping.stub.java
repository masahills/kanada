/**
 * Development stub for KanaMapping - will be replaced by generated version during build
 * This file allows development without requiring the full generated mapping
 */
package com.iciao.kanada.maps;

import java.util.HashMap;
import java.util.Map;

public class KanaMapping {
    
    public enum RomanizationSystem {
        MODIFIED_HEPBURN(2),
        KUNREI(3),
        GAIMUSHO_HEPBURN(4),
        NIHON(5),
        STATION_HEPBURN(6),
        ROAD_SIGN_HEPBURN(7);
        
        private final int columnIndex;
        
        RomanizationSystem(int columnIndex) {
            this.columnIndex = columnIndex;
        }
        
        public int getColumnIndex() {
            return columnIndex;
        }
    }
    
    private final Map<String, String[]> hiraganaMap = new HashMap<>();
    private final Map<String, String[]> katakanaMap = new HashMap<>();
    
    private static KanaMapping instance;
    
    public static synchronized KanaMapping getInstance() {
        if (instance == null) {
            instance = new KanaMapping();
        }
        return instance;
    }
    
    private KanaMapping() {
        // Minimal stub data for development
        hiraganaMap.put("あ", new String[]{"a", "a", "a", "a", "a", "a"});
        hiraganaMap.put("か", new String[]{"ka", "ka", "ka", "ka", "ka", "ka"});
        katakanaMap.put("ア", new String[]{"a", "a", "a", "a", "a", "a"});
        katakanaMap.put("カ", new String[]{"ka", "ka", "ka", "ka", "ka", "ka"});
    }
    
    public String toRomaji(String kana, RomanizationSystem system) {
        String[] romanizations = hiraganaMap.get(kana);
        if (romanizations == null) {
            romanizations = katakanaMap.get(kana);
        }
        
        if (romanizations != null) {
            String result = romanizations[system.getColumnIndex() - 2];
            return result.isEmpty() ? null : result;
        }
        
        return null;
    }
    
    public String removeMacrons(String text) {
        return text.replace("ā", "aa")
                  .replace("ī", "ii")
                  .replace("ū", "uu")
                  .replace("ē", "ee")
                  .replace("ō", "oo");
    }
}