package com.example.j_king.course;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.example.j_king.getsetdata.SharedPreferencesHelper;
import com.example.j_king.myutil.CourseSpeakUtil;

import java.util.Calendar;

/**
 * Created by 杨通 on 2017/11/22.
 */

public class TimeActivity extends AppCompatActivity {
    private TextView time12Text;
    private TextView time56Text;
    private TextView time34Text;
    private TextView time78Text;
    private TextView time910Text;
    private TextView time1112Text;

    private Spinner selectAlarmCount;
    private SharedPreferencesHelper sp;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.time);
        sp = new SharedPreferencesHelper(TimeActivity.this, "taskConfig");
        selectAlarmCount = (Spinner) findViewById(R.id.selectAlarmCount);
        selectAlarmCount.setSelection(sp.getInt("setAlarmCount") - 1);
        time12Text = (TextView) findViewById(R.id.time12Text);
        time34Text = (TextView) findViewById(R.id.time34Text);
        time56Text = (TextView) findViewById(R.id.time56Text);
        time78Text = (TextView) findViewById(R.id.time78Text);
        time910Text = (TextView) findViewById(R.id.time910Text);
        time1112Text = (TextView) findViewById(R.id.time1112Text);

        time12Text.setText(sp.getString(CourseSpeakUtil.course12Time));
        time34Text.setText(sp.getString(CourseSpeakUtil.course34Time));
        time56Text.setText(sp.getString(CourseSpeakUtil.course56Time));
        time78Text.setText(sp.getString(CourseSpeakUtil.course78Time));
        time910Text.setText(sp.getString(CourseSpeakUtil.course910Time));
        time1112Text.setText(sp.getString(CourseSpeakUtil.course1112Time));
        Touch touch = new Touch();
        TouchListener touchListener = new TouchListener();
        time12Text.setOnTouchListener(touch);
        time34Text.setOnTouchListener(touch);
        time56Text.setOnTouchListener(touch);
        time78Text.setOnTouchListener(touch);
        time910Text.setOnTouchListener(touch);
        time1112Text.setOnTouchListener(touch);


        time12Text.setOnFocusChangeListener(touchListener);
        time34Text.setOnFocusChangeListener(touchListener);
        time56Text.setOnFocusChangeListener(touchListener);
        time78Text.setOnFocusChangeListener(touchListener);
        time910Text.setOnFocusChangeListener(touchListener);
        time1112Text.setOnFocusChangeListener(touchListener);
        AdapterView.OnItemSelectedListener listenerSelectCount = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                final int setCount = selectAlarmCount.getSelectedItemPosition() + 1;
                sp.putValue("setAlarmCount", setCount);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        };
        selectAlarmCount.setOnItemSelectedListener(listenerSelectCount);
        Button quit = (Button) findViewById(R.id.quit);
        quit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    class TouchListener implements View.OnFocusChangeListener {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {

            showDatePickDlg(v.getId());

        }

    }

    class Touch implements View.OnTouchListener {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                showDatePickDlg(v.getId());
                return true;
            }
            return false;
        }
    }

    protected void showDatePickDlg(final int id) {
        Calendar calendar = Calendar.getInstance();
        TimePickerDialog datePickerDialog = new TimePickerDialog(TimeActivity.this, new TimePickerDialog.OnTimeSetListener() {

            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                TextView timeSetText = (TextView) findViewById(id);

                String minutes;
                String hourOfDays;
                if (minute <= 9) {
                    minutes = Integer.toString(minute);
                    minutes = "0" + minutes;
                } else {
                    minutes = Integer.toString(minute);
                }
                if (hourOfDay <= 9) {
                    hourOfDays = Integer.toString(hourOfDay);
                    hourOfDays = "0" + hourOfDays;
                } else {
                    hourOfDays = Integer.toString(hourOfDay);
                }
                timeSetText.setText(hourOfDays + ":" + minutes);
                String AllMinutes = hourOfDays + ":" + minutes;
                switch (id) {
                    case R.id.time12Text:
                        sp.putValue(CourseSpeakUtil.course12HTime, hourOfDay);
                        sp.putValue(CourseSpeakUtil.course12MTime, minute);
                        sp.putValue(CourseSpeakUtil.course12Time, AllMinutes);
                        break;
                    case R.id.time34Text:
                        sp.putValue(CourseSpeakUtil.course34HTime, hourOfDay);
                        sp.putValue(CourseSpeakUtil.course34MTime, minute);
                        sp.putValue(CourseSpeakUtil.course34Time, AllMinutes);
                        break;
                    case R.id.time56Text:
                        sp.putValue(CourseSpeakUtil.course56HTime, hourOfDay);
                        sp.putValue(CourseSpeakUtil.course56MTime, minute);
                        sp.putValue(CourseSpeakUtil.course56Time, AllMinutes);
                        break;
                    case R.id.time78Text:
                        sp.putValue(CourseSpeakUtil.course78HTime, hourOfDay);
                        sp.putValue(CourseSpeakUtil.course78MTime, minute);
                        sp.putValue(CourseSpeakUtil.course78Time, AllMinutes);
                        break;
                    case R.id.time910Text:
                        sp.putValue(CourseSpeakUtil.course910HTime, hourOfDay);
                        sp.putValue(CourseSpeakUtil.course910MTime, minute);
                        sp.putValue(CourseSpeakUtil.course910Time, AllMinutes);
                        break;
                    case R.id.time1112Text:
                        sp.putValue(CourseSpeakUtil.course1112HTime, hourOfDay);
                        sp.putValue(CourseSpeakUtil.course1112MTime, minute);
                        sp.putValue(CourseSpeakUtil.course1112Time, AllMinutes);
                        break;

                }

            }
        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);
        datePickerDialog.show();
    }

}
