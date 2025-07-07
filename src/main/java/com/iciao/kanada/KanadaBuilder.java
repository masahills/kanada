package com.iciao.kanada;

/**
 * Builder pattern for creating Kanada instances with fluent API
 */
public class KanadaBuilder {
    
    public enum OutputFormat {
        ROMAJI, HIRAGANA, KATAKANA, AS_IS
    }
    
    public enum RomanizationSystem {
        HEPBURN, KUNREI
    }
    
    private OutputFormat outputFormat = OutputFormat.AS_IS;
    private boolean addSpaces = false;
    private boolean upperCaseFirst = false;
    private boolean upperCaseAll = false;
    private RomanizationSystem romanizationSystem = RomanizationSystem.HEPBURN;
    
    public static KanadaBuilder create() {
        return new KanadaBuilder();
    }
    
    public KanadaBuilder toRomaji() {
        this.outputFormat = OutputFormat.ROMAJI;
        return this;
    }
    
    public KanadaBuilder toHiragana() {
        this.outputFormat = OutputFormat.HIRAGANA;
        return this;
    }
    
    public KanadaBuilder toKatakana() {
        this.outputFormat = OutputFormat.KATAKANA;
        return this;
    }
    
    public KanadaBuilder withSpaces() {
        this.addSpaces = true;
        return this;
    }
    
    public KanadaBuilder upperCaseFirst() {
        this.upperCaseFirst = true;
        this.upperCaseAll = false;
        return this;
    }
    
    public KanadaBuilder upperCaseAll() {
        this.upperCaseAll = true;
        this.upperCaseFirst = false;
        return this;
    }
    
    public KanadaBuilder kunreiRomaji() {
        this.romanizationSystem = RomanizationSystem.KUNREI;
        return this;
    }
    
    public Kanada build() throws java.io.IOException {
        int config = mapToLegacyConfig();
        Kanada kanada = new Kanada(config);
        
        int mode = 0;
        if (addSpaces) mode |= Kanada.FLAG_ADD_SPACE;
        if (upperCaseFirst) mode |= Kanada.FLAG_UC_FIRST;
        if (upperCaseAll) mode |= Kanada.FLAG_UC_ALL;
        if (romanizationSystem == RomanizationSystem.KUNREI) mode |= Kanada.FLAG_KUNREI_ROMAJI;
        
        if (mode != 0) {
            kanada.setMode(mode);
        }
        
        return kanada;
    }
    
    private int mapToLegacyConfig() {
        switch (outputFormat) {
            case ROMAJI: return Kanada.CONFIG_GET_ROMAJI;
            case HIRAGANA: return Kanada.CONFIG_GET_HIRAGANA;
            case KATAKANA: return Kanada.CONFIG_GET_KATAKANA;
            default: return Kanada.CONFIG_GET_AS_IS;
        }
    }
}