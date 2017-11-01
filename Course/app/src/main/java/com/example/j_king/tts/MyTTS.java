package com.example.j_king.tts ;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import java.util.Locale;

import static android.speech.tts.TextToSpeech.*;

public class MyTTS extends Activity   {

    private static final String TAG = "MyTTS" ;
    private final Context context;
    private TextToSpeech mTts ;

    public MyTTS(Context context){
        this.context = context ;
    }

    public void createTTSObject(){
        mTts = new TextToSpeech(context, new MyOnInitialListener());
    }

    public TextToSpeech getTTSInstance(){
        return mTts ;
    }
    public void speakVoice(String text){
        mTts.speak(text,QUEUE_FLUSH,null,"222") ;
    }

    class MyOnInitialListener implements TextToSpeech.OnInitListener {
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
    public void installTTSLang() {
        AlertDialog.Builder alertInstall = new AlertDialog.Builder(context)
                .setTitle("缺少语音包")
                .setMessage("当前语音引擎不支持中文语音，建议使用讯飞语音，前往下载？")
                .setPositiveButton("去下载",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                // 下载讯飞语音的语音数据包
                                String ttsDataUrl = "http://server.m.pp.cn/download/apk?"+
                                        "query=%E8%AE%AF%E9%A3%9E%E8%AF%AD%E9%9F%B3&ch=smweb&"+
                                        "ch_src=sm&appId=1001787&custom=0&uc_param_str=frvecpeimintnidnut";
                                Uri ttsDataUri = Uri.parse(ttsDataUrl);
                                Intent ttsIntent = new Intent(
                                        Intent.ACTION_VIEW, ttsDataUri);
                                context.startActivity(ttsIntent);
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
