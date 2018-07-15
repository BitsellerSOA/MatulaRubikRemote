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

    //SENSORES///////////////////////////////
    private SensorManager mSensorManager;
    private Sensor sLuz;
    private Sensor sPosicion;
    private Sensor sAcelerometro;
    /////////////////////////////////////////

    ///CONTROLES/////////////////////////////
    private ImageView imvMotorSeleccionado;
    private ImageView imvSentido;
    private ImageView imvAyuda;
    private Button btnMover;
    /////////////////////////////////////////

    //Maquina de estados/////////////////////
    private Estados estado = new Estados();
    /////////////////////////////////////////

    ///Constantes para el SHAKE//////////////////////////////////
    private static final float SHAKE_THRESHOLD_GRAVITY = 2.7F;
    private static final int SHAKE_SLOP_TIME_MS = 500;
    private static final int SHAKE_COUNT_RESET_TIME_MS = 3000;
    /////////////////////////////////////////////////////////////

    //Var shake que usamos para que sea un poco menos sensible
    private long mShakeTimestamp;
    private int mShakeCount;
    /////////////////////////////////////////////////////////////

    //Constantes para determinar el sentido de giro
    public static final double anguloAntiHorario = -0.4;
    public static final double anguloHorario = 0.4;
    /////////////////////////////////////////////////////////////

    ///Esta var la mantenmos como flag para saber si esta bajo de luz
    private boolean cambio = false;
    /////////////////////////////////////////////////////////////

    private boolean pocaLuz = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Codigo autogenerado////////////////////////////////////////////////////////////////////////////////////////
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
        //////////////////////////////////////////////////////////////////////////////////////////////////////////////

        //RECUPERAMOS los sensores y al administrador
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sLuz = mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        sPosicion = mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        sAcelerometro = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        ////////////////////////////////////////////////////////////////////////////////

        //Recuperamos los controles
        imvMotorSeleccionado    = findViewById(R.id.imvMotor);
        imvSentido    = findViewById(R.id.imvSentidoGiro);
        imvAyuda    = findViewById(R.id.imvAyuda);
        btnMover = findViewById(R.id.btnMover);
        ////////////////////////////////////////////////////////////////////////////////

        //Evento al apretar el boton mover
        btnMover.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //Obtenemos de los setting la ip y puerto del arduino
                SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(btnMover.getContext());
                String dir = "http://".concat(pref.getString("ip","192.168.1.50").concat(":").concat(pref.getString("port","8080")).concat("/"));
                //Obtenemos de la maquina de estados que mensaje vamos a enviar
                String msj = estado.GetMovimiento();
                if(msj != null)
                    new MensajeriaASYN().execute(dir, "MOV", msj); //Enviamos el mensaje de forma asyn
            }
        });
        ////////////////////////////////////////////////////////////////////////////////
    }

    private void mostrarAyuda() {
        btnMover.setVisibility(View.INVISIBLE);
        imvMotorSeleccionado.setVisibility(View.INVISIBLE);
        imvSentido.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(mSensorManager  == null)
            return;
        //Habilitamos el monitoreo de los sensores//////////////////////////////////////////////////////////
        mSensorManager.registerListener(this, sLuz, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, sPosicion, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, sAcelerometro, SensorManager.SENSOR_DELAY_NORMAL);
        ///////////////////////////////////////////////////////////////////////////////////////////////////
    }

    @Override
    protected void onPause() {
        super.onPause();
        //Dejamos de recibir notificaciones del administrador de sensores
        mSensorManager.unregisterListener(this);
    }

    //Evento que se ejecuta cuando un sensor cambia el valor que lee
    @Override
    public final void onSensorChanged(SensorEvent event) {
        synchronized (this) {
            switch (event.sensor.getType()) {
                case (Sensor.TYPE_LIGHT):
                    float lux = event.values[0];
                    // Este 4 tenemos que sacarlos de los setting
                    if(lux < 4){
                        cambio = true;
                        imvMotorSeleccionado.setImageResource(estado.SetAmbosMotores());
                    }
                    else if(cambio) {
                        imvMotorSeleccionado.setImageResource(estado.SetLastIndex());
                        cambio = false;
                    }
                    break;
                case (Sensor.TYPE_ROTATION_VECTOR):

                    if(!pocaLuz)
                        return;
                    float[] rotationMatrix = new float[16];
                    SensorManager.getRotationMatrixFromVector(
                            rotationMatrix, event.values);
                    float[] remappedRotationMatrix = new float[16];
                    SensorManager.remapCoordinateSystem(rotationMatrix,
                            SensorManager.AXIS_X,
                            SensorManager.AXIS_Z,
                            remappedRotationMatrix);
                    float[] orientations = new float[3];
                    SensorManager.getOrientation(remappedRotationMatrix, orientations);
                    if(orientations[2] > anguloHorario) //SENTIDO HORARIO (RAD)
                    {
                        imvSentido.setVisibility(View.VISIBLE);
                        imvSentido.setImageResource(estado.SetSentidoHorario());
                    }
                    else if(orientations[2] < anguloAntiHorario)  //SENTIDO ANTIHORARIO (RAD)
                    {
                        imvSentido.setVisibility(View.VISIBLE);
                        imvSentido.setImageResource(estado.SetSentidoAntiHorario());
                    }
                    else {
                        estado.SetSentidoQuieto();
                        imvSentido.setVisibility(View.INVISIBLE);
                    }
                    break;

                case (Sensor.TYPE_ACCELEROMETER):
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

                        if(!pocaLuz) {
                            pocaLuz = true;
                            imvMotorSeleccionado.setVisibility(View.VISIBLE);
                            imvSentido.setVisibility(View.VISIBLE);

                            btnMover.setVisibility(View.VISIBLE);
                            imvAyuda.setVisibility(View.INVISIBLE);
                        }

                        imvMotorSeleccionado.setImageResource(estado.MotorNext());
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
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
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

    //CLASE PARA ENVIAR MENSAJES
    private class MensajeriaASYN extends AsyncTask<String, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
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
                jsonParam.put(voids[1], voids[2]);

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
