package com.example.j_king.task;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.speech.tts.TextToSpeech;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.j_king.getsetdata.CourseDB;
import com.example.j_king.getsetdata.SharedPreferencesHelper;

import java.util.Locale;

/**
 * @name Course
 * @class name：com.example.j_king.task
 * @class describe
 * @anthor J-King QQ:1032006226
 * @time 2017/11/1 17:57
 */
public class CourseServices extends Service {
    private static final String TAG = "CourseServices";
    TextToSpeech textToSpeech = null;
    Bundle bundleText;
    private SharedPreferencesHelper sp;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

    }

    @Override
    public int onStartCommand(final Intent intent, int flags, final int startId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (intent != null)
                    bundleText = intent.getExtras();
                String cName = bundleText.getString(CourseDB.cName);
                String cAddr = bundleText.getString(CourseDB.cAddr);
                String cTime = bundleText.getString(CourseDB.cTime);
                long triggerAtTime = bundleText.getLong("triggerAtTime");
                String voiceText = "请前往" + cAddr + "听，" + cName;
                Log.e(TAG, "run: 播报的文本：" + voiceText);
                sp=new SharedPreferencesHelper(CourseServices.this,"taskConfig") ;
                //唤醒CPU
                PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
                PowerManager.WakeLock wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MyWakeLock");
                wakeLock.acquire();
                int status=0;
                for (int i=0;i<sp.getInt("setAlarmCount");++i) {
                    status = speakCourse(voiceText);
                }

                Log.e(TAG, "run: speak状态：" + status);
                //speak完了之后，启动taskServices,并告知speak的返回状态
                Intent broadcastIntent = new Intent(CourseServices.this, CourseReceive.class);
                broadcastIntent.putExtra("speakStatus", status);
                PendingIntent pi = PendingIntent.getBroadcast(CourseServices.this, 1, broadcastIntent, PendingIntent.FLAG_CANCEL_CURRENT);
                AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
                manager.set(AlarmManager.RTC_WAKEUP, triggerAtTime + 15 * 60 * 1000, pi);
                stopSelf();
                wakeLock.release();
            }
        }).start();
        return super.onStartCommand(intent, flags, startId);
    }


    public int speakCourse(final String text) {
        final int[] speakStatus = {-2};
        Log.e(TAG, "speakCourse: " + speakStatus[0]);

        textToSpeech = new TextToSpeech(CourseServices.this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                textToSpeech.setLanguage(Locale.CHINA);
            }
        });

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            speakStatus[0] = textToSpeech.speak(text, TextToSpeech.QUEUE_ADD, null, "222");
        else
            speakStatus[0] = textToSpeech.speak(text, TextToSpeech.QUEUE_ADD, null);

        while (textToSpeech.isSpeaking()) {
            ;
        }
        textToSpeech.shutdown();
        textToSpeech = null;
        return speakStatus[0];
    }
}
