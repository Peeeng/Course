package com.example.j_king.task;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.j_king.course.R;
import com.example.j_king.course.TimeActivity;
import com.example.j_king.getsetdata.CourseDB;
import com.example.j_king.getsetdata.CurWeekSet;
import com.example.j_king.getsetdata.SharedPreferencesHelper;
import com.example.j_king.mylistener.MySensorEventListener;
import com.example.j_king.myunit.CourseSpeakUnit;
import com.example.j_king.pojo.VoiceContent;

import java.text.SimpleDateFormat;
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
public class TaskServices extends Service {
    private static final String TAG = "TaskServices";
    private CourseSpeakUnit courseSpeakUnit ;

//    private  List<Map<String, Object>> curDayCourse ;
    private int addDay = 0 ;

    //已经呼叫的次数
    private int voicedTimes;
    private int curWeek, curDay ;

    private Bitmap largeIcon;
    private int smallIcon;




    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        voicedTimes = 0;
        getCurDayAndWeek();

        courseSpeakUnit = new CourseSpeakUnit(this) ;
        //获取小图标
        smallIcon = R.drawable.course;
        //获取大图标
        largeIcon = BitmapFactory.decodeResource(this.getResources(), R.drawable.course);

        Log.e(TAG, "onCreate: 首次启动TaskServices");

        SensorManager manager = (SensorManager) this.getSystemService(Service.SENSOR_SERVICE);
        MySensorEventListener listener = new MySensorEventListener();
        manager.registerListener(listener, manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
               // curDayCourse = getOneDayCourse(curWeek, curDay);
                startCourseServices(intent);

            }
        }).start();

        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    public void startCourseServices(Intent intent) {

        Intent broadcastIntent = new Intent(TaskServices.this, TaskReceiver.class);
        Bundle bundle = getSpeakContent(intent) ;
        broadcastIntent.putExtras(bundle);

        PendingIntent pi = PendingIntent.getBroadcast(TaskServices.this, 0, broadcastIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        //获取系统alarm服务
        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        //让系统定时发送pi指定的广播：启动TaskReceiver
        manager.setExact(AlarmManager.RTC_WAKEUP, bundle.getLong("triggerAtTime") - 15 * 60 * 1000, pi);
        //将已呼叫的次数增1
        ++voicedTimes;
    }

    private Bundle getSpeakContent(Intent intent){
        VoiceContent voiceContent = getNextCourse() ;
        int lastSpeakStatus;
        if (intent != null)
            lastSpeakStatus = intent.getIntExtra("speakStatus", 0);
        else
            lastSpeakStatus = 0;
        Log.e(TAG, "onStartCommand: 开始TaskServices任务,上一次的呼叫状态：" + lastSpeakStatus);

        if (voiceContent == null) {
            Log.e(TAG, "getSpeakContent: "+"没有课程了，开始结束服务" );
            stopSelf();
            Log.e(TAG, "getSpeakContent: "+"结束服务失败" );
            return null;
        }
        //设置数据--播报的字符串：课程名，地点
        String cName = voiceContent.getcName();
        String cAddr = voiceContent.getcAddr();
        long triggerAtTime = voiceContent.getTriggerAtTime();
        SimpleDateFormat timeFormat = new SimpleDateFormat("EEEE  HH:mm");
        String cTime = timeFormat.format(new Date(triggerAtTime));
        Log.e(TAG, "run: 执行下一次呼叫的时间：" + cTime);
        startForeground(110,(Notification)sendNotification(cName,cAddr,cTime));// 开始前台服务

        Bundle bundle = new Bundle() ;
        bundle.putString(CourseDB.cName,cName);
        bundle.putString(CourseDB.cAddr,cAddr);
        bundle.putString(CourseDB.cTime,cTime);
        bundle.putLong("triggerAtTime",triggerAtTime);

        return bundle ;
    }



    /**
     * @return 获取当天的星期和周次
     */
    public void getCurDayAndWeek() {
        //获取当前周次
        CurWeekSet curWeekSet = new CurWeekSet(this);
        curWeek = curWeekSet.getNewCurWeek();

        //获取当前星期
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        //0-6:周一到周日
        curDay = (calendar.get(Calendar.DAY_OF_WEEK) + 5) % 7 +1 ;
    }

    /**
     *
     * @return  返回某一天下一个需要通知的课程信息
     */
    public VoiceContent getNextCourse() {
        int addDay = 0;
        VoiceContent voiceContent = courseSpeakUnit.getNextVoiceContent(curWeek,curDay,addDay,voicedTimes) ;
        while (voiceContent.getMessage().equals("noCourseToday")) {
            //如果当天的课程信息都被呼叫完，获取第二天的课程并返回
            ++addDay ;
            //重新计算周次和星期
            curWeek += (curDay + addDay ) / 7;
            curDay = ( ++curDay - 1) % 7 + 1 ;
            if (curWeek >= 20) {
                return null;
            }
            voicedTimes = 0;
            voiceContent = courseSpeakUnit.getNextVoiceContent(curWeek,curDay,addDay,voicedTimes) ;
        }
        voicedTimes = voiceContent.getVoicedTime() ;
        return voiceContent ;
    }


    public Notification sendNotification(String cName, String cAddr, String time) {
        Notification taskNotification;
        //获取NotificationManager实例
        //实例化NotificationCompat.Builde并设置相关属性
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this.getApplicationContext())
                //设置小图标
                .setSmallIcon(smallIcon)
                .setLargeIcon(largeIcon)
                //设置通知标题
                .setContentTitle(cName)
                .setTicker(cName)
                //设置通知内容
                .setContentText("地点:" + cAddr + "  时间:" + time);
        //设置通知时间，默认为系统发出通知的时间，通常不用设置
        //.setWhen(System.currentTimeMillis());
        //通过builder.build()方法生成Notification对象,并发送通知,id=1
        taskNotification = builder.build(); // 获取构建好的Notification
        taskNotification.defaults = Notification.DEFAULT_SOUND; //设置为默认的声音
        return taskNotification;
    }
}
