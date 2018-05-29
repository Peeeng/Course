package com.example.j_king.task;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.ArrayList;

public class AlarmReceiver extends BroadcastReceiver {
    private static final String TAG = "AlarmReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        if( ! isTaskServicesRunning(context,"com.example.j_king.task.TaskServices")){
            Log.e(TAG, "onReceive: 时钟变化，并且TaskServices服务未启动" );
            Intent taskServicesIntent = new Intent(context, TaskServices.class);
            taskServicesIntent.putExtra("speakStatus",-1) ;
            context.startService(taskServicesIntent);
        }
        Log.e(TAG, "onReceive: 时钟变化" );

    }
    /**
     *
     * @param context
     * @param serviceName
     * @return 判断taskservices是否在运行状态
     */
    private boolean isTaskServicesRunning(Context context , String serviceName){
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ArrayList<ActivityManager.RunningServiceInfo> runningService = (ArrayList<ActivityManager.RunningServiceInfo>) manager.getRunningServices(30);
        for(ActivityManager.RunningServiceInfo serviceInfo : runningService){
            serviceInfo.service.getClassName()
                    .equals(serviceName) ;
            if(serviceInfo.service.getClassName()
                    .equals(serviceName))
                return true ;
        }
        return false ;
    }
}