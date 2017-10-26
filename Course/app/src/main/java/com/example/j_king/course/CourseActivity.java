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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.j_king.getsetdata.CourseDB;
import com.example.j_king.getsetdata.XlsData;
import com.example.j_king.getsetdata.XlsSetDB;


public class CourseActivity extends AppCompatActivity {

    private static final String TAG = "Course" ;
    private static final int REQUEST_EXTERNAL_STRONGE = 3 ;

    private  Button btSet ;
    private Spinner selectWeek ;
    private GridView gridCourse ;

    public CourseDB courseDB = null;
    public String xlsPath = null;

    private AdapterView.OnItemSelectedListener listenerSelectWeek = null ;
    private View.OnClickListener listenerSet = null ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.course);
        ActionBar bar = getSupportActionBar() ;
        bar.hide();
        courseDB = new CourseDB(CourseActivity.this) ;
        prepareListener();

        Cursor cursor = new XlsSetDB(this).queryFromXlsSet(new String[]{XlsSetDB.curWeek},null,null,null,null,null) ;
        if(cursor.getCount() == 1){
            cursor.moveToFirst() ;
            int curWeek = cursor.getInt(cursor.getColumnIndex(XlsSetDB.curWeek));
            selectWeek.setSelection(curWeek);
            cursor.close();
        }
        btSet.setOnClickListener(listenerSet);
        selectWeek.setOnItemSelectedListener(listenerSelectWeek);

    }



    private void prepareListener(){
        btSet = (Button) findViewById(R.id.btSet) ;
        selectWeek = (Spinner) findViewById(R.id.selectWeek) ;
        gridCourse = (GridView) findViewById(R.id.gridCourse) ;


        listenerSet = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CourseActivity.this,XlsSetActivity.class) ;
                startActivityForResult(intent,1);
            }
        };

        listenerSelectWeek = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String [] selectItemValue=
                        {
                             "1","2","3","4","5","6","7","8","9","10","11","12","13","14","15","16","17","18","19","20"
                        };

                String week = Integer.valueOf(selectItemValue[position]).toString() ;

                String [] from = new String [] { CourseDB.cName,CourseDB.cTeacher} ;
                int [] to = new int[]{R.id.courseName,R.id.teacherName} ;
                List <Map<String,String>> cNameAndtNameList =  getSelectWeekData(week) ;
                SimpleAdapter simpleAdapter = new SimpleAdapter(CourseActivity.this,cNameAndtNameList,R.layout.courseitem,from,to) ;

                gridCourse.setAdapter(simpleAdapter);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        };
    }

    @TargetApi(23)
    @Override
    protected void onActivityResult(int requestCode,int resultCode , Intent data){

        switch(requestCode){
            case 1:
                if(resultCode == RESULT_OK){
                    xlsPath = data.getStringExtra("xlsPath") ;
                    int curWeek = data.getIntExtra("curWeek",1) ;
                    selectWeek.setSelection(curWeek);
                    Log.i(TAG, "onActivityResult: 返回成功"+xlsPath);


                    Log.i(TAG, "onActivityResult: "+android.os.Build.VERSION.SDK);
                    if(Integer.valueOf(android.os.Build.VERSION.SDK) >= 23) {
                        int writeStoragePermission = CourseActivity.this.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                        if (writeStoragePermission != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(CourseActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_EXTERNAL_STRONGE);
                            return;
                        }
                    }
                    try {
                        InputStream xls = new FileInputStream(new File(xlsPath )) ;
                        courseDB.deleteTable();
                        List<ContentValues> courseList =  getXlsData(xls);
                        for(ContentValues values : courseList){
                            courseDB.insertCourse(values);
                        }

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                else if(resultCode == RESULT_CANCELED){
                    ;
                }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//根据请求是否通过的返回码进行判断，然后进一步运行程序
        if (grantResults.length > 0 && requestCode == REQUEST_EXTERNAL_STRONGE && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            InputStream xls = null;
            try {
                xls = new FileInputStream(new File(xlsPath ));
                courseDB.deleteTable();
                List<ContentValues> courseList =  getXlsData(xls);
                for(ContentValues values : courseList){
                    courseDB.insertCourse(values);
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

        }

    }


    /**
     *
     * @param xlsFile xls表格文件流
     * @return 课程列表
     */
    public List<ContentValues> getXlsData(InputStream xlsFile) {
        List<ContentValues> courseList = new ArrayList<>() ; 
        XlsData data = new XlsData(xlsFile);
        ArrayList<Map<String, Object>> courseXls = data.getAllData();
        data.closeConnect();

        for (Map<String, Object> param : courseXls) {
            ContentValues value = new ContentValues();
            value.put(CourseDB.cName, param.get(CourseDB.cName).toString());
            value.put(CourseDB.cTeacher, param.get(CourseDB.cTeacher).toString());
            value.put(CourseDB.cWeeks, param.get(CourseDB.cWeeks).toString());
            value.put(CourseDB.cAddr, param.get(CourseDB.cAddr).toString());
            value.put(CourseDB.cTime, param.get(CourseDB.cTime).toString());
            value.put(CourseDB.cWeekday, param.get(CourseDB.cWeekday).toString());
            courseList.add(value) ;
        }
        return courseList ;
    }


    public List< Map<String , String> > getSelectWeekData(String week){
        List< Map<String , String > > cNameAndtNameList =new ArrayList<>() ;

        String [] columns = new String[]{CourseDB.cName,CourseDB.cAddr,CourseDB.cWeekday,CourseDB.cTime};
        String selection = CourseDB.cWeeks+"=? and "+CourseDB.cTime + "=? and " +CourseDB.cWeekday + "= ?";

        for(int times = 1 ;times <= 6 ;times ++ )
            for(int weekday = 1 ; weekday <= 7 ;weekday++){

                String []selectionArgs = new String[]{ week,Integer.valueOf(times+2).toString(),Integer.valueOf(weekday).toString()};
                Cursor cursor = courseDB.queryCourse(columns,selection,selectionArgs,null,null,null);
                int num = cursor.getCount();
                if(num == 0 ){
                    Map<String , String > cNameAndtName = new HashMap<>() ;
                    cNameAndtName.put(CourseDB.cName,"") ;
                    cNameAndtName.put(CourseDB.cTeacher,"") ;
                    cNameAndtNameList.add(cNameAndtName) ;
                }
                else if (num == 1){
                    cursor.moveToFirst() ;
                    String courseName ;
                    String teacherName   ;
                    Map<String , String > cNameAndtName = new HashMap<>() ;
                    courseName = cursor.getString(cursor.getColumnIndex(CourseDB.cName)) ;
                    teacherName = cursor.getString(cursor.getColumnIndex(CourseDB.cAddr)) ;
                    cNameAndtName.put(CourseDB.cName,courseName) ;
                    cNameAndtName.put(CourseDB.cTeacher,teacherName) ;
                    cNameAndtNameList.add(cNameAndtName) ;
                }
                else
                    Log.e(TAG, "getSelectWeekData: 该时间存在两个或以上课程" );
                cursor.close();

            }

        return cNameAndtNameList ;
    }


}




