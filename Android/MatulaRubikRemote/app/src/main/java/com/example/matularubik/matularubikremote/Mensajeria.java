package com.example.matularubik.matularubikremote;

import android.util.Log;

import org.json.JSONObject;

import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class Mensajeria {

    private final static char  LC='L';
    private final static char  LA='Y';
    private final static char  UC='U';
    private final static char  UA='V';
    private final static char  DC='D';
    private final static char  DA='Z';
    private final static char  XC='X';
    private final static String  MOVER="MOV";


    public static void EnviarMoverLC(){
        EnviarMensaje(MOVER,LC);
    }
    public static void EnviarMoverLA(){
        EnviarMensaje(MOVER,LA);
    }
    public static void EnviarMoverUC(){
        EnviarMensaje(MOVER,UC);
    }
    public static void EnviarMoverUA(){
        EnviarMensaje(MOVER,UA);
    }
    public static void EnviarMoverDC(){
        EnviarMensaje(MOVER,DC);
    }
    public static void EnviarMoverDA(){
        EnviarMensaje(MOVER,DA);
    }
    public static void EnviarMoverXC(){
        EnviarMensaje(MOVER,XC);
    }

    private static void EnviarMensaje(final String mover, final char mensaje){

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    URL url = new URL("http://192.168.1.50:8080/");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
                    conn.setRequestProperty("Accept","application/json");
                    conn.setDoOutput(true);
                    conn.setDoInput(true);

                    JSONObject jsonParam = new JSONObject();
                    jsonParam.put(MOVER, String.valueOf(mensaje));

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
            }});

        thread.start();



    }
}
