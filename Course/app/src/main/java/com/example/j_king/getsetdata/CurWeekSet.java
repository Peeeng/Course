package com.example.j_king.getsetdata;

import android.content.Context;
import android.database.Cursor;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @name Course
 * @class name：com.example.j_king.getsetdata
 * @class describe 用来自动更新当前周次
 * @anthor J-King QQ:1032006226
 * @time 2017/10/28 20:06
 * @change
 * @chang time
 * @class describe
 */
public class CurWeekSet {
    private XlsSetDB xlsSetDB ;
    private int curWeek ;
    private String startDate ;
    public CurWeekSet(Context context) {
        super();
        xlsSetDB = new XlsSetDB(context) ;
        curWeek = 0 ;
    }

    private void getWeekAndDate(){
        Cursor cur = xlsSetDB.queryFromXlsSet(new String[]{XlsSetDB.curWeek,XlsSetDB.startDate},XlsSetDB.xlsSetId+"=?",new String[]{XlsSetDB.defaultId},null,null,null);
        if(cur.getCount() > 0){
            cur.moveToFirst() ;
            curWeek = cur.getInt(cur.getColumnIndex(XlsSetDB.curWeek));
            startDate = cur.getString(cur.getColumnIndex(XlsSetDB.startDate));
        }
        else{
            curWeek = 0 ;
            SimpleDateFormat tmp = new SimpleDateFormat("yyyy-MM-dd");
            startDate = tmp.format(new Date()) ;
        }
        cur.close();
    }

    public int getNewCurWeek(){
        getWeekAndDate();
        //当前日期
        Date eDate = new Date() ;
        //将设置当前周次的字符串日期转换为Date类型
        SimpleDateFormat tmp = new SimpleDateFormat("yyyy-MM-dd");
        ParsePosition pos = new ParsePosition(0) ;
        Date sDate = tmp.parse(startDate,pos) ;

        //设置当前周次的日期和现在的日期相差的毫秒数
        long gapMilliSecond = eDate.getTime() - sDate.getTime() ;
        //将毫秒数转换为天数
        long gapDay = gapMilliSecond/(1000*60*60*24);

        //获取设置当前周次时间的星期，日1，一2
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(sDate);
        //计算当前星期，从周一到周日分别对应0-6
        int sDay = (calendar.get(Calendar.DAY_OF_WEEK) + 5 ) % 7 ;
        int addWeek = (sDay  + (int)gapDay)/7;
        int newCurWeek = curWeek + addWeek ;
        return newCurWeek ;
    }
}
