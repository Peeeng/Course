package com.example.j_king.navlistener;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.j_king.course.MainActivity;
import com.example.j_king.course.R;
import com.example.j_king.course.TaskActivity;

import static com.example.j_king.getsetdata.CourseDB.TAG;

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
                ;
            case R.id.btTask:
                Intent intent = new Intent(context,TaskActivity.class) ;
                context.startActivity(intent);
        }
    }
}

