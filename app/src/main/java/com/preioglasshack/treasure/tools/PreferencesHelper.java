package com.preioglasshack.treasure.tools;

import android.content.Context;
import android.preference.PreferenceManager;

/**
 * Created by g123k on 21/06/14.
 */
public class PreferencesHelper {

    private static final String FIRST_LAUNCH = "first_launch";
    private static final String CURRENT_BOOK = "current_book";

    public static final String BOOK_DEFAULT_VALUE = "-1";

    public static boolean isFirstLaunch(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(FIRST_LAUNCH, true);
    }

    public static void setIsNotFirstLaunchAnymore(Context context) {
        PreferenceManager.getDefaultSharedPreferences(context).edit()
                .putBoolean(FIRST_LAUNCH, false).apply();
    }

    public static void setCurrentBook(Context context, String bookId) {
        PreferenceManager.getDefaultSharedPreferences(context).edit()
                .putString(CURRENT_BOOK, bookId).apply();
    }

    public static String getCurrentBook(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getString(CURRENT_BOOK, BOOK_DEFAULT_VALUE);
    }

    public static void removeCurrentBook(Context context) {
        PreferenceManager.getDefaultSharedPreferences(context).edit()
                .remove(CURRENT_BOOK).apply();
    }
}
