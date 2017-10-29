package com.example.j_king.course;

import android.app.Application;

import com.facebook.stetho.Stetho;

/**
 * Created by J-King on 2017/10/19.
 */

public class MyApplication extends Application {
    @Override
    public void onCreate(){
        super.onCreate();
        Stetho.initializeWithDefaults(this);
    }
}
