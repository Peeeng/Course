package com.example.j_king.course;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AlertDialog.Builder;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;


import com.example.j_king.tts.MyTTS;

import static android.speech.tts.TextToSpeech.QUEUE_FLUSH;


public class TaskActivity extends AppCompatActivity   {
    private static final String TAG = "TTS Demo" ;
    private static final Integer MY_DATA_CHECK_CODE = 0x0011;
    private TextToSpeech mTts ;
    private Switch switchTask ;
    private MyTTS myTTS ;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.task);

        myTTS = new MyTTS(this) ;

        switchTask  = (Switch) findViewById(R.id.switchTask) ;
        prepareListen();

    }

    private void prepareListen(){
        Button test = (Button) findViewById(R.id.test) ;
        test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(myTTS.getTTSInstance() != null)
                    myTTS.speakVoice("已启动课程任务。"); ;
            }
        });
        switchTask.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    //启动TTS 语音引擎，此步骤将会打开对话框选择哪一个引擎
                    Intent checkIntent = new Intent();
                    checkIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
                    startActivityForResult(checkIntent, MY_DATA_CHECK_CODE);

                }else{
                    if(mTts != null){
                        mTts.stop();
                    }
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //判断当前选中引擎是否可用
        switch (resultCode) {
            case TextToSpeech.Engine.CHECK_VOICE_DATA_PASS:
                //这个返回结果表明TTS Engine可以用
                Log.i(TAG, "TTS Engine is enabled!");
                myTTS.createTTSObject();
                break ;
            case TextToSpeech.Engine.CHECK_VOICE_DATA_FAIL:
                //这情况表明数据有错,重新下载安装需要的数据
                Log.e(TAG, "TTS Data is disabled!");
                myTTS.installTTSData();
                break ;
            default:
                Log.v(TAG, "Got a failure. TTS apparently not available");
        }
    }


}
