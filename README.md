# Kanada - Japanese Text Transliteration Library

A pure Java library for converting Japanese text containing kanji characters into hiragana, katakana, or romaji text, as
well as for performing word segmentation.

## Features

- **Kanji to Kana Conversion**
    - Effortlessly convert kanji characters into hiragana or katakana.

- **Romanization**
    - Transform Japanese text into the romaji equivalent.

- **Multiple Romanization Systems**
    - Supports Hepburn, Kunrei, and other romanization standards.

- **Word Segmentation**
    - Automatically adds spaces between Japanese words for better readability.

- **Furigana Mode**
    - Generate furigana (ruby text) for kanji characters.

- **Character Width Conversion**
    - Seamlessly switch between half-width and full-width characters.

- **Pure Java**
    - Minimal external dependencies, implemented entirely in Java with no native (JNI) code required

- **Lightweight**
    - Designed for speed and efficiency without requiring morphological analysis.

### Experimental Features

- **AI-Assisted Reading Selection**  
  Leverages Generative AI to disambiguate kanji readings in context.
    - Supports integration with OpenAI API and Ollama.
    - Resolves homonym disambiguation challenges effectively.
    - Queries AI selectively for ambiguous kanji readings, ensuring high processing speed.
    - Optimizes token usage by limiting AI consultations to necessary cases.
    - Enhances accuracy while maintaining a lightweight, dictionary-based approach.

## Quick Start

### Gradle

```gradle
dependencies {
    implementation 'com.iciao:kanada:1.0.0'
}
```

### Simple Usage

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

- **Dictionary-based pattern matching**
    - Text is processed using dictionary lookups for character patterns
- **Simplified word boundary estimation**
    - Word boundaries are estimated based on character type transitions

### Limitations

- Without morphological analysis, context-dependent conversions may not be perfect
- Homonyms in kanji cannot be disambiguated without context
- Word segmentation may not match linguistic word boundaries precisely
- Proper nouns may not follow standard conversion rules
- Compound words may be incorrectly segmented
- Conversion results can be improved by editing the dictionary file in the `dictionary/japanese/` directory

## Development Setup

For development, first generate the mapping file:

```bash
# Generate KanaMapping.java from TSV
./gradlew generateMapping
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