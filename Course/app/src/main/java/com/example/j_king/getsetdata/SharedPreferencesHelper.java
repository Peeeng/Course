package com.example.j_king.getsetdata;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * @name Course
 * @class name：com.example.j_king.getsetdata
 * @class describe
 * @anthor J-King QQ:2354345263
 * @time 2017/11/14 9:29
 */
public class SharedPreferencesHelper {
    private SharedPreferences sp ;
    private SharedPreferences.Editor editor ;
    private Context context ;

    public SharedPreferencesHelper(Context context , String name){
        this.context = context ;
        sp = context.getSharedPreferences(name,0) ;
    }

    public void putValue(String key,String value){
        editor = sp.edit() ;
        editor.putString(key,value) ;
        editor.apply() ;
    }

    public void putValue(String key,int value){
        editor = sp.edit() ;
        editor.putInt(key,value) ;
        editor.apply() ;
    }

    public void putValue(String key,boolean value){
        editor = sp.edit() ;
        editor.putBoolean(key,value) ;
        editor.apply() ;
    }

    public String getString(String key){
        return sp.getString(key,"0") ;
    }

    public int getInt(String key){
        return sp.getInt(key,0) ;
    }
    public boolean getBoolean(String key){
        return sp.getBoolean(key,false) ;
    }

}
