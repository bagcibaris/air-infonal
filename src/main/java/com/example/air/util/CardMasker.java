package com.example.air.util;

public final class CardMasker {
    private CardMasker(){}

    public static String mask(String raw) {
        if (raw == null) return null;
        String digits = raw.replaceAll("[^0-9]", "");
        if (digits.length() <= 10) return digits;
        int keepStart = 6, keepEnd = 4;
        String start = digits.substring(0, keepStart);
        String end = digits.substring(digits.length() - keepEnd);
        StringBuilder mid = new StringBuilder();
        for (int i = 0; i < digits.length() - keepStart - keepEnd; i++) {
            mid.append('*');
        }
        return start + mid + end;
    }
}
