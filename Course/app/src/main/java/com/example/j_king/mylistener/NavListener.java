package com.example.j_king.mylistener;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Button;

import com.example.j_king.course.R;
import com.example.j_king.course.TaskActivity;
import com.example.j_king.course.TimeActivity;

/**
 * @name Course
 * @class name：com.example.j_king.navlistener
 * @class describe
 * @anthor J-King QQ:1032006226
 * @time 2017/10/30 21:06
 * @change
 * @chang time
 * @class describe
 */
public class NavListener implements View.OnClickListener {
    Context context ;

    public NavListener(Context context){
        this.context = context ;
    }

    @Override
    public void onClick(View view){
        Button bt = (Button)view  ;
        switch (bt.getId()){
            case R.id.btMain :
                break;

            case R.id.btTask:
                Intent intent = new Intent(context,TaskActivity.class) ;
                context.startActivity(intent);
                break;
            case R.id.btTime:
                Intent intent1=new Intent(context,TimeActivity.class);
                context.startActivity(intent1);
                break;
        }
    }
}

