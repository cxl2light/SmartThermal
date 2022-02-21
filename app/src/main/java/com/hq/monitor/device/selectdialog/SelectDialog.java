package com.hq.monitor.device.selectdialog;

import android.os.Bundle;
import android.text.TextUtils;
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

import java.util.Locale;

public class SelectDialog extends DialogFragment {

    View dialogView;
    String mTitle;
    String mContent;
    OnSelectClickListener listener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        dialogView = inflater.inflate(R.layout.dialog_fragment_tip, null);
        return dialogView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

//       TextView tvOne = view.findViewById(R.id.tvDialogTitle);
//        tvOne.setText(mTitle);
//
        TextView tvContent = view.findViewById(R.id.tvContent);
        if (TextUtils.isEmpty(mContent)){
            tvContent.setVisibility(View.GONE);
        } else {
            tvContent.setText(mContent);
        }

        view.findViewById(R.id.tvOne).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClickPositive(SelectDialog.this);
            }
        });

        view.findViewById(R.id.tvTwo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClickNegative(SelectDialog.this);
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        //获取DisplayMetrics
        DisplayMetrics dm = getActivity().getResources().getDisplayMetrics();
        getDialog().getWindow().setLayout((int) (dm.widthPixels * 0.3), WindowManager.LayoutParams.WRAP_CONTENT);
        /**
         * https://blog.csdn.net/u011183394/article/details/51445202?utm_source=blogxgwz9
         */
        getDialog().getWindow().setBackgroundDrawableResource(android.R.color.transparent);
    }

    public void show(FragmentManager fragmentManager, Boolean isCancelable, String title, String content, OnSelectClickListener listener){
        setCancelable(isCancelable);
        mTitle = title;
        mContent = content;
        this.listener = listener;
        show(fragmentManager,"mDialogFragment");
    }

    private boolean isZh() {
        Locale locale = getResources().getConfiguration().locale;
        String language = locale.getLanguage();
        if (language.endsWith("zh"))
            return true;
        else
            return false;
    }
}
