package com.hq.monitor.db;

/**
 * @author Administrator
 * @date 2022/2/18 0018 10:47
 */
public class NotificationInfo {
    private int notification_id; // id
    private String device_name; // 设备名称
    private int target_distance; // 目标距离
    private int target_type; // 目标类别
    private String notification_date; // 通知日期
    private String notification_time; // 通知时间
    private String marks; // 预留标记

    public int getNotification_id() {
        return notification_id;
    }

    public void setNotification_id(int notification_id) {
        this.notification_id = notification_id;
    }

    public String getDevice_name() {
        return device_name;
    }

    public void setDevice_name(String device_name) {
        this.device_name = device_name;
    }

    public int getTarget_distance() {
        return target_distance;
    }

    public void setTarget_distance(int target_distance) {
        this.target_distance = target_distance;
    }

    public int getTarget_type() {
        return target_type;
    }

    public void setTarget_type(int target_type) {
        this.target_type = target_type;
    }

    public String getNotification_date() {
        return notification_date;
    }

    public void setNotification_date(String notification_date) {
        this.notification_date = notification_date;
    }

    public String getNotification_time() {
        return notification_time;
    }

    public void setNotification_time(String notification_time) {
        this.notification_time = notification_time;
    }

    public String getMarks() {
        return marks;
    }

    public void setMarks(String marks) {
        this.marks = marks;
    }
}
