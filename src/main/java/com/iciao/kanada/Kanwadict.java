/**
 * Kanada (Kanji-Kana Transliteration Library for Java)
 * Copyright (C) 2002-2014 Masahiko Sato
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package com.iciao.kanada;

import java.io.*;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Japanese dictionary class.<br>
 *
 * @author Masahiko Sato
 */
public class Kanwadict {
    private static final String SRC_PATH = "dictionary/japanese/";
    private static final String SRC_FILES = "kanwadict";
    private static final String KANWA_FILENAMES = "kanwadict.dat";

    private static final int SIZE_OF_LONG = 8;

    private static Kanwadict kanwa = new Kanwadict();
    private static boolean initFailed = false;

    private HashMap<KanwaKey, ArrayList<YomiKanjiData>> kanwaMap = new HashMap<>();
    private HashMap<KanwaKey, KanwaAddress> kanwaIndex = new HashMap<>();

    static {
        File kanwaDict = new File(SRC_PATH, KANWA_FILENAMES);

        if (kanwaDict.exists()) {
            System.out.println("Kanada: Found a pre-built Japanese dictionary.");
            try {
                kanwa.loadIndex(kanwaDict);
                System.out.println("Kanada: Init completed!");
            } catch (Exception e) {
                System.out.println("Kanada: Error!! Could not load the dictionary index: " + e);
                initFailed = true;
            }
        } else {
            System.out.println("Kanada: Building Japanese dictionary...");

            Calendar start = Calendar.getInstance();
            StringTokenizer token = new StringTokenizer(SRC_FILES, ",");

            try {
                Calendar lap = start;
                while (token.hasMoreTokens()) {
                    String sourceFile = token.nextToken();
                    System.out.print("-> Loading Data: " + SRC_PATH + sourceFile + "... ");
                    kanwa.loadData(SRC_PATH, sourceFile);
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
                    kanwa.buildDict(kanwa.kanwaMap);
                    kanwa.loadIndex(kanwaDict);
                    System.out.println("Kanada: Init completed!");
                } catch (IOException e) {
                    System.out.println("Kanada: Error!! Could not load the dictionary index: " + e);
                    initFailed = true;
                }
            }
        }
    }

    public static Kanwadict getKanwa() {
        return kanwa;
    }

    private void loadIndex(File objFile) throws IOException {
        FileInputStream fileStream = new FileInputStream(objFile);
        DataInputStream dataStream = new DataInputStream(fileStream);

        try {
            for (int i = 0x4e; i <= 0x9f; ++i) {
                for (int j = 0x00; j <= 0xff; ++j) {
                    KanwaKey thisKey = new KanwaKey(((i << 8) | j));
                    KanwaAddress thisAddress = new KanwaAddress();
                    thisAddress.value = dataStream.readLong();
                    kanwaIndex.put(thisKey, thisAddress);
                }
            }
        } finally {
            fileStream.close();
            dataStream.close();
        }
    }

    private void loadObject(KanwaKey key) throws Exception {
        File objFile;
        FileInputStream fileStream = null;
        BufferedInputStream buffer = null;
        ObjectInputStream objectStream = null;

        try {
            objFile = new File(SRC_PATH, KANWA_FILENAMES);
            fileStream = new FileInputStream(objFile);

            long objAddress = (kanwaIndex.get(key)).value;

            fileStream.skip(objAddress);

            buffer = new BufferedInputStream(fileStream);
            objectStream = new ObjectInputStream(buffer);

            try {
                Object obj = objectStream.readObject();
                if (obj instanceof ArrayList) {
                    @SuppressWarnings("unchecked")
                    ArrayList<YomiKanjiData> valueList = (ArrayList<YomiKanjiData>) obj;
                    kanwaMap.put(key, valueList);
                }
            } catch (ClassNotFoundException e) {
                throw new Exception(e.toString());
            }
        } finally {
            if (objectStream != null)
                objectStream.close();

            if (buffer != null)
                buffer.close();

            if (fileStream != null)
                fileStream.close();
        }

    }

    private void buildDict(final HashMap<KanwaKey, ArrayList<YomiKanjiData>> map) throws IOException {
        File outFile = new File(SRC_PATH, KANWA_FILENAMES);

        if (outFile.exists() && outFile.delete() && outFile.createNewFile()) {
            System.out.println("Creating a new dictionary...");
        }

        RandomAccessFile dictFile = new RandomAccessFile(outFile, "rw");

        // Create a space for key indices.
        for (int i = 0x4e; i <= 0x9f; ++i) {
            for (int j = 0x00; j <= 0xff; ++j) {
                dictFile.writeLong(0);
            }
        }

        Iterator<KanwaKey> iterator = map.keySet().iterator();

        while (iterator.hasNext()) {
            KanwaKey key = iterator.next();

            long pos = (key.key - 0x4e00) * SIZE_OF_LONG;

            // Move to the key address.
            dictFile.seek(pos);
            dictFile.writeLong(dictFile.length());

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

    private void loadData(String filepath, String filename) throws IOException {
        File kanwaFile = new File(filepath, filename);

        if (!kanwaFile.exists()) {
            System.out.println("Dictionary File Not Found: " + filepath + filename);
            initFailed = true;
            return;
        }

        InputStreamReader fileStream = new InputStreamReader(new FileInputStream(kanwaFile), "JISAutoDetect");
        BufferedReader reader = new BufferedReader(fileStream);
        try {
            for (; ; ) {
                String line = reader.readLine();

                if (line == null) {
                    return;
                }

                line = line.trim();

                if (line.length() > 0) {
                    parseLine(line);
                }
            }
        } catch (IOException e) {
            throw new IOException();
        } finally {
            fileStream.close();
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
            if (yomiLen > 0 && kanji.length() > 0) {
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
        long value;
    }

    public static class KanwaKey implements Serializable {
        private int key;

        public KanwaKey(int codepoint) {
            key = codepoint;
        }

        public boolean equals(Object obj) {
            if (obj instanceof KanwaKey) {
                KanwaKey thisKey = (KanwaKey) obj;
                return (this.key == thisKey.key);
            }
            return false;
        }

        public int hashCode() {
            return key;
        }
    }

    public static class YomiKanjiData implements Serializable {
        private String yomi;
        private int tail;
        private String kanji;

        public YomiKanjiData(String yomi, int tail, String kanji) {
            this.yomi = yomi;
            this.tail = tail;
            this.kanji = kanji;
        }

        public String getYomi() {
            return yomi;
        }

        public String getKanji() {
            return kanji;
        }

        public int getLength() {
            return kanji.length();
        }

        public int getTail() {
            return tail;
        }

        public boolean equals(Object obj) {
            if (obj instanceof YomiKanjiData) {
                YomiKanjiData data = (YomiKanjiData) obj;
                return (yomi.equals(data.getYomi()) && tail == data.getTail() && kanji.equals(data.getKanji()));
            }
            return false;
        }
    }
}
