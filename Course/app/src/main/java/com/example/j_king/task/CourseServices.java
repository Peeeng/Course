package com.example.j_king.task;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.SystemClock;
import android.speech.tts.TextToSpeech;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.j_king.getsetdata.CourseDB;
import com.example.j_king.getsetdata.CurWeekSet;
import com.example.j_king.tts.MyTTSCheck;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
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
    TextToSpeech textToSpeech = null ;
    String voiceText ;

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
//        voiceText = intent.getExtras().getString("voiceText") ;
        voiceText = intent.getStringExtra("voiceText") ;
        Log.e(TAG, "onStartCommand: 开始CourseServices 服务,voiceText:"+voiceText);
        new Thread(new Runnable() {
            @Override
            public void run() {


                Log.e(TAG, "run: 播报的文本："+ voiceText );

                int status = speakCourse(voiceText);

                Log.e(TAG, "run: speak状态："+status );
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

    public int speakCourse(String text) {
        int status = -1 ;
        textToSpeech = new TextToSpeech(CourseServices.this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                textToSpeech.setLanguage(Locale.CHINA);
            }
        });
        try {
            Thread.sleep(2000);
            if(Build.VERSION.SDK_INT >=  Build.VERSION_CODES.LOLLIPOP)
                status = textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null,"222");
            else
                status = textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null);
            while (textToSpeech.isSpeaking()) {
                ;
            }
            textToSpeech.shutdown();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return status ;
    }






}
