package com.hq.monitor.device.versiontipdialog;

import android.content.res.Configuration;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import com.hq.monitor.R;
import com.hq.monitor.util.Constant;

public class VersionTipDialog extends DialogFragment {

    View dialogView;
    String mTitle;
    String mContent;
    OnClickVersionListener listener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        dialogView = inflater.inflate(R.layout.dialog_fragment_versiontip, null);
        return dialogView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView tvContent = view.findViewById(R.id.tvContent);
        tvContent.setText(getString(R.string.version_content, Constant.mDownLoadUrl));
        TextView tvOne = view.findViewById(R.id.tvOne);
        TextView tvTwo = view.findViewById(R.id.tvTwo);
        TextView tvThree = view.findViewById(R.id.tvThree);

        tvOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                  listener.onClickOne();
            }
        });
        tvTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClickTwo();
            }
        });
        tvThree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClickThree();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        //获取DisplayMetrics
        DisplayMetrics dm = getActivity().getResources().getDisplayMetrics();
        int orientation = getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE){
            getDialog().getWindow().setLayout((int) (dm.widthPixels * 0.5), WindowManager.LayoutParams.WRAP_CONTENT);
            /**
             * https://blog.csdn.net/u011183394/article/details/51445202?utm_source=blogxgwz9
             */
            getDialog().getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }
    }

    public void show(FragmentManager fragmentManager, Boolean isCancelable, String title, String content, OnClickVersionListener listener){
        setCancelable(isCancelable);
        mTitle = title;
        mContent = content;
        this.listener = listener;
        show(fragmentManager,"mDialogFragment");
    }
}
