package com.example.j_king.task;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * @name Course
 * @class name：com.example.j_king.task
 * @class describe
 * @anthor J-King QQ:1032006226
 * @time 2017/11/1 18:14
 * @change
 * @chang time
 * @class describe
 */

public class AlarmReceiver extends BroadcastReceiver {
    private static final String TAG = "AlarmReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent i = new Intent(context, TaskServices.class);
        context.startService(i);
        Log.e(TAG, "onStartCommand: "+"接收到AlarmService的任务" );

    }
}