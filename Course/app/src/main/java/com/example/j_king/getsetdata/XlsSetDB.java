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
    public static final int DB_VERSION = 1 ;
    public static final String TAG = "XlsSetDB" ;

    public static final String xlsSetId = "xlsSetId";
    public static final String xlsPath = "xlsPath";
    public static final String curWeek = "curWeek" ;

    public static final String defaultId = "XlsUniqueueId";
    private XlsSetDBHelper xlsSetDBHelper  ;
    private SQLiteDatabase db ;

    public XlsSetDB(Context context){
        xlsSetDBHelper = new XlsSetDBHelper(context) ;
        db = xlsSetDBHelper.getReadableDatabase() ;

    }

    private class XlsSetDBHelper extends SQLiteOpenHelper {

        private XlsSetDBHelper(Context context) {
            super(context, DB_TABLE, null, DB_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            String sql = "create table " + DB_TABLE +
                    "( "+
                    xlsSetId + " varchar(20) primary key ," +
                    xlsPath + " varchar(250),"+
                    curWeek + " int " +
                    ")";
            db.execSQL(sql);
            Log.i(TAG, "onCreate: "+DB_TABLE+"表创建成功");
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
        db.insert(DB_TABLE,nullColumnsHack,content) ;
        Log.i(TAG, "insertToXlsSet: "+"数据插入成功");
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
