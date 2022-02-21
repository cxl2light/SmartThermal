package com.hq.monitor.ui.dialog.versionupdate;

import androidx.fragment.app.DialogFragment;

public interface OnUpdateClickListener {
    void onClickPositive(DialogFragment dialogFragment);
    void onClickNegative(DialogFragment dialogFragment);
}
