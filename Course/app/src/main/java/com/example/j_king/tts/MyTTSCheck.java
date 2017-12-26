package com.example.j_king.tts ;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import java.util.Locale;

import static android.speech.tts.TextToSpeech.*;

public class MyTTSCheck {

    private final String TAG = "MyTTSCheck" ;
    private Context contextAct ;
    private TextToSpeech mTts ;

    public MyTTSCheck(Context contextAct){
        this.contextAct = contextAct ;
        mTts = new TextToSpeech(contextAct,new MyOnInitialListener());
    }


    public int speakVoice(String text){
        //返回speak的状态，ERROR、SUCCESS
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            return mTts.speak(text,QUEUE_ADD,null,"222") ;
        else
            return mTts.speak(text,QUEUE_FLUSH,null) ;
    }

    public void stopTTS(){
        while(mTts.isSpeaking()){
            ;
        }
        mTts.stop();
        mTts.shutdown();
    }

    private class MyOnInitialListener implements TextToSpeech.OnInitListener {
        /**
         *
         * @param status 创建TTS 对象返回的状态
         */
        @Override
        public void onInit(int status){
            //TTS Engine初始化完成
            if(status == SUCCESS) {
                //设置发音语言
                int result = mTts.setLanguage(Locale.CHINA);
                //判断语言是否可用
                if(result == LANG_MISSING_DATA || result == LANG_NOT_SUPPORTED) {
                    //下载语音包
                    Log.v(TAG, "Language is not available");
                    installTTSLang(contextAct);
                }
                else {
                    Log.v(TAG, "TTS is available");
                }
            }
        }
    }

    /**
     *  安装TTS 语音服务
     */
    public void installTTSData(){
        Intent dataIntent = new Intent();
        dataIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
        contextAct.startActivity(dataIntent);
    }

    /**
     * 安装语音相关资源包
     */
    private void installTTSLang(final Context contextAct) {
        AlertDialog.Builder alertInstall = new AlertDialog.Builder(contextAct)
                .setTitle("缺少语音包")
                .setMessage("当前语音引擎不支持中文语音，建议使用讯飞语音。")
                .setPositiveButton("去下载",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                // 下载讯飞语音引擎
                                String ttsDataUrl = "http://server.m.pp.cn/download/apk?"+
                                        "query=%E8%AE%AF%E9%A3%9E%E8%AF%AD%E9%9F%B3&ch=smweb&"+
                                        "ch_src=sm&appId=1001787&custom=0&uc_param_str=frvecpeimintnidnut";
                                Uri ttsDataUri = Uri.parse(ttsDataUrl);
                                Intent ttsIntent = new Intent(
                                        Intent.ACTION_VIEW, ttsDataUri);
                                contextAct.startActivity(ttsIntent);
                            }
                        })
                .setNeutralButton("去设置",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                // 设置TTS引擎为讯飞语音
                                contextAct.startActivity(new Intent("com.android.settings.TTS_SETTINGS"));
                            }
                        })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
        alertInstall.create().show();
    }
}
