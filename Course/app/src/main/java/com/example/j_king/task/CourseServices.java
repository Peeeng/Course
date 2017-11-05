package com.example.j_king.task;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.IBinder;
import android.os.SystemClock;
import android.speech.tts.TextToSpeech;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.j_king.getsetdata.CourseDB;
import com.example.j_king.getsetdata.CurWeekSet;
import com.example.j_king.tts.MyTTSTool;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @name Course
 * @class name：com.example.j_king.task
 * @class describe
 * @anthor J-King QQ:1032006226
 * @time 2017/11/1 17:57
 */
public class CourseServices extends Service {
    private static final String TAG = "CourseServices" ;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate(){
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, final int startId) {
        final Bundle cNameAndAddr = intent.getExtras();
        new Thread(new Runnable() {
            @Override
            public void run() {
                String cName = cNameAndAddr.get(CourseDB.cName).toString() ;
                String cAddr = cNameAndAddr.get(CourseDB.cAddr).toString();
                Log.e(TAG, "run: "+ cName + cAddr );
                MyTTSTool myTTSTool = new MyTTSTool(CourseServices.this) ;
                int status = myTTSTool.speakVoice("接下来您将在"+cAddr+"有一堂"+cName);
                myTTSTool.stopTTS();
                //speak完了之后，启动taskServices,并告知speak的返回状态
                Intent taskServiceIntent = new Intent(CourseServices.this,TaskServices.class) ;
                taskServiceIntent.putExtra("speakStatus",status) ;
                startService(taskServiceIntent) ;
                //关闭服务
                stopSelf();
            }
        }).start();
        return super.onStartCommand(intent,flags,startId) ;
    }






}
