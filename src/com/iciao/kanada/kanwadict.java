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
public class kanwadict {
    private static final String SRC_PATH = "dictionary/japanese/";
    private static final String SRC_FILES = "kanwadict";
    private static final String KANWA_FILENAMES = "kanwadict.dat";

    private static final int SIZE_OF_LONG = 8;

    private static kanwadict kanwa = new kanwadict();
    private static boolean init_failed = false;

    private HashMap<kanwa_key, ArrayList<yomi_kanji_data>> kanwa_map = new HashMap<kanwa_key, ArrayList<yomi_kanji_data>>();
    private HashMap<kanwa_key, kanwa_address> kanwa_index = new HashMap<kanwa_key, kanwa_address>();

    static {
        File kanwa_dict = new File(SRC_PATH, KANWA_FILENAMES);

        if (kanwa_dict.exists()) {
            System.out.println("Kanada: Found a pre-built Japanese dictionary.");
            try {
                kanwa.load_index(kanwa_dict);
                System.out.println("Kanada: Init completed!");
            } catch (Exception e) {
                System.out.println("Kanada: Error!! Could not load the dictionary index: " + e);
                init_failed = true;
            }
        } else {
            System.out.println("Kanada: Building Japanese dictionary...");

            Calendar start = Calendar.getInstance();
            StringTokenizer token = new StringTokenizer(SRC_FILES, ",");

            try {
                Calendar lap = start;
                while (token.hasMoreTokens()) {
                    String source_file = token.nextToken();
                    System.out.print("-> Loading Data: " + SRC_PATH + source_file + "... ");
                    kanwa.load_data(SRC_PATH, source_file);
                    Calendar now = Calendar.getInstance();
                    int lap_time = (int) Math.ceil(now.getTime().getTime() - lap.getTime().getTime());
                    System.out.println("Done (" + lap_time + " ms)");
                    lap = now;
                }
            } catch (IOException e) {
                System.out.println("Kanada: Error!! Could not build dictionary: " + e);
                init_failed = true;
            }

            if (!init_failed) {
                int loading_time = (int) Math.ceil((Calendar.getInstance().getTime().getTime() - start.getTime().getTime()));
                System.out.println("Kanada: Build Completed! (" + loading_time + " ms)");
                try {
                    kanwa.build_dict(kanwa.kanwa_map);
                    kanwa.load_index(kanwa_dict);
                    System.out.println("Kanada: Init completed!");
                } catch (IOException e) {
                    System.out.println("Kanada: Error!! Could not load the dictionary index: " + e);
                    init_failed = true;
                }
            }
        }
    }

    public static kanwadict get_kanwa() {
        return kanwa;
    }

    private void load_index(File obj_file) throws IOException {
        FileInputStream file_stream = new FileInputStream(obj_file);
        DataInputStream data_stream = new DataInputStream(file_stream);

        try {
            for (int i = 0x4e; i <= 0x9f; ++i) {
                for (int j = 0x00; j <= 0xff; ++j) {
                    kanwa_key this_key = new kanwa_key(((i << 8) | j));
                    kanwa_address this_address = new kanwa_address();
                    this_address.value = data_stream.readLong();
                    kanwa_index.put(this_key, this_address);
                }
            }
        } finally {
            file_stream.close();
            data_stream.close();
        }
    }

    private void load_object(kanwa_key key) throws Exception {
        File obj_file;
        FileInputStream file_stream = null;
        BufferedInputStream buffer = null;
        ObjectInputStream object_stream = null;

        try {
            obj_file = new File(SRC_PATH, KANWA_FILENAMES);
            file_stream = new FileInputStream(obj_file);

            long obj_address = (kanwa_index.get(key)).value;

            file_stream.skip(obj_address);

            buffer = new BufferedInputStream(file_stream);
            object_stream = new ObjectInputStream(buffer);

            try {
                Object obj = object_stream.readObject();
                if (obj instanceof ArrayList) {
                    ArrayList<yomi_kanji_data> value_list;
                    value_list = (ArrayList<yomi_kanji_data>) obj;
                    kanwa_map.put(key, value_list);
                }
            } catch (ClassNotFoundException e) {
                throw new Exception(e.toString());
            }
        } finally {
            if (object_stream != null)
                object_stream.close();

            if (buffer != null)
                buffer.close();

            if (file_stream != null)
                file_stream.close();
        }

    }

