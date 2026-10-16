package dev.lyonpro.tiktoklandscape;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Recognises an explicit visible player counter such as "Tập 3/12" or "Episode 3 of 12".
 * Absence of a recognised counter is UNKNOWN, never interpreted as the last episode. */
public final class EpisodeCounter {
    private static final Pattern COUNTER = Pattern.compile(
        "(?:^|[^\\p{L}\\d])(?:tập|episode|ep\\.?|part)\\s*[:#-]?\\s*(\\d{1,3})\\s*(?:/|of|trên)\\s*(\\d{1,3})(?!\\d)",
        Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
    public final int current;
    public final int total;
    private EpisodeCounter(int current, int total) { this.current=current; this.total=total; }
    public boolean last() { return current == total; }
    public static EpisodeCounter parse(CharSequence text) {
        if (text == null || text.length() > 600) return null;
        Matcher m=COUNTER.matcher(text);
        if (!m.find()) return null;
        int current=Integer.parseInt(m.group(1));
        int total=Integer.parseInt(m.group(2));
        if (current < 1 || total < 1 || total > 500 || current > total) return null;
        return new EpisodeCounter(current, total);
    }
    public boolean same(EpisodeCounter other) {
        return other != null && current == other.current && total == other.total;
    }
}
