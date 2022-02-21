package com.hq.monitor.device.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.LinearLayoutCompat;

import com.hq.monitor.R;
import com.hq.monitor.util.SpUtils;

/**
 * @author Administrator
 * @date 2022/2/11 0011 10:15
 */
public class AlarmSetWidget extends LinearLayoutCompat implements View.OnClickListener {

    private TextView textOne, textTwo, textThree;

    private TextView textSelected = null;

    private OnOptionChange mOnOptionChange;

    public AlarmSetWidget(@NonNull Context context) {
        this(context, null);
    }

    public AlarmSetWidget(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOrientation(VERTICAL);
        setGravity(Gravity.CENTER_HORIZONTAL);
        LayoutInflater.from(context).inflate(R.layout.widget_alarm_set_pop_layout, this);
        textOne = findViewById(R.id.text_one);
        textOne.setOnClickListener(this);
        textTwo = findViewById(R.id.text_two);
        textTwo.setOnClickListener(this);
        textThree = findViewById(R.id.text_three);
        textThree.setOnClickListener(this);

        int POP_TYPE = SpUtils.getInt(context,SpUtils.ALARM_SET_POP_TYPE, 0);
        if (POP_TYPE == SpUtils.ALARM_SAVE_TIME){
            textOne.setText(getResources().getString(R.string.detection_alarm_save_one_day));
            textTwo.setText(getResources().getString(R.string.detection_alarm_save_a_week));
            textThree.setText(getResources().getString(R.string.detection_alarm_save_one_month));
        }
        else if (POP_TYPE == SpUtils.ALARM_INTERVAL){
            textOne.setText(getResources().getString(R.string.detection_alarm_interval_1_minute));
            textTwo.setText(getResources().getString(R.string.detection_alarm_interval_10_minutes));
            textThree.setText(getResources().getString(R.string.detection_alarm_interval_30_minutes));
        }
    }

    @Override
    public void onClick(View v) {
        if (mOnOptionChange == null) {
            return;
        }
        switch (v.getId()) {
            case R.id.text_one:
                optionSelected(textOne);
                mOnOptionChange.onOptionChange(0);
                break;
            case R.id.text_two:
                optionSelected(textTwo);
                mOnOptionChange.onOptionChange(1);
                break;
            case R.id.text_three:
                optionSelected(textThree);
                mOnOptionChange.onOptionChange(2);
                break;
        }
    }

    private void optionSelected(TextView text) {
        if (text == null)return;
        if (textSelected == text && text.isSelected())return;
        resetTextSelected();

        text.setTextColor(getResources().getColor(R.color.text_color_selected));
        text.setSelected(true);
        textSelected = text;
    }

    private void resetTextSelected() {
        int colorWhite = getResources().getColor(R.color.white);
        if (textOne != null)
        {
            textOne.setTextColor(colorWhite);
            textOne.setSelected(false);
        }
        if (textTwo != null)
        {
            textTwo.setTextColor(colorWhite);
            textTwo.setSelected(false);
        }
        if (textThree != null)
        {
            textThree.setTextColor(colorWhite);
            textThree.setSelected(false);
        }

    }

    public void setOnOptionChange(OnOptionChange onOptionChange) {
        this.mOnOptionChange = onOptionChange;
    }

    public interface OnOptionChange {

        void onOptionChange(int value);

    }
}
