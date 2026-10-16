package dev.lyonpro.tiktoklandscape;

/** Deterministic, fail-closed decision after an end-to-start playback loop.
 * Do not infer next episode availability or last episode from a generic swipe. */
public final class EpisodePolicy {
    public enum Action { CLICK_NEXT, SWIPE_OPT_IN, STOP_LAST, NO_SAFE_NEXT }
    private EpisodePolicy() {}
    public static Action afterLoop(EpisodeCounter counter, boolean nextButton, boolean swipeEnabled) {
        if (counter != null && counter.last()) return Action.STOP_LAST;
        if (nextButton) return Action.CLICK_NEXT;
        if (swipeEnabled) return Action.SWIPE_OPT_IN;
        return Action.NO_SAFE_NEXT;
    }
}
