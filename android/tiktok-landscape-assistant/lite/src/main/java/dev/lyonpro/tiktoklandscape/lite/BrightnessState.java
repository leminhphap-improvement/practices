package dev.lyonpro.tiktoklandscape.lite;

import android.content.Context;
import android.content.SharedPreferences;
import android.provider.Settings;

/** User-triggered brightness override. No AccessibilityService or TikTok inspection. */
final class BrightnessState {
    private static final String PREFS = "brightness_lite";
    private static final String ACTIVE = "active", SAVED = "saved", MODE = "old_mode", LEVEL = "old_level";
    private BrightnessState() {}
    private static SharedPreferences p(Context c) { return c.getSharedPreferences(PREFS, Context.MODE_PRIVATE); }
    static boolean granted(Context c) { return Settings.System.canWrite(c); }
    static boolean active(Context c) { return p(c).getBoolean(ACTIVE, false); }

    static boolean enable(Context c) {
        if (!granted(c)) return false;
        SharedPreferences pref=p(c);
        if (!pref.getBoolean(SAVED, false)) {
            int mode=Settings.System.getInt(c.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE,
                    Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
            int level=Settings.System.getInt(c.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, 128);
            if (!pref.edit().putInt(MODE, mode).putInt(LEVEL, level).putBoolean(SAVED,true).commit()) return false;
        }
        try {
            boolean okMode=Settings.System.putInt(c.getContentResolver(),
                    Settings.System.SCREEN_BRIGHTNESS_MODE, Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
            boolean okBrightness=Settings.System.putInt(c.getContentResolver(),
                    Settings.System.SCREEN_BRIGHTNESS, 255);
            if (!okMode || !okBrightness) { disable(c); return false; }
            pref.edit().putBoolean(ACTIVE,true).apply();
            return true;
        } catch (SecurityException e) { disable(c); return false; }
    }

    static boolean disable(Context c) {
        SharedPreferences pref=p(c);
        if (pref.getBoolean(SAVED,false)) {
            if (!granted(c)) return false;
            try {
                boolean restoredBrightness=Settings.System.putInt(c.getContentResolver(),
                        Settings.System.SCREEN_BRIGHTNESS, pref.getInt(LEVEL,128));
                boolean restoredMode=Settings.System.putInt(c.getContentResolver(),
                        Settings.System.SCREEN_BRIGHTNESS_MODE, pref.getInt(MODE,
                            Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL));
                if (!restoredBrightness || !restoredMode) return false;
            } catch (SecurityException e) { return false; }
        }
        pref.edit().putBoolean(ACTIVE,false).putBoolean(SAVED,false).apply();
        return true;
    }
}
