package dev.lyonpro.tiktoklandscape;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.GestureDescription;
import android.content.res.Configuration;
import android.graphics.Path;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import java.util.ArrayDeque;
import java.util.Locale;

/**
 * TikTok-only, landscape-only deterministic Accessibility automation.
 * Source title, account details, and AccessibilityNodeInfo data never leave process memory.
 * Playback automation only acts on an observed end-to-start loop or explicit user request.
 */
public final class TikTokAccessibilityService extends AccessibilityService {
    private static TikTokAccessibilityService instance;
    static TikTokAccessibilityService current() { return instance; }

    private final Handler handler=new Handler(Looper.getMainLooper());
    private final EndDetector detector=new EndDetector();
    private BrightnessController brightness;
    private String activePackage="";
    private long startedAt=0, lastNext=0, firstMissing=0, pendingManualUntil=0;
    private String lastAction="none";
    private boolean running=false;
    private static final long MANUAL_REQUEST_MS=10000L;

    private static final class Scan {
        Float progress;
        AccessibilityNodeInfo next;
        AccessibilityNodeInfo pause;
        EpisodeCounter episode;
        boolean counterConflict;
    }

    private final Runnable tick=new Runnable() {
        @Override public void run() {
            check();
            if (running) handler.postDelayed(this, Prefs.enabled(TikTokAccessibilityService.this)?500:2500);
        }
    };

    @Override protected void onServiceConnected() {
        super.onServiceConnected();
        instance=this;
        brightness=new BrightnessController(this);
        running=true;
        refresh();
    }
    @Override public void onAccessibilityEvent(AccessibilityEvent e) { refresh(); }
    @Override public void onInterrupt() { leave("INTERRUPTED"); }
    @Override public void onDestroy() {
        running=false;handler.removeCallbacks(tick);leave("SERVICE_STOPPED");instance=null;super.onDestroy();
    }
    @Override public void onConfigurationChanged(Configuration c) { super.onConfigurationChanged(c);refresh(); }
    void refresh() { handler.removeCallbacks(tick);if (running)handler.post(tick); }

    /** Triggered by the user's separate Quick Settings Next tile. Wait until shade is dismissed. */
    void requestManualNext() {
        if (!Prefs.enabled(this)) return;
        pendingManualUntil=SystemClock.elapsedRealtime()+MANUAL_REQUEST_MS;
        lastAction="USER_REQUEST_PENDING";
        refresh();
    }

    private static boolean isTikTok(String pkg) {
        return "com.zhiliaoapp.musically".equals(pkg)||"com.ss.android.ugc.trill".equals(pkg);
    }

    private void check() {
        if (!Prefs.enabled(this) || getResources().getConfiguration().orientation!=Configuration.ORIENTATION_LANDSCAPE) {
            leave("INACTIVE_OR_PORTRAIT");
            return;
        }
        AccessibilityNodeInfo root=getRootInActiveWindow();
        if (root==null || root.getPackageName()==null || !isTikTok(root.getPackageName().toString())) {
            if (firstMissing==0) firstMissing=SystemClock.elapsedRealtime();
            if (SystemClock.elapsedRealtime()-firstMissing>1250) leave("NOT_TIKTOK");
            return;
        }
        firstMissing=0;
        if (Prefs.get(this).getBoolean(Prefs.BRIGHTNESS,true)) brightness.maximize();
        else brightness.restore();

        long now=SystemClock.elapsedRealtime();
        String pkg=root.getPackageName().toString();
        if (!pkg.equals(activePackage)) {
            activePackage=pkg;startedAt=now;detector.reset();lastAction="PLAYER_OPEN";
        }

        Scan scan;
        try { scan=scan(root); }
        catch (RuntimeException staleTree) {
            ProbeState.record(this,"PLAYER_TREE_UNAVAILABLE",false,false,null,lastAction);
            return;
        }

        if (scan.progress!=null) startedAt=now;
        boolean loop=scan.progress!=null && detector.observe(scan.progress,now);
        boolean manual=pendingManualUntil>0 && now<=pendingManualUntil;
        if (pendingManualUntil>0 && now>pendingManualUntil) {
            pendingManualUntil=0;lastAction="MANUAL_NEXT_TIMED_OUT";
        }
        if (manual) {
            pendingManualUntil=0; // one user request, one attempt
            attemptNext(scan,now,"manual");
        } else if (loop) {
            attemptNext(scan,now,"loop");
        } else if (scan.progress==null) {
            // Fallback timer is OFF by default and does not prove playback end or episode order.
            int seconds=Prefs.get(this).getInt(Prefs.TIMER,0);
            if (seconds>=15 && seconds<=7200 && now-startedAt>=seconds*1000L) {
                startedAt=now;attemptNext(scan,now,"timer");
            }
        }
        String status=scan.progress!=null?"PROGRESS_VISIBLE":"PROGRESS_NOT_EXPOSED";
        if (scan.counterConflict) status="EPISODE_COUNTER_AMBIGUOUS";
        ProbeState.record(this,status,scan.progress!=null,scan.next!=null,scan.episode,lastAction);
    }

    private void leave(String reason) {
        firstMissing=0;activePackage="";startedAt=0;detector.reset();
        if (brightness!=null) brightness.restore();
        ProbeState.record(this,reason,false,false,null,lastAction);
    }

