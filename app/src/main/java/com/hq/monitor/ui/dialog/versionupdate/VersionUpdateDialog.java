package com.hq.monitor.ui.dialog.versionupdate;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import com.hq.monitor.R;
import com.hq.monitor.view.IOSLoadingView;

import java.util.Locale;

public class VersionUpdateDialog extends DialogFragment {

    View dialogView;
    int mContent;
    OnUpdateClickListener listener;
    int status = 0;
    TextView tvContent;
    TextView tvThree;
    LinearLayout llOne;
    IOSLoadingView loadingOne;
    ProgressBar pbOne;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        dialogView = inflater.inflate(R.layout.dialog_fragment_update, null);
        return dialogView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        llOne = view.findViewById(R.id.llOne);
        loadingOne = view.findViewById(R.id.loadingOne);
        pbOne = view.findViewById(R.id.pbOne);
        tvContent = view.findViewById(R.id.tvContent);
        tvThree = view.findViewById(R.id.tvThree);
        if (mContent < 0){
            tvContent.setVisibility(View.GONE);
        } else {
            tvContent.setText(mContent);
        }

        if (status == 1){
            setProgressing(R.string.about_update_pushing,0);
        }

        view.findViewById(R.id.tvOne).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClickPositive(VersionUpdateDialog.this);
            }
        });

        view.findViewById(R.id.tvTwo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissDialog();
            }
        });

        tvThree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                listener.onClickNegative(VersionUpdateDialog.this);
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        //获取DisplayMetrics
        DisplayMetrics dm = getActivity().getResources().getDisplayMetrics();
        getDialog().getWindow().setLayout((int) (dm.widthPixels * 0.4), WindowManager.LayoutParams.WRAP_CONTENT);
        /**
         * https://blog.csdn.net/u011183394/article/details/51445202?utm_source=blogxgwz9
         */
        getDialog().getWindow().setBackgroundDrawableResource(android.R.color.transparent);
    }

    public void show(FragmentManager fragmentManager, Boolean isCancelable, int content, OnUpdateClickListener listener){
        setCancelable(isCancelable);
        mContent = content;
        this.listener = listener;
        show(fragmentManager,"mDialogFragment");
    }

    public void show(FragmentManager fragmentManager, Boolean isCancelable,int content,int status, OnUpdateClickListener listener){
        setCancelable(isCancelable);
        mContent = content;
        this.listener = listener;
        this.status = status;
        show(fragmentManager,"mDialogFragment");
    }

    public void dismissDialog(){
        if (getDialog() != null){
            dismiss();
        }
    }

    public void setGotNewVersion(String version){
        if (getDialog() != null  && tvContent != null && loadingOne != null && llOne != null){
            tvContent.setText(getString(R.string.about_got_update,version));
            loadingOne.setVisibility(View.GONE);
            llOne.setVisibility(View.VISIBLE);
        }
    }

    public void setProgressing(int text, int progress){
        if (getDialog() != null  &&  tvContent != null && pbOne != null){
            tvContent.setText(getString(text,progress + "%"));
            pbOne.setProgress(progress);
            pbOne.setVisibility(View.VISIBLE);
            llOne.setVisibility(View.GONE);
            loadingOne.setVisibility(View.GONE);
        }
    }

    public void setLoading(int text){
        if (getDialog() != null  &&  tvContent != null && pbOne != null){
            tvContent.setText(getString(text));
            pbOne.setVisibility(View.GONE);
            llOne.setVisibility(View.GONE);
            tvThree.setVisibility(View.GONE);
            loadingOne.setVisibility(View.VISIBLE);
        }
    }

    public void setOneButton(int text){
        if (getDialog() != null  &&  tvContent != null && pbOne != null){
            Log.d("ZeUpgrade", "text1=" + getString(text));
            tvContent.setText(text);
            Log.d("ZeUpgrade", "text2=");
            tvThree.setVisibility(View.VISIBLE);
            Log.d("ZeUpgrade", "text3=");
            pbOne.setVisibility(View.GONE);
            Log.d("ZeUpgrade", "text4=");
            llOne.setVisibility(View.GONE);
            Log.d("ZeUpgrade", "text5=");
            loadingOne.setVisibility(View.GONE);
            Log.d("ZeUpgrade", "text6=");
        }
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
