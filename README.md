# Kanada - Japanese Text Transliteration Library

A pure Java library for converting Japanese text between Kanji, Hiragana, Katakana, and Romaji.

## Features

- **Kanji to Kana conversion** - Convert Kanji to Hiragana or Katakana
- **Romanization** - Convert Japanese text to Latin alphabet
- **Multiple romanization systems** - Hepburn and Kunrei-shiki
- **Flexible formatting** - Space insertion, case conversion
- **Character width conversion** - Half-width ↔ Full-width
- **Pure Java** - No external dependencies or native libraries

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
    
String result = converter.process("東京都"); // "Tokyo To"

// Or as a one-liner
String result = Kanada.create().toRomaji().withSpaces().process("東京都");
```



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
- **Dictionary Files**: GPL v2 - The dictionary files in the `dictionary/japanese/` directory are derived from KAKASI and are licensed under GPL v2. See LICENSE file for details.

This dual licensing allows you to use the Kanada library in both open source and proprietary projects, while respecting the original license of the dictionary files.