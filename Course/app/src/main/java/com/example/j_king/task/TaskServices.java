package com.example.j_king.task;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.j_king.course.R;
import com.example.j_king.getsetdata.CourseDB;
import com.example.j_king.getsetdata.CurWeekSet;
import com.example.j_king.getsetdata.XlsSetDB;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
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
    private static CourseDB courseDB;

    //已经呼叫的次数
    private int voicedTimes;
    private List<Map<String, Object>> curDayCourse;
    private Map<String, Object> curVoiceCourse;

    private Bitmap largeIcon;
    private int smallIcon;

    private int addDay, curDay, curWeek;

    private long deliveryTime;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //course表数据库操作对象
        courseDB = new CourseDB(this);

        voicedTimes = 0;
        getCurDayAndWeek();
        //设置超时提醒时间
        curDayCourse = getOneDayCourse(curWeek, curDay);

        //获取小图标
        smallIcon = R.drawable.course;
        //获取大图标
        largeIcon = BitmapFactory.decodeResource(this.getResources(), R.drawable.course);

        Log.e(TAG, "onCreate: 首次启动TaskServices");

    }

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {
        new Thread(new Runnable() {
            @Override
            public void run() {

                startCourseServices(intent);

            }
        }).start();

        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    private void startCourseServices(Intent intent) {
        int lastSpeakStatus;
        if (intent != null)
            lastSpeakStatus = intent.getIntExtra("speakStatus", 0);
        else
            lastSpeakStatus = 0;
        Log.e(TAG, "onStartCommand: 开始TaskServices任务,上一次的呼叫状态：" + lastSpeakStatus);

        long triggerAtTime = getNextVoiceTime(lastSpeakStatus);
        if (triggerAtTime == -1) {
            stopSelf();
            return;
        }
        Intent broadcastIntent = new Intent(TaskServices.this, TaskReceiver.class);
        //设置数据--播报的字符串：课程名，地点
        String cName = curVoiceCourse.get(CourseDB.cName).toString();
        String cAddr = curVoiceCourse.get(CourseDB.cAddr).toString();
        SimpleDateFormat timeFormat = new SimpleDateFormat("EEEE  HH:mm");
        String cTime = timeFormat.format(new Date(triggerAtTime));

        Bundle bundle = new Bundle() ;
        bundle.putString(CourseDB.cName,cName);
        bundle.putString(CourseDB.cAddr,cAddr);
        bundle.putString(CourseDB.cTime,cTime);
        bundle.putLong("triggerAtTime",triggerAtTime);
        broadcastIntent.putExtras(bundle);

        Log.e(TAG, "run: 执行下一次呼叫的时间：" + cTime);

        startForeground(110,(Notification)sendNotification(cName,cAddr,cTime));// 开始前台服务

        PendingIntent pi = PendingIntent.getBroadcast(TaskServices.this, 0, broadcastIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        //获取系统alarm服务
        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        //让系统定时发送pi指定的广播：启动TaskReceiver
        manager.setExact(AlarmManager.RTC_WAKEUP, triggerAtTime - 15 * 60 * 1000, pi);
        //将已呼叫的次数增1
        ++voicedTimes;

    }


    /**
     * @return 返回下一个呼叫的时间，若没有，则返回-1
     */
    public long getNextVoiceTime(int lastSpeakStatus) {
        if (lastSpeakStatus == -1) {
            if (--voicedTimes < 0)
                voicedTimes = 0;
        }

        Date triggerDate = transCourseTimeToDateTime(voicedTimes);
        //如果下个呼叫的时间为null，则结束services
        if (triggerDate == null)
            return -1;
        //获取系统当前时间
        long curTime = System.currentTimeMillis();
        //获取下一个需要呼叫的时间
        long triggerTime = triggerDate.getTime();
        //设置可接受的呼叫延时时间

        //如果当前时间大于当前需要通知课程时间与允许延时时间之和的话，那么跳过此课程,获取下一个课程的时间
        while (curTime > triggerTime + deliveryTime) {
            triggerTime = transCourseTimeToDateTime(++voicedTimes).getTime();
            Log.e(TAG, "getNextVoiceTime: " + "curtime=" + new Date(curTime) + " triggerTime = " + new Date(triggerTime) + "  voice=" + voicedTimes);
        }
        return triggerTime;
    }

    /**
     * @return 返回下一个需要通知的课程时间:具体时间，如果没有要呼叫的下一个课程，则返回null
     */
    public Date transCourseTimeToDateTime(int index) {
        curVoiceCourse = getNextCourse(index);
        if (curVoiceCourse == null)
            return null;
        //设置时间为当前年月日
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = 0, minute = 0, seconds = 0;

        //获取课程节次，转换为具体的时间
        int cTime = (int) curVoiceCourse.get(CourseDB.cTime);
        switch (cTime) {
            case 1:
                hour = 8;
                minute = 0;
                break;
            case 3:
                hour = 10;
                minute = 0;
                break;
            case 5:
                hour = 13;
                minute = 30;
                break;
            case 7:
                hour = 15;
                minute = 30;
                break;
            case 9:
                hour = 19;
                minute = 0;
                break;
            case 11:
                hour = 21;
                minute = 0;
                break;

/*            case 1:hour = 8;minute = 0;break;
            case 3:hour = 8;minute = 1;break;
            case 5:hour = 8;minute = 2;break;
            case 7:hour = 8;minute = 4;break;
            case 9:hour = 8;minute = 8;break;
            case 11:hour = 8;minute = 10;break;
            default:hour = 0;minute = 0;*/
        }
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);
        calendar.add(Calendar.DAY_OF_MONTH, addDay);
        return calendar.getTime();
    }

    /**
     * @return 返回某一天下一个需要通知的课程信息
     */
    private Map<String, Object> getNextCourse(int index) {
        addDay = 0;
        while (index >= curDayCourse.size()) {
            //如果当天的课程信息都被呼叫完，获取第二天的课程并返回
            ++addDay;
            //重新计算周次和星期
            curWeek += (curDay + addDay - 1) / 7;
            curDay = (curDay + addDay - 1) % 7 + 1;
            if (curWeek >= 20) {
                curDayCourse = null;
                return null;
            }

            curDayCourse = getOneDayCourse(curWeek, curDay);
            index = voicedTimes = 0;
        }
        return curDayCourse.get(index);
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
        curDay = (calendar.get(Calendar.DAY_OF_WEEK) + 5) % 7 + 1;
    }

    /**
     * 获取某一周某星期几的课程信息
     *
     * @param week
     * @param day
     */
    private List<Map<String, Object>> getOneDayCourse(Integer week, Integer day) {
        Cursor cursor = courseDB.queryCourse(new String[]{CourseDB.cName, CourseDB.cAddr, CourseDB.cTime, CourseDB.cWeekday},
                CourseDB.cWeeks + "=? and " + CourseDB.cWeekday + "=?",
                new String[]{week.toString(), day.toString()},
                null, null, null);
        if (cursor.getCount() > 0) {
            List<Map<String, Object>> tmpCourse = new ArrayList<>();
            cursor.moveToFirst();
            do {
                Map<String, Object> tmp = new HashMap<>();
                tmp.put(CourseDB.cTime, cursor.getInt(cursor.getColumnIndex(CourseDB.cTime)));
                tmp.put(CourseDB.cName, cursor.getString(cursor.getColumnIndex(CourseDB.cName)));
                tmp.put(CourseDB.cAddr, cursor.getString(cursor.getColumnIndex(CourseDB.cAddr)));
                tmp.put(CourseDB.cWeekday, cursor.getInt(cursor.getColumnIndex(CourseDB.cWeekday)));
                tmpCourse.add(tmp);
            } while (cursor.moveToNext());
            cursor.close();
            return tmpCourse;
        }
        return null;
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
