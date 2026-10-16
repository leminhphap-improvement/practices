package dev.lyonpro.tiktoklandscape;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Local, minimal diagnostic snapshot. No player text, video URLs, title,
 * account information, screenshot, or accessibility node content is stored.
 */
final class ProbeState {
    private static final String KEY_STATUS="probe_status";
    private static final String KEY_PROGRESS="probe_progress";
    private static final String KEY_NEXT="probe_next";
    private static final String KEY_COUNTER="probe_counter";
    private static final String KEY_ACTION="probe_action";
    private static final String KEY_TIME="probe_time";
    private static String previous="";
    private static long lastStoredAt=0;
    private static String previousExit="";
    private ProbeState() {}

    static void record(Context context, String status, boolean progress,
                       boolean next, EpisodeCounter episode, String action) {
        int n=episode==null?0:episode.current;
        int t=episode==null?0:episode.total;
        String signature=status+"|"+progress+"|"+next+"|"+n+"/"+t+"|"+action;
        long now=System.currentTimeMillis();
        if (signature.equals(previous) && now-lastStoredAt<10000) return; // throttle unchanged state
        previous=signature;
        lastStoredAt=now;
        Prefs.get(context).edit()
                .putString(KEY_STATUS,status)
                .putBoolean(KEY_PROGRESS,progress)
                .putBoolean(KEY_NEXT,next)
                .putString(KEY_COUNTER,n==0?"unknown":n+"/"+t)
                .putString(KEY_ACTION,action)
                .putLong(KEY_TIME,System.currentTimeMillis())
                .apply();
    }

    /** Preserve the last active TikTok snapshot when the user opens Settings. */
    static void markInactive(Context context,String reason) {
        if (reason.equals(previousExit)) return;
        previousExit=reason;
        Prefs.get(context).edit().putString("probe_exit",reason)
                .putLong("probe_exit_time",System.currentTimeMillis()).apply();
    }

    static String summary(Context context) {
        SharedPreferences p=Prefs.get(context);
        long timestamp=p.getLong(KEY_TIME,0);
        if (timestamp==0) return "Chưa quét được player TikTok ngang.";
        StringBuilder s=new StringBuilder();
        s.append("Scan: ").append(p.getString(KEY_STATUS,"unknown"));
        s.append("\nProgress RangeInfo: ").append(p.getBoolean(KEY_PROGRESS,false)?"YES":"NO");
        s.append("\nNút Next Episode: ").append(p.getBoolean(KEY_NEXT,false)?"YES":"NO");
        s.append("\nTập / Tổng: ").append(p.getString(KEY_COUNTER,"unknown"));
        s.append("\nHành động: ").append(p.getString(KEY_ACTION,"none"));
        s.append("\nThời điểm: ").append(new java.text.SimpleDateFormat("HH:mm:ss dd/MM",java.util.Locale.getDefault()).format(new java.util.Date(timestamp)));
        s.append("\\nRời player: ").append(p.getString("probe_exit","unknown"));
        return s.toString();
    }
}
