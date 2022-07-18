/*
 * Decompiled with CFR 0.150.
 */
package com.myhyuny.lang;

import java.util.HashMap;

public class NumberShortener {
    private static final char[] WORDS = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ-_".toCharArray();
    private static final int LENGTH = WORDS.length;
    private static final HashMap<Character, Character> KEYS = new HashMap(LENGTH);

    static {
        for (char i = '\u0000'; i < LENGTH; i = (char)(i + '\u0001')) {
            KEYS.put(Character.valueOf(WORDS[i]), Character.valueOf(i));
        }
    }

    public static String encode(long value) {
        StringBuilder builder = new StringBuilder();
        while (value > 0L) {
            builder.insert(0, WORDS[(int)(value % (long)LENGTH)]);
            value /= (long)LENGTH;
        }
        return builder.toString();
    }

    public static long decode(String value) {
        long multiplication = 1L;
        long number = 0L;
        char[] list = value.trim().toCharArray();
        for (int i = list.length - 1; i > -1; --i) {
            Character val = KEYS.get(Character.valueOf(list[i]));
            if (val == null) {
                return 0L;
            }
            number += (long)val.charValue() * multiplication;
            multiplication *= (long)LENGTH;
        }
        return number;
    }
}

