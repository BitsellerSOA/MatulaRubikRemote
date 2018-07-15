package com.example.adrianradice.matularubik;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;

import org.json.JSONObject;

import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, SensorEventListener {


    private SensorManager mSensorManager;
    private Sensor mLuz;
    private Sensor mGiroscopio;
    private Sensor mAcelerometro;
    private ImageView image;
    private ImageView image2;
    private ImageView image3;

    private Button btn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Cambiar", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mLuz = mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        mGiroscopio = mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        mAcelerometro = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);


        image    = findViewById(R.id.imageView3);
        image2    = findViewById(R.id.imageView5);
        image3    = findViewById(R.id.imageView8);
        btn = findViewById(R.id.button);
        btn.setVisibility(View.INVISIBLE);

        btn.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(btn.getContext());
                String dir = "http://".concat(pref.getString("ip","192.168.1.50").concat(":").concat(pref.getString("port","8080")).concat("/"));

                new MensajeriaASYN().execute(dir);

            }
        });


        image.setVisibility(View.INVISIBLE);
        image2.setVisibility(View.INVISIBLE);

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



    private static final float SHAKE_THRESHOLD_GRAVITY = 2.7F;
    private static final int SHAKE_SLOP_TIME_MS = 500;
    private static final int SHAKE_COUNT_RESET_TIME_MS = 3000;

    private long mShakeTimestamp;
    private int mShakeCount;

    int i=0;
    boolean cambio = false;

    boolean f = false;

    @Override
    public final void onSensorChanged(SensorEvent event) {

        synchronized (this) {
            switch (event.sensor.getType()) {
                case (Sensor.TYPE_ROTATION_VECTOR):

                    if(!f )
                        return;
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
                        image2.setVisibility(View.VISIBLE);
                        image2.setImageResource(R.drawable.ic_mov_horario);
                    } else if(orientations[2] < -0.4) {
                        image2.setVisibility(View.VISIBLE);
                        image2.setImageResource(R.drawable.ic_mov_anti);
                    } else  {
                        image2.setVisibility(View.INVISIBLE);
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

                    float gForce = (float)Math.sqrt(gX * gX + gY * gY + gZ * gZ);

                    if (gForce > SHAKE_THRESHOLD_GRAVITY) {
                        final long now = System.currentTimeMillis();
                        if (mShakeTimestamp + SHAKE_SLOP_TIME_MS > now) {
                            return;
                        }

                        if (mShakeTimestamp + SHAKE_COUNT_RESET_TIME_MS < now) {
                            mShakeCount = 0;
                        }

                        mShakeTimestamp = now;
                        mShakeCount++;

                        if(!f) {
                            f = true;
                            image.setVisibility(View.VISIBLE);
                            image2.setVisibility(View.VISIBLE);

                            btn.setVisibility(View.VISIBLE);
                            image3.setVisibility(View.INVISIBLE);
                        }

                        i++;
                        if(i>=3)i=0;
                        if(i==0)
                            image.setImageResource(R.drawable.ic_mov_lateral);
                        if(i==1)
                            image.setImageResource(R.drawable.ic_mov_sup);
                        if(i==2)
                            image.setImageResource(R.drawable.ic_mov_inferior);
                    }

                    break;
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {


            Intent homeIntent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(homeIntent);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {


        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        }else if (id == R.id.nav_acerca) {

            Intent homeIntent = new Intent(MainActivity.this, AcercaActivity.class);
            startActivity(homeIntent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    boolean test = false;
    private class MensajeriaASYN extends AsyncTask<String, Void, Void> {

        private final static char  LC='L';
        private final static char  LA='Y';
        private final static char  UC='U';
        private final static char  UA='V';
        private final static char  DC='D';
        private final static char  DA='Z';
        private final static char  XC='X';
        private final static String  MOVER="MOV";

        @Override
        protected void onPreExecute() {

            test = true;
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            test = false;
            super.onPostExecute(aVoid);
        }

        @Override
        protected Void doInBackground(String... voids) {
            try {

                URL url = new URL(voids[0]);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
                conn.setRequestProperty("Accept","application/json");
                conn.setDoOutput(true);
                conn.setDoInput(true);

                JSONObject jsonParam = new JSONObject();
                jsonParam.put(MOVER, String.valueOf(XC));

                DataOutputStream os = new DataOutputStream(conn.getOutputStream());
                os.writeBytes(jsonParam.toString());

                conn.getResponseCode();
                conn.getResponseMessage();

                os.flush();
                os.close();

                conn.disconnect();


            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }


}
