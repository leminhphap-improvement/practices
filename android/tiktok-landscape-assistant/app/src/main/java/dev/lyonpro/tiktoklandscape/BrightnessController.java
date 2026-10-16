package dev.lyonpro.tiktoklandscape;

import android.content.Context;
import android.content.SharedPreferences;
import android.provider.Settings;

/** Scoped system-brightness override; restores the original auto/manual mode and level. */
final class BrightnessController {
    private final Context context;
    BrightnessController(Context context) { this.context = context.getApplicationContext(); }

    boolean canWrite() { return Settings.System.canWrite(context); }

    void maximize() {
        if (!canWrite()) return;
        SharedPreferences p = Prefs.get(context);
        int currentMode = Settings.System.getInt(context.getContentResolver(),
                Settings.System.SCREEN_BRIGHTNESS_MODE, Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
        int currentLevel = Settings.System.getInt(context.getContentResolver(),
                Settings.System.SCREEN_BRIGHTNESS, 128);
        if (!p.getBoolean(Prefs.SAVED, false)) {
            boolean saved = p.edit()
                    .putInt(Prefs.OLD_MODE, currentMode)
                    .putInt(Prefs.OLD_LEVEL, currentLevel)
                    .putBoolean(Prefs.SAVED, true).commit();
            if (!saved) return;
        }
        // The accessibility service polls frequently: don't rewrite global settings on every poll.
        if (currentMode == Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL && currentLevel == 255) return;
        try {
            boolean manual = Settings.System.putInt(context.getContentResolver(),
                    Settings.System.SCREEN_BRIGHTNESS_MODE, Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
            boolean full = Settings.System.putInt(context.getContentResolver(),
                    Settings.System.SCREEN_BRIGHTNESS, 255);
            if (!manual || !full) restore();
        } catch (SecurityException e) { restore(); }
    }

    void restore() {
        SharedPreferences p = Prefs.get(context);
        if (!canWrite() || !p.getBoolean(Prefs.SAVED, false)) return;
        try {
            boolean level = Settings.System.putInt(context.getContentResolver(),
                    Settings.System.SCREEN_BRIGHTNESS, p.getInt(Prefs.OLD_LEVEL, 128));
            boolean mode = Settings.System.putInt(context.getContentResolver(),
                    Settings.System.SCREEN_BRIGHTNESS_MODE, p.getInt(Prefs.OLD_MODE, 0));
            if (level && mode) p.edit().putBoolean(Prefs.SAVED, false).commit();
        } catch (SecurityException ignored) {
            // If permissions were revoked, restoration can be retried from the settings screen.
        }
    }
}
