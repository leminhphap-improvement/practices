package dev.lyonpro.tiktoklandscape;

import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;

public class QuickTileService extends TileService {
    @Override public void onStartListening() { super.onStartListening(); update(); }
    @Override public void onClick() {
        super.onClick();
        boolean enabled=!Prefs.enabled(this);
        Prefs.enabled(this,enabled);
        if (!enabled) new BrightnessController(this).restore();
        TikTokAccessibilityService service=TikTokAccessibilityService.current();
        if (service!=null) service.refresh();
        update();
    }
    private void update() {
        Tile tile=getQsTile();
        if (tile==null) return;
        tile.setState(Prefs.enabled(this)?Tile.STATE_ACTIVE:Tile.STATE_INACTIVE);
        tile.updateTile();
    }
}
