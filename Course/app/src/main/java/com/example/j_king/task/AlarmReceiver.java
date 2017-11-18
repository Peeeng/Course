package com.example.j_king.task;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.ArrayList;

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
     * @param ServicesName
     * @return 判断taskservices是否在运行状态
     */
    private boolean isTaskServicesRunning(Context context , String ServicesName){
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ArrayList<ActivityManager.RunningServiceInfo> runningService = (ArrayList<ActivityManager.RunningServiceInfo>) manager.getRunningServices(30);
        for(ActivityManager.RunningServiceInfo serviceInfo : runningService){
            serviceInfo.service.getClassName()
                    .equals(ServicesName) ;
            if(serviceInfo.service.getClassName()
                    .equals(ServicesName))
                return true ;
        }
        return false ;
    }

}