package com.philips.utilities;

public class Utilities {
    public static String removeSingleQuotes(String string) {
        return string.replaceAll("'", "");
    }
}
