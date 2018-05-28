package com.example.j_king.myView;

/**
 * @name Course
 * @class name：com.example.j_king.myView
 * @class describe
 * @anthor J-King QQ:2354345263
 * @time 2017/11/14 10:28
 */

import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioManager;
import android.net.Uri;
import android.preference.DialogPreference;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.text.Layout;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Space;
import android.widget.Toast;

import com.example.j_king.getsetdata.SharedPreferencesHelper;

import static com.example.j_king.getsetdata.CourseDB.TAG;

public class SeekBarDialog  {

    private Context contextAct ;
    private SharedPreferencesHelper sp ;
    private SeekBar voiceSeekBar ;
    String key ;
    public SeekBarDialog(Context context ,String key, SharedPreferencesHelper sp){
        contextAct = context ;
        this.sp = sp ;
        this.key = key ;
        voiceSeekBar = new SeekBar(context) ;
        voiceSeekBar.setPadding(5,15,5,0);


        //获取系统最大音量
        AudioManager mAudioManager = (AudioManager) contextAct.getSystemService(Context.AUDIO_SERVICE);
        int maxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_RING) ;
        //获取之前设置的音量值
        int oldVolume = sp.getInt(key) ;
        voiceSeekBar.setMax(maxVolume);
        voiceSeekBar.setProgress(oldVolume);
    }



    public void alertSeekBarDialog(String title) {

        AlertDialog.Builder alertInstall = new AlertDialog.Builder(contextAct)
                .setTitle(title)
                .setView(voiceSeekBar)
                .setPositiveButton("确定",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                int volume = voiceSeekBar.getProgress() ;
                                Log.e(TAG, "onClick: "+ volume);
                                sp.putValue(key,volume);
                                Toast.makeText(contextAct,"音量设置为"+volume,Toast.LENGTH_SHORT).show();
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



