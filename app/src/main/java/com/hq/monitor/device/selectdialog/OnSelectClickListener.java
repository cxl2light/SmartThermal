package com.hq.monitor.device.selectdialog;

import androidx.fragment.app.DialogFragment;

public interface OnSelectClickListener {
    void onClickPositive(DialogFragment dialogFragment);
    void onClickNegative(DialogFragment dialogFragment);
}
