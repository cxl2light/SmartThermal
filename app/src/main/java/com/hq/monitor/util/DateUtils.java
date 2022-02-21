package com.hq.monitor.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @author Administrator
 * @date 2022/2/18 0018 11:28
 */
public class DateUtils {

    /** 年-月-日 时:分:秒 显示格式 */
    // 备注:如果使用大写HH标识使用24小时显示格式,如果使用小写hh就表示使用12小时制格式。
    public static String DATE_TO_STRING_DETAIL_PATTERN = "yyyy.MM.dd HH:mm:ss";

    /** 年.月.日 显示格式 */
    public static String DATE_TO_STRING_DATE_PATTERN = "yyyy.MM.dd";

    /** 年-月-日 时:分:秒 显示格式 */
    // 备注:如果使用大写HH标识使用24小时显示格式,如果使用小写hh就表示使用12小时制格式。
    public static String DATE_TO_STRING_TIME_PATTERN = "HH:mm:ss";
    public static String DATE_TO_STRING_TIME_SHORT_PATTERN = "HH:mm";

    private static SimpleDateFormat simpleDateFormat;

    /**
     * 获取现在时间
     *
     * @return返回字符串格式 yyyy.MM.dd HH:mm:ss
     */
    public static String getStringDateDetail() {
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat(DATE_TO_STRING_DETAIL_PATTERN);
        String dateString = formatter.format(currentTime);
        return dateString;
    }

    /**
     * 获取现在时间
     *
     * @return 返回短时间字符串格式yyyy.MM.dd
     */
    public static String getStringDate() {
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat(DATE_TO_STRING_DATE_PATTERN);
        String dateString = formatter.format(currentTime);
        return dateString;
    }

    /**
     * n天前的日期
     * @param days
     * @return 返回短时间字符串格式yyyy.MM.dd
     */
    public static String getStringOverdueDate(int days) {
        Calendar now = Calendar.getInstance();
        now.add(Calendar.DAY_OF_MONTH, -days);
        String endDate = new SimpleDateFormat(DATE_TO_STRING_DATE_PATTERN).format(now.getTime());
        return endDate;
    }

    /**
     * 获取时间 小时:分;秒 HH:mm:ss
     * @return
     */
    public static String getTimeString() {
        SimpleDateFormat formatter = new SimpleDateFormat(DATE_TO_STRING_TIME_PATTERN);
        Date currentTime = new Date();
        String dateString = formatter.format(currentTime);
        return dateString;
    }

    /**
     * 获取时间 小时:分;秒 HH:mm
     *
     * @return
     */
    public static String getTimeShort() {
        SimpleDateFormat formatter = new SimpleDateFormat(DATE_TO_STRING_TIME_SHORT_PATTERN);
        Date currentTime = new Date();
        String dateString = formatter.format(currentTime);
        return dateString;
    }
}
