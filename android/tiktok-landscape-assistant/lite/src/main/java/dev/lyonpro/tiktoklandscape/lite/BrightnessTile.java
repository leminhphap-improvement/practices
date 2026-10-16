package dev.lyonpro.tiktoklandscape.lite;

import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;

/** A visible, user-triggered Quick Settings tile, never an overlay. */
public final class BrightnessTile extends TileService {
    @Override public void onStartListening() { super.onStartListening(); update(); }
    @Override public void onClick() {
        super.onClick();
        if (BrightnessState.granted(this)) {
            if (BrightnessState.active(this)) BrightnessState.disable(this);
            else BrightnessState.enable(this);
        }
        update();
    }
    private void update() {
        Tile tile=getQsTile();
        if(tile==null)return;
        tile.setState(BrightnessState.active(this)?Tile.STATE_ACTIVE:Tile.STATE_INACTIVE);
        tile.updateTile();
    }
}
