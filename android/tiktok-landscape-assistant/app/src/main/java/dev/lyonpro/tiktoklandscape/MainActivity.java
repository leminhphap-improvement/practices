package dev.lyonpro.tiktoklandscape;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

/** Settings UI stays outside TikTok. No app-owned view overlays playback. */
public final class MainActivity extends Activity {
    private LinearLayout root;
    private TextView state;
    private EditText timer;
    private final BrightnessControllerHolder brightness=new BrightnessControllerHolder();
    private static class BrightnessControllerHolder { BrightnessController impl; }

    @Override public void onCreate(Bundle saved) {
        super.onCreate(saved);
        brightness.impl=new BrightnessController(this);
        ScrollView scroll=new ScrollView(this);
        root=new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        int pad=(int)(18*getResources().getDisplayMetrics().density);
        root.setPadding(pad,pad,pad,pad);
        scroll.addView(root);
        setContentView(scroll);

        header("TikTok Landscape Assistant");
        label("Chỉ hoạt động khi bật và TikTok ở màn hình ngang. Không đặt floating overlay lên video.");
        label("THÔNG BÁO QUYỀN NHẠY CẢM: Khi được bật, Accessibility Service có thể đọc nội dung điều khiển hiện trên TikTok (bao gồm tiến trình video, văn bản và nhãn nút) và thực hiện thao tác chạm/vuốt để thử chuyển tập. Android cấp quyền Accessibility rộng; hãy chỉ cho phép nếu bạn tin tưởng source. App không thu thập, lưu hay gửi nội dung đọc được lên mạng, không ghi màn hình. Quyền Modify system settings dùng để tăng và khôi phục độ sáng, độc lập với Accessibility. Bạn có thể tắt Auto Next hoặc thu hồi Accessibility bất cứ lúc nào.");
        check("Tôi đã đọc và đồng ý cho app dùng Accessibility để điều khiển TikTok khi màn hình ngang",Prefs.CONSENT,false);
        state=label("");
        button("1. Cấp quyền Accessibility",v->{
            if (!Prefs.consented(this)) {
                android.widget.Toast.makeText(this,"Đọc thông báo và tích đồng ý trước khi cấp Accessibility",android.widget.Toast.LENGTH_LONG).show();
                return;
            }
            startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS));
        });
        button("2. Cho phép thay đổi độ sáng",v->{
            Intent i=new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS,Uri.parse("package:"+getPackageName()));
            startActivity(i);
        });
        check("Bật Auto Next",Prefs.ENABLED,false);
        check("Max brightness khi xem ngang",Prefs.BRIGHTNESS,true);
        check("Cho phép vuốt khi không tìm thấy nút Next (có thể sang video khác)",Prefs.SWIPE,false);
        check("Dùng vuốt ngang thay vì vuốt lên",Prefs.HORIZONTAL,false);
        label("Timer dự phòng (giây). Để 0 nếu chưa biết thời lượng. Timer không đảm bảo đúng thời điểm kết thúc và có thể qua video sai.");
        timer=new EditText(this);
        timer.setInputType(InputType.TYPE_CLASS_NUMBER);
        timer.setSingleLine(true);
        timer.setText(String.valueOf(Prefs.get(this).getInt(Prefs.TIMER,0)));
        root.addView(timer);
        button("Lưu Timer",v->{
            int seconds=0;
            try { seconds=Integer.parseInt(timer.getText().toString().trim()); }catch(NumberFormatException ignored){}
            if (seconds!=0 && (seconds<15||seconds>7200)) { timer.setError("Nhập 0 hoặc 15-7200 giây");return; }
            Prefs.get(this).edit().putInt(Prefs.TIMER,seconds).apply();
            TikTokAccessibilityService svc=TikTokAccessibilityService.current();if(svc!=null)svc.refresh();
            status();
        });
        button("Khôi phục độ sáng ban đầu",v->{brightness.impl.restore();status();});
        label("Mở Quick Settings > Chỉnh sửa > kéo ô 'TikTok Auto Next' vào danh sách. Chạm ô này để bật/tắt không cần overlay.");
        label("Tự qua tập chỉ hoạt động khi TikTok expose thanh tiến trình hoặc khi bật timer. App thử nút Next trước, rồi mới vuốt nếu bạn cho phép. Không thể bảo đảm nhận biết tập cuối hay TikTok không hạn chế automation.");
    }
    private void header(String text) {
        TextView t=label(text);t.setTextSize(22);t.setPadding(0,0,0,16);
    }
    private TextView label(String text) {
        TextView t=new TextView(this);t.setText(text);t.setTextSize(15);t.setPadding(0,10,0,10);
        root.addView(t);return t;
    }
    private void button(String text, View.OnClickListener listener) {
        Button b=new Button(this);b.setText(text);b.setOnClickListener(listener);root.addView(b);
    }
    private void check(String text,String key,boolean fallback) {
        CheckBox cb=new CheckBox(this);cb.setText(text);
        cb.setChecked(key.equals(Prefs.ENABLED)?Prefs.enabled(this):Prefs.get(this).getBoolean(key,fallback));
        cb.setOnCheckedChangeListener((button, checked)->{
            if (key.equals(Prefs.ENABLED) && checked && !Prefs.consented(this)) {
                cb.setChecked(false);
                android.widget.Toast.makeText(this,"Cần đồng ý thông báo Accessibility trước khi bật Auto Next",android.widget.Toast.LENGTH_LONG).show();
                return;
            }
            Prefs.get(this).edit().putBoolean(key,checked).apply();
            if (key.equals(Prefs.CONSENT) && !checked) Prefs.enabled(this,false);
            if (key.equals(Prefs.ENABLED))Prefs.enabled(this,checked);
            if (!Prefs.enabled(this)||key.equals(Prefs.BRIGHTNESS)&&!checked)brightness.impl.restore();
            TikTokAccessibilityService svc=TikTokAccessibilityService.current();if(svc!=null)svc.refresh();
            status();
        });
        root.addView(cb);
    }
    @Override protected void onResume() { super.onResume();status(); }
    private void status() {
        if (state==null)return;
        state.setText("Auto Next: "+(Prefs.enabled(this)?"ON":"OFF")+
                " | Accessibility: "+(accessibilityEnabled()?"đã bật":"chưa bật")+
                " | Modify brightness: "+(brightness.impl.canWrite()?"đã cấp":"chưa cấp"));
    }
    private boolean accessibilityEnabled() {
        String enabled=Settings.Secure.getString(getContentResolver(),Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
        ComponentName component=new ComponentName(this,TikTokAccessibilityService.class);
        return enabled!=null&&enabled.toLowerCase(java.util.Locale.ROOT).contains(component.flattenToString().toLowerCase(java.util.Locale.ROOT));
    }
}
