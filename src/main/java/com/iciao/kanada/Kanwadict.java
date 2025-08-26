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

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Japanese dictionary class.<br>
 *
 * @author Masahiko Sato
 */
public class Kanwadict {
    private static final Logger LOGGER = Logger.getLogger(Kanwadict.class.getName());

    private static final String DICTIONARY_PATH = getDictionaryPath();
    private static final String DICTIONARY_SRC = System.getProperty("dictionaries", "kakasidict");
    private static final String DICTIONARY_DAT = "kanwadict.dat";

    // Unicode ranges for CJK characters
    private static final int CJK_UNIFIED_IDEOGRAPHS_FIRST = 0x4e;
    private static final int CJK_UNIFIED_IDEOGRAPHS_LAST = 0x9f;

    private static final int INDEX_ENTRY_SIZE = Integer.BYTES;

    private static final Kanwadict KANWADICT = new Kanwadict();

    private final HashMap<KanwaKey, ArrayList<YomiKanjiData>> kanwaMap = new HashMap<>();
    private final HashMap<KanwaKey, KanwaAddress> kanwaIndex = new HashMap<>();

    static {
        boolean initState = false;
        File kanwaDict = new File(DICTIONARY_PATH, DICTIONARY_DAT);

        if (kanwaDict.exists()) {
            LOGGER.info("Kanada: Found a pre-built Japanese dictionary.");
            initState = true;
        } else {
            LOGGER.info("Kanada: Building a Japanese dictionary...");

            long start = System.currentTimeMillis();
            long lap = start;

            StringTokenizer token = new StringTokenizer(DICTIONARY_SRC, ",");
            while (token.hasMoreTokens()) {
                String sourceFile = token.nextToken();
                File kanwaFile = new File(DICTIONARY_PATH, sourceFile);
                if (!kanwaFile.exists()) {
                    LOGGER.warning("--> The file not found: " + DICTIONARY_PATH + sourceFile);
                    continue;
                }
                try {
                    LOGGER.info("--> Loading dictionary data from " + DICTIONARY_PATH + sourceFile + "...");
                    KANWADICT.loadData(kanwaFile);
                    initState = true;
                } catch (IOException e) {
                    LOGGER.log(Level.SEVERE, "--> Failed to load data from " + sourceFile, e);
                }
                long now = System.currentTimeMillis();
                int lapTime = (int) Math.ceil(now - lap);
                LOGGER.info("Done (" + lapTime + " ms)");
                lap = now;
            }

            if (initState) {
                int loadingTime = (int) Math.ceil(System.currentTimeMillis() - start);
                LOGGER.info("Kanada: Finished reading data form the source files. (" + loadingTime + " ms)");
                try {
                    LOGGER.info("Kanada: Compling a dictionary...");
                    KANWADICT.buildDict(KANWADICT.kanwaMap);
                    long now = System.currentTimeMillis();
                    int lapTime = (int) Math.ceil(now - lap);
                    LOGGER.info("Done (" + lapTime + " ms)");
                } catch (IOException e) {
                    LOGGER.log(Level.SEVERE, "Kanada: Failed to build a dictionary.", e);
                    initState = false;
                }
            }
        }

        if (initState) {
            try {
                KANWADICT.loadIndex(kanwaDict);
                LOGGER.info("Kanada: The dictionary index has been loaded successfully.");
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "Kanada: Failed to load the dictionary index.", e);
            }
        }
    }

    public static Kanwadict getKanwadict() {
        return KANWADICT;
    }

    private static String getDictionaryPath() {
        String[] possiblePaths = {
                "dictionary/japanese/",
                "../dictionary/japanese/",
                "../../dictionary/japanese/"
        };

        // Try each possible path
        for (String path : possiblePaths) {
            File dictDir = new File(path);
            if (dictDir.exists()) {
                return path;
            }
        }

        // Default fallback
        return possiblePaths[0];
    }

    private void loadIndex(File objFile) throws IOException {
        try (FileInputStream fileStream = new FileInputStream(objFile);
             DataInputStream dataStream = new DataInputStream(fileStream)) {
            for (int i = CJK_UNIFIED_IDEOGRAPHS_FIRST; i <= CJK_UNIFIED_IDEOGRAPHS_LAST; ++i) {
                for (int j = 0x00; j <= 0xff; ++j) {
                    KanwaKey thisKey = new KanwaKey(((i << 8) | j));
                    KanwaAddress thisAddress = new KanwaAddress();
                    thisAddress.value = dataStream.readInt();
                    kanwaIndex.put(thisKey, thisAddress);
                }
            }
        }
    }

    private void loadObject(KanwaKey key) throws Exception {
        File objFile = new File(DICTIONARY_PATH, DICTIONARY_DAT);

        try (RandomAccessFile randomFile = new RandomAccessFile(objFile, "r")) {
            int objAddress = (kanwaIndex.get(key)).value;

            randomFile.seek(objAddress);

            byte[] data = new byte[(int) (randomFile.length() - objAddress)];
            randomFile.readFully(data);

            try (ObjectInputStream objectStream = new ObjectInputStream(
                    new ByteArrayInputStream(data))) {

                Object obj = objectStream.readObject();
                if (obj instanceof ArrayList<?> list) {
                    @SuppressWarnings("unchecked")
                    ArrayList<YomiKanjiData> valueList = (ArrayList<YomiKanjiData>) list;
                    kanwaMap.put(key, valueList);
                }
            } catch (ClassNotFoundException e) {
                throw new Exception("Failed to deserialize dictionary object", e);
            }
        }
    }

    private void buildDict(final HashMap<KanwaKey, ArrayList<YomiKanjiData>> map) throws IOException {
        File outFile = new File(DICTIONARY_PATH, DICTIONARY_DAT);

        if (outFile.exists() && outFile.delete() && outFile.createNewFile()) {
            LOGGER.info("Creating a new dictionary...");
        }

        try (RandomAccessFile dictFile = new RandomAccessFile(outFile, "rw")) {
            // Create a space for key indices.
            for (int i = CJK_UNIFIED_IDEOGRAPHS_FIRST; i <= CJK_UNIFIED_IDEOGRAPHS_LAST; ++i) {
                for (int j = 0x00; j <= 0xff; ++j) {
                    dictFile.writeInt(0);
                }
            }

            for (KanwaKey key : map.keySet()) {
                int pos = (key.key - 0x4e00) * INDEX_ENTRY_SIZE;

                // Move to the key address.
                dictFile.seek(pos);
                dictFile.writeInt((int) dictFile.length());

                try (ByteArrayOutputStream byteArrayStream = new ByteArrayOutputStream();
                     ObjectOutputStream objectStream = new ObjectOutputStream(byteArrayStream)) {

                    objectStream.writeObject(map.get(key));

                    // Move to the end and append data.
                    dictFile.seek(dictFile.length());
                    dictFile.write(byteArrayStream.toByteArray());
                }
            }
        }
    }

    public KanwaKey getKey(int codepoint) {
        return new KanwaKey(codepoint);
    }

    public ArrayList<YomiKanjiData> getValue(KanwaKey key) {
        return kanwaMap.get(key);
    }

    public boolean searchKey(KanwaKey key) throws Exception {
        KanwaAddress thisAddress = kanwaIndex.get(key);
        if (thisAddress != null && !kanwaMap.containsKey(key) && thisAddress.value > 0) {
            loadObject(key);
        }
        return kanwaMap.containsKey(key);
    }

    private void loadData(File kanwaFile) throws IOException {

        try (InputStreamReader fileStream = new InputStreamReader(new FileInputStream(kanwaFile), "JISAutoDetect");
             BufferedReader reader = new BufferedReader(fileStream)) {

            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty()) {
                    parseLine(line);
                }
            }
        } catch (IOException e) {
            throw new IOException("Error reading dictionary file", e);
        }
    }

    private void parseLine(String line) {
        // Check if the line starts with a Japanese character (hiragana/katakana)
        int firstChar = line.codePointAt(0);
        Character.UnicodeBlock block = Character.UnicodeBlock.of(firstChar);

        // Only process lines that start with Japanese phonetic characters
        if (block != Character.UnicodeBlock.HIRAGANA
                && block != Character.UnicodeBlock.KATAKANA
                && block != Character.UnicodeBlock.KATAKANA_PHONETIC_EXTENSIONS) {
            return;
        }

        // Remove SKK annotations from the line
        if (line.contains("/")) {
            line = line.replaceAll(";[^/]*", "");
        }

        // Normalize separators to spaces
        line = line.replace('/', ' ')
                .replace(',', ' ')
                .replace('\t', ' ')
                .trim();

        StringTokenizer tokenizer = new StringTokenizer(line, " ");
        int count = tokenizer.countTokens();
        if (count < 1) {
            return;
        }

        // The first token is the reading (yomi)
        String yomi = tokenizer.nextToken();
        int yomiLen = yomi.length();
        int tail = yomi.codePointAt(yomiLen - 1);

        // Check if the reading has a Latin character suffix (used for part of speech)
        // Not sure if tail markers can be uppercased but include them just in case
        if ((tail > 0x40 && tail < 0x5b) || (tail > 0x60 && tail < 0x7b)) {
            // Remove the suffix character but keep it as the 'tail' marker
            yomiLen = yomiLen - 1;
            yomi = yomi.substring(0, yomiLen);
            tail = Character.toLowerCase(tail);
        } else {
            // No special suffix
            tail = ' ';
        }

        // Process all kanji entries for this reading
        while (tokenizer.hasMoreTokens()) {
            String kanji = tokenizer.nextToken();
            if (yomiLen > 0 && !kanji.isEmpty()) {
                addEntry(yomi, kanji, tail);
            }
        }
    }

    private void addEntry(String yomi, String kanji, int tail) {
        ArrayList<YomiKanjiData> valueList;

        // Validate that yomi contains only hiragana
        if (!yomi.matches("^[\\p{Script=Hiragana}\\u30FC]+$")) {
            return;
        }

        int cp = kanji.codePointAt(0);

        // The codepoint must be within the CJK Unified Ideographs range (0x4E00-0x9FFF)
        if (cp < 0x4e00 || cp > 0x9fff) {
            return;
        }

        KanwaKey key = new KanwaKey(cp);
        YomiKanjiData value = new YomiKanjiData(yomi, tail, kanji);

        if (kanwaMap.containsKey(key)) {
            valueList = kanwaMap.get(key);
        } else {
            valueList = new ArrayList<>();
        }

        // Insert the new word into the valueList, placing the longer words before the shorter ones.
        int insertIdx = 0;
        for (; insertIdx < valueList.size(); insertIdx++) {
            if (value.kanji().length() > valueList.get(insertIdx).kanji().length()) {
                break;
            }
        }
        valueList.add(insertIdx, value);
        //valueList.add(value);
        kanwaMap.put(key, valueList);
    }

    public static class KanwaAddress implements Serializable {
        int value;
    }

    public static class KanwaKey implements Serializable {
        private final int key;

        public KanwaKey(int codepoint) {
            key = codepoint;
        }

        public boolean equals(Object obj) {
            if (obj instanceof KanwaKey thisKey) {
                return (this.key == thisKey.key);
            }
            return false;
        }

        public int hashCode() {
            return key;
        }
    }

    public void exportAllEntries() {
        try (PrintWriter writer = new PrintWriter(DICTIONARY_PATH + "kanwadict_export.txt", StandardCharsets.UTF_8)) {
            List<KanwaKey> keyList = new ArrayList<>(kanwaIndex.keySet());
            keyList.sort(Comparator.comparingInt(k -> k.key));
            for (KanwaKey key : keyList) {
                try {
                    if (searchKey(key)) {
                        ArrayList<YomiKanjiData> valueList = getValue(key);
                        StringBuilder line = new StringBuilder();
                        line.append("U+")
                                .append(Integer.toHexString(key.key).toUpperCase())
                                .append(" ")
                                .append(Character.toString(key.key));
                        for (YomiKanjiData data : valueList) {
                            line.append(",")
                                    .append(data.kanji())
                                    .append("/")
                                    .append(data.yomi)
                                    .append(data.tail() == ' ' ? "" : (char) data.tail());
                        }
                        writer.println(line);
                    }
                } catch (Exception e) {
                    LOGGER.log(Level.SEVERE, "Failed to load or process key: " + key, e);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        Kanwadict kanwadict = Kanwadict.getKanwadict();
        kanwadict.exportAllEntries();
    }

    public int getMaxEntryLength() {
        int maxLength = 20; // Conservative default
        try {
            // Load some entries to get a better estimate
            for (int i = 0x4e00; i < 0x4e10 && maxLength < 50; i++) {
                KanwaKey key = new KanwaKey(i);
                if (searchKey(key)) {
                    ArrayList<YomiKanjiData> valueList = getValue(key);
                    if (valueList != null) {
                        for (YomiKanjiData data : valueList) {
                            maxLength = Math.max(maxLength, data.kanji.length());
                        }
                    }
                }
            }
        } catch (Exception e) {
            // Fallback to safe default
        }
        return Math.max(maxLength, 20);
    }

    public record YomiKanjiData(String yomi, int tail, String kanji) implements Serializable {

        public int getLength() {
            return kanji.length();
        }

        public boolean equals(Object obj) {
            if (obj instanceof YomiKanjiData data) {
                return (yomi.equals(data.yomi()) && tail == data.tail() && kanji.equals(data.kanji()));
            }
            return false;
        }
    }
}
