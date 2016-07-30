package com.sbtn.androidtv.utils;

import android.support.annotation.Nullable;

import java.util.Date;

public final class StringUtils {

    public static final String PATTERN_ALPHANUMERIC = "^[a-zA-Z0-9]*$";
    public static final String PATTERN_ALPHABETIC = "^[a-zA-Z]*$";
    private static final String HTML_BOLD_START = "<b>";
    private static final String HTML_BOLD_END = "</b>";

    public static String rTagToString(String rTag) {
        try {
            long time = Long.valueOf(rTag);
            return new Date(time).toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static boolean isNotEmpty(@Nullable String text) {
        return !isEmpty(text);
    }

    public static String wrapInHtmlBold(final String text) {
        return HTML_BOLD_START + text + HTML_BOLD_END;
    }

    public static boolean isEmpty(@Nullable String text) {
        return text == null || text.length() == 0;
    }

    public static String getNotEmpty(@Nullable String text) {
        return isEmpty(text) ? "" : text;
    }

    public static boolean stringEqual(@Nullable String str1, @Nullable String str2) {
        //noinspection SimplifiableIfStatement
        if (isEmpty(str1) && isEmpty(str2))
            return true;
        return !isEmpty(str1) && str1.equals(str2);
    }

    public static boolean stringEqualNonEmpty(@Nullable String str1, @Nullable String str2) {
        //noinspection SimplifiableIfStatement
        if (isEmpty(str1) || isEmpty(str2))
            return false;
        return str1.equals(str2);
    }

    public static boolean stringNotEqual(@Nullable String str1, @Nullable String str2) {
        return isEmpty(str1) || isEmpty(str2) || str1.equals(str2);
    }

    public static int hashCode(@Nullable String text) {
        if (isEmpty(text))
            return 0;
        return text.hashCode();
    }

    public static long convertStringToInteger(String input) {
        try {
            return Long.valueOf(input);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    public static boolean isAlphaNumeric(String s) {
        return s.matches(PATTERN_ALPHANUMERIC);
    }

    public static boolean isAlphabetic(String s) {
        return s.matches(PATTERN_ALPHABETIC);
    }

}
