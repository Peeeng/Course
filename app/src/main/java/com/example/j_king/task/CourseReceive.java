package com.example.j_king.task;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class CourseReceive extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        Intent taskServiceIntent = new Intent(context, TaskServices.class);
        taskServiceIntent.putExtra("speakStatus", intent.getStringExtra("speakStatus"));
        context.startService(taskServiceIntent);
    }

}