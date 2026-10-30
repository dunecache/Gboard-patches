package dev.jason.gboardpatches.extension.customtheme;

import android.content.Context;
import android.content.res.Configuration;

/** Swaps the active theme variant when the system dark mode changes. */
public final class GboardCustomThemeRuntime {
    private GboardCustomThemeRuntime() { }

    public static void applyForCurrentMode(Context context) {
        Context storage = context.createDeviceProtectedStorageContext();
        android.content.SharedPreferences prefs = storage.getSharedPreferences(
                context.getPackageName() + "_patches_theme", Context.MODE_PRIVATE);
        String light = prefs.getString("light", null);
        String dark = prefs.getString("dark", null);
        if (light == null || dark == null) return;
        boolean night = (context.getResources().getConfiguration().uiMode
                & Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES;
        String target = night ? dark : light;
        Context sp = context.createDeviceProtectedStorageContext();
        android.content.SharedPreferences gboard = sp.getSharedPreferences(
                context.getPackageName() + "_preferences", Context.MODE_PRIVATE);
        gboard.edit().putString("additional_keyboard_theme", target).commit();
    }
}