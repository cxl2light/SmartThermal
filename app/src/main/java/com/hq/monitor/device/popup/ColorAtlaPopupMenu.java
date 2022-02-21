package com.hq.monitor.device.popup;

import android.content.Context;
import android.widget.PopupWindow;

import com.hq.base.util.ScreenUtils;
import com.hq.basebean.device.DeviceConfig;
import com.hq.monitor.R;
import com.hq.monitor.device.widget.ColourAtlaWidget;

public class ColorAtlaPopupMenu extends PopupWindow {

    private final ColourAtlaWidget colourAtlaWidget;

    public ColorAtlaPopupMenu(Context context) {
        super((int) context.getResources().getDimension(R.dimen.common_menu_popup_width_one),
                (int) (ScreenUtils.getScreenHeightPixels(context) * 1f));
        colourAtlaWidget = new ColourAtlaWidget(context);
        final int paddingH = (int) context.getResources().getDimension(R.dimen.common_menu_popup_padding_h);
        colourAtlaWidget.setPadding(paddingH, colourAtlaWidget.getPaddingTop(), paddingH, colourAtlaWidget.getPaddingBottom());
        setContentView(colourAtlaWidget);
        setBackgroundDrawable(context.getDrawable(R.drawable.common_round_rect_dark_bg));
        setOutsideTouchable(false);
    }

    public void setOnPaletteChange(ColourAtlaWidget.OnPaletteChange onPaletteChange) {
        colourAtlaWidget.setOnPaletteChange(onPaletteChange);
    }

    public void updateValue(DeviceConfig deviceConfig) {
        if (deviceConfig == null) {
            return;
        }
        colourAtlaWidget.updateValue(deviceConfig);
    }

}
