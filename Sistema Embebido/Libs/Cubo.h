#ifndef Cubo_h
#define Cubo_h

//#define RC 'R'  // CARACTER QUE REPRESENTA EN LA SOLUCION UN MOVIMIENTO DE LA CARA DERECHA SENTIDO HORARIO
#define LC 'L'  // CARACTER QUE REPRESENTA EN LA SOLUCION UN MOVIMIENTO DE LA CARA IZQUIERDA SENTIDO HORARIO
#define DC 'D'  // CARACTER QUE REPRESENTA EN LA SOLUCION UN MOVIMIENTO DE LA CARA INFERIOR SENTIDO HORARIO
#define UC 'U'  // CARACTER QUE REPRESENTA EN LA SOLUCION UN MOVIMIENTO DE LA CARA SUPERIOR SENTIDO HORARIO
//#define BC 'B'  // CARACTER QUE REPRESENTA EN LA SOLUCION UN MOVIMIENTO DE LA CARA TRASERA SENTIDO HORARIO
//#define FC 'F' // CARACTER QUE REPRESENTA EN LA SOLUCION UN MOVIMIENTO DE LA CARA FRONTAL SENTIDO HORARIO
//#define RA 'X' // CARACTER QUE REPRESENTA EN LA SOLUCION UN MOVIMIENTO DE LA CARA DERECHA SENTIDO ANTI-HORARIO
#define LA 'Y' // CARACTER QUE REPRESENTA EN LA SOLUCION UN MOVIMIENTO DE LA CARA IZQUIERDA SENTIDO ANTI-HORARIO
#define DA 'Z' // CARACTER QUE REPRESENTA EN LA SOLUCION UN MOVIMIENTO DE LA CARA INFERIOR SENTIDO ANTI-HORARIO
#define UA 'V' // CARACTER QUE REPRESENTA EN LA SOLUCION UN MOVIMIENTO DE LA CARA SUPERIOR SENTIDO ANTI-HORARIO
//#define BA 'W' // CARACTER QUE REPRESENTA EN LA SOLUCION UN MOVIMIENTO DE LA CARA TRASERA SENTIDO ANTI-HORARIO
//#define FA 'N' // CARACTER QUE REPRESENTA EN LA SOLUCION UN MOVIMIENTO DE LA CARA FRONTAL SENTIDO ANTI-HORARIO
#define XC 'X'

#include "Arduino.h"
//#include "DRV8825.h"

class Cubo
{
    public:

        Cubo( const int dirArr,    const int stepArr,   const int enblArr,
              const int dirAbajo,  const int stepAbajo, const int enblAbajo,
              const int dirCost,   const int stepCost,  const int enblCost,
              const int fwdPin, const int revPin, const int inputFinCarrera,
              const int pasos, const int grados180, const int grados90, const int grados45, const int tiempoLectora,const  int pausa
        );

        void Mover( char c);

        void MovL();
        void MovU();
        void MovD();
        void MovX();

        void MovLA();
        void MovUA();
        void MovDA();
        //void MovXA();

        void ponerBrazoCostado();
        void sacarBrazoCostado();
        void girarBrazoCostadoHorario(int cantidadDePasos);
        void girarBrazoCostadoAntihorario(int cantidadDePasos);


        int getFwdPin();
        int getRevPin();
        int getInputFinDeCarrera();
        int getPasos();
        int getGrados180();
        int getGrados90();
        int getGrados45();
        int getTiempoLectora();
        int getPausa();

        private:
            int dirArr;
            int stepArr;
            int enblArr;
            int dirAbajo;
            int stepAbajo;
            int enblAbajo;
            int dirCost;
            int stepCost;
            int enblCost;
            int fwdPin;
            int revPin;
            int inputFinCarrera;
            int pasos;
            int steps;
            int grados180;
            int grados90;
            int grados45;
            int tiempoLectora;
            int pausa;
            int value; // HIGH significa que esta apretando el fin de carrera
};
#endif
