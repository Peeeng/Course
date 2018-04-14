package com.example.j_king.course;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.j_king.getsetdata.CourseDB;
import com.example.j_king.getsetdata.ReadSqlite;
import com.example.j_king.getsetdata.XlsSetDB;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;



/**
 * Created by J-King on 2017/9/23.
 */

public class XlsSetActivity extends AppCompatActivity {
    private XlsSetDB xlsSetDB ;
    private CourseDB courseDB ;
    private ReadSqlite readSqlite ;
    private Button btSelectXls;


    private Button btShowXls ;
    private EditText editXlsUrl;
    private Spinner spinnerWeek ;
    private Button btOk ;
    private TextView link;

    private static final int SELECTXLS = 0x0011;
    private static final int SHOWXLS = 0x0012;
    public static final int NOCHANGE = 0x0013 ;
    public static final int CHANGEXLS = 0x0014 ;
    private static final int REQUEST_EXTERNAL_STRONGE = 0x0015 ;

    protected String xlsPath;
    protected int curWeek ;
    private Uri xlsUri;
    private static final String TAG = "XlsSetActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.xlsset);

        xlsSetDB = new XlsSetDB(XlsSetActivity.this) ;
        courseDB = new CourseDB(XlsSetActivity.this);
        readSqlite = new ReadSqlite(XlsSetActivity.this);

