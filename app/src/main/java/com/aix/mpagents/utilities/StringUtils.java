package com.aix.mpagents.utilities;

import androidx.annotation.NonNull;

public class StringUtils {

    public static String capitalize(@NonNull String input) {

        String[] words = input.toLowerCase().split(" ");
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < words.length; i++) {
            String word = words[i];

            if (i > 0 && word.length() > 0) {
                builder.append(" ");
            }

            String cap = word.substring(0, 1).toUpperCase() + word.substring(1);
            builder.append(cap);
        }
        return builder.toString();
    }

    public static String getFistName(@NonNull String input) {
        String[] words = input.toLowerCase().split(" ");
        if(!words[0].isEmpty()) return words[0];
        else return "";
    }

    public static String getLastName(@NonNull String input) {
        String[] words = input.toLowerCase().split(" ");
        if(!words[words.length - 1].isEmpty()) return words[words.length - 1];
        else return "";
    }
}
