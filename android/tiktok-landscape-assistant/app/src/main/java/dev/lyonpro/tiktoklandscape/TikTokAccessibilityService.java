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

/** Optional TikTok-specific controls. No overlay, network or screen recording. */
public final class TikTokAccessibilityService extends AccessibilityService {
    private static TikTokAccessibilityService instance;
    static TikTokAccessibilityService current() { return instance; }
    private final Handler handler=new Handler(Looper.getMainLooper());
    private final EndDetector detector=new EndDetector();
    private BrightnessController brightness;
    private String activePackage="";
    private long startedAt=0, lastNext=0, firstMissing=0;
    private boolean running=false;
    private final Runnable tick=new Runnable() {
        @Override public void run() {
            check();
            if (running) handler.postDelayed(this,Prefs.enabled(TikTokAccessibilityService.this)?500:2500);
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
    @Override public void onInterrupt() { leave(); }
    @Override public void onDestroy() {
        running=false;handler.removeCallbacks(tick);leave();instance=null;super.onDestroy();
    }
    @Override public void onConfigurationChanged(Configuration c) { super.onConfigurationChanged(c);refresh(); }
    void refresh() { handler.removeCallbacks(tick);if (running)handler.post(tick); }
    private static boolean isTikTok(String pkg) {
        return "com.zhiliaoapp.musically".equals(pkg)||"com.ss.android.ugc.trill".equals(pkg);
    }
    private void check() {
        if (!Prefs.enabled(this)||getResources().getConfiguration().orientation!=Configuration.ORIENTATION_LANDSCAPE) { leave();return; }
        AccessibilityNodeInfo root=getRootInActiveWindow();
        if (root==null||root.getPackageName()==null||!isTikTok(root.getPackageName().toString())) {
            if (firstMissing==0) firstMissing=SystemClock.elapsedRealtime();
            if (SystemClock.elapsedRealtime()-firstMissing>1250) leave();
            return;
        }
        firstMissing=0;
        if (Prefs.get(this).getBoolean(Prefs.BRIGHTNESS,true))brightness.maximize();else brightness.restore();
        long now=SystemClock.elapsedRealtime();
        String pkg=root.getPackageName().toString();
        if (!pkg.equals(activePackage)) { activePackage=pkg;startedAt=now;detector.reset(); }
        Float position=progress(root);
        if (position!=null) {
            startedAt=now;
            if (detector.observe(position,now)) next(root);
        } else {
            int seconds=Prefs.get(this).getInt(Prefs.TIMER,0);
            if (seconds>=15&&seconds<=7200&&now-startedAt>=seconds*1000L) {
                startedAt=now;
                next(root);
            }
        }
    }
    private void leave() {
        firstMissing=0;activePackage="";startedAt=0;detector.reset();
        if (brightness!=null)brightness.restore();
    }
    private static boolean progressLabel(AccessibilityNodeInfo node) {
        String type=String.valueOf(node.getClassName()).toLowerCase(Locale.ROOT);
        String label=String.valueOf(node.getContentDescription()).toLowerCase(Locale.ROOT);
        String id=String.valueOf(node.getViewIdResourceName()).toLowerCase(Locale.ROOT);
        return type.contains("seekbar")||label.contains("progress")||label.contains("tiến trình")||id.contains("seek")||id.contains("progress");
    }
    private Float progress(AccessibilityNodeInfo root) {
        ArrayDeque<AccessibilityNodeInfo> nodes=new ArrayDeque<>();nodes.add(root);
        int scanned=0;
        while (!nodes.isEmpty()&&scanned++<400) {
            AccessibilityNodeInfo node=nodes.remove();
            AccessibilityNodeInfo.RangeInfo range=node.getRangeInfo();
            if (range!=null&&range.getMax()>range.getMin()&&progressLabel(node)) {
                float value=(range.getCurrent()-range.getMin())/(range.getMax()-range.getMin());
                if (value>=0&&value<=1)return value;
            }
            for (int i=0;i<node.getChildCount();i++) {
                AccessibilityNodeInfo child=node.getChild(i);if (child!=null)nodes.add(child);
            }
        }
        return null;
    }
    private AccessibilityNodeInfo episodeNext(AccessibilityNodeInfo root) {
        ArrayDeque<AccessibilityNodeInfo> nodes=new ArrayDeque<>();nodes.add(root);
        int scanned=0;
        while (!nodes.isEmpty()&&scanned++<500) {
            AccessibilityNodeInfo node=nodes.remove();
            String text=(String.valueOf(node.getText())+" "+String.valueOf(node.getContentDescription())).toLowerCase(Locale.ROOT);
            if (text.contains("tập tiếp")||text.contains("tập kế tiếp")||text.contains("next episode")||text.contains("next part"))return node;
            for(int i=0;i<node.getChildCount();i++) { AccessibilityNodeInfo child=node.getChild(i);if(child!=null)nodes.add(child); }
        }
        return null;
    }
    private boolean click(AccessibilityNodeInfo node) {
        for(int i=0;node!=null&&i<5;i++,node=node.getParent()) {
            if (node.isClickable()&&node.performAction(AccessibilityNodeInfo.ACTION_CLICK))return true;
        }
        return false;
    }
    private void next(AccessibilityNodeInfo root) {
        long now=SystemClock.elapsedRealtime();
        if (now-lastNext<2500)return;
        AccessibilityNodeInfo control=episodeNext(root);
        if (control!=null&&click(control)) { lastNext=now;detector.reset();startedAt=now;return; }
        // A swipe might navigate to an unrelated video. Explicit opt-in only.
        if (!Prefs.get(this).getBoolean(Prefs.SWIPE,false))return;
        DisplayMetrics metrics=new DisplayMetrics();
        ((WindowManager)getSystemService(WINDOW_SERVICE)).getDefaultDisplay().getRealMetrics(metrics);
        boolean horizontal=Prefs.get(this).getBoolean(Prefs.HORIZONTAL,false);
        Path path=new Path();
        if(horizontal){path.moveTo(metrics.widthPixels*.82f,metrics.heightPixels*.5f);path.lineTo(metrics.widthPixels*.18f,metrics.heightPixels*.5f);}
        else{path.moveTo(metrics.widthPixels*.5f,metrics.heightPixels*.8f);path.lineTo(metrics.widthPixels*.5f,metrics.heightPixels*.2f);}
        GestureDescription action=new GestureDescription.Builder().addStroke(new GestureDescription.StrokeDescription(path,0,300)).build();
        dispatchGesture(action,null,null);
        lastNext=now;detector.reset();startedAt=now;
    }
}
