package com.example.j_king.course;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ShowDetails extends AppCompatActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.coursedetails);

        Intent intent=getIntent();
        Bundle bundle = intent.getExtras();

        TextView courseDetailsName=(TextView)findViewById(R.id.courseDetailsName);
        TextView courseDetailsAddr=(TextView)findViewById(R.id.courseDetailsAddr);
        TextView courseDetailsName1=(TextView)findViewById(R.id.courseDetailsName1);
        TextView courseDetailsTeacher=(TextView)findViewById(R.id.courseDetailsTeacher);
        TextView courseDetailsCount=(TextView)findViewById(R.id.courseDetailsCount);
        TextView courseDetailsWeek=(TextView)findViewById(R.id.courseDetailsWeek);

        courseDetailsName.setText(bundle.getString("CName"));
        courseDetailsName1.setText(bundle.getString("CName"));
        courseDetailsAddr.setText(bundle.getString("CAddr"));
        courseDetailsTeacher.setText(bundle.getString("CTeacher"));
        courseDetailsCount.setText(bundle.getString("CTime"));
        courseDetailsWeek.setText(bundle.getString("CCurWeek"));

        Button quitDetailsWeek = (Button) findViewById(R.id.quitDetailsWeek);
        quitDetailsWeek.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
