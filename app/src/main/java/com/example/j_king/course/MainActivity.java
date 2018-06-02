package com.example.j_king.course;


import android.annotation.TargetApi;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.j_king.getsetdata.CourseDB;
import com.example.j_king.getsetdata.CurWeekSet;
import com.example.j_king.getsetdata.ReadSqlite;
import com.example.j_king.getsetdata.XlsSetDB;
import com.example.j_king.mylistener.NavListener;
import com.example.j_king.pojo.CourseData;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static com.example.j_king.course.R.id.event;
import static com.example.j_king.course.R.id.place;
import static com.example.j_king.getsetdata.CourseDB.cAddr;
import static com.example.j_king.getsetdata.CourseDB.cName;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private CourseDB courseDB ;
    //请求课程设置的code
    private static final int REQUEST_XLSSET_ACTIVITY = 0x0002;
    private static final String[] weekdays = {"", "一", "二", "三", "四", "五", "六", "日"};
    private Spinner selectWeek;
    private GridView gridCourse;

    private ReadSqlite readSqlite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.course);

        courseDB = new CourseDB(MainActivity.this);
        readSqlite = new ReadSqlite(this);
        //监听各按钮的点击事件
        prepareListener();
        //设置主界面的当前周次数据
        setCurWeek();
    }

    private void prepareListener() {

        selectWeek = (Spinner) findViewById(R.id.selectWeek);
        gridCourse = (GridView) findViewById(R.id.gridCourse);

        //监听设置按钮，打开设置课程表路径界面
        View.OnClickListener listenerSet = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, XlsSetActivity.class);
                startActivityForResult(intent, REQUEST_XLSSET_ACTIVITY);
            }
        };

        //监听周次下拉框，显示选中周次的课程信息
        AdapterView.OnItemSelectedListener listenerSelectWeek = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //显示选中周次的课程信息
                showWeekCourse(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectWeek.setSelection(0);
            }
        };

        /*
        监听课程Item点击事件
            若存在课程，则显示课程详细信息。
            若不存在课程，则弹出新增事件编辑框
            若存在时间，则弹出修改事件编辑框
         */
        AdapterView.OnItemClickListener listenerGridCourse = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //行求商，列求余。
                //获取当前周次
                final int curWeek = selectWeek.getSelectedItemPosition() + 1;

                int colnum = gridCourse.getNumColumns();//获取列数量
                int rowIndex = position / colnum;//获取行标，从 0 开始计数
                //获取当前点击的上课节次
                final int cTime = rowIndex * 2 + 1;
                //获取当前点击的星期
                final int cWeekday = position % colnum + 1;

                List<CourseData> courseDataList = getCurCourseData(curWeek, cWeekday, cTime);

                if(courseDataList.size() == 0)
                    showAddCourseDialog(curWeek, cWeekday, cTime);
                else{
                    CourseData course = courseDataList.get(0);
                    if (course.getcTeacher().equals("无")) {
                        showDeleteCourseDialog(course);
                    }else{
                        showCourseDetail(course);
                    }
                }
            }
        };

        selectWeek.setOnItemSelectedListener(listenerSelectWeek);
        gridCourse.setOnItemClickListener(listenerGridCourse);

        findViewById(R.id.btSet).setOnClickListener(listenerSet);

        findViewById(R.id.btTask).setOnClickListener(new NavListener(MainActivity.this));
        findViewById(R.id.btMain).setOnClickListener(new NavListener(MainActivity.this));
        findViewById(R.id.btTime).setOnClickListener(new NavListener(MainActivity.this));
    }

    /**
     * 显示课程详情--名称、时间、地点、教师、教室、周次
     * @param courseData 课程详细数据
     */
    private void showCourseDetail(CourseData courseData) {
        String cTimes = "周 " + weekdays[courseData.getcWeekday()] + courseData.getcTime() + "-" + (courseData.getcTime() + 1) + " 节";

        Bundle bundle = new Bundle();
        bundle.putString("CName", courseData.getcName());
        bundle.putString("CAddr", courseData.getcAddr());
        bundle.putString("CTeacher", courseData.getcTeacher());
        bundle.putString("CTime", cTimes);
        bundle.putString("CCurWeek", courseData.getcWeeks() + "周");

        Intent intent = new Intent(MainActivity.this, ShowDetails.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    /**
     * 显示删除(编辑)事件的对话框
     * @param courseData 事件的详细数据
     */
    private void showDeleteCourseDialog(final CourseData courseData){
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        LayoutInflater inflater = LayoutInflater.from(MainActivity.this);
        View viewDialog = inflater.inflate(R.layout.affair, null);
        final EditText eventBtn = (EditText) viewDialog.findViewById(event);
        final EditText placeBtn = (EditText) viewDialog.findViewById(place);
        eventBtn.setText(courseData.getcName());
        placeBtn.setText(courseData.getcAddr());

        builder.setView(viewDialog);
        builder.setTitle("编辑事件");
        builder.setIcon(android.R.drawable.ic_menu_save);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String event = eventBtn.getText().toString();
                String place = placeBtn.getText().toString();
                if (event.equals("") || place.equals("")) {
                    Toast.makeText(getApplicationContext(), "数据不能为空",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                ContentValues cValue = new ContentValues();
                cValue.put(cName, event);
                cValue.put(cAddr, place);
                String selection = CourseDB.cWeekday + "=? and " + CourseDB.cTime + "=? and " + CourseDB.cWeeks + "=?";
                String [] selectionArgs = new String[]{courseData.getcWeekday().toString(), courseData.getcTime().toString(), courseData.getcWeeks()};
                int n = courseDB.updateByCourse(cValue, selection, selectionArgs);
                if (n > 0) {
                    Toast.makeText(getApplicationContext(), "修改数据成功",
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "未知错误!",
                            Toast.LENGTH_LONG).show();
                }
                showWeekCourse(Integer.valueOf(courseData.getcWeeks())-1);
            }
        });
        builder.setNeutralButton("CANCEL", null);
        builder.setNegativeButton("DELETE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                String selection = CourseDB.cWeekday + "=? and " + CourseDB.cTime + "=? and " + CourseDB.cWeeks + "=?";
                String [] selectionArgs = new String[]{courseData.getcWeekday().toString(), courseData.getcTime().toString(), courseData.getcWeeks()};
                int n = courseDB.deleteByCourse(selection, selectionArgs);
                if (n > 0) {
                    Toast.makeText(getApplicationContext(), "删除数据成功",
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "未知错误!",
                            Toast.LENGTH_LONG).show();
                }
                showWeekCourse(Integer.valueOf(courseData.getcWeeks())-1);
            }
        });
        builder.create().show();

    }

    private void showAddCourseDialog(final Integer curWeek, final Integer weekday, final Integer cTime){
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        LayoutInflater inflater = LayoutInflater.from(MainActivity.this);
        View viewDialog = inflater.inflate(R.layout.affair, null);
        final EditText eventBtn = (EditText) viewDialog.findViewById(event);
        final EditText placeBtn = (EditText) viewDialog.findViewById(place);

        builder.setView(viewDialog);
        builder.setTitle("添加事件");
        builder.setIcon(android.R.drawable.ic_menu_save);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String event = eventBtn.getText().toString();
                String place = placeBtn.getText().toString();
                if (event.equals("") || place.equals("")) {
                    Toast.makeText(getApplicationContext(), "数据不能为空",
                            Toast.LENGTH_SHORT).show();
                    return;

                }
                ContentValues cValue = new ContentValues();
                cValue.put(CourseDB.cName, event);
                cValue.put(CourseDB.cAddr, place);
                cValue.put(CourseDB.cTeacher, "无");
                cValue.put(CourseDB.cWeeks, Integer.toString(curWeek));
                cValue.put(CourseDB.cWeekday, weekday);
                cValue.put(CourseDB.cTime, cTime);
                long row = courseDB.insertCourse(cValue);
                if (row != 0) {
                    Toast.makeText(getApplicationContext(), "插入数据成功",
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "未知错误!",
                            Toast.LENGTH_LONG).show();
                }
                showWeekCourse(curWeek-1);

            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.create().show();
    }

    /**
     * 初始化数据：设置当前周次
     */
    private void setCurWeek() {
        //计算当前周次
        CurWeekSet curWeekSet = new CurWeekSet(this);
        int curWeek = curWeekSet.getNewCurWeek();
        //更新xlsset表中的curWeek
        ContentValues values = new ContentValues();
        values.put(XlsSetDB.curWeek, curWeek);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
        values.put(XlsSetDB.startDate, simpleDateFormat.format(new Date()));

        XlsSetDB xlsSetDB = new XlsSetDB(this);
        Cursor cur = xlsSetDB.queryFromXlsSet(new String[]{XlsSetDB.xlsSetId}, null, null, null, null, null);
        if (cur.getCount() <= 0) {
            values.put(XlsSetDB.xlsSetId, XlsSetDB.defaultId);
            xlsSetDB.insertToXlsSet("1", values);
        } else {
            xlsSetDB.updateByClause(XlsSetDB.DB_TABLE, values, null, null);
        }
        cur.close();
        //设置下拉框的值为当前周次
        selectWeek.setSelection(curWeek - 1);
    }

    @TargetApi(23)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_XLSSET_ACTIVITY:
                if (resultCode == XlsSetActivity.CHANGEXLS) {
                    int position = data.getIntExtra("curWeek", 1) - 1;
                    showWeekCourse(position);
                } else if (resultCode == XlsSetActivity.NOCHANGE) {
                    int position = data.getIntExtra("curWeek", 1) - 1;
                    showWeekCourse(position);
                }
                break;
        }
    }

    /**
     * 显示指定周次的课程信息
     *
     * @param position 周次下拉框position
     */
    private void showWeekCourse(int position) {
        selectWeek.setSelection(position);
        Integer curWeek = position + 1;
        String[] from = new String[]{cName, cAddr};
        int[] to = new int[]{R.id.courseName, R.id.courseAddr};
        List<Map<String, String>> cNameAndtNameList = readSqlite.getSelectWeekData(curWeek);
        SimpleAdapter simpleAdapter = new SimpleAdapter(MainActivity.this, cNameAndtNameList, R.layout.courseitem, from, to);
        gridCourse.setAdapter(simpleAdapter);
    }

    private List<CourseData> getCurCourseData(Integer curWeek, Integer cWeekday, Integer cTime) {
        String selection =
                CourseDB.cWeeks + "=? and " +
                        CourseDB.cWeekday + "=? and " +
                        CourseDB.cTime + "=?";
        String[] selectionArgs = {String.valueOf(curWeek), String.valueOf(cWeekday), String.valueOf(cTime)};
        return courseDB.queryCourseData(null, selection, selectionArgs, null, null, null);
    }
}




