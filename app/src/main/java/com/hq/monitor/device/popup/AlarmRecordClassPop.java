package com.hq.monitor.device.popup;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupWindow;

import com.hq.base.util.ScreenUtils;
import com.hq.monitor.R;
import com.hq.monitor.adapter.MyAlarmRecordAdapter;
import com.hq.monitor.adapter.AlarmClassInfo;

import java.util.ArrayList;

/**
 * @author Administrator
 * @date 2022/2/14 0014 09:06
 */
public class AlarmRecordClassPop extends PopupWindow {

    private ArrayList<AlarmClassInfo> mListData;
    private OnOptionChange mOnOptionChange;
    private Context mContext;
    private ListView listView;
    private int MAX_ITEMS = 0;

    public AlarmRecordClassPop(Context context, ArrayList<AlarmClassInfo> list) {
        this.mListData = list;
        this.mContext = context;
        // 一个自定义的布局，作为显示的内容
        View contentView = LayoutInflater.from(context).inflate(
                R.layout.alarm_record_class_popup_listview, null);
        initListView(contentView);

        setContentView(contentView);
        setWidth((int) (ScreenUtils.getScreenWidthPixels(context) * 0.25f));
        // 计算pop最多能显示item个数，（屏幕高度-顶部布局高度/item高度）
        MAX_ITEMS = (ScreenUtils.getScreenHeight() - 70)/52;
        if (MAX_ITEMS > 0 && list.size() > MAX_ITEMS){
            setHeight((int) (ScreenUtils.getScreenHeightPixels(context)- (int) context.getResources().getDimension(R.dimen.common_menu_popup_top_margin)));
        }
        else {
            setHeight((int) ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        setBackgroundDrawable(context.getDrawable(R.drawable.shape_rect_alarm_pop_bg));
        setOutsideTouchable(true);
        setTouchable(true);
        setFocusable(true);
    }

    /**
     * 初始化列表
     * @param contentView
     */
    private void initListView(View contentView) {
        listView = contentView.findViewById(R.id.recycler_list);
        listView.setNestedScrollingEnabled(true);
        listView.setAdapter(new MyAlarmRecordAdapter(mContext, mListData));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mListData != null && mListData.size() > 0){
                    mOnOptionChange.onOptionChange(position);
                }
            }
        });
    }

    public void setOnOptionChange(OnOptionChange onOptionChange) {
        this.mOnOptionChange = onOptionChange;
    }

    public interface OnOptionChange {

        void onOptionChange(int value);

    }
}
