/**
 * Kanada (Kanji-Kana Transliteration Library for Java)
 * Copyright (C) 2002-2014 Masahiko Sato
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 */
package com.iciao.kanada.maps;

/**
 * Kana mapping utilities with romanization and half-width conversion support.
 */
public class KanaMapping {

    public enum RomanizationSystem {
        MODIFIED_HEPBURN(2),    // 修正ヘボン式
        KUNREI(3),              // 訓令式
        GAIMUSHO_HEPBURN(4),    // 外務省ヘボン式
        NIHON(5),               // 日本式
        STATION_HEPBURN(6),     // 駅名標ヘボン式
        ROAD_SIGN_HEPBURN(7);   // 道路標識ヘボン式

        private final int columnIndex;

        RomanizationSystem(int columnIndex) {
            this.columnIndex = columnIndex;
        }

        public int getColumnIndex() {
            return columnIndex;
        }
    }

    private final KanaMappingData mappingData = new KanaMappingData();

    private static KanaMapping instance;

    public static synchronized KanaMapping getInstance() {
        if (instance == null) {
            instance = new KanaMapping();
        }
        return instance;
    }

    private KanaMapping() {
    }

    public String toRomaji(String kana, RomanizationSystem system) {
        String[] romanizations = mappingData.getRomanizations(kana);
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

    public String toHalfWidthKana(String text) {
        if (text == null || text.isEmpty()) return text;

        StringBuilder result = new StringBuilder();
        for (char c : text.toCharArray()) {
            String special = mappingData.getHalfWidthKana(c);
            if (special != null) {
                result.append(special);
            } else if (c >= 0x30A1 && c <= 0x30F6) {
                result.append((char) (c - 0x60));
            } else if (c >= 0x3041 && c <= 0x3096) {
                result.append((char) (c + 0x60));
            } else {
                result.append(c);
            }
        }
        return result.toString();
    }
}