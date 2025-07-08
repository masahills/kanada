package com.iciao.kanada;

/**
 * Simple output format enumeration for cleaner API
 */
public enum OutputFormat {
    ROMAJI,
    HIRAGANA, 
    KATAKANA,
    AS_IS;
    
    /**
     * Convert to legacy config constant
     */
    public int toConfig() {
        switch (this) {
            case ROMAJI: return Kanada.CONFIG_GET_ROMAJI;
            case HIRAGANA: return Kanada.CONFIG_GET_HIRAGANA;
            case KATAKANA: return Kanada.CONFIG_GET_KATAKANA;
            default: return Kanada.CONFIG_GET_AS_IS;
        }
    }
}