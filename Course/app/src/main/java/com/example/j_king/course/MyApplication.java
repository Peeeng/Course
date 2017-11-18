package com.example.j_king.course;

import android.app.Application;


/**
 * Created by J-King on 2017/10/19.
 */

public class MyApplication extends Application {
    @Override
    public void onCreate(){
        super.onCreate();
/*        Stetho.initializeWithDefaults(this);
        new OkHttpClient.Builder()
                .addNetworkInterceptor(new StethoInterceptor())
                .build();*/
    }

}
