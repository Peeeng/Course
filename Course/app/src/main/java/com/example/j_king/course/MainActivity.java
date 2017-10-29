package com.example.j_king.course;


import android.Manifest;
import android.annotation.TargetApi;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;

import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.j_king.getsetdata.CourseDB;
import com.example.j_king.getsetdata.CurWeekSet;
import com.example.j_king.getsetdata.ReadSqlite;
import com.example.j_king.getsetdata.XlsData;
import com.example.j_king.getsetdata.XlsSetDB;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity" ;

    private static final int REQUEST_XLSSET_ACTIVITY = 0x0002 ;
    private  Button btSet ;
    private Spinner selectWeek ;
    private GridView gridCourse ;

    private ReadSqlite readSqlite ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.course);
        ActionBar bar = getSupportActionBar() ;
        bar.hide();

        readSqlite = new ReadSqlite(this) ;
        prepareListener();
        setCurWeek();

    }


    private void prepareListener(){
        
        btSet = (Button) findViewById(R.id.btSet) ;
        selectWeek = (Spinner) findViewById(R.id.selectWeek) ;
        gridCourse = (GridView) findViewById(R.id.gridCourse) ;


        View.OnClickListener listenerSet = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,XlsSetActivity.class) ;
                startActivityForResult(intent,REQUEST_XLSSET_ACTIVITY);
            }
        };

        AdapterView.OnItemSelectedListener listenerSelectWeek = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                showWeekCourse(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectWeek.setSelection(0);
            }
        };
        btSet.setOnClickListener(listenerSet);
        selectWeek.setOnItemSelectedListener(listenerSelectWeek);
    }

    /**
     * 初始化数据：设置当前周次
     */
    private void setCurWeek(){
        //计算当前周次
        CurWeekSet curWeekSet = new CurWeekSet(this) ;
        int curWeek = curWeekSet.getNewCurWeek() ;
        //更新xlsset表中的curWeek
        ContentValues values = new ContentValues() ;
        values.put(XlsSetDB.curWeek , curWeek) ;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        values.put(XlsSetDB.startDate,simpleDateFormat.format(new Date())) ;

        XlsSetDB xlsSetDB = new XlsSetDB(this) ;
        Cursor cur = xlsSetDB.queryFromXlsSet(new String[]{XlsSetDB.xlsSetId},null,null,null,null,null) ;
        if(cur.getCount() <= 0){
            values.put(XlsSetDB.xlsSetId,XlsSetDB.defaultId) ;
            xlsSetDB.insertToXlsSet("1",values) ;
        }else{
            xlsSetDB.updateByClause(XlsSetDB.DB_TABLE,values,null,null);
        }
        cur.close();
        //设置下拉框的值为当前周次
        selectWeek.setSelection(curWeek);
    }


    @TargetApi(23)
    @Override
    protected void onActivityResult(int requestCode,int resultCode , Intent data){

        switch(requestCode){
            case REQUEST_XLSSET_ACTIVITY:
                if(resultCode == XlsSetActivity.CHANGEXLS){
                    int curWeek = data.getIntExtra("curWeek",1) ;
                    showWeekCourse(curWeek);
                }
                else if(resultCode == XlsSetActivity.NOCHANGE){
                    int curWeek = data.getIntExtra("curWeek",1) ;

                    showWeekCourse(curWeek);
                }
                break ;
        }
    }

    private void showWeekCourse(int curWeek){
        String [] selectItemValue=
                {
                        "1","2","3","4","5","6","7","8","9","10","11","12","13","14","15","16","17","18","19","20"
                };
        selectWeek.setSelection(curWeek);
        String week = Integer.valueOf(selectItemValue[curWeek]).toString() ;

        String [] from = new String [] { CourseDB.cName,CourseDB.cTeacher} ;
        int [] to = new int[]{R.id.courseName,R.id.teacherName} ;
        List <Map<String,String>> cNameAndtNameList =  readSqlite.getSelectWeekData(week) ;
        SimpleAdapter simpleAdapter = new SimpleAdapter(MainActivity.this,cNameAndtNameList,R.layout.courseitem,from,to) ;

        gridCourse.setAdapter(simpleAdapter);
    }




}




