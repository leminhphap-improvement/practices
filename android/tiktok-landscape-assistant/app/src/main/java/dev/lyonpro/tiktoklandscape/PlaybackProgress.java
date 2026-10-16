package dev.lyonpro.tiktoklandscape;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Strict fallback for a single visible player label such as "00:41 / 02:05".
 * Other timestamps in comments or independent nodes are NOT combined. */
public final class PlaybackProgress {
    private static final Pattern TIMER=Pattern.compile(
        "(?<!\\d)(\\d{1,2}:\\d{2}(?::\\d{2})?)\\s*/\\s*(\\d{1,2}:\\d{2}(?::\\d{2})?)(?!\\d)");
    private PlaybackProgress() {}

    private static int seconds(String value) {
        String[] parts=value.split(":");
        if(parts.length!=2 && parts.length!=3)return -1;
        int total=0;
        for(int i=0;i<parts.length;i++) {
            int n=Integer.parseInt(parts[i]);
            if(i>0 && n>=60) return -1;
            total=total*60+n;
        }
        return total;
    }
    public static Float fromVisibleLabel(CharSequence label) {
        if(label==null||label.length()>120)return null;
        Matcher m=TIMER.matcher(label);
        if(!m.find())return null;
        int elapsed=seconds(m.group(1));
        int duration=seconds(m.group(2));
        if(duration<10 || duration>86400 || elapsed<0 || elapsed>duration) return null;
        return (float)elapsed/duration;
    }
}
