package dev.lyonpro.tiktoklandscape;

/** Detect the transition from a nearly finished playback to a looped beginning. */
public final class EndDetector {
    private float previous=-1f;
    private boolean nearEnd=false;
    private long triggeredAt=-100000000L;
    public boolean observe(float progress, long nowMs) {
        if (Float.isNaN(progress) || progress<0f || progress>1f) return false;
        boolean loop=nearEnd && previous>=.85f && progress<=.15f;
        if (progress>=.90f) nearEnd=true;
        if (progress>=.20f && progress<.80f) nearEnd=false;
        previous=progress;
        if (loop && nowMs-triggeredAt>=2500) {
            nearEnd=false;
            triggeredAt=nowMs;
            return true;
        }
        return false;
    }
    public void reset() { previous=-1f;nearEnd=false; }
}
