package com.example.j_king.mylistener;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.util.Log;

/**
 * @name Course
 * @class name：com.example.j_king.mylistener
 * @class describe
 * @anthor J-King QQ:2354345263
 * @time 2017/12/27 15:32
 */
public class MySensorEventListener implements SensorEventListener {

    @Override
    public void onSensorChanged(SensorEvent event) {
        //获取传感器类型
        int sensorType = event.sensor.getType();
        //values[0]:X轴，values[1]：Y轴，values[2]：Z轴
        float[] values = event.values;
        //如果传感器类型为加速度传感器，则判断是否为摇一摇
        if(sensorType == Sensor.TYPE_ACCELEROMETER){
            if ((Math.abs(values[0]) > 12 || Math.abs(values[1]) > 12 || Math
                    .abs(values[2]) > 15))
            {
                Log.e("sensor x ", "============ values[0] = " + values[0]);
                Log.e("sensor y ", "============ values[1] = " + values[1]);
                Log.e("sensor z ", "============ values[2] = " + values[2]);
                //在这里编写功能代码。。。
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
