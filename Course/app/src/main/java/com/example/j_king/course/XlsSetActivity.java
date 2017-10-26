package com.example.j_king.course;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.j_king.getsetdata.XlsSetDB;

/**
 * Created by J-King on 2017/9/23.
 */

public class XlsSetActivity extends AppCompatActivity {
    XlsSetDB xlsSetDB ;

    private Button btSelectXls;
    private Button btShowXls ;
    private EditText editXlsUrl;
    private Spinner spinnerWeek ;
    private Button btOk ;


    private static final int SELECTXLS = 1;
    private static final int SHOWXLS = 2;

    protected String xlsPath;
    protected int curWeek ;
    private Uri xlsUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.xlsset);

        xlsSetDB = new XlsSetDB(XlsSetActivity.this) ;
        ActionBar bar = getSupportActionBar();
        bar.hide();
        prepareListen();
        initData();


    }

    public void initData(){
        Cursor cur = xlsSetDB.queryFromXlsSet(new String[]{XlsSetDB.xlsPath,XlsSetDB.curWeek},null,null,null,null,null);
        if (cur.getCount() != 0 ){
            cur.moveToFirst() ;
            xlsPath = cur.getString(cur.getColumnIndex(XlsSetDB.xlsPath)) ;
            curWeek = cur.getInt(cur.getColumnIndex(XlsSetDB.curWeek)) ;
            String [] tmp = xlsPath.split("/");
            String xlsName = tmp[tmp.length-1];
            editXlsUrl.setText(xlsName);
            spinnerWeek.setSelection(curWeek);
            xlsPath = null ;

        }
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

        View.OnClickListener listenerSelectXls = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("*/*");//设置类型，我这里是任意类型，任意后缀的可以这样写。
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
                if(xlsPath==null || xlsPath.isEmpty()){
/*                    Toast toast = Toast.makeText(XlsSetActivity.this,"未修改课程文件",Toast.LENGTH_LONG);
                    toast.show();*/
                    Intent intent = new Intent() ;
                    setResult(RESULT_CANCELED,intent);
                    finish();
                }
                else{
                    xlsSetDB.deleteTable();

                    curWeek = spinnerWeek.getSelectedItemPosition()  ;
                    ContentValues contentValues = new ContentValues() ;
                    contentValues.put(XlsSetDB.curWeek,curWeek) ;
                    contentValues.put(XlsSetDB.xlsPath,xlsPath) ;
                    xlsSetDB.insertToXlsSet("1",contentValues);

                    //返回到上一级（主）窗口
                    Intent intent = new Intent() ;
                    intent.putExtra("xlsPath",xlsPath);
                    intent.putExtra("curWeek",curWeek) ;
                    setResult(RESULT_OK,intent);
                    finish();
                }

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

    public static String getRealFilePath(final Context context, final Uri uri ) {
        if ( null == uri ) return null;
        final String scheme = uri.getScheme();
        String data = null;
        if ( scheme == null )
            data = uri.getPath();
        else if ( ContentResolver.SCHEME_FILE.equals( scheme ) ) {
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

}
