/*
 * MIT License
 *
 * Copyright (C) 2025 Masahiko Sato
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.iciao.kanada;

import com.iciao.kanada.llm.LlmClient;
import com.iciao.kanada.maps.KanaMapping;

import java.io.BufferedReader;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Parse strings and look up the kanji dictionary.<br>
 *
 * @author Masahiko Sato
 */
class KanjiParser {
    private final static Kanwadict kanwa = Kanwadict.getKanwadict();
    private final Kanada kanada;
    private final JWriter jWriter;
    private final LlmClient llmClient;

    @SuppressWarnings("unused")
    protected KanjiParser(JWriter writer) {
        this(writer, null);
    }

    protected KanjiParser(JWriter writer, LlmClient llmClient) {
        kanada = writer.getKanada();
        jWriter = writer;
        this.llmClient = llmClient;
    }

    private static boolean isClosingPunctuation(int c) {
        return c == '、' || c == '。' || c == '」' || c == '）' || c == '！' || c == '？' || c == '・';
    }

    protected void parse(Reader reader, Writer writer) throws Exception {
        if (reader == null || writer == null) {
            throw new IllegalArgumentException("Reader and Writer must not be null");
        }
        try (BufferedReader bufferedReader = new BufferedReader(reader)) {
            StringBuilder buffer = new StringBuilder();
            int position = 0;
            // For LLM disambiguation, the context is searched for 25 characters before and after the position.
            int contextSize = 60;
            int maxPosition = 30;

            // Initial read to fill the buffer
            readForward(bufferedReader, buffer, contextSize);

            while (!buffer.isEmpty() && buffer.length() > position) {
                int matched = processCharacterAt(bufferedReader, writer, buffer, position);
                if (position >= maxPosition) {
                    // Slide the context window by the matched length.
                    readForward(bufferedReader, buffer, matched);
                    buffer.delete(0, matched);
                } else {
                    position += matched;
                }
            }
            // Flush the remaining characters in the buffer.
            jWriter.flushBuffer(writer);
        }
    }

    private void readForward(BufferedReader reader, StringBuilder buffer, int length) throws Exception {
        for (int i = 0; i < length; i++) {
            int ch = reader.read();
            if (ch == -1) break;
            buffer.append((char) ch);
        }
    }

