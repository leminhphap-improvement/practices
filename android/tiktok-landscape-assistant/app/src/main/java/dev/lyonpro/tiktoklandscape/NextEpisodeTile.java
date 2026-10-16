package dev.lyonpro.tiktoklandscape;

import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;
import android.widget.Toast;

/**
 * User-driven Next Episode Quick Settings control.
 * The request is queued until the shade closes and TikTok becomes the active window.
 * If TikTok offers no Next Episode action, fail closed (unless user opted into fallback swipe).
 */
public final class NextEpisodeTile extends TileService {
    @Override public void onStartListening() { super.onStartListening(); update(); }
    @Override public void onClick() {
        super.onClick();
        TikTokAccessibilityService service=TikTokAccessibilityService.current();
        if (!Prefs.enabled(this) || service==null) {
            Toast.makeText(this,"Bật Auto Next và quyền Accessibility trong app trước",Toast.LENGTH_SHORT).show();
        } else {
            service.requestManualNext();
            Toast.makeText(this,"Đóng Quick Settings để gửi yêu cầu Next",Toast.LENGTH_SHORT).show();
        }
        update();
    }
    private void update() {
        Tile tile=getQsTile();
        if (tile==null) return;
        tile.setState(Prefs.enabled(this)?Tile.STATE_ACTIVE:Tile.STATE_INACTIVE);
        tile.updateTile();
    }
}
