package com.example.j_king.myunit;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.example.j_king.getsetdata.CourseDB;
import com.example.j_king.getsetdata.SharedPreferencesHelper;
import com.example.j_king.pojo.VoiceContent;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @name Course
 * @class name：com.example.j_king.myunit
 * @class describe
 * @anthor J-King QQ:2354345263
 * @time 2017/12/27 16:12
 */
public class CourseSpeakUnit {
    public static final String course12HTime = "course12HTime";
    public static final String course34HTime = "course34HTime";
    public static final String course56HTime = "course56HTime";
    public static final String course78HTime = "course78HTime";
    public static final String course910HTime = "course910HTime";
    public static final String course1112HTime = "course1112HTime";

    public static final String course12MTime = "course12MTime";
    public static final String course34MTime = "course34MTime";
    public static final String course56MTime = "course56MTime";
    public static final String course78MTime = "course78MTime";
    public static final String course910MTime = "course910MTime";
    public static final String course1112MTime = "course1112MTime";

    public static final String course12Time = "course12Time";
    public static final String course34Time = "course34Time";
    public static final String course56Time = "course56Time";
    public static final String course78Time = "course78Time";
    public static final String course910Time = "course910Time";
    public static final String course1112Time = "course1112Time";

    public static final String TAG = "CourseSpeakUnit";
    public Context context;
    private static CourseDB courseDB;
    private SharedPreferencesHelper sp;
    private List<Map<String, Object>> curDayCourse;
    private VoiceContent voiceContent;
    private long deliveryTime;

    /**
     * @param context 上下文
     */
    public CourseSpeakUnit(Context context) {
        this.context = context;
        courseDB = new CourseDB(context);
        sp = new SharedPreferencesHelper(context, "taskConfig");
        deliveryTime = 5 * 60 * 1000;
        voiceContent = new VoiceContent();
    }

    /**
     * @param voicedTimes 已呼叫的次数
     * @return 返回下一个呼叫的时间，若没有，则返回-1
     */
    public long getNextVoiceDTime(int voicedTimes,int addDay) {

        Map<String, Object> curVoiceCourse = new HashMap<>();
        //获取系统当前时间
        long curTime = System.currentTimeMillis();

        //设置可接受的呼叫延时时间
        long triggerTime;
        //如果当前时间大于当前需要通知课程时间与允许延时时间之和的话，那么跳过此课程,获取下一个课程的时间
        do {
            if (curDayCourse == null || voicedTimes >= curDayCourse.size()) {
                return -1;
            }
            curVoiceCourse = curDayCourse.get(voicedTimes);
            //获取课程节次，转换为具体的时间
            int cTime = (int) curVoiceCourse.get(CourseDB.cTime);
            triggerTime = transCourseTimeToDateTime(cTime,addDay).getTime();
            voiceContent.setVoicedTime(voicedTimes);
            Log.e(TAG, "getNextVoiceTime: " + "curtime=" + new Date(curTime) + " triggerTime = " + new Date(triggerTime) + "  voice=" + voicedTimes);
        } while (curTime > triggerTime + deliveryTime && (++voicedTimes > 0));
        return triggerTime;
    }


    public Map<String, Object> getNextVoiceCourse(int voicedTime) {
        if (voicedTime >= curDayCourse.size())
            return null;
        return curDayCourse.get(voicedTime);
    }

    /**
     * @param cTime 课程时间
     * @return 将课程时间转换为日期时间
     */
    public Date transCourseTimeToDateTime(int cTime,int addDay) {
        //设置时间为当前年月日
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = 0, minute = 0, seconds = 0;
        switch (cTime) {
            case 1:
                hour = sp.getInt(course12HTime);
                minute = sp.getInt(course12MTime);
                break;
            case 3:
                hour = sp.getInt(course34HTime);
                minute = sp.getInt(course34MTime);
                break;
            case 5:
                hour = sp.getInt(course56HTime);
                minute = sp.getInt(course56MTime);
                break;
            case 7:
                hour = sp.getInt(course78HTime);
                minute = sp.getInt(course78MTime);
                break;
            case 9:
                hour = sp.getInt(course910HTime);
                minute = sp.getInt(course910MTime);
                break;
            case 11:
                hour = sp.getInt(course1112HTime);
                minute = sp.getInt(course1112MTime);
                break;
        }

        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);
        calendar.add(Calendar.DAY_OF_MONTH,addDay);
        return calendar.getTime();
    }

    public VoiceContent getNextVoiceContent(int week, int day,int addDay, int voicedTime) {
        curDayCourse = getOneDayCourse(week, day);
        long triggerTime = getNextVoiceDTime(voicedTime,addDay);
        if (triggerTime == -1) {
            voiceContent.setMessage("noCourseToday");
        } else {
            int newVoicedTime = voiceContent.getVoicedTime();
            voiceContent.setTriggerAtTime(triggerTime);
            Map<String, Object> nextVoiceCourse = getNextVoiceCourse(newVoicedTime);
            voiceContent.setcName(nextVoiceCourse.get(CourseDB.cName).toString());
            voiceContent.setcAddr(nextVoiceCourse.get(CourseDB.cAddr).toString());
            voiceContent.setcTime(Integer.valueOf(nextVoiceCourse.get(CourseDB.cTime).toString()));
            voiceContent.setMessage("success");
        }

        return voiceContent;
    }


    /**
     * @param week 要返回的课程周次
     * @param day  要返回的星期数，如果为null，返回整周课程
     * @return 返回指定周次和星期的课程信息
     */
    public List<Map<String, Object>> getOneDayCourse(Integer week, Integer day) {
        String selection;
        String[] selectionArgs;
        if (day == null) {
            selection = CourseDB.cWeeks + "=?";
            selectionArgs = new String[1];
            selectionArgs[0] = week.toString();
        } else {
            selection = CourseDB.cWeeks + "=? and " + CourseDB.cWeekday + "=?";
            selectionArgs = new String[2];
            selectionArgs[0] = week.toString();
            selectionArgs[1] = day.toString();
        }
        Cursor cursor = courseDB.queryCourse(new String[]{CourseDB.cName, CourseDB.cAddr, CourseDB.cTime, CourseDB.cWeekday},
                selection, selectionArgs, null, null, "cTime asc");
        if (cursor.getCount() > 0) {
            List<Map<String, Object>> tmpCourse = new ArrayList<>();
            cursor.moveToFirst();
            do {
                Map<String, Object> tmp = new HashMap<>();
                tmp.put(CourseDB.cTime, cursor.getInt(cursor.getColumnIndex(CourseDB.cTime)));
                tmp.put(CourseDB.cName, cursor.getString(cursor.getColumnIndex(CourseDB.cName)));
                tmp.put(CourseDB.cAddr, cursor.getString(cursor.getColumnIndex(CourseDB.cAddr)));
                tmp.put(CourseDB.cWeekday, cursor.getInt(cursor.getColumnIndex(CourseDB.cWeekday)));
                String name = cursor.getString(cursor.getColumnIndex(CourseDB.cName));
                tmpCourse.add(tmp);
            } while (cursor.moveToNext());
            cursor.close();
            return tmpCourse;
        }
        return null;
    }

}