    private int processCharacterAt(BufferedReader reader, Writer writer, StringBuilder inputString, int i) throws Exception {
        int thisChar = inputString.codePointAt(i);
        Character.UnicodeBlock currentBlock = Character.UnicodeBlock.of(thisChar);

        if (i > 0 && kanada.modeAddSpace) {
            int prevChar = inputString.codePointAt(i - 1);
            Character.UnicodeBlock prevBlock = Character.UnicodeBlock.of(prevChar);
            if (prevBlock != currentBlock) {
                // Insert a space at the word boundary when necessary.
                boolean isBoundaryAtTransition = true;
                if (isClosingPunctuation(thisChar) || prevChar == '「' || prevChar == '（' || prevChar == '・') {
                    isBoundaryAtTransition = false;
                } else if (Character.isWhitespace(thisChar) || Character.isWhitespace(prevChar)) {
                    isBoundaryAtTransition = false;
                } else if (thisChar == 'ー' && prevBlock == Character.UnicodeBlock.HIRAGANA) {
                    isBoundaryAtTransition = false;
                } else if (prevChar == 'ー') {
                    int prevPrevChar = i > 1 ? inputString.codePointAt(i - 2) : 0;
                    Character.UnicodeBlock prevPrevBlock = Character.UnicodeBlock.of(prevPrevChar);
                    if (currentBlock == prevPrevBlock || prevPrevChar == 'ー') {
                        isBoundaryAtTransition = false;
                    }
                }
                if (isBoundaryAtTransition) {
                    appendSeparator();
                }
            }
        }

        // The dictionary is indexed by characters from the CJK Unified Ideographs block.
        if (currentBlock != Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS) {
            jWriter.append(thisChar);
            return 1;
        }

        Kanwadict.KanwaKey key = kanwa.getKey(thisChar);
        List<Kanwadict.YomiKanjiData> valueList = new ArrayList<>();

        if (kanwa.searchKey(key)) {
            valueList = kanwa.getValue(key);
        }

        if (valueList.isEmpty()) {
            jWriter.append(thisChar);
            return 1;
        }

        // Flush non-dictionary characters before looking up the dictionary.
        jWriter.flushBuffer(writer);

        int matchedLen = 0;
        String yomi;
        String kanji = "";
        int tail = ' ';
        String yomiWithTail = "";
        String yomiWithoutTail = "";
        List<Kanwadict.YomiKanjiData> candidates = new ArrayList<>();

        for (Kanwadict.YomiKanjiData term : valueList) {
            int searchLen = term.getLength();
            // Skip if a longer word is already found.
            if (searchLen < matchedLen) {
                continue;
            }
            // Add more characters to the input buffer if needed
            int readMore = i + searchLen - inputString.length();
            if (readMore > 0) {
                readForward(reader, inputString, readMore);
            }
            // Check again if enough characters are available for matching
            if ((i + searchLen) > inputString.length()) {
                continue;
            }

            String searchWord = inputString.substring(i, i + searchLen);

            int searchTail = ' ';
            if (term.tail() != ' ' && i + searchWord.length() < inputString.length()) {
                char nextChar = inputString.charAt(i + searchWord.length());
                if (Character.UnicodeBlock.of(nextChar) == Character.UnicodeBlock.HIRAGANA) {
                    // The tail letters from the SKK dictionary are assumed to be based on the Hepburn system.
                    searchTail = KanaMapping.getInstance().getRomajiInitial(nextChar, KanaMapping.ConversionSystem.MODIFIED_HEPBURN);
                }
            }

            if (searchWord.equals(term.kanji())) {
                kanji = term.kanji();
                matchedLen = searchLen;
                if (term.tail() == ' ') {
                    if (yomiWithoutTail.isEmpty()) {
                        yomiWithoutTail = term.yomi();
                    }
                } else if (term.tail() == searchTail) {
                    if (yomiWithTail.isEmpty()) {
                        yomiWithTail = term.yomi();
                        tail = term.tail();
                    }
                }
                if (kanada.modeShowAllYomi || llmClient != null) {
                    // Collect all YomiKanjiData for this word chunk
                    if (candidates.isEmpty() || candidates.get(0).getLength() == searchLen) {
                        if (candidates.isEmpty() || candidates.get(0).getLength() < searchLen) {
                            candidates.clear();
                        }
                        if (term.tail() == searchTail) {
                            candidates.add(term);
                        }
                    }
                } else if (!yomiWithTail.isEmpty()) {
                    // Otherwise, finish the search if yomi with tail is found
                    break;
                }
            }
        }

        if (!yomiWithTail.isEmpty()) {
            yomi = yomiWithTail;
            jWriter.tail = tail;
        } else {
            yomi = yomiWithoutTail;
            jWriter.tail = ' ';
        }

        // Use LLM for disambiguation if multiple candidates exist
        if (tail == ' ' && llmClient != null && candidates.size() > 1) {
            Kanwadict.YomiKanjiData selectedTerm = askGenerativeAI(candidates, inputString.toString(), i);
            yomi = selectedTerm.yomi();
            jWriter.tail = selectedTerm.tail();
        }

        if (matchedLen > 0 && !yomi.isEmpty()) {
            if (kanada.optionKanji == JMapper.AS_IS) {
                jWriter.append(kanji);
            } else {
                jWriter.append(yomi);
            }

            // These modes should be used with the Kanji option but leaving the choice up to the user.
            if (kanada.modeFurigana) {
                jWriter.append("[").append(yomi).append("]");
            } else if (kanada.modeShowAllYomi && !candidates.isEmpty()) {
                List<String> possibleReadings = candidates.stream()
                        .map(Kanwadict.YomiKanjiData::yomi)
                        .distinct()
                        .toList();
                jWriter.append("{").append(String.join("|", possibleReadings)).append("}");
            }

            if (kanada.modeAddSpace && jWriter.tail == ' ') {
                int nextIndex = i + matchedLen;
                if (nextIndex < inputString.length()) {
                    int nextChar = inputString.codePointAt(nextIndex);
                    if (!isClosingPunctuation(nextChar)) {
                        appendSeparator();
                    }
                }
            }
            jWriter.flushBuffer(writer);
            return matchedLen;
        }

        jWriter.append(thisChar);
        return 1;
    }

    private Kanwadict.YomiKanjiData askGenerativeAI(List<Kanwadict.YomiKanjiData> candidates, String sentence, int position) {
        if (candidates.size() == 1) {
            return candidates.get(0);
        }

        if (llmClient != null) {
            try {
                String targetKanji = candidates.get(0).kanji();
                List<String> possibleReadings = candidates.stream()
                        .map(Kanwadict.YomiKanjiData::yomi)
                        .distinct()
                        .collect(Collectors.toList());

                String context = extractContext(sentence, targetKanji, position);

                String bestReading = llmClient.selectBestReading(targetKanji, possibleReadings, context);
                for (Kanwadict.YomiKanjiData candidate : candidates) {
                    if (candidate.yomi().equals(bestReading)) {
                        return candidate;
                    }
                }
            } catch (Exception e) {
                // Fall back to default selection on error
            }
        }

        // Default: return the first candidate
        return candidates.get(0);
    }

    private String extractContext(String sentence, String targetKanji, int position) {
        sentence = sentence.replace("\r", "").replace("\n", "");

        // Find positions 25 characters before and after
        int contextStart = Math.max(0, position - 25);
        int contextEnd = Math.min(sentence.length(), position + targetKanji.length() + 25);

        // Look for a preceding punctuation mark
        for (int i = contextStart; i < position; i++) {
            char c = sentence.charAt(i);
            if (c == '。' || c == '、') {
                contextStart = i + 1;
            }
        }

        // Look for the next punctuation mark
        for (int i = position + targetKanji.length(); i < contextEnd; i++) {
            char c = sentence.charAt(i);
            if (c == '。' || c == '、') {
                contextEnd = i;
                break;
            }
        }

        return sentence.substring(contextStart, contextEnd);
    }

    private void appendSeparator() {
        if (!jWriter.buffer.isEmpty()
                && jWriter.buffer.charAt(jWriter.buffer.length() - 1) != '\n'
                && jWriter.buffer.charAt(jWriter.buffer.length() - 1) != '\r') {
            jWriter.append(kanada.settingSeparatorChar);
        }
    }
}
