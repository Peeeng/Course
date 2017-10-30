package com.example.j_king.myView;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.widget.GridView;
import android.widget.TextView;

import com.example.j_king.course.R;

/**
 * Created by J-King on 2017/10/20.
 */

public class LineGridView extends GridView {
    private int rownum ;

    public LineGridView(Context context){
        super(context) ;
    }

    public LineGridView(Context context , AttributeSet attributeSet){
        super(context,attributeSet) ;
    }

    public LineGridView(Context context , AttributeSet attributeSet , int defStyle){
        super(context , attributeSet , defStyle);
    }

    @Override
    protected void onDraw(Canvas canvas){
        super.onDraw(canvas);
        int colnum = getNumColumns(); //获取列数
        int total = getChildCount();  //获取Item总数

        //计算行数
        rownum = (total-1)/colnum + 1 ;

        Paint localPaint = new Paint();//设置画笔
        localPaint.setStyle(Paint.Style.STROKE); //画笔实心
        localPaint.setColor(getContext().getResources().getColor(R.color.grid_line));//画笔颜色


        localPaint.setStrokeWidth(1);
        if(total == 0)
            return ;
        View view0 = getChildAt(0); //第一个view
        View viewColLast = getChildAt(colnum - 1);//第一行最后一个view
        View viewRowLast = getChildAt((rownum - 1) * colnum); //第一列最后一个view


        for (int i = 1, c = 1; i < rownum || c < colnum; i++, c++) {
            //画横线
            canvas.drawLine(view0.getLeft(), view0.getBottom() * i, viewColLast.getRight(), viewColLast.getBottom() * i, localPaint);
            //画竖线
            canvas.drawLine(view0.getRight() * c, view0.getTop(), viewRowLast.getRight() * c, viewRowLast.getBottom(), localPaint);

        }


        for(int j = 0 ;j < colnum ; j++){
            Paint rectPaint = new Paint();
            TypedArray colors = getResources().obtainTypedArray(R.array.itemColors);

            rectPaint.setColor(colors.getColor(j,0));

            for(int i = 0 ; i < rownum ; i++){
                View view = getChildAt(i * colnum + j) ;
                TextView courseName = (TextView) view.findViewById(R.id.courseName);
                if(courseName.getText() != null && !courseName.getText().equals("")){
                    RectF rect = new RectF();
                    int padding = 6 ;
                    float round =10f ;
                    rect.set(view.getLeft()+padding,view.getTop()+padding,view.getRight()-padding,view.getBottom()-padding);
                    canvas.drawRoundRect(rect,round,round,rectPaint);
                }
            }
        }

    }

}
