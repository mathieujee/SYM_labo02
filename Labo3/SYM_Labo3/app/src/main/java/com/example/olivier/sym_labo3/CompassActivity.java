/**
 * File : CompassActivity.java
 *
 * Authors : Jee Mathieu, Kopp Olivier, Silvestri Romain
 *
 * Date : 16.12.2018
 *
 * This activity display on the screen a compass that show where the north is.
 */

package com.example.olivier.sym_labo3;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.opengl.GLSurfaceView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

public class CompassActivity extends AppCompatActivity implements SensorEventListener {

    // Sources:
    // https://www.techrepublic.com/article/pro-tip-create-your-own-magnetic-compass-using-androids-internal-sensors/
    // https://stackoverflow.com/questions/7046608/getrotationmatrix-and-getorientation-tutorial
    // https://developer.android.com/reference/android/hardware/SensorManager.html


    //opengl
    private OpenGLRenderer  opglr         = null;
    private GLSurfaceView m3DView         = null;

    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private Sensor mMagnetometer;

    private float[] mGravs = new float[3];
    private float[] mGeoMags = new float[3];
    private float[] mRotationM = new float[16];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // we need fullscreen
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // we initiate the view
        setContentView(R.layout.activity_compass);

        // link to GUI
        this.m3DView = findViewById(R.id.compass_opengl);

        //we create the 3D renderer
        this.opglr = new OpenGLRenderer(getApplicationContext());

        //init opengl surface view
        this.m3DView.setRenderer(this.opglr);

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mMagnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
    }

    // When the app hibernate, we unregister the sensor
    protected void onPause(){
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    // When the app resume, we register the sensor again
    protected void onResume(){
        super.onResume();
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, mMagnetometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if(event.sensor == mAccelerometer){
            mGravs = event.values.clone();
        }

        if(event.sensor == mMagnetometer){
            mGeoMags = event.values.clone();
        }

        if(SensorManager.getRotationMatrix(mRotationM, null, mGravs, mGeoMags)){
            mRotationM = this.opglr.swapRotMatrix(mRotationM);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
