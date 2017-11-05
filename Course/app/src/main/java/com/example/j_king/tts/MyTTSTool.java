package com.example.j_king.tts ;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import java.util.Locale;

import static android.speech.tts.TextToSpeech.*;

public class MyTTSTool {

    private static final String TAG = "MyTTSTool" ;
    private Context context;
    private static TextToSpeech mTts ;


    public MyTTSTool(Context context){
        this.context = context.getApplicationContext() ;
    }

    public TextToSpeech getInstance()
    {
        synchronized(mTts){
            if (mTts == null) {
                mTts = new TextToSpeech(context,new MyOnInitialListener());
            }
            return mTts;
        }

    }

    public int speakVoice(String text){
        //返回speak的状态，ERROR、SUCCESS
        return mTts.speak(text,QUEUE_FLUSH,null,"222") ;
    }

    public void stopTTS(){
//        mTts.stop();
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
                    installTTSLang();
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
        context.startActivity(dataIntent);
    }

    /**
     * 安装语音相关资源包
     */
    private void installTTSLang() {
        AlertDialog.Builder alertInstall = new AlertDialog.Builder(context)
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
                                context.startActivity(ttsIntent);
                            }
                        })
                .setNeutralButton("去设置",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                // 设置TTS引擎为讯飞语音
                                context.startActivity(new Intent("com.android.settings.TTS_SETTINGS"));
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