    private static boolean progressLabel(AccessibilityNodeInfo node) {
        String type=String.valueOf(node.getClassName()).toLowerCase(Locale.ROOT);
        String label=String.valueOf(node.getContentDescription()).toLowerCase(Locale.ROOT);
        String id=String.valueOf(node.getViewIdResourceName()).toLowerCase(Locale.ROOT);
        return type.contains("seekbar")||label.contains("progress")||
                label.contains("tiến trình")||id.contains("seek")||id.contains("progress");
    }
    private static boolean nextLabel(String lower) {
        return lower.contains("tập tiếp")||lower.contains("tập kế tiếp")||
                lower.contains("next episode")||lower.contains("next part");
    }
    private static boolean pauseLabel(String lower) {
        return lower.equals("pause")||lower.contains("pause playback")||
                lower.equals("tạm dừng")||lower.equals("dừng phát");
    }

    /** One bounded BFS per poll. Counter is UNKNOWN if multiple conflicting counters are visible. */
    private static Scan scan(AccessibilityNodeInfo root) {
        Scan out=new Scan();
        ArrayDeque<AccessibilityNodeInfo> nodes=new ArrayDeque<>();
        nodes.add(root);
        int scanned=0;
        while (!nodes.isEmpty() && scanned++<500) {
            AccessibilityNodeInfo node=nodes.remove();
            if (node.isVisibleToUser()) {
                AccessibilityNodeInfo.RangeInfo range=node.getRangeInfo();
                if (out.progress==null && range!=null && range.getMax()>range.getMin() && progressLabel(node)) {
                    float value=(range.getCurrent()-range.getMin())/(range.getMax()-range.getMin());
                    if (value>=0 && value<=1) out.progress=value;
                }
                CharSequence t=node.getText(), c=node.getContentDescription();
                String label=(String.valueOf(t)+" "+String.valueOf(c)).toLowerCase(Locale.ROOT);
                if (out.next==null && nextLabel(label)) out.next=node;
                if (out.pause==null && pauseLabel(label.trim())) out.pause=node;
                EpisodeCounter candidate=EpisodeCounter.parse(label);
                if (candidate!=null) {
                    if (out.episode!=null && !out.episode.same(candidate)) out.counterConflict=true;
                    else if (out.episode==null) out.episode=candidate;
                }
            }
            for(int i=0;i<node.getChildCount();i++) {
                AccessibilityNodeInfo child=node.getChild(i);
                if (child!=null) nodes.add(child);
            }
        }
        if (out.counterConflict) out.episode=null;
        return out;
    }

    private static boolean click(AccessibilityNodeInfo node) {
        for(int i=0;node!=null && i<5;i++,node=node.getParent()) {
            if (node.isVisibleToUser() && node.isEnabled() &&
                node.isClickable() && node.performAction(AccessibilityNodeInfo.ACTION_CLICK)) return true;
        }
        return false;
    }

    private void attemptNext(Scan scan,long now,String trigger) {
        if (now-lastNext<2500) { lastAction="COOLDOWN";return; }
        boolean allowSwipe=Prefs.get(this).getBoolean(Prefs.SWIPE,false);
        EpisodePolicy.Action action=EpisodePolicy.afterLoop(scan.episode,scan.next!=null,allowSwipe);
        switch (action) {
            case STOP_LAST:
                lastAction=click(scan.pause)?"LAST_EPISODE_PAUSED":"LAST_EPISODE_DETECTED_PAUSE_NOT_AVAILABLE";
                lastNext=now;
                detector.reset();
                return;
            case CLICK_NEXT:
                if (click(scan.next)) {
                    lastNext=now;startedAt=now;detector.reset();
                    lastAction=trigger.equals("manual")?"USER_NEXT_CLICK_SENT":"NEXT_CLICK_SENT_UNVERIFIED";
                    return;
                }
                lastAction="NEXT_BUTTON_NOT_CLICKABLE";
                return;
            case SWIPE_OPT_IN:
                if (dispatchConfiguredGesture()) {
                    lastNext=now;startedAt=now;detector.reset();
                    lastAction="OPT_IN_SWIPE_SENT_UNVERIFIED";
                } else lastAction="SWIPE_REJECTED";
                return;
            default:
                lastAction="NO_SAFE_NEXT_CONTROL";
        }
    }

    private boolean dispatchConfiguredGesture() {
        DisplayMetrics metrics=new DisplayMetrics();
        ((WindowManager)getSystemService(WINDOW_SERVICE)).getDefaultDisplay().getRealMetrics(metrics);
        boolean horizontal=Prefs.get(this).getBoolean(Prefs.HORIZONTAL,false);
        Path path=new Path();
        if (horizontal) {
            path.moveTo(metrics.widthPixels*.82f,metrics.heightPixels*.5f);
            path.lineTo(metrics.widthPixels*.18f,metrics.heightPixels*.5f);
        } else {
            path.moveTo(metrics.widthPixels*.5f,metrics.heightPixels*.8f);
            path.lineTo(metrics.widthPixels*.5f,metrics.heightPixels*.2f);
        }
        GestureDescription gesture=new GestureDescription.Builder()
                .addStroke(new GestureDescription.StrokeDescription(path,0,300)).build();
        return dispatchGesture(gesture,null,null);
    }
}
