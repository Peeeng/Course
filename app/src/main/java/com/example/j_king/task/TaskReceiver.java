package com.example.j_king.task;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;


public class TaskReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        Intent i = new Intent(context, CourseServices.class);
        i.putExtras(intent.getExtras()) ;
        context.startService(i);
    }

}