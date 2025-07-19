/**
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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

/**
 * Japanese dictionary class.<br>
 *
 * @author Masahiko Sato
 */
public class Kanwadict {
    private static final String DICTIONARY_PATH = getDictionaryPath();
    private static final String DICTIONARY_SRC = "kanwadict";
    private static final String DICTIONARY_DAT = "kanwadict.dat";

    private static final int INDEX_ENTRY_SIZE = Integer.BYTES;

    private static final Kanwadict KANWADICT = new Kanwadict();
    private static boolean initFailed = false;

    private final HashMap<KanwaKey, ArrayList<YomiKanjiData>> kanwaMap = new HashMap<>();
    private final HashMap<KanwaKey, KanwaAddress> kanwaIndex = new HashMap<>();

    static {
        File kanwaDict = new File(DICTIONARY_PATH, DICTIONARY_DAT);

        if (kanwaDict.exists()) {
            System.out.println("Kanada: Found a pre-built Japanese dictionary.");
            try {
                KANWADICT.loadIndex(kanwaDict);
                System.out.println("Kanada: Init completed!");
            } catch (Exception e) {
                System.out.println("Kanada: Error!! Could not load the dictionary index: " + e);
                initFailed = true;
            }
        } else {
            System.out.println("Kanada: Building Japanese dictionary...");

            Calendar start = Calendar.getInstance();
            StringTokenizer token = new StringTokenizer(DICTIONARY_SRC, ",");

            try {
                Calendar lap = start;
                while (token.hasMoreTokens()) {
                    String sourceFile = token.nextToken();
                    System.out.print("-> Loading Data: " + DICTIONARY_PATH + sourceFile + "... ");
                    KANWADICT.loadData();
                    Calendar now = Calendar.getInstance();
                    int lapTime = (int) Math.ceil(now.getTime().getTime() - lap.getTime().getTime());
                    System.out.println("Done (" + lapTime + " ms)");
                    lap = now;
                }
            } catch (IOException e) {
                System.out.println("Kanada: Error!! Could not build dictionary: " + e);
                initFailed = true;
            }

            if (!initFailed) {
                int loadingTime = (int) Math.ceil((Calendar.getInstance().getTime().getTime() - start.getTime().getTime()));
                System.out.println("Kanada: Build Completed! (" + loadingTime + " ms)");
                try {
                    KANWADICT.buildDict(KANWADICT.kanwaMap);
                    KANWADICT.loadIndex(kanwaDict);
                    System.out.println("Kanada: Init completed!");
                } catch (IOException e) {
                    System.out.println("Kanada: Error!! Could not load the dictionary index: " + e);
                    initFailed = true;
                }
            }
        }
    }

    public static Kanwadict getKanwadict() {
        return KANWADICT;
    }

    private static String getDictionaryPath() {
        // Try the current directory first
        File dictDir = new File("dictionary/japanese/");
        if (dictDir.exists()) {
            return "dictionary/japanese/";
        }

        // Try the parent directory (for when running from build/)
        dictDir = new File("../dictionary/japanese/");
        if (dictDir.exists()) {
            return "../dictionary/japanese/";
        }

        // Try the two levels up
        dictDir = new File("../../dictionary/japanese/");
        if (dictDir.exists()) {
            return "../../dictionary/japanese/";
        }

        // Default fallback
        return "dictionary/japanese/";
    }

    private void loadIndex(File objFile) throws IOException {
        FileInputStream fileStream = new FileInputStream(objFile);

        try (fileStream; DataInputStream dataStream = new DataInputStream(fileStream)) {
            for (int i = 0x4e; i <= 0x9f; ++i) {
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
                if (obj instanceof ArrayList) {
                    @SuppressWarnings("unchecked")
                    ArrayList<YomiKanjiData> valueList = (ArrayList<YomiKanjiData>) obj;
                    kanwaMap.put(key, valueList);
                }
            } catch (ClassNotFoundException e) {
                throw new Exception(e.toString());
            }
        }
    }

