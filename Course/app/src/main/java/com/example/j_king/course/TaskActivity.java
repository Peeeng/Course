package com.example.j_king.course;

import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.textservice.TextServicesManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;


import com.example.j_king.task.AlarmService;
import com.example.j_king.tts.MyTTSCheck;

import org.w3c.dom.Text;

import java.util.Locale;


public class TaskActivity extends AppCompatActivity   {
    private static final String TAG = "TTS Demo" ;
    private static final Integer MY_DATA_CHECK_CODE = 0x0011;
    private Switch switchTask ;
    private MyTTSCheck myTTSCheck;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.task);

        Log.e(TAG, "onCreate: " );


        switchTask  = (Switch) findViewById(R.id.switchTask) ;
        prepareListen();

    }

    @Override
    public void onRestart(){
        super.onRestart();
        Log.e(TAG, "onRestart: " );
    }

    @Override
    public void onStart(){
        super.onStart();
        Log.e(TAG, "onStart: " );
    }
    @Override
    protected void onResume() {
        super.onResume();
        Log.e(TAG, "onResume");
    }
    @Override
    protected void onPause() {
        super.onPause();
        Log.e(TAG, "onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.e(TAG, "onStop");
    }


    @Override
    public void onDestroy(){
        super.onDestroy();
        if(myTTSCheck != null)
            myTTSCheck.stopTTS();
        Log.e(TAG, "onDestroy: " );
    }


    private void prepareListen(){
        Button test = (Button) findViewById(R.id.test) ;
        test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(myTTSCheck != null){
                    myTTSCheck.speakVoice("已启动课程任务。");
                    myTTSCheck.stopTTS();
                }
                Intent alarmServices = new Intent(TaskActivity.this.getApplicationContext(),AlarmService.class) ;
                startService(alarmServices);
            }
        });
        switchTask.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    //检查TTS 语音引擎数据是否完备，此步骤将会打开对话框选择哪一个引擎
                    Intent checkIntent = new Intent();
                    checkIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
                    startActivityForResult(checkIntent, MY_DATA_CHECK_CODE);
                }
            }
        });
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
/*                    TextToSpeech tts = myTTSTool.getInstance() ;
                    int checkLanguage = tts.isLanguageAvailable(Locale.CHINA) ;
                    if(checkLanguage != TextToSpeech.LANG_MISSING_DATA && checkLanguage != TextToSpeech.LANG_NOT_SUPPORTED)
                        Toast.makeText(TaskActivity.this,"已成功设置课程提醒",Toast.LENGTH_LONG).show() ;
                    myTTSTool.stopTTS();*/
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
