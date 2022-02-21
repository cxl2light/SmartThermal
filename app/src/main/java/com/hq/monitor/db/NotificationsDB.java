package com.hq.monitor.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.hq.base.util.Logger;
import com.hq.monitor.adapter.AlarmClassInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Administrator
 * @date 2022/2/18 0018 09:55
 */
public class NotificationsDB extends SQLiteOpenHelper {
    private String TAG = "NotificationsDB";
    private final static String DATABASE_NAME = "NOTIFICATIONS.db";
    private final static int DATABASE_VERSION = 1;
    private final static String TABLE_NAME = "notifications_table";
    public final static String NOTIFICATION_ID = "notification_id";
    public final static String DEVICE_NAME = "device_name";
    public final static String TARGET_DISTANCE = "target_distance";
    public final static String TARGET_TYPE = "target_type";
    public final static String NOTIFICATION_DATE = "notification_date";
    public final static String NOTIFICATION_TIME = "notification_time";
    public final static String MARKS = "marks";

    public NotificationsDB(Context context) {
        // TODO Auto-generated constructor stub
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE " + TABLE_NAME + " (" + NOTIFICATION_ID
                + " INTEGER primary key autoincrement, "
                + DEVICE_NAME + " varchar, "
                + TARGET_DISTANCE +" integer,"
                + TARGET_TYPE +" integer,"
                + NOTIFICATION_DATE +" varchar,"
                + NOTIFICATION_TIME +" varchar,"
                + MARKS + " varchar);";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String sql = "DROP TABLE IF EXISTS " + TABLE_NAME;
        db.execSQL(sql);
        onCreate(db);
    }

    /**
     * 插入数据
     * @return
     */
    public long insert(NotificationInfo notificationInfo)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        /* ContentValues */
        ContentValues cv = new ContentValues();
        cv.put(DEVICE_NAME, notificationInfo.getDevice_name());
        cv.put(TARGET_DISTANCE, notificationInfo.getTarget_distance());
        cv.put(TARGET_TYPE, notificationInfo.getTarget_type());
        cv.put(NOTIFICATION_DATE, notificationInfo.getNotification_date());
        cv.put(NOTIFICATION_TIME, notificationInfo.getNotification_time());
        cv.put(MARKS, notificationInfo.getMarks());
        long row = db.insert(TABLE_NAME, null, cv);
        return row;
    }

    /**
     * 删除某一天的数据
     * @param date
     */
    public void delete(String date)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        String where = NOTIFICATION_DATE + " = ?";
        String[] whereValue ={ date };
        db.delete(TABLE_NAME, where, whereValue);
    }

    /**
     * 删除日期之前的记录
     * @param date
     */
    public void deleteOverdue(String date)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        String where = NOTIFICATION_DATE + " < ?";
        String[] whereValue ={ date };
        db.delete(TABLE_NAME, where, whereValue);
    }

    /**
     * 清空数据
     */
    public void deleteAllDate()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, null, null);
    }

    /**
     * 查询数据
     * @param deviceName
     * @param targetType
     * @param notificationDate
     * @return
     */
    public List<NotificationInfo> find(String deviceName, Integer targetType, String notificationDate) {
        try {
            List<NotificationInfo> lists = new ArrayList<NotificationInfo>();
            NotificationInfo notificationInfo = null;
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor;
            if (targetType == 0){
                cursor = db.rawQuery("select * from " + TABLE_NAME
                        + " where " + DEVICE_NAME + "=? and "
                        + NOTIFICATION_DATE + "=? order by " + NOTIFICATION_ID + " desc",
                        new String[] { deviceName, notificationDate });
            }
            else {
                cursor = db.rawQuery("select * from " + TABLE_NAME
                        + " where " + DEVICE_NAME + "=? and "
                        + TARGET_TYPE + "=? and " + NOTIFICATION_DATE + "=? order by " + NOTIFICATION_ID + " desc",
                        new String[] { deviceName, targetType.toString(), notificationDate });
            }
            while (cursor.moveToNext()) {
                notificationInfo = new NotificationInfo();
                notificationInfo.setNotification_id(cursor.getInt(cursor.getColumnIndex(NOTIFICATION_ID)));
                notificationInfo.setDevice_name(cursor.getString(cursor.getColumnIndex(DEVICE_NAME)));
                notificationInfo.setTarget_distance(cursor.getInt(cursor.getColumnIndex(TARGET_DISTANCE)));
                notificationInfo.setTarget_type(cursor.getInt(cursor.getColumnIndex(TARGET_TYPE)));
                notificationInfo.setNotification_date(cursor.getString(cursor.getColumnIndex(NOTIFICATION_DATE)));
                notificationInfo.setNotification_time(cursor.getString(cursor.getColumnIndex(NOTIFICATION_TIME)));
                lists.add(notificationInfo);
            }
            db.close();
            return lists;
        } catch (Exception e) {
            Logger.e(TAG, e.getMessage());
            return null;
        }
    }

    /**
     * 查询设备列表
     * @return
     */
    public List<AlarmClassInfo> findDeviceList() {
        try {
            List<AlarmClassInfo> lists = new ArrayList<AlarmClassInfo>();
            AlarmClassInfo alarmClassInfo = null;
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.query(true, TABLE_NAME, new String[] { DEVICE_NAME }, null, null, null, null, NOTIFICATION_ID + " desc", null);

            while (cursor.moveToNext()) {
                alarmClassInfo = new AlarmClassInfo();
                alarmClassInfo.setContent(cursor.getString(cursor.getColumnIndex(DEVICE_NAME)));
                lists.add(alarmClassInfo);
            }
            db.close();
            return lists;
        } catch (Exception e) {
            Logger.e(TAG, e.getMessage());
            return null;
        }
    }

    /**
     * 查询日期列表
     * @return
     */
    public List<AlarmClassInfo> findDateNotificationList() {
        try {
            List<AlarmClassInfo> lists = new ArrayList<AlarmClassInfo>();
            AlarmClassInfo alarmClassInfo = null;
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.query(true, TABLE_NAME, new String[] { NOTIFICATION_DATE }, null, null, null, null, NOTIFICATION_ID + " desc", null);

            while (cursor.moveToNext()) {
                alarmClassInfo = new AlarmClassInfo();
                alarmClassInfo.setContent(cursor.getString(cursor.getColumnIndex(NOTIFICATION_DATE)));
                lists.add(alarmClassInfo);
            }
            db.close();
            return lists;
        } catch (Exception e) {
            Logger.e(TAG, e.getMessage());
            return null;
        }
    }

}