    private void buildDict(final HashMap<KanwaKey, ArrayList<YomiKanjiData>> map) throws IOException {
        File outFile = new File(DICTIONARY_PATH, DICTIONARY_DAT);

        if (outFile.exists() && outFile.delete() && outFile.createNewFile()) {
            System.out.println("Creating a new dictionary...");
        }

        RandomAccessFile dictFile = new RandomAccessFile(outFile, "rw");

        // Create a space for key indices.
        for (int i = 0x4e; i <= 0x9f; ++i) {
            for (int j = 0x00; j <= 0xff; ++j) {
                dictFile.writeInt(0);
            }
        }

        for (KanwaKey key : map.keySet()) {
            int pos = (key.key - 0x4e00) * INDEX_ENTRY_SIZE;

            // Move to the key address.
            dictFile.seek(pos);
            dictFile.writeInt((int) dictFile.length());

            ByteArrayOutputStream byteArrayStream = new ByteArrayOutputStream();
            ObjectOutputStream objectStream = new ObjectOutputStream(byteArrayStream);

            objectStream.writeObject(map.get(key));

            // Move to the end and append data.
            dictFile.seek(dictFile.length());
            dictFile.write(byteArrayStream.toByteArray());

            byteArrayStream.close();
            objectStream.close();
        }

        dictFile.close();
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

    private void loadData() throws IOException {
        File kanwaFile = new File(DICTIONARY_PATH, DICTIONARY_SRC);

        if (!kanwaFile.exists()) {
            System.out.println("Dictionary File Not Found: " + DICTIONARY_PATH + DICTIONARY_SRC);
            System.out.println("Current working directory: " + System.getProperty("user.dir"));
            System.out.println("Absolute path attempted: " + kanwaFile.getAbsolutePath());
            initFailed = true;
            return;
        }

        InputStreamReader fileStream = new InputStreamReader(new FileInputStream(kanwaFile), "JISAutoDetect");
        try (fileStream) {
            BufferedReader reader = new BufferedReader(fileStream);
            for (; ; ) {
                String line = reader.readLine();

                if (line == null) {
                    return;
                }

                line = line.trim();

                if (!line.isEmpty()) {
                    parseLine(line);
                }
            }
        } catch (IOException e) {
            throw new IOException();
        }
    }

    private void parseLine(String line) {
        int firstChar = line.codePointAt(0);
        Character.UnicodeBlock block = Character.UnicodeBlock.of(firstChar);


        if (block != Character.UnicodeBlock.HIRAGANA
                && block != Character.UnicodeBlock.KATAKANA
                && block != Character.UnicodeBlock.KATAKANA_PHONETIC_EXTENSIONS) {
            return;
        }

        line = line.replace('/', ' ');
        line = line.replace(',', ' ');
        line = line.replace('\t', ' ');

        StringTokenizer tokenizer = new StringTokenizer(line, " ");
        int count = tokenizer.countTokens();
        if (count < 1) {
            return;
        }

        String yomi = tokenizer.nextToken();
        int yomiLen = yomi.length();
        int tail = yomi.codePointAt(yomiLen - 1);

        if ((tail > 0x40 && tail < 0x5b) || (tail > 0x60 && tail < 0x7b)) {
            yomiLen = yomiLen - 1;
            yomi = yomi.substring(0, yomiLen);
        } else {
            tail = ' ';
        }

        while (tokenizer.hasMoreTokens()) {
            String kanji = tokenizer.nextToken();
            if (yomiLen > 0 && !kanji.isEmpty()) {
                addEntry(yomi, kanji, tail);
            }
        }
    }

    private void addEntry(String yomi, String kanji, int tail) {
        ArrayList<YomiKanjiData> valueList;

        if (!Pattern.matches("^\\p{IsHiragana}+$", yomi)) {
            return;
        }

        if (!Pattern.matches("^[\\p{IsHiragana}\\p{IsKatakana}\\p{IsHan}]+$", kanji)) {
            return;
        }

        int cp = kanji.codePointAt(0);

        KanwaKey key = new KanwaKey(cp);
        YomiKanjiData value = new YomiKanjiData(yomi, tail, kanji);

        if (kanwaMap.containsKey(key)) {
            valueList = kanwaMap.get(key);
        } else {
            valueList = new ArrayList<>();
        }

        valueList.add(value);
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
