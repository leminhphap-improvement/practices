package dev.lyonpro.tiktoklandscape;

import android.content.Context;
import android.content.SharedPreferences;

final class Prefs {
    static final String CONSENT="consent_accessibility", ENABLED="enabled", BRIGHTNESS="brightness", SWIPE="swipe", HORIZONTAL="horizontal", TIMER="timer_seconds", SAVED="saved_brightness", OLD_MODE="old_mode", OLD_LEVEL="old_level";
    private Prefs() {}
    static SharedPreferences get(Context context) { return context.getSharedPreferences("preferences", Context.MODE_PRIVATE); }
    static boolean consented(Context c) { return get(c).getBoolean(CONSENT,false); }
    static boolean enabled(Context c) { return consented(c) && get(c).getBoolean(ENABLED,false); }
    static void enabled(Context c, boolean enabled) { get(c).edit().putBoolean(ENABLED,enabled && consented(c)).apply(); }
}
