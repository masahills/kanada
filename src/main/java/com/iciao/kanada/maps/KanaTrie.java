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