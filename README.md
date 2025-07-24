# Kanada - Japanese Text Transliteration Library

A pure Java library for converting Japanese text between Kanji, Hiragana, Katakana, and Romaji.

## Features

- **Kanji to Kana conversion** - Convert Kanji to Hiragana or Katakana
- **Romanization** - Convert Japanese text to Latin alphabet
- **Multiple romanization systems** - Hepburn and Kunrei-shiki
- **Flexible formatting** - Space insertion, case conversion
- **Character width conversion** - Half-width ↔ Full-width
- **Pure Java** - No external dependencies or native libraries
- **Lightweight** - No morphological analysis required

## Quick Start

### Gradle

```gradle
dependencies {
    implementation 'com.iciao:kanada:1.0.0'
}
```

### Simple Usage (Recommended)

```java
import com.iciao.kanada.Kanada;

// Static methods for quick conversion
String romaji = Kanada.toRomaji("日本語");     // "nihongo"
        String hiragana = Kanada.toHiragana("日本語"); // "にほんご"
        String katakana = Kanada.toKatakana("日本語");  // "ニホンゴ"
```

### Advanced Usage (Builder Pattern)

```java
import com.iciao.kanada.Kanada;

// Customized conversion with spaces and capitalization
Kanada converter = Kanada.create()
        .toRomaji()
        .withSpaces()
        .upperCaseFirst();

        String result = converter.process("進撃の巨人"); // "Shingeki No Kyojin"

        // Or as a one-liner
        String result = Kanada.create().toRomaji().withSpaces().process("進撃の巨人");
```

## Implementation Details

### Dictionary-based Approach

Kanada uses a dictionary-based approach for all conversions (kana conversion, romanization, and word segmentation)
without relying on morphological analysis. This means:

- **Dictionary-based pattern matching** - Text is processed using dictionary lookups for character patterns
- **Simplified word boundary estimation** - Word boundaries are estimated based on character type transitions

### Limitations

- Without morphological analysis, context-dependent conversions may not be perfect
- Homonyms in kanji cannot be disambiguated without context
- Word segmentation may not match linguistic word boundaries precisely
- Proper nouns may not follow standard conversion rules
- Compound words may be incorrectly segmented
- Conversion results can be improved by editing the kanwadict dictionary file in the `dictionary/japanese/` directory

## Development Setup

For development, first generate the mapping file:

```bash
# Generate KanaMapping.java from TSV
./gradlew generateMapping
```

Alternatively, use the stub file for basic functionality:

```bash
# Development stub is available as KanaMappingStub.java (minimal functionality)
# The actual KanaMapping.java will be generated during build
```

## Building

```bash
# Full build (includes mapping generation)
./gradlew build
```

**Note**: `KanaMapping.java` is generated from `tools/mappings/kanatable.tsv` and should not be edited manually.

## Running Examples

```bash
./gradlew :examples:run
```

## License

This project is distributed under dual licenses:

- **Kanada Library Source Code**: MIT License - See MIT-LICENSE file for details.
- **Dictionary File**: GPL v2 - The kakasidict dictionary file in the `dictionary/japanese/` directory is from the
  KAKASI project and is licensed under GPL v2. See LICENSE file for details.

  **Note**: SKK dictionaries are also supported but are not included in this package. SKK dictionaries are also licensed
  under GPL v2, and you can use your own SKK dictionary files for additional functionality.

This dual licensing allows you to use the Kanada library in both open source and proprietary projects, while respecting
the original license of the dictionary files.