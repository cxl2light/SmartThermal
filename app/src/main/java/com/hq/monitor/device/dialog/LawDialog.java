package com.hq.monitor.device.dialog;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.DisplayMetrics;
import android.util.Log;
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
import java.util.Objects;

public class LawDialog extends DialogFragment {

    View dialogView;
    String mTitle;
    String mContent;
    TextView tvTwo;
    OnClickListener listener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        dialogView = inflater.inflate(R.layout.dialog_fragment_laws, null);
        return dialogView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

       TextView tvOne = view.findViewById(R.id.tvDialogTitle);
        tvOne.setText(mTitle);
//        mVBinding.tvDialogOne.setText(mContent);

        tvTwo = view.findViewById(R.id.tvDialogOne);
        initSpannable();

        view.findViewById(R.id.tvPositive).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                ConnectDeviceActivity.startActivity(getActivity());
//                Objects.requireNonNull(getActivity()).finish();
                listener.onClickOne();
                dismiss();
            }
        });

        view.findViewById(R.id.tvNegative).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Objects.requireNonNull(getActivity()).finish();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        //获取DisplayMetrics
        DisplayMetrics dm = getActivity().getResources().getDisplayMetrics();
        getDialog().getWindow().setLayout((int) (dm.widthPixels * 0.85), WindowManager.LayoutParams.WRAP_CONTENT);
        /**
         * https://blog.csdn.net/u011183394/article/details/51445202?utm_source=blogxgwz9
         */
        getDialog().getWindow().setBackgroundDrawableResource(android.R.color.transparent);
    }

    public void show(FragmentManager fragmentManager, Boolean isCancelable, String title, String content, OnClickListener listener){
        setCancelable(isCancelable);
        mTitle = title;
        mContent = content;
        this.listener = listener;
        show(fragmentManager,"mDialogFragment");
    }

    private void initSpannable() {
        int start = 62;
        int end = 74;
        Log.d("initSpannable","initSpannable=" + isZh());
        if (!isZh()){
            start = 60;
            end = 78;
        }
        SpannableString spannableString = new SpannableString(mContent);
        //设置颜色
        spannableString.setSpan(new ForegroundColorSpan(requireContext().getResources().getColor(R.color.black)), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        //文本可点击，有点击事件
        spannableString.setSpan(new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                Uri uri = Uri.parse("http://qinkung.com/index/index/protocol");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                super.updateDrawState(ds);
            }
        },start, end,Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        // 设置此方法后，点击事件才能生效
        tvTwo.setMovementMethod(LinkMovementMethod.getInstance());
        tvTwo.setText(spannableString);
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
