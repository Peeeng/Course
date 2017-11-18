package com.example.j_king.task;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

import com.example.j_king.course.R;
import com.example.j_king.getsetdata.CourseDB;

/**
 * @name Course
 * @class name：com.example.j_king.task
 * @class describe
 * @anthor J-King QQ:2354345263
 * @time 2017/11/14 17:34
 */
public class NotificationService extends Service {
    private Bitmap largeIcon;
    private int smallIcon;
    private String cName , cAddr , cTime ;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate(){
        //获取小图标
        smallIcon = R.drawable.course;
        //获取大图标
        largeIcon = BitmapFactory.decodeResource(this.getResources(), R.drawable.course);

    }
    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {
        Bundle bundle = intent.getExtras() ;
        String cName = bundle.getString(CourseDB.cName);
        String cAddr = bundle.getString(CourseDB.cAddr) ;
        String cTime = bundle.getString(CourseDB.cTime) ;
        sendNotification(cName,cAddr,cTime) ;
        return super.onStartCommand(intent,flags,startId) ;
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