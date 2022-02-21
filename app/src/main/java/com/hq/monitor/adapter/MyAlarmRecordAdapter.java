package com.hq.monitor.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.hq.monitor.R;

import java.util.ArrayList;

/**
 * @author Administrator
 * @date 2022/2/14 0014 13:58
 */
public class MyAlarmRecordAdapter extends BaseAdapter {

    private ArrayList<AlarmClassInfo> mListData;
    private Context mContext;

    public MyAlarmRecordAdapter(Context context, ArrayList<AlarmClassInfo> list){
        this.mListData = list;
        this.mContext = context;
    }

    @Override
    public int getCount() {
        if (mListData != null && mListData.size() > 0){
            return mListData.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return mListData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(mContext).inflate(R.layout.detection_alarm_tab_item, null);
        AlarmClassInfo info = (AlarmClassInfo) getItem(position);
        TextView content = convertView.findViewById(R.id.text_content);
        content.setText(info.getContent());
        ImageView img = convertView.findViewById(R.id.img_triangle);
        if (position != 0){
            img.setVisibility(View.INVISIBLE);
        }

        return convertView;
    }
}
