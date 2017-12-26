package com.example.j_king.getsetdata;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by J-King on 2017/10/20.
 */

public class XlsSetDB {
    public static final String DB_TABLE = "xlsset" ;
    private static final int DB_VERSION = 1 ;
    public final String TAG = "XlsSetDB" ;

    //xlsset表的主键，id
    public static final String xlsSetId = "xlsSetId";
    public static final String xlsPath = "xlsPath";
    public static final String curWeek = "curWeek" ;
    public static final String startDate = "startDate";

    public static final String defaultId = "XlsUniqueueId";
    private static XlsSetDBHelper xlsSetDBHelper  ;
    private SQLiteDatabase db ;

    public XlsSetDB(Context context){
        db = getInstance(context).getWritableDatabase() ;
    }

    private static synchronized XlsSetDBHelper getInstance(Context context){
        if(xlsSetDBHelper == null)
            xlsSetDBHelper = new XlsSetDBHelper(context) ;
        return xlsSetDBHelper ;
    }

    private static class XlsSetDBHelper extends SQLiteOpenHelper {

        private XlsSetDBHelper(Context context) {
            super(context, DB_TABLE, null, DB_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            String sql = "create table " + DB_TABLE +
                    "( "+
                    xlsSetId + " varchar(20) primary key ," +
                    xlsPath + " varchar(250),"+
                    curWeek + " int ," +
                    startDate + " varchar(50) " +
                    ")";
            db.execSQL(sql);
            Log.i("XlsSetDBHelper", "onCreate: "+DB_TABLE+"表创建成功");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }



    /**
     *
     * @param nullColumnsHack content为空时，新增nullColumnsHack,并为它赋值为空
     * @param content 要插入的内容
     */
    public void insertToXlsSet(String nullColumnsHack , ContentValues content){
        long i = db.insert(DB_TABLE,nullColumnsHack,content) ;
        Log.v(TAG, "insertToXlsSet: "+"数据插入成功");
    }

    public Cursor queryFromXlsSet(String [] columns,String selection,String [] selectionArgs,String groupBy,String having,String orderBy){
        Cursor cursor = db.query(DB_TABLE,columns,selection,selectionArgs,groupBy,having,orderBy) ;
        return cursor ;
    }

    public int updateByClause(String table,ContentValues values, String whereClause,String[] whereArgs){
        int rtn = db.update(table,values,whereClause,whereArgs);
        return rtn ;
    }

    public int deleteTable(){
        int  rtn = db.delete(DB_TABLE,null,null) ;
        return rtn ;
    }
}
