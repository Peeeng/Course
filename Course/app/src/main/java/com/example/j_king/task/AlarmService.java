package com.example.j_king.task;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;


import java.util.Date;

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
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerAtTime, pi);
                }else
                    alarmManager.set(AlarmManager.RTC_WAKEUP, triggerAtTime, pi);
                Log.e(TAG, "onStartCommand: " + "启动了定时任务");

            }
        }).start();
        return super.onStartCommand(intent, flags, startId);
    }
}
