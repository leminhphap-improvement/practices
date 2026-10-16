package dev.lyonpro.tiktoklandscape.lite;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

/** Manual, user-controlled brightness-only mode. No background inspection. */
public final class MainActivity extends Activity {
    private TextView status;
    private Button toggle;

    @Override public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        LinearLayout layout=new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        int padding=(int)(20*getResources().getDisplayMetrics().density);
        layout.setPadding(padding,padding,padding,padding);
        TextView title=new TextView(this);
        title.setText("Landscape Brightness Lite");
        title.setTextSize(22);
        layout.addView(title);
        TextView details=new TextView(this);
        details.setText("Bản Lite an toàn quyền hạn: chỉ thay đổi độ sáng khi bạn chủ động bật bằng nút hoặc Quick Settings. " +
                "Không truy cập màn hình TikTok, không có Auto Next, không theo dõi xoay ngang, không có kết nối Internet. " +
                "Khi xem xong, chạm lại để khôi phục chế độ và độ sáng đã lưu.");
        details.setTextSize(16);
        details.setPadding(0,padding,0,padding);
        layout.addView(details);
        status=new TextView(this);
        status.setTextSize(16);
        layout.addView(status);
        Button permission=new Button(this);
        permission.setText("Cấp quyền Modify system settings");
        permission.setOnClickListener(v -> startActivity(new Intent(
                Settings.ACTION_MANAGE_WRITE_SETTINGS,Uri.parse("package:"+getPackageName()))));
        layout.addView(permission);
        toggle=new Button(this);
        toggle.setOnClickListener(v -> {
            if (!BrightnessState.granted(this)) {
                startActivity(new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS,
                        Uri.parse("package:"+getPackageName())));
                return;
            }
            if (BrightnessState.active(this)) BrightnessState.disable(this);
            else BrightnessState.enable(this);
            render();
        });
        layout.addView(toggle);
        TextView hint=new TextView(this);
        hint.setText("Quick Settings > Chỉnh sửa > thêm 'Fullscreen Brightness'. " +
                "Chạm tile khi bắt đầu/kết thúc xem. Ứng dụng không hiển thị overlay.");
        layout.addView(hint);
        setContentView(layout);
    }
    @Override protected void onResume() { super.onResume(); render(); }
    private void render() {
        if(status==null)return;
        status.setText("Modify system settings: "+(BrightnessState.granted(this)?"Đã cấp":"Chưa cấp")+
                "\nĐộ sáng 100%: "+(BrightnessState.active(this)?"ĐANG BẬT":"ĐANG TẮT"));
        toggle.setText(BrightnessState.active(this)?"Khôi phục độ sáng":"Bật độ sáng 100%");
    }
}
