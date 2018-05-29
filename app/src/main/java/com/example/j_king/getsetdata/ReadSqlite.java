package com.example.j_king.getsetdata;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 读取数据库
 */
public class ReadSqlite {
    private CourseDB courseDB ;
    private static final String TAG = "ReadSqlite";
    public ReadSqlite(Context context){
        courseDB = new CourseDB(context) ;
    }

    /**
     *
     * @param xlsFile xls表格文件流
     * @return 课程列表
     */
    public List<ContentValues> getXlsContentValues(InputStream xlsFile) {
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

    /**
     * 获取指定周次的数据
     * @param week 当前学习周次
     * @return 课程名和地点列表
     */
    public List< Map<String , String> > getSelectWeekData(Integer week){
        List< Map<String , String > > cNameAndtNameList = new ArrayList<>() ;

        String [] columns = new String[]{CourseDB.cName,CourseDB.cAddr,CourseDB.cWeekday,CourseDB.cTime};
        String selection = CourseDB.cWeeks+"=? and "+CourseDB.cTime + "=? and " +CourseDB.cWeekday + "= ?";

        for(int times = 1 ;times <= 12 ;times = times + 2 )
            for(int weekday = 1 ; weekday <= 7 ;weekday++){
                String []selectionArgs = new String[]{ week.toString(),Integer.valueOf(times).toString(),Integer.valueOf(weekday).toString()};
                Cursor cursor = courseDB.queryCourse(columns,selection,selectionArgs,null,null,null);
                int num = cursor.getCount();
                if(num == 0 ){
                    Map<String , String > cNameAndtName = new HashMap<>() ;
                    cNameAndtName.put(CourseDB.cName,"") ;
                    cNameAndtName.put(CourseDB.cAddr,"") ;
                    cNameAndtNameList.add(cNameAndtName) ;
                }
                else if (num == 1){
                    cursor.moveToFirst() ;
                    Map<String , String > cNameAndtName = new HashMap<>() ;
                    cNameAndtName.put(CourseDB.cName,cursor.getString(cursor.getColumnIndex(CourseDB.cName))) ;
                    cNameAndtName.put(CourseDB.cAddr,cursor.getString(cursor.getColumnIndex(CourseDB.cAddr))) ;
                    cNameAndtNameList.add(cNameAndtName) ;
                }
                else
                    Log.e(TAG, "getSelectWeekData: 该时间存在两个或以上课程" );
                cursor.close();
            }

        return cNameAndtNameList ;
    }
}
