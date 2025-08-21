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

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Parse strings and look up the kanji dictionary.<br>
 *
 * @author Masahiko Sato
 */
public class KanjiParser {
    private final static Kanwadict kanwa = Kanwadict.getKanwadict();
    private final Kanada kanada;
    private final JWriter jWriter;
    private final StringBuilder outputBuffer;
    private final LlmClient llmClient;

    @SuppressWarnings("unused")
    public KanjiParser(JWriter writer) {
        this(writer, null);
    }

    public KanjiParser(JWriter writer, LlmClient llmClient) {
        kanada = writer.getKanada();
        jWriter = writer;
        outputBuffer = new StringBuilder();
        this.llmClient = llmClient;
    }

    public String parse(String inputString) throws Exception {
        int strLen = inputString.length();
        outputBuffer.ensureCapacity(strLen);

        for (int i = 0; i < strLen; i++) {
            int thisChar = inputString.codePointAt(i);

            if (i > 0 && kanada.modeAddSpace) {
                int prevChar = inputString.codePointAt(i - 1);
                Character.UnicodeBlock prevBlock = Character.UnicodeBlock.of(prevChar);
                Character.UnicodeBlock currentBlock = Character.UnicodeBlock.of(thisChar);
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

            if (!Pattern.matches("[\\p{IsHiragana}\\p{IsKatakana}\\p{IsHan}]",
                    String.valueOf(Character.toChars(thisChar)))) {
                jWriter.append(thisChar);
                continue;
            }

            Kanwadict.KanwaKey key = kanwa.getKey(thisChar);
            List<Kanwadict.YomiKanjiData> valueList = new ArrayList<>();

            if (kanwa.searchKey(key)) {
                valueList = kanwa.getValue(key);
            }

            if (valueList.isEmpty()) {
                jWriter.append(thisChar);
                continue;
            }

            // Flush non-dictionary characters before looking up the dictionary.
            flushBuffer();

            int matchedLen = 0;
            String yomi;
            String kanji = "";
            int tail = ' ';
            String yomiWithTail = "";
            String yomiWithoutTail = "";
            List<Kanwadict.YomiKanjiData> candidates = new ArrayList<>();

            for (Kanwadict.YomiKanjiData term : valueList) {
                int searchLen = term.getLength();
                if ((i + searchLen) > inputString.length() || searchLen < matchedLen) {
                    continue;
                }

                String searchWord = inputString.substring(i, i + searchLen);

                int searchTail = 0;
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
                            if (term.tail() == ' ') {
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
                Kanwadict.YomiKanjiData selectedTerm = askGenerativeAI(candidates, inputString, i);
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
                    appendSeparator();
                }
                flushBuffer();
                i = i + matchedLen - 1;
            } else {
                outputBuffer.appendCodePoint(thisChar);
            }
        }

        // Flush the remaining characters in the buffer.
        flushBuffer();

        return outputBuffer.toString();
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

    private void flushBuffer() {
        if (!jWriter.buffer.isEmpty()) {
            StringBuilder str = jWriter.map();
            outputBuffer.append(str);
            jWriter.clear();
        }
    }

    private static boolean isClosingPunctuation(int c) {
        return c == '、' || c == '。' || c == '」' || c == '）' || c == '！' || c == '？' || c == '・';
    }
}
