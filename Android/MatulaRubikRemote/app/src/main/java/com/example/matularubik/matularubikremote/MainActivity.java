package com.example.matularubik.matularubikremote;

import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    private SensorManager mSensorManager;
    private Sensor mLuz;
    private Sensor mGiroscopio;
    private Sensor mAcelerometro;


    private TextView text;
  //  private TextView textX;
  //  private TextView textY;
  //  private TextView textZ;
    private TextView textW;
    private TextView textE;

    private Button button;

    @Override
    public final void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mLuz = mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        mGiroscopio = mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        mAcelerometro = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        text = findViewById(R.id.test);
    //    textX = findViewById(R.id.textView2);
    //    textY = findViewById(R.id.textView3);
    //    textZ = findViewById(R.id.textView4);
        textW = findViewById(R.id.textView5);
        textE = findViewById(R.id.textView6);


        button = findViewById(R.id.button);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Mensajeria.EnviarMoverDA();
            }
        });

    }

    @Override
    public final void onAccuracyChanged(Sensor sensor, int accuracy) {
    }


    private static final float SHAKE_THRESHOLD_GRAVITY = 2.7F;
    private static final int SHAKE_SLOP_TIME_MS = 500;
    private static final int SHAKE_COUNT_RESET_TIME_MS = 3000;

    private long mShakeTimestamp;
    private int mShakeCount;

    int i=0;
    boolean cambio = false;
    @Override
    public final void onSensorChanged(SensorEvent event) {

        synchronized (this) {
            switch (event.sensor.getType()) {
                case (Sensor.TYPE_LIGHT):
                    float lux = event.values[0];
                    if(lux < 4){
                        if(cambio  == true)
                            Mensajeria.EnviarMoverXC();
                        cambio = false;
                    }
                    else
                        cambio = true;
                    textW.setText(String.valueOf(event.values[0]));
                    break;
                case (Sensor.TYPE_ROTATION_VECTOR):

                    float[] rotationMatrix = new float[16];
                    SensorManager.getRotationMatrixFromVector(
                            rotationMatrix, event.values);

                    // Remap coordinate system
                    float[] remappedRotationMatrix = new float[16];
                    SensorManager.remapCoordinateSystem(rotationMatrix,
                            SensorManager.AXIS_X,
                            SensorManager.AXIS_Z,
                            remappedRotationMatrix);

                    // Convert to orientations
                    float[] orientations = new float[3];
                    SensorManager.getOrientation(remappedRotationMatrix, orientations);


                /*    textX.setText(String.valueOf(orientations[0]));
                    textY.setText(String.valueOf(orientations[1]));
                    textZ.setText(String.valueOf(orientations[2]));
*/

                    if(orientations[2] > 0.4) {
                        text.setText("HORARIO");
                    } else if(orientations[2] < -0.4) {
                        text.setText("anti HORARIO");
                    } else  {
                        text.setText("nada");
                    }


                    break;
                case (Sensor.TYPE_ACCELEROMETER):
                 /*       if ((Math.abs(event.values[2]) > 50 ))
                        {
                            i++;
                            if(i>=4)
                                i=0;
                            getWindow().getDecorView().setBackgroundColor(coloresX[i]);
                        }
                    if ( Math.abs(event.values[0]) > 50 )
                    {
                        i++;
                        if(i>=2)
                            i=0;
                        getWindow().getDecorView().setBackgroundColor(coloresY[i]);
                    }
*/

                    float x = event.values[0];
                    float y = event.values[1];
                    float z = event.values[2];

                    float gX = x / SensorManager.GRAVITY_EARTH;
                    float gY = y / SensorManager.GRAVITY_EARTH;
                    float gZ = z / SensorManager.GRAVITY_EARTH;

                    // gForce will be close to 1 when there is no movement.
                    float gForce = (float)Math.sqrt(gX * gX + gY * gY + gZ * gZ);

                    if (gForce > SHAKE_THRESHOLD_GRAVITY) {
                        final long now = System.currentTimeMillis();
                        // ignore shake events too close to each other (500ms)
                        if (mShakeTimestamp + SHAKE_SLOP_TIME_MS > now) {
                            return;
                        }

                        // reset the shake count after 3 seconds of no shakes
                        if (mShakeTimestamp + SHAKE_COUNT_RESET_TIME_MS < now) {
                            mShakeCount = 0;
                        }

                        mShakeTimestamp = now;
                        mShakeCount++;

                        i++;
                        if(i>=3)i=0;
                        if(i==0)
                            textW.setText("LATERAL");
                        if(i==1)
                            textW.setText("SUP");
                        if(i==2)
                            textW.setText("INF");
                    }

                    break;
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mLuz, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, mGiroscopio, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, mAcelerometro, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

}