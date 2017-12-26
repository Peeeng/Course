package com.example.j_king.course;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;


import com.example.j_king.getsetdata.SharedPreferencesHelper;
import com.example.j_king.myView.SeekBarDialog;
import com.example.j_king.task.AlarmService;
import com.example.j_king.tts.MyTTSCheck;


public class TaskActivity extends AppCompatActivity   {
    private static final String TAG = "TTS Demo" ;
    private static final Integer MY_DATA_CHECK_CODE = 0x0011;
    private Switch switchOpenTTS ,switchNotification,switchVoiceDown,switchVoiceUp;
    private MyTTSCheck myTTSCheck;
    private SharedPreferencesHelper sp;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.task);


        Log.e(TAG, "onCreate: ");

        switchOpenTTS  = (Switch) findViewById(R.id.switchOpenTTS);
        switchNotification = (Switch) findViewById(R.id.switchNotification);
        switchVoiceDown  = (Switch) findViewById(R.id.switchVoiceDown);
        switchVoiceUp  = (Switch) findViewById(R.id.switchVoiceUp);

        sp = new SharedPreferencesHelper(TaskActivity.this,"taskConfig") ;
        if (sp.getBoolean("isOpenTTS")){
            Intent checkIntent = new Intent();
            checkIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
            startActivityForResult(checkIntent, MY_DATA_CHECK_CODE);
            switchOpenTTS.setChecked(true);
        }
        if (sp.getBoolean("isSendNotification")){
            Notification taskNotification ;
            //获取NotificationManager实例
            //实例化NotificationCompat.Builde并设置相关属性
            NotificationCompat.Builder builder = new NotificationCompat.Builder(TaskActivity.this)
                    //设置小图标
                    .setTicker( "课程通知" )
                    .setContentTitle("课程通知")
                    .setSmallIcon(R.drawable.course)
                    //设置通知内容
                    .setContentText("已启动课程通知");
            //通过builder.build()方法生成Notification对象,并发送通知,id=1
            taskNotification = builder.build(); // 获取构建好的Notification
            taskNotification.defaults = Notification.DEFAULT_SOUND; //设置为默认的声音
            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            manager.notify(1,taskNotification);
            switchNotification.setChecked(true);
        }
        prepareListen();
    }
    @Override
    public void onDestroy(){
        super.onDestroy();
        if(myTTSCheck != null)
            myTTSCheck.stopTTS();
        myTTSCheck = null ;
        Log.e(TAG, "onDestroy: " );
    }

    private void prepareListen(){
        Button test = (Button) findViewById(R.id.test) ;

        CompoundButton.OnCheckedChangeListener switchListener = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                switch(buttonView.getId()){
                    case R.id.switchOpenTTS:
                       if (isChecked) {
                            //检查TTS 语音引擎数据是否完备，此步骤将会打开对话框选择哪一个引擎
                            Intent checkIntent = new Intent();
                        checkIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
                        startActivityForResult(checkIntent, MY_DATA_CHECK_CODE);
                        }
                        sp.putValue("isOpenTTS",isChecked) ;
                        break ;
                    case R.id.switchNotification:
                        if(isChecked){
                            Notification taskNotification ;
                            //获取NotificationManager实例
                            //实例化NotificationCompat.Builde并设置相关属性
                            NotificationCompat.Builder builder = new NotificationCompat.Builder(TaskActivity.this)
                                    //设置小图标
                                    .setTicker( "课程通知" )
                                    .setContentTitle("课程通知")
                                    .setSmallIcon(R.drawable.course)
                                    //设置通知内容
                                    .setContentText("已启动课程通知");
                            //通过builder.build()方法生成Notification对象,并发送通知,id=1
                            taskNotification = builder.build(); // 获取构建好的Notification
                            taskNotification.defaults = Notification.DEFAULT_SOUND; //设置为默认的声音
                            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                            manager.notify(1,taskNotification);
                        }else{
                            NotificationManager manager=(NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
                            manager.cancel(1);
                        }

                        sp.putValue("isSendNotification",isChecked) ;
                        break ;
                    case R.id.switchVoiceUp:
                        if(isChecked){
                            SeekBarDialog seekBarDialog = new SeekBarDialog(TaskActivity.this,"VoiceUpRing",sp) ;
                            seekBarDialog.alertSeekBarDialog("设置课后音量");
                        }
                        sp.putValue("isVoiceUp",isChecked);
                        break ;
                    case R.id.switchVoiceDown:
                        if(isChecked){
                            SeekBarDialog seekBarDialog = new SeekBarDialog(TaskActivity.this,"VoiceDownRing",sp) ;
                            seekBarDialog.alertSeekBarDialog("设置课前音量");
                        }
                        sp.putValue("isVoiceDown",isChecked);
                        break ;
                }
            }
        };

        test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        switchOpenTTS.setOnCheckedChangeListener(switchListener);
        switchNotification.setOnCheckedChangeListener(switchListener);
        switchVoiceDown.setOnCheckedChangeListener(switchListener);
        switchVoiceUp.setOnCheckedChangeListener(switchListener);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //如果请求码为“检查TTS引擎”
        if(requestCode == MY_DATA_CHECK_CODE){
            //判断当前选中TTS引擎是否可用
            switch (resultCode) {
                case TextToSpeech.Engine.CHECK_VOICE_DATA_PASS:
                    //这个返回结果表明TTS Engine可以用
                    Log.i(TAG, "TTS Engine is enabled!");
                    myTTSCheck = new MyTTSCheck(this) ;
/*                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    myTTSCheck.speakVoice("已启动课程任务。");
                    myTTSCheck.stopTTS();*/

                    Intent alarmServices = new Intent(TaskActivity.this.getApplicationContext(),AlarmService.class) ;
                    startService(alarmServices);
                    break ;
                case TextToSpeech.Engine.CHECK_VOICE_DATA_FAIL:
                    //这情况表明数据有错,重新下载安装需要的数据
                    Log.e(TAG, "TTS Engine is disabled!");
                    Toast.makeText(TaskActivity.this,"TTS引擎不可用",Toast.LENGTH_LONG).show() ;
                    myTTSCheck.installTTSData();
                    break ;
                default:
                    Log.e(TAG, "It's occur ERROR when check TTS engine");
            }
        }
    }
}
