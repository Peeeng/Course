package com.example.j_king.course;


import android.annotation.TargetApi;
import android.app.Service;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;

import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.j_king.getsetdata.CourseDB;
import com.example.j_king.getsetdata.CurWeekSet;
import com.example.j_king.getsetdata.ReadSqlite;
import com.example.j_king.getsetdata.SharedPreferencesHelper;
import com.example.j_king.getsetdata.XlsSetDB;
import com.example.j_king.mylistener.MySensorEventListener;
import com.example.j_king.mylistener.NavListener;

import static com.example.j_king.getsetdata.XlsSetDB.DB_TABLE;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity" ;

    private static final int REQUEST_XLSSET_ACTIVITY = 0x0002 ;
    private Button btSet ;
    private Spinner selectWeek ;
    private GridView gridCourse ;
    String cTeacher;
    int cWeekday;
    String cWeekdays;
    int cTime;
    String cTimes;
    String cClassWeeks;


    private ReadSqlite readSqlite ;
    private SharedPreferencesHelper sp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.course);

        readSqlite = new ReadSqlite(this) ;

        prepareListener();
        setCurWeek();
    }

    private void prepareListener(){
        
        btSet = (Button) findViewById(R.id.btSet) ;
        selectWeek = (Spinner) findViewById(R.id.selectWeek) ;
        gridCourse = (GridView) findViewById(R.id.gridCourse) ;
        final CourseDB courseDB=new CourseDB(this);

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

        AdapterView.OnItemClickListener listenerGridCourse = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //行求商，列求余。
                //获取当前周次
                final int curWeek = selectWeek.getSelectedItemPosition() + 1;
                String curWeeks=null;
                int colnum = gridCourse.getNumColumns() ;//获取列数量
                int rowIndex = position / colnum ;//获取行标，从 0 开始计数
                //获取当前点击的上课节次
                final int cT = rowIndex * 2 + 1;
                //获取当前点击的星期
                int cWeekDay = position % colnum;
                ++cWeekDay;
                final int WeekDay=cWeekDay;
                showCourseDetails(curWeek,cWeekDay,cTime);
             /*   System.out.println(position);*/
                String cWeekDays=Integer.toString(cWeekDay);
                final String cTs=Integer.toString(cT);

                HashMap<String,String> map=(HashMap<String,String>)gridCourse.getItemAtPosition(position);
                String cName=map.get(CourseDB.cName);
                String cAddr=map.get(CourseDB.cAddr);
                Cursor c=courseDB.queryCourse(null,CourseDB.cName+"=? and "+CourseDB.cAddr+"=? and "+CourseDB.cWeekday+"=? and "+CourseDB.cTime+"=?",new String[]{cName,cAddr,cWeekDays,cTs},null,null,null);
                if (c.moveToFirst()){
                    do {
                        cTeacher=c.getString(c.getColumnIndex("cTeacher"));
                        cWeekday=c.getInt(c.getColumnIndex("cWeekday"));
                 //     cClassWeeks=c.getString(c.getColumnIndex("cClassWeeks"));
                        String s = Integer.toString(cWeekday);
                        if(s.equals("1")){
                            s="一";
                        }
                        if (s.equals("2")){
                            s="二";
                        }
                        if (s.equals("3")){
                            s="三";
                        }
                        if (s.equals("4")){
                            s="四";
                        }
                        if (s.equals("5")){
                            s="五";
                        }
                        if (s.equals("6")){
                            s="六";
                        }
                        if (s.equals("7")){
                            s="日";
                        }
                        cWeekdays="周"+s;
                        cTime=c.getInt(c.getColumnIndex("cTime"));
                        String cb=Integer.toString(cTime);
                        String ce=Integer.toString(++cTime);
                        cTimes=cb+"-"+ce+"节";
                        cTimes=cWeekdays+" "+cTimes;
                        curWeeks=curWeek+"周";
                    }while(c.moveToNext());
                    c.close();
                }

                if(!cName.equals("")) {
                    Bundle bundle = new Bundle();
                    bundle.putString("CName", cName);
                    bundle.putString("CAddr", cAddr);
                    bundle.putString("CTeacher",cTeacher);
                    bundle.putString("CTime",cTimes);
                    bundle.putString("CCurWeek",curWeeks);
                    if(!cTeacher.equals("无")) {
                        Intent intent = new Intent(MainActivity.this, ShowDetails.class);
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }else{
                        AlertDialog.Builder builder=new AlertDialog.Builder(MainActivity.this);
                        LayoutInflater inflater=LayoutInflater.from(MainActivity.this);
                        View viewDialog=inflater.inflate(R.layout.affair,null);
                        final EditText event=(EditText)viewDialog.findViewById(R.id.event);
                        final EditText place=(EditText)viewDialog.findViewById(R.id.place);
                        event.setText(cName);
                        place.setText(cAddr);

                        builder.setView(viewDialog);
                        builder.setTitle("编辑事件");
                        builder.setIcon(android.R.drawable.ic_menu_save);
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                String eventthing=event.getText().toString();
                                String placething=place.getText().toString();
                                if(eventthing.equals("")&&placething.equals("")){
                                    Toast.makeText(getApplicationContext(), "数据不能为空",
                                            Toast.LENGTH_SHORT).show();
                                    return;

                                }
                                ContentValues cValue=new ContentValues();
                                cValue.put(CourseDB.cName,eventthing);
                                cValue.put(CourseDB.cAddr,placething);
                                String cWeekday1=Integer.toString(cWeekday);
                                String cTime1=Integer.toString(--cTime);
                                String curWeek1=Integer.toString(curWeek);
                                int n=courseDB.updateByCourse(cValue,CourseDB.cWeekday+"=? and "+CourseDB.cTime+"=? and "+CourseDB.cWeeks+"=?",new String[]{cWeekday1,cTime1,curWeek1});
                                if (n>0){
                                    Toast.makeText(getApplicationContext(), "修改数据成功",
                                            Toast.LENGTH_SHORT).show();
                                }else{
                                    Toast.makeText(getApplicationContext(),"未知错误!",
                                            Toast.LENGTH_LONG).show();
                                }
                                int setcurWeek=curWeek-1;
                                showWeekCourse(setcurWeek);
                            }
                        });
                        builder.setNeutralButton("CANCEL",null);
                        builder.setNegativeButton("DELETE",new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                String cWeekday1=Integer.toString(cWeekday);
                                String cTime1=Integer.toString(--cTime);
                                String curWeek1=Integer.toString(curWeek);
                                int n=courseDB.deleteByCourse(CourseDB.cWeekday+"=? and "+CourseDB.cTime+"=? and "+CourseDB.cWeeks+"=?",new String[]{cWeekday1,cTime1,curWeek1});
                                if (n>0){
                                    Toast.makeText(getApplicationContext(),"删除数据成功",
                                            Toast.LENGTH_SHORT).show();
                                }else{
                                    Toast.makeText(getApplicationContext(),"未知错误!",
                                            Toast.LENGTH_LONG).show();
                                }
                                int setcurWeek=curWeek-1;
                                showWeekCourse(setcurWeek);
                            }
                        });
                        builder.create().show();

                    }
                }else{
                 //   final EditText inputServer = new EditText(MainActivity.this);
                  /* LayoutInflater inflater = getLayoutInflater();
                    View layout = inflater.inflate(R.layout.affair,
                            (ViewGroup) findViewById(R.id.dialog));
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle("Affair").setIcon(android.R.drawable.ic_dialog_info).setView(layout)
                            .setNegativeButton("Cancel", null);
                    builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int which) {
                          //  textView3.setText( inputServer.getText().toString());
                            AlertDialog.Builder builder=new AlertDialog.Builder(MainActivity.this);
                            LayoutInflater inflater=LayoutInflater.from(MainActivity.this);
                            View viewDialog=inflater.inflate(R.layout.affair,null);
                            final EditText event=(EditText)viewDialog.findViewById(R.id.event);
                            final EditText place=(EditText)viewDialog.findViewById(R.id.place);
                             String eventthing=event.getText().toString();
                             String placething=place.getText().toString();
                            ContentValues cValue=new ContentValues();
                            cValue.put(CourseDB.cName,eventthing);
                            cValue.put(CourseDB.cAddr,placething);
                            cValue.put(CourseDB.cTeacher,"无");
                            String curWeeks=Integer.toString(curWeek);
                            cValue.put(CourseDB.cWeeks,curWeeks);
                            cValue.put(CourseDB.cWeekday,WeekDay);
                            cValue.put(CourseDB.cTime,cTs);
                            courseDB.insertCourse(cValue);
                            gridCourse.deferNotifyDataSetChanged();

                        }
                    });
                    builder.show();*/
                    AlertDialog.Builder builder=new AlertDialog.Builder(MainActivity.this);
                    LayoutInflater inflater=LayoutInflater.from(MainActivity.this);
                    View viewDialog=inflater.inflate(R.layout.affair,null);
                    final EditText event=(EditText)viewDialog.findViewById(R.id.event);
                    final EditText place=(EditText)viewDialog.findViewById(R.id.place);

                    builder.setView(viewDialog);
                    builder.setTitle("添加事件");
                    builder.setIcon(android.R.drawable.ic_menu_save);
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            String eventthing=event.getText().toString();
                            String placething=place.getText().toString();
                            if(eventthing.equals("")||placething.equals("")){
                                Toast.makeText(getApplicationContext(), "数据不能为空",
                                        Toast.LENGTH_SHORT).show();
                                return;

                            }
                            ContentValues cValue=new ContentValues();
                            cValue.put(CourseDB.cName,eventthing);
                            cValue.put(CourseDB.cAddr,placething);
                            cValue.put(CourseDB.cTeacher,"无");
                            String curWeeks=Integer.toString(curWeek);
                            cValue.put(CourseDB.cWeeks,curWeeks);
                            cValue.put(CourseDB.cWeekday,WeekDay);
                            cValue.put(CourseDB.cTime,cTs);
                            long row=courseDB.insertCourse(cValue);
                            if(row!=0){
                                Toast.makeText(getApplicationContext(), "插入数据成功",
                                        Toast.LENGTH_SHORT).show();
                              //   Intent intent=new Intent(MainActivity.this, TaskServices.class);
                              //    startService(intent);
                            }else{
                                Toast.makeText(getApplicationContext(),"未知错误!",
                                        Toast.LENGTH_LONG).show();
                            }
                            int setcurWeek=curWeek-1;
                            showWeekCourse(setcurWeek);

                        }
                    });
                    builder.setNegativeButton("Cancel",null);
                    builder.create().show();
                }

            }
        };

        btSet.setOnClickListener(listenerSet);
        selectWeek.setOnItemSelectedListener(listenerSelectWeek);
        gridCourse.setOnItemClickListener(listenerGridCourse);
        Button btMain = (Button) findViewById(R.id.btMain) ;
        Button btTask = (Button) findViewById(R.id.btTask) ;
        Button btTime=(Button)findViewById(R.id.btTime);
        btTask.setOnClickListener(new NavListener(MainActivity.this));
        btMain.setOnClickListener(new NavListener(MainActivity.this));
        btTime.setOnClickListener(new NavListener(MainActivity.this));
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
            xlsSetDB.updateByClause(DB_TABLE,values,null,null);
        }
        cur.close();
        //设置下拉框的值为当前周次
        selectWeek.setSelection(curWeek-1);
    }

    @TargetApi(23)
    @Override
    protected void onActivityResult(int requestCode,int resultCode , Intent data){
        switch(requestCode){
            case REQUEST_XLSSET_ACTIVITY:
                if(resultCode == XlsSetActivity.CHANGEXLS){
                    int position = data.getIntExtra("curWeek",1) -1;

                    showWeekCourse(position);
                }
                else if(resultCode == XlsSetActivity.NOCHANGE){
                    int position = data.getIntExtra("curWeek",1) -1;

                    showWeekCourse(position);
                }
                break ;
        }
    }

    private void showWeekCourse(int position){

        selectWeek.setSelection(position);
        Integer curWeek = position+1 ;
        String [] from = new String [] { CourseDB.cName,CourseDB.cAddr} ;
        int [] to = new int[]{R.id.courseName,R.id.courseAddr};
        List <Map<String,String>> cNameAndtNameList =  readSqlite.getSelectWeekData(curWeek) ;
        SimpleAdapter simpleAdapter = new SimpleAdapter(MainActivity.this,cNameAndtNameList,R.layout.courseitem,from,to);
        gridCourse.setAdapter(simpleAdapter);
    }
    private void  showCourseDetails(Integer curWeek , Integer cWeekDay , Integer cTime){
        CourseDB courseDB = new CourseDB(MainActivity.this) ;
        String selections =
                CourseDB.cWeeks +"=? and "+
                CourseDB.cWeekday +"=? and " +
                CourseDB.cTime + "=?" ;
        String []selectArgs = new String[]{curWeek.toString(),cWeekDay.toString(),cTime.toString()} ;
        Cursor cursor = courseDB.queryCourse(null,selections,selectArgs,null,null,null) ;
        if(cursor.getCount() > 0){
            cursor.moveToFirst();

            cursor.moveToNext();
        }
        cursor.close();
    }
}




