package com.joysistvi.recording.Utility;

public final class CliViewUtility {
    private static final int WIDTH = 40;
    private static final String HEADER_BORDER = "+".repeat(WIDTH);

    private CliViewUtility() {
    }

    public static void showHeader(String pageTitle) {
        System.out.println("\n" + HEADER_BORDER);
        System.out.println(center(pageTitle.toUpperCase()));
        System.out.println(HEADER_BORDER);
        System.out.println();
    }

    private static String center(String text) {
        if (text.length() >= WIDTH) {
            return text;
        }
        return " ".repeat((WIDTH - text.length()) / 2) + text;
    }
}