    private void build_dict(final HashMap map) throws IOException {
        File out_file = new File(SRC_PATH, KANWA_FILENAMES);

        if (out_file.exists() && out_file.delete() && out_file.createNewFile()) {
            System.out.println("Creating a new dictionary...");
        }

        RandomAccessFile dict_file = new RandomAccessFile(out_file, "rw");

        // Create a space for key indices.
        for (int i = 0x4e; i <= 0x9f; ++i) {
            for (int j = 0x00; j <= 0xff; ++j) {
                dict_file.writeLong(0);
            }
        }

        Iterator iterator = map.keySet().iterator();

        while (iterator.hasNext()) {
            kanwa_key key = (kanwa_key) iterator.next();

            long pos = (key.key_mbr - 0x4e00) * SIZE_OF_LONG;

            // Move to the key address.
            dict_file.seek(pos);
            dict_file.writeLong(dict_file.length());

            ByteArrayOutputStream byte_array_stream = new ByteArrayOutputStream();
            ObjectOutputStream object_stream = new ObjectOutputStream(byte_array_stream);

            object_stream.writeObject(map.get(key));

            // Move to the end and append data.
            dict_file.seek(dict_file.length());
            dict_file.write(byte_array_stream.toByteArray());

            byte_array_stream.close();
            object_stream.close();
        }

        dict_file.close();
    }

    public kanwa_key get_key(int codepoint) {
        return new kanwa_key(codepoint);
    }

    public ArrayList get_value(kanwa_key key) {
        return (ArrayList) kanwa_map.get(key);
    }

    public boolean search_key(kanwa_key key) throws Exception {
        kanwa_address this_address = kanwa_index.get(key);
        if (this_address != null && !kanwa_map.containsKey(key) && this_address.value > 0) {
            load_object(key);
        }
        return kanwa_map.containsKey(key);
    }

    private void load_data(String filepath, String filename) throws IOException {
        File kanwa_file = new File(filepath, filename);

        if (!kanwa_file.exists()) {
            System.out.println("Dictionary File Not Found: " + filepath + filename);
            init_failed = true;
            return;
        }

        InputStreamReader file_stream = new InputStreamReader(new FileInputStream(kanwa_file), "JISAutoDetect");
        BufferedReader reader = new BufferedReader(file_stream);
        try {
            for (; ; ) {
                String line = reader.readLine();

                if (line == null) {
                    return;
                }

                line = line.trim();

                if (line.length() > 0) {
                    parse_line(line);
                }
            }
        } catch (IOException e) {
            throw new IOException();
        } finally {
            file_stream.close();
        }
    }

    private void parse_line(String line) {
        int first_char = line.codePointAt(0);
        Character.UnicodeBlock block = Character.UnicodeBlock.of(first_char);


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
        int yomi_len = yomi.length();
        int tail = yomi.codePointAt(yomi_len - 1);

        if ((tail > 0x40 && tail < 0x5b) || (tail > 0x60 && tail < 0x7b)) {
            yomi_len = yomi_len - 1;
            yomi = yomi.substring(0, yomi_len);
        } else {
            tail = ' ';
        }

        while (tokenizer.hasMoreTokens()) {
            String kanji = tokenizer.nextToken();
            if (yomi_len > 0 && kanji.length() > 0) {
                add_entry(yomi, kanji, tail);
            }
        }
    }

    private void add_entry(String yomi, String kanji, int tail) {
        ArrayList<yomi_kanji_data> value_list;

        if (!Pattern.matches("^\\p{IsHiragana}+$", yomi)) {
            return;
        }

        if (!Pattern.matches("^[\\p{IsHiragana}\\p{IsKatakana}\\p{IsHan}]+$", kanji)) {
            return;
        }

        int cp = kanji.codePointAt(0);

        kanwa_key key = new kanwa_key(cp);
        yomi_kanji_data value = new yomi_kanji_data(yomi, tail, kanji);

        if (kanwa_map.containsKey(key)) {
            value_list = kanwa_map.get(key);
        } else {
            value_list = new ArrayList<yomi_kanji_data>();
        }

        value_list.add(value);
        kanwa_map.put(key, value_list);
    }

    public static class kanwa_address implements Serializable {
        long value;
    }

    public static class kanwa_key implements Serializable {
        private int key_mbr;

        public kanwa_key(int codepoint) {
            key_mbr = codepoint;
        }

        public boolean equals(Object obj) {
            if (obj instanceof kanwa_key) {
                kanwa_key this_key = (kanwa_key) obj;
                return (this.key_mbr == this_key.key_mbr);
            }
            return false;
        }

        public int hashCode() {
            return key_mbr;
        }
    }

    public static class yomi_kanji_data implements Serializable {
        private String yomi_mbr;
        private int tail_mbr;
        private String kanji_mbr;

        public yomi_kanji_data(String yomi, int tail, String kanji) {
            yomi_mbr = yomi;
            tail_mbr = tail;
            kanji_mbr = kanji;
        }

        public String get_yomi() {
            return yomi_mbr;
        }

        public String get_kanji() {
            return kanji_mbr;
        }

        public int get_length() {
            return kanji_mbr.length();
        }

        public int get_tail() {
            return tail_mbr;
        }

        public boolean equals(Object obj) {
            if (obj instanceof yomi_kanji_data) {
                yomi_kanji_data data = (yomi_kanji_data) obj;
                return (yomi_mbr.equals(data.get_yomi()) && tail_mbr == data.get_tail() && kanji_mbr.equals(data.get_kanji()));
            }
            return false;
        }
    }
}
