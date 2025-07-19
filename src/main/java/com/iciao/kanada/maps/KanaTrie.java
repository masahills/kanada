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
package com.iciao.kanada.maps;

import java.util.HashMap;
import java.util.Map;

class KanaTrie {
    static class TrieNode {
        Map<Character, TrieNode> children = new HashMap<>();
        String[] romanizations;
    }

    private final TrieNode root = new TrieNode();

    void insert(String key, String[] values) {
        TrieNode node = root;
        for (char c : key.toCharArray()) {
            node = node.children.computeIfAbsent(c, k -> new TrieNode());
        }
        node.romanizations = values;
    }

    public MatchResult searchLongest(String input) {
        TrieNode node = root;
        String[] result = null;
        int matchLength = 0;

        for (int i = 0; i < input.length(); i++) {
            char ch = input.charAt(i);
            node = node.children.get(ch);
            if (node == null) break;
            if (node.romanizations != null) {
                result = node.romanizations;
                matchLength = i + 1;
            }
        }

        return result != null ? new MatchResult(result, matchLength) : null;
    }

    public record MatchResult(String[] values, int length) {
    }
}