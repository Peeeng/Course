package com.example.j_king.course;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
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

        //请求存储权限
        setRequestExternalStronge();
        //准备监听事件
        prepareListen();
        //初始化数据
        initData();
    }

    /**
     * 初始化数据
     */
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
                    /*xlsPath = getRealFilePath(this,xlsUri);
                    xlsPath = xlsPath.replace("/root","");*/
                    xlsPath = getPath(this,xlsUri);
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


    /**
     * 专为Android4.4设计的从Uri获取文件绝对路径，以前的方法已不好使
     */
    @SuppressLint("NewApi")
    public String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{split[1]};

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }
    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public String getDataColumn(Context context, Uri uri, String selection,
                                String[] selectionArgs) {
        Cursor cursor = null ;
        try {
             cursor = context.getContentResolver().query( uri, new String[] { MediaStore.Images.ImageColumns.DATA }, null, null, null );

            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndex( MediaStore.Images.ImageColumns.DATA);
                if(column_index >= 0 ) {
                    return cursor.getString(column_index);
                }else
                    return  uri.getPath().replace("/root","");
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
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

