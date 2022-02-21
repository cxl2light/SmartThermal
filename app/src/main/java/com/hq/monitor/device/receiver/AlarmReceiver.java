package com.hq.monitor.device.receiver;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import com.hq.monitor.app.MyApplication;
import com.hq.monitor.device.ControlDeviceIJKActivity;

import com.hq.monitor.R;
import com.hq.monitor.device.alarm.DetectionAlarmListActivity;
import com.hq.monitor.util.SpUtils;

/**
 * 侦测报警广播接收器
 * @author Administrator
 * @date 2022/2/15 0015 09:21
 */
public class AlarmReceiver extends BroadcastReceiver {

    private static final int NOTIFICATION_ID = 1000;
    /**
     * 静音
     */
    private int MODE_MUTE = 0;
    /**
     * 震动
     */
    private int MODE_VIBRATION = 1;
    /**
     * 铃声
     */
    private int MODE_SOUND = 2;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("NOTIFICATION_ACTION")) {

            String title = intent.getStringExtra("notification_title");
            String content = intent.getStringExtra("notification_content");
            int notificationType = SpUtils.getInt(context, SpUtils.ALARM_MODE_STRING, 0);
            // 创建通知管理器
            NotificationManager manager = (NotificationManager) context
                    .getSystemService(Context.NOTIFICATION_SERVICE);
            Intent intent2 = new Intent(MyApplication.getAppContext(), DetectionAlarmListActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent2, 0);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                // 1. 创建一个通知(必须设置channelId)
                String channelId = "com.hq.monitor.ChannelId" + notificationType; // 通知渠道
                Notification notification = new Notification.Builder(context)
                        .setChannelId(channelId)
                        .setSmallIcon(R.mipmap.ic_launcher_one)
                        .setContentTitle(title)
                        .setContentText(content)
                        .setContentIntent(pendingIntent)
                        .setAutoCancel(true)
                        .build();
                // 2. 获取系统的通知管理器(必须设置channelId)
                @SuppressLint("WrongConstant") NotificationChannel channel = new NotificationChannel(
                        channelId,
                        "NOTIFICATION",
                        NotificationManager.IMPORTANCE_MAX);
                if (notificationType == MODE_MUTE){
                    channel.setSound(null, null);
                    channel.setImportance(NotificationManager.IMPORTANCE_LOW);
                }
                else if (notificationType == MODE_VIBRATION){
                    channel.setSound(null, null);
                    channel.enableVibration(true);
                    channel.setVibrationPattern(new long[]{0,1000,1000,1000});
                }
                else if (notificationType == MODE_SOUND){

                }
                manager.createNotificationChannel(channel);
                // 3. 发送通知(Notification与NotificationManager的channelId必须对应)
                manager.notify(NOTIFICATION_ID, notification);
            }
            else {
                // 创建通知(标题、内容、图标)
                Notification notification = new Notification.Builder(context)
                        .setSmallIcon(R.mipmap.ic_launcher_one)
                        .setContentTitle(title)
                        .setContentText(content)
                        .setContentIntent(pendingIntent)
                        .setAutoCancel(true)
                        .build();
                if (notificationType == MODE_MUTE){
                    // 静音
                    notification.sound = null;
                    notification.vibrate = null;
                    // 发送通知
                    manager.notify(NOTIFICATION_ID, notification);
                }
                else if (notificationType == MODE_VIBRATION){
                    // 震动
                    notification.sound = null;
                    notification.defaults |= Notification.DEFAULT_VIBRATE;
                    // 发送通知
                    manager.notify(NOTIFICATION_ID, notification);
                }
                else if (notificationType == MODE_SOUND){
                    // 铃声
                    notification.defaults |= Notification.DEFAULT_SOUND;
                    // 发送通知
                    manager.notify(NOTIFICATION_ID, notification);
                }
            }
        }

    }

    /**
     * 默认铃声
     */
    public void notificationPlayVideo(){
        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);//类型通知
        Ringtone rt = RingtoneManager.getRingtone(MyApplication.getAppContext(), uri);
        rt.play();
    }

}

