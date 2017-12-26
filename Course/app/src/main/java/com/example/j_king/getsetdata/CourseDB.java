package com.example.j_king.getsetdata;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

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

    public static final String cName = "cName";
    public static final String cNo = "cNo";
    public static final String cTeacher = "cTeacher";
    public static final String cWeeks = "cWeeks";
    public static final String cWeekday = "cWeekday";
    public static final String cTime = "cTime";
    public static final String cAddr = "cAddr";

    public CourseDB(Context context){
        courseDBHelper = getInstance(context) ;
        db = courseDBHelper.getWritableDatabase() ;
    }

    private static synchronized CourseDBHelper getInstance(Context context){
        if(courseDBHelper == null)
            courseDBHelper = new CourseDBHelper(context) ;
        return courseDBHelper ;
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

    public  void deleteTable(){

        db.delete(DB_TABLE,null,null) ;

    }
    public Cursor queryCourse(String []columns,String selection,String []selectionArgs ,String groupBy,String having,String orderBy) {

        Cursor cur = db.query(DB_TABLE, columns, selection, selectionArgs, groupBy, having, orderBy);
//        cur.close();
        return cur ;
    }
    public int updateByCourse(ContentValues values, String whereClause,String[] whereArgs){
        int rtn = db.update(DB_TABLE,values,whereClause,whereArgs);
        return rtn ;
    }
    public int deleteByCourse(String whereClause,String[] whereArgs){
        int rtn = db.delete(DB_TABLE,whereClause,whereArgs);
        return rtn ;
    }
}
