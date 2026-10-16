package dev.lyonpro.tiktoklandscape;

import android.content.Context;
import android.content.SharedPreferences;
import android.provider.Settings;

/** Saves auto/manual mode plus level BEFORE setting the system brightness to maximum. */
final class BrightnessController {
    private final Context context;
    BrightnessController(Context context) { this.context=context.getApplicationContext(); }
    boolean canWrite() { return Settings.System.canWrite(context); }
    void maximize() {
        if (!canWrite()) return;
        SharedPreferences p=Prefs.get(context);
        if (!p.getBoolean(Prefs.SAVED,false)) {
            int mode=Settings.System.getInt(context.getContentResolver(),Settings.System.SCREEN_BRIGHTNESS_MODE,0);
            int level=Settings.System.getInt(context.getContentResolver(),Settings.System.SCREEN_BRIGHTNESS,128);
            if (!p.edit().putInt(Prefs.OLD_MODE,mode).putInt(Prefs.OLD_LEVEL,level).putBoolean(Prefs.SAVED,true).commit()) return;
        }
        try {
            Settings.System.putInt(context.getContentResolver(),Settings.System.SCREEN_BRIGHTNESS_MODE,Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
            Settings.System.putInt(context.getContentResolver(),Settings.System.SCREEN_BRIGHTNESS,255);
        } catch (SecurityException e) { restore(); }
    }
    void restore() {
        SharedPreferences p=Prefs.get(context);
        if (!canWrite() || !p.getBoolean(Prefs.SAVED,false)) return;
        try {
            Settings.System.putInt(context.getContentResolver(),Settings.System.SCREEN_BRIGHTNESS,p.getInt(Prefs.OLD_LEVEL,128));
            Settings.System.putInt(context.getContentResolver(),Settings.System.SCREEN_BRIGHTNESS_MODE,p.getInt(Prefs.OLD_MODE,0));
            p.edit().putBoolean(Prefs.SAVED,false).commit();
        } catch (SecurityException ignored) { /* Retry on next app start. */ }
    }
}
