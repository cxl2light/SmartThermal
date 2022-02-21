package com.hq.monitor.util;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringRegUtils {
    /**
     * 使用正则表达式提取中括号中的内容
     * @param msg
     * @return
     */
    public static List<String> extractMessageByRegular(String msg){
        List<String> list=new ArrayList<String>();
        Pattern p = Pattern.compile("(\\[[^\\]]*\\])");
        Matcher m = p.matcher(msg);
        while(m.find()){
            list.add(m.group().substring(1, m.group().length()-1));
        }
        return list;
    }

    public static List<String> extractMessageByRegular(String msg,String pat){
        List<String> list= new ArrayList<>();
        Pattern p = Pattern.compile(pat);
        Matcher m = p.matcher(msg);
        while(m.find()){
            Log.d("ZeReg",m.toString());
            list.add(m.group());
        }
        return list;
    }
}