        setRequestExternalStronge();
        prepareListen();
        initData();
    }

    public void initData(){
        Cursor cur = xlsSetDB.queryFromXlsSet(new String[]{XlsSetDB.xlsPath,XlsSetDB.curWeek},null,null,null,null,null);
        if (cur.getCount() > 0 ){
            cur.moveToFirst() ;
            xlsPath = cur.getString(cur.getColumnIndex(XlsSetDB.xlsPath)) ;
            curWeek = cur.getInt(cur.getColumnIndex(XlsSetDB.curWeek)) ;
            if(xlsPath != null && !xlsPath.equals("")){
                String [] tmp = xlsPath.split("/");
                String xlsName = tmp[tmp.length-1];
                editXlsUrl.setText(xlsName);
            }

            spinnerWeek.setSelection(curWeek-1);
            xlsPath = null ;
        }
        cur.close();
    }

    /**
     * 绑定按钮的点击事件
     */
    private void prepareListen(){

        btSelectXls = (Button) findViewById(R.id.btSelectXls);
        editXlsUrl = (EditText) findViewById(R.id.editXlsUrl);
        btShowXls = (Button) findViewById(R.id.btShowXls) ;
        spinnerWeek = (Spinner) findViewById(R.id.spinnerWeek) ;
        btOk = (Button) findViewById(R.id.btOk) ;
        link=(TextView)findViewById(R.id.link);
      //  link.setText(Html.fromHtml( "<b>text3:</b> Text with a " + "<a href=\"http://www.baidu.com\">link</a> " +"created in the Java source code using HTML."));
        link.setText(
                Html.fromHtml(
                        "<b>还没有下载课表?</b>"+"<a href=\"http://www.nchu.edu.cn/\">点击这里下载课程表</a>" ));
        link.setMovementMethod(LinkMovementMethod.getInstance());



        View.OnClickListener listenerSelectXls = new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("application/vnd.ms-excel");//设置类型为.xls
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                startActivityForResult(intent, SELECTXLS);
            }
        };

        View.OnClickListener listenerShowXls = new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent showXls = new Intent(Intent.ACTION_VIEW, xlsUri);
                startActivityForResult(showXls,SHOWXLS);
            }
        };

        View.OnClickListener listenerBtOk = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(xlsPath !=  null && !xlsPath.equals("")){
                    updateCourseTableContent(xlsPath);
                }

              /*  CourseUtil courseUtil = new CourseUtil("15046218","King1001","学生");
                String flag =  courseUtil.connectToNchu();
                Log.e(TAG, "onClick: "+flag );
                InputStream in =courseUtil.getInputStream() ;
                try {
                    courseUtil.saveToFile(in,"data/aaaaa.xls") ;
                } catch (IOException e) {
                    e.printStackTrace();
                }
                courseUtil.close();*/

                //返回到上一级（主）窗口
                Intent intent = new Intent() ;
                intent.putExtra("xlsPath",xlsPath);
                curWeek = spinnerWeek.getSelectedItemPosition() + 1  ;
                updateXlsSetTableContent();
                intent.putExtra("curWeek",curWeek) ;
                setResult(CHANGEXLS,intent);

                finish();
            }
        };

        btSelectXls.setOnClickListener( listenerSelectXls);
        btShowXls.setOnClickListener( listenerShowXls ) ;
        btOk.setOnClickListener(listenerBtOk);
    }

    /**
     *
     * @param requestCode 请求码
     * @param resultCode 结果码
     * @param data 返回数据
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        //是否选择，没选择就不会继续
        if (resultCode == Activity.RESULT_OK) {
            //得到uri，后面就是将uri转化成file的过程。
            xlsUri = data.getData();
            switch (requestCode) {
                //请求码为--浏览，获取选中的路径
                case SELECTXLS:
                    xlsPath = getRealFilePath(this,xlsUri);
                    xlsPath = xlsPath.replace("/root","");
                    String [] tmp = xlsPath.split("/");
                    String xlsName = tmp[tmp.length-1];
                    editXlsUrl.setText(xlsName);
                    break;
                //请求码为--预览
                case SHOWXLS:
                    break;
            }
        }
    }

    @TargetApi(26)
    private  String getRealFilePath(final Context context, final Uri uri ) {
        if ( null == uri ) return null;
        final String scheme = uri.getScheme();
        String data = null;
        if ( scheme == null )
            data = uri.getPath();
        else if ( Build.VERSION.SDK_INT >= 26 || ContentResolver.SCHEME_FILE.equals( scheme ) ) {
            data = uri.getPath();
        } else if ( ContentResolver.SCHEME_CONTENT.equals( scheme ) ) {//Images.ImageColumns.DATA
            Cursor cursor = context.getContentResolver().query( uri, new String[] { MediaStore.Images.ImageColumns.DATA }, null, null, null );
            if ( null != cursor ) {
                if ( cursor.moveToFirst() ) {
                    int index = cursor.getColumnIndex( MediaStore.Images.ImageColumns.DATA );
                    if ( index > -1 ) {
                        data = cursor.getString( index );
                    }
                }
                cursor.close();
            }
        }
        return data;
    }

    @TargetApi(23)
    public void setRequestExternalStronge(){
        Log.i(TAG, "onActivityResult: 当前Android设备支持api版本为："+Build.VERSION.SDK_INT);
        if(Build.VERSION.SDK_INT >= 23) {
            int writeStoragePermission = XlsSetActivity.this.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (writeStoragePermission != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(XlsSetActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_EXTERNAL_STRONGE);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //根据请求是否通过的返回码进行判断，然后进一步运行程序
        if (grantResults.length > 0 && requestCode == REQUEST_EXTERNAL_STRONGE && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            ;
        }

    }

    /**
     *
     * 根据xlsPath更新Course表中内容
     */
    public void updateCourseTableContent(String xlsPath){
        try {
            InputStream xls = new FileInputStream(new File(xlsPath ));
            courseDB.deleteTable();
            List<ContentValues> courseList =  readSqlite.getXlsContentValues(xls);
            for(ContentValues values : courseList){
                courseDB.insertCourse(values);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void updateXlsSetTableContent(){

        //修改系统设置表里的路径和周次
        curWeek = spinnerWeek.getSelectedItemPosition() + 1  ;
        ContentValues contentValues = new ContentValues() ;
        contentValues.put(XlsSetDB.curWeek,curWeek) ;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        contentValues.put(XlsSetDB.startDate,simpleDateFormat.format(new Date())) ;
        if(xlsPath != null && !xlsPath.equals(""))
            contentValues.put(XlsSetDB.xlsPath,xlsPath) ;
        Cursor cur = xlsSetDB.queryFromXlsSet(new String[]{XlsSetDB.xlsPath,XlsSetDB.curWeek},null,null,null,null,null);
        if (cur.getCount() <= 0 ){
            contentValues.put(XlsSetDB.xlsSetId,XlsSetDB.defaultId);
            xlsSetDB.insertToXlsSet("1",contentValues);
        }
        else if (cur.getCount()==1){
            int rtn = xlsSetDB.updateByClause(XlsSetDB.DB_TABLE,contentValues,XlsSetDB.xlsSetId+"='"+XlsSetDB.defaultId+"'",null);
            Log.i(TAG, "updateXlsSetTableContent: 更新状态--"+rtn);
        }
        else
            //删除表
            xlsSetDB.deleteTable();
        cur.close();
    }
}

