package com.example.j_king.task;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.j_king.course.TaskActivity;
import com.example.j_king.getsetdata.CourseDB;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @name Course
 * @class name：com.example.j_king.alarm
 * @class describe
 * @anthor J-King QQ:1032006226
 * @time 2017/11/1 17:56
 * @change
 * @chang time
 * @class describe
 */
public class AlarmService extends Service{
    private static final String TAG = "AlarmService" ;
    private static final Integer ALARM_SERVICE_REQUESTCODE = 0x1001 ;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                long triggerAtTime = new Date().getTime() + 1000;
                long oneDayIntervalMills = 24 * 60 * 60 * 1000;
                Intent alarmReceiver = new Intent(AlarmService.this, AlarmReceiver.class);
                PendingIntent pi = PendingIntent.getBroadcast(AlarmService.this, ALARM_SERVICE_REQUESTCODE, alarmReceiver, 0);

//                alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, triggerAtTime, oneDayIntervalMills, pi);
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerAtTime, pi);
                Log.e(TAG, "onStartCommand: " + "启动了定时任务");

            }
        }).start();
        return super.onStartCommand(intent, flags, startId);
    }
}
