package com.example.adrianradice.matularubik;
import android.support.annotation.Nullable;

import com.example.adrianradice.matularubik.R;

public class Estados {

    private final static char  LC='L';
    private final static char  LA='Y';
    private final static char  UC='U';
    private final static char  UA='V';
    private final static char  DC='D';
    private final static char  DA='Z';
    private final static char  XC='X';
    private final static String  MOVER="MOV";


    public final static char M_SUP = 'U';
    public final static char M_INF = 'I';
    public final static char M_LAT = 'L';
    public final static char M_AMB = 'X';

    public final static int S_HORARIO = 1;
    public final static int S_ANTI = 2;
    public final static int S_QUIETO = 0;


    private char Motor[] = {M_SUP,M_INF,M_LAT,M_AMB};
    private int indexMotor = 0;
    private int lastIndexMotor = 0;


    private int sentido = S_QUIETO;

    private boolean ocupado = false;

    public int MotorNext() {
        indexMotor++;
        if(indexMotor  >= 3)
            indexMotor = 0;
        return getIconoMov();

    }

    @Nullable
    private int getIconoMov() {
        switch (Motor[indexMotor]){
            case M_SUP:
                return R.drawable.ic_mov_sup;
            case M_INF:
                return R.drawable.ic_mov_inferior;
            case M_LAT:
                return R.drawable.ic_mov_lateral;
        }
        return 0;
    }

    public int SetAmbosMotores() {
        if(indexMotor != 3)
            lastIndexMotor= indexMotor;
        indexMotor = 3;
        return R.drawable.ic_mov_ambos;
    }

    public int SetLastIndex(){
        indexMotor = lastIndexMotor;
        return getIconoMov();
    }

    public int SetSentidoHorario(){
        sentido = S_HORARIO;
        return R.drawable.ic_mov_horario;
    }
    public int SetSentidoAntiHorario(){
        sentido = S_ANTI;
        return R.drawable.ic_mov_anti;
    }

    public int SetSentidoQuieto(){
        sentido = S_QUIETO;
        return -1;
    }


    public String GetMovimiento()
    {

        if(Motor[indexMotor] == M_AMB){
                return String.valueOf( XC );
        }
        if(sentido == S_QUIETO)
            return null;
        if(Motor[indexMotor] == M_SUP){
            if(sentido == S_HORARIO)
                return String.valueOf( UC );
            return String.valueOf( UA );
        }
        if(Motor[indexMotor] == M_INF){
            if(sentido == S_HORARIO)
                return String.valueOf( DC );
            return String.valueOf( DA );
        }
        if(Motor[indexMotor] == M_LAT){
            if(sentido == S_HORARIO)
                return String.valueOf( LC );
            return String.valueOf( LA );
        }
        return null;
    }



}
