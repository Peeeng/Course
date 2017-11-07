package com.example.j_king.task;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.example.j_king.getsetdata.CourseDB;

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

public class TaskReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent i = new Intent(context, CourseServices.class);
        Log.e("TaskReceiver", "onReceive: voiceText "+intent.getStringExtra("voiceText") );
        i.putExtra("voiceText",intent.getStringExtra("voiceText")) ;
        context.startService(i);
    }
}