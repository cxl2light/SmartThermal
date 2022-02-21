package com.hq.base.widget;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.core.graphics.drawable.DrawableCompat;

import com.hq.base.R;
import com.hq.base.util.ScreenUtils;

/**
 * Created on 2020/4/5.
 * author :
 * desc :
 */
public class CommonTitleBar extends LinearLayoutCompat implements View.OnClickListener {
    private AppCompatTextView titleTv,titleTvCenter, menuTv,shareTv;
    private AppCompatImageView backIcon, closeIcon, menuIcon,ivShare,ivDelete;

    final View backBtnLayout;

    public CommonTitleBar(Context context) {
        this(context, null);
    }

    public CommonTitleBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CommonTitleBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOrientation(VERTICAL);
        LayoutInflater.from(context).inflate(R.layout.widget_common_title_bar, this);
        setPadding(getPaddingLeft(), ScreenUtils.getStatusHeight(), getPaddingRight(), getPaddingBottom());
//        setBackgroundResource(R.color.background_common_title);
        backBtnLayout = findViewById(R.id.back_btn_layout);
        backBtnLayout.setOnClickListener(this);
        final View closeBtnLayout = findViewById(R.id.close_btn_layout);
        closeBtnLayout.setOnClickListener(this);
        titleTv = findViewById(R.id.title_tv);
        titleTvCenter = findViewById(R.id.title_tv_center);
        shareTv = findViewById(R.id.menu_share);
        menuTv = findViewById(R.id.menu_text);
        backIcon = findViewById(R.id.back_icon);
        closeIcon = findViewById(R.id.close_icon);
        menuIcon = findViewById(R.id.menu_icon);

        ivShare = findViewById(R.id.ivShare);
        ivDelete = findViewById(R.id.ivDelete);
        initAttr(attrs);
    }

    private void initAttr(@Nullable AttributeSet attrs) {
        if (attrs == null) {
            return;
        }
        final TypedArray array = getContext().obtainStyledAttributes(attrs, R.styleable.CommonTitleBar);
        titleTv.setText(array.getString(R.styleable.CommonTitleBar_widget_attr_title));
        array.recycle();
    }

    @Override
    public void onClick(View v) {
        final Context context = getContext();
        if (v.getId() == R.id.back_btn_layout) {
            if (context instanceof Activity) {
                ((Activity) context).finish();
            }
            return;
        }
        if (v.getId() == R.id.close_btn_layout) {
            if (context instanceof Activity) {
                ((Activity) context).finish();
            }
        }
    }

    public void setTitle(String title) {
        titleTv.setText(title);
    }

    public void setMenu(String menu) {
        menuTv.setText(menu);
        menuTv.setVisibility(VISIBLE);
    }

    public void setShareTv(String text) {
        shareTv.setText(text);
        shareTv.setVisibility(VISIBLE);
    }

    public void setTitleTvCenter(String text) {
        titleTvCenter.setText(text);
        titleTvCenter.setVisibility(VISIBLE);
    }

    public void setImageShareAndClick(int image,OnClickListener onClickListener) {
        ivShare.setImageResource(image);
        ivShare.setVisibility(VISIBLE);
        ivShare.setOnClickListener(onClickListener);
    }

    public void setMenuClick(OnClickListener onClickListener) {
        menuTv.setOnClickListener(onClickListener);
    }

    public void setMenuShareClick(OnClickListener onClickListener) {
        shareTv.setOnClickListener(onClickListener);
    }

    public void setImageShareClick(OnClickListener onClickListener) {
        ivShare.setVisibility(VISIBLE);
        ivShare.setOnClickListener(onClickListener);
    }

    public void setImageDeleteClick(OnClickListener onClickListener) {
        ivDelete.setVisibility(VISIBLE);
        ivDelete.setOnClickListener(onClickListener);
    }

    public void setIconTintColor(@ColorRes int colorId) {
        try {
            final int color = getResources().getColor(colorId, null);
            tintDrawable(backIcon.getDrawable(), color);
            tintDrawable(closeIcon.getDrawable(), color);
            tintDrawable(menuIcon.getDrawable(), color);
        } catch (Exception ignore) {

        }
    }

    public View getBackBtnLayout() {
        return backBtnLayout;
    }

    public void setIvBackVisibility(int visibility) {
        backIcon.setVisibility(visibility);
    }

    public void setMenuTextColor(@ColorRes int colorId) {
        menuTv.setTextColor(getResources().getColor(colorId, null));
    }

    public void setShareTvTextColor(@ColorRes int colorId) {
        shareTv.setTextColor(getResources().getColor(colorId, null));
    }

    public void setTitleTextColor(@ColorRes int colorId) {
        titleTv.setTextColor(getResources().getColor(colorId, null));
    }

    private void tintDrawable(Drawable drawable, @ColorInt int color) {
        if (drawable == null) {
            return;
        }
        drawable.mutate();
        final Drawable wrappedDrawable = DrawableCompat.wrap(drawable);
        DrawableCompat.setTint(wrappedDrawable, color);
    }

}
