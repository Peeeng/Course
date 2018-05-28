package com.example.j_king.getsetdata;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.j_king.pojo.CourseData;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by J-King on 2017/10/12.
 */

public class CourseDB {
    private static CourseDBHelper courseDBHelper;
    private SQLiteDatabase db = null;

    public static final String TAG = "DATABASE";
    public static final String DB_NAME = "Course.db";
    public static final String DB_TABLE = "mycourse";
    public static final Integer DB_VERSION = 1;

    //课程编号，主键
    public static final String cNo = "cNo";
    //课程名
    public static final String cName = "cName";
    //教师
    public static final String cTeacher = "cTeacher";
    //周次
    public static final String cWeeks = "cWeeks";
    //星期
    public static final String cWeekday = "cWeekday";
    //课程时间
    public static final String cTime = "cTime";
    //课程地点
    public static final String cAddr = "cAddr";

    public CourseDB(Context context) {
        courseDBHelper = getInstance(context);
        db = courseDBHelper.getWritableDatabase();
    }

    private static synchronized CourseDBHelper getInstance(Context context) {
        if (courseDBHelper == null)
            courseDBHelper = new CourseDBHelper(context);
        return courseDBHelper;
    }

    private static class CourseDBHelper extends SQLiteOpenHelper {

        private CourseDBHelper(Context context) {
            super(context, DB_NAME, null, DB_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            String sql = "create table " + DB_TABLE +
                    "(" +
                    cNo + " integer  primary key autoincrement," +
                    cName + " varchar(100)," +
                    cTeacher + " varchar(20)," +
                    cWeeks + " varchar(20)," +
                    cWeekday + " int ," +
                    cTime + " int," +
                    cAddr + " varchar(20)" +
                    ")";

            db.execSQL(sql);
            Log.i(TAG, "onCreate: 数据库创建成功！");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldDB_VERSION, int newDB_VERSION) {
            Log.i(TAG, "onUpgrade: 数据库更新！");
        }
    }


    public long insertCourse(ContentValues values) {
        long rowId = db.insert(DB_TABLE, null, values);
        Log.i(TAG, "insertCourse: 插入第" + rowId + "行数据。");
        return rowId;
    }

    public void deleteTable() {
        db.delete(DB_TABLE, null, null);
    }

    public Cursor queryCourse(String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy) {
        return db.query(DB_TABLE, columns, selection, selectionArgs, groupBy, having, orderBy);
    }

    public List<CourseData> queryCourseData(String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy) {
        Cursor cursor = db.query(DB_TABLE, columns, selection, selectionArgs, groupBy, having, orderBy);
        List<CourseData> courseDataList = new ArrayList<>();
        if(cursor.moveToFirst() ){
            do{
                CourseData courseData = new CourseData();
                courseData.setcNo(cursor.getInt(cursor.getColumnIndex(CourseDB.cNo)));
                courseData.setcName(cursor.getString(cursor.getColumnIndex(CourseDB.cName)));
                courseData.setcAddr( cursor.getString(cursor.getColumnIndex(CourseDB.cAddr)));
                courseData.setcTeacher(cursor.getString(cursor.getColumnIndex(CourseDB.cTeacher)));
                courseData.setcTime(cursor.getInt(cursor.getColumnIndex(CourseDB.cTime)));
                courseData.setcWeekday(cursor.getInt(cursor.getColumnIndex(CourseDB.cWeekday)));
                courseData.setcWeeks(cursor.getString(cursor.getColumnIndex(CourseDB.cWeeks)));
                courseDataList.add(courseData);
            }while(cursor.moveToNext());
        }
        return courseDataList;
    }

    public int updateByCourse(ContentValues values, String whereClause, String[] whereArgs) {
        return db.update(DB_TABLE, values, whereClause, whereArgs);
    }

    public int deleteByCourse(String whereClause, String[] whereArgs) {
        return db.delete(DB_TABLE, whereClause, whereArgs);
    }
}
