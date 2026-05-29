package com.carddemo.testharness.parser;

import java.util.Map;

public class CobolSignDecoder {

    public record SignedValue(String digits, boolean positive) {}

    private static final Map<Character, int[]> SIGN_MAP = Map.ofEntries(
        Map.entry('{', new int[]{0, 1}),
        Map.entry('A', new int[]{1, 1}),
        Map.entry('B', new int[]{2, 1}),
        Map.entry('C', new int[]{3, 1}),
        Map.entry('D', new int[]{4, 1}),
        Map.entry('E', new int[]{5, 1}),
        Map.entry('F', new int[]{6, 1}),
        Map.entry('G', new int[]{7, 1}),
        Map.entry('H', new int[]{8, 1}),
        Map.entry('I', new int[]{9, 1}),
        Map.entry('}', new int[]{0, -1}),
        Map.entry('J', new int[]{1, -1}),
        Map.entry('K', new int[]{2, -1}),
        Map.entry('L', new int[]{3, -1}),
        Map.entry('M', new int[]{4, -1}),
        Map.entry('N', new int[]{5, -1}),
        Map.entry('O', new int[]{6, -1}),
        Map.entry('P', new int[]{7, -1}),
        Map.entry('Q', new int[]{8, -1}),
        Map.entry('R', new int[]{9, -1})
    );

    public static SignedValue decode(String rawValue) {
        if (rawValue == null || rawValue.isEmpty()) {
            throw new IllegalArgumentException("Raw value must not be null or empty");
        }

        char lastChar = rawValue.charAt(rawValue.length() - 1);
        String prefix = rawValue.substring(0, rawValue.length() - 1);

        if (Character.isDigit(lastChar)) {
            return new SignedValue(rawValue, true);
        }

        int[] mapping = SIGN_MAP.get(lastChar);
        if (mapping == null) {
            throw new IllegalArgumentException("Invalid sign character: " + lastChar);
        }

        String digits = prefix + mapping[0];
        boolean positive = mapping[1] > 0;

        return new SignedValue(digits, positive);
    }
}
