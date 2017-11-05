package com.example.j_king.task;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.j_king.getsetdata.CourseDB;
import com.example.j_king.getsetdata.CurWeekSet;
import com.example.j_king.getsetdata.XlsSetDB;

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
    private static final String TAG = "TaskServices" ;
    private static CourseDB courseDB ;
    private static int voicedTimes ;
    private List<Map<String,Object>> curDayCourse ;
    private Map<String,Object> curVoiceCourse ;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate(){
        super.onCreate();
        courseDB = new CourseDB(this) ;
        //已经呼叫的次数
        voicedTimes = 0 ;
        //当天的课程信息
        curDayCourse = getCourse();
        Log.e(TAG, "onCreate: 启动" );

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        new Thread(new Runnable() {
            @Override
            public void run() {

                long triggerAtTime = getNextVoiceTime() ;
                if(triggerAtTime == -1){
                    stopSelf();
                    return;
                }
                Intent broadcastIntent = new Intent(TaskServices.this, TaskReceiver.class);
                //设置数据--课程名，地点
                Bundle bundle = new Bundle() ;
                bundle.putString(CourseDB.cName,curVoiceCourse.get(CourseDB.cName).toString());
                bundle.putString(CourseDB.cAddr,curVoiceCourse.get(CourseDB.cAddr).toString());
                broadcastIntent.putExtras(bundle) ;

                PendingIntent pi = PendingIntent.getBroadcast(TaskServices.this, 0, broadcastIntent, 0);
                //获取系统alarm服务
                AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
                //定时启动services
                manager.setExact(AlarmManager.RTC_WAKEUP, triggerAtTime, pi);
                //将已呼叫的次数增1
                ++voicedTimes ;
            }
        }).start();
        super.onStartCommand(intent, flags, startId);
        return START_STICKY ;
    }

    /**
     *
     * @return 返回下一个呼叫的时间，若没有，则返回-1
     */
    public long getNextVoiceTime(){
        //如果下个呼叫的时间为null，则结束services
        if(transCourseTimeToDateTime(voicedTimes) == null){
            return -1;
        }
        //获取系统当前时间
        long curTime = System.currentTimeMillis();
        //获取下一个需要呼叫的时间
        long triggerAtTime = transCourseTimeToDateTime(voicedTimes).getTime() ;
        //设置可接受的呼叫延时时间
        long deliveryTime = 5*60*1000 ;
        //如果当前时间大于当前需要通知课程时间与允许延时时间之和的话，那么跳过此课程,叫下一个课程
        while(curTime > triggerAtTime + deliveryTime){
            if( ++voicedTimes >= curDayCourse.size()) {
                return -1;
            }
            Log.e(TAG, "run: "+"curtime="+curTime+" triggerTime = "+triggerAtTime + "  voice="+voicedTimes);
            triggerAtTime = transCourseTimeToDateTime(voicedTimes).getTime() ;
        }
        return triggerAtTime ;
    }

    /**
     * @return 返回下一个需要通知的课程时间:具体时间，如果没有要呼叫的下一个课程，则返回null
     */
    public Date transCourseTimeToDateTime(int index){
        curVoiceCourse = getNextCourse(index) ;
        if(curVoiceCourse == null)
            return null ;
        //设置时间为当前年月日
        Calendar calendar = Calendar.getInstance() ;
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hours = 0 ,minutes = 0 ;

        //获取课程节次，转换为具体的时间
        int cTime = (int)curVoiceCourse.get(CourseDB.cTime) ;
        switch (cTime){
            case 1: hours = 7 ;minutes = 30 ;break;
            case 3: hours = 9 ;minutes = 42 ;break ;
            case 5: hours = 13 ;minutes = 15 ;break ;
            case 7: hours = 15 ;minutes = 12 ;break ;
            default:hours = 0 ;minutes = 0 ;
        }
        calendar.set(year,month,day,hours,minutes);
        return calendar.getTime();
    }

    /**
     *
     * @return 返回当天下一个需要通知的课程信息
     */
    private Map<String,Object> getNextCourse(int index){
        if(index >= curDayCourse.size()){
            //如果当天的课程信息都被呼叫完，返回空
            return null ;
        }
        return curDayCourse.get(index) ;
    }


    /**
     * 获取课程信息
     */
    public List<Map<String, Object>> getCourse(){
        //获取当前周次
        CurWeekSet curWeekSet = new CurWeekSet(this) ;
        Integer curWeek = curWeekSet.getNewCurWeek();
        //获取当前星期
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        Log.e(TAG, "getCourse: "+ calendar.get(Calendar.DAY_OF_WEEK)) ;
        Integer curDay = (calendar.get(Calendar.DAY_OF_WEEK) + 5 )%7 + 1;

        List<Map<String, Object>> tmpCourse = getCurDayCourse(curWeek,curDay);
        return tmpCourse ;
    }

    /**
     * 获取当天的课程信息
     * @param curWeek
     * @param curDay
     */
    private List<Map<String, Object>> getCurDayCourse(Integer curWeek , Integer curDay){
        Cursor cursor = courseDB.queryCourse(new String []{CourseDB.cName,CourseDB.cAddr,CourseDB.cTime,CourseDB.cWeekday},
                CourseDB.cWeeks + "=? and "+ CourseDB.cWeekday + "=?",
                new String []{ curWeek.toString() , curDay.toString() },
                null,null,null);
        if(cursor.getCount() > 0 ){
            List<Map<String, Object>> tmpCourse = new ArrayList<>() ;
            cursor.moveToFirst();
            do{
                Map<String,Object> tmp = new HashMap<>() ;
                tmp.put(CourseDB.cTime,cursor.getInt(cursor.getColumnIndex(CourseDB.cTime))) ;
                tmp.put(CourseDB.cName,cursor.getString(cursor.getColumnIndex(CourseDB.cName))) ;
                tmp.put(CourseDB.cAddr,cursor.getString(cursor.getColumnIndex(CourseDB.cAddr))) ;
                tmp.put(CourseDB.cWeekday,cursor.getInt(cursor.getColumnIndex(CourseDB.cWeekday))) ;
                tmpCourse.add(tmp);
            }while(cursor.moveToNext());
            return tmpCourse ;
        }
        return null;
    }


}
