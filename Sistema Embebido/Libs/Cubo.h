#ifndef Cubo_h
#define Cubo_h

#define RC 'R'  // CARACTER QUE REPRESENTA EN LA SOLUCION UN MOVIMIENTO DE LA CARA DERECHA SENTIDO HORARIO
#define LC 'L'  // CARACTER QUE REPRESENTA EN LA SOLUCION UN MOVIMIENTO DE LA CARA IZQUIERDA SENTIDO HORARIO
#define DC 'D'  // CARACTER QUE REPRESENTA EN LA SOLUCION UN MOVIMIENTO DE LA CARA INFERIOR SENTIDO HORARIO
#define UC 'U'  // CARACTER QUE REPRESENTA EN LA SOLUCION UN MOVIMIENTO DE LA CARA SUPERIOR SENTIDO HORARIO
#define BC 'B'  // CARACTER QUE REPRESENTA EN LA SOLUCION UN MOVIMIENTO DE LA CARA TRASERA SENTIDO HORARIO
#define FC 'F' // CARACTER QUE REPRESENTA EN LA SOLUCION UN MOVIMIENTO DE LA CARA FRONTAL SENTIDO HORARIO
#define RA 'X' // CARACTER QUE REPRESENTA EN LA SOLUCION UN MOVIMIENTO DE LA CARA DERECHA SENTIDO ANTI-HORARIO
#define LA 'Y' // CARACTER QUE REPRESENTA EN LA SOLUCION UN MOVIMIENTO DE LA CARA IZQUIERDA SENTIDO ANTI-HORARIO
#define DA 'Z' // CARACTER QUE REPRESENTA EN LA SOLUCION UN MOVIMIENTO DE LA CARA INFERIOR SENTIDO ANTI-HORARIO
#define UA 'V' // CARACTER QUE REPRESENTA EN LA SOLUCION UN MOVIMIENTO DE LA CARA SUPERIOR SENTIDO ANTI-HORARIO
#define BA 'W' // CARACTER QUE REPRESENTA EN LA SOLUCION UN MOVIMIENTO DE LA CARA TRASERA SENTIDO ANTI-HORARIO
#define FA 'N' // CARACTER QUE REPRESENTA EN LA SOLUCION UN MOVIMIENTO DE LA CARA FRONTAL SENTIDO ANTI-HORARIO


#include "Arduino.h"
#include "DRV8825.h"

class Cubo
{
    public:
        Cubo(DRV8825 &arriba, DRV8825 &abajo, DRV8825 &lateral,
         const int fwdPin, const int revPin, const int pasos, const int steps, const int grados120, const int grados90, const int grados45, const int tiempoLectora,const  int pausa 
        );
        
        void Mover( char c);

        void MovL();
        void MovR();
        void MovU();
        void MovD();
        void MovF();
        void MovB();

        void MovLA();
        void MovRA();
        void MovUA();
        void MovDA();
        void MovFA();
        void MovBA();

        int getFwdPin();
        int getRevPin();
        int getPasos();
        int getSteps();
        int getGrados120();
        int getGrados90();
        int getGrados45();
        int getTiempoLectora();
        int getPausa();

        private:
            int fwdPin;
            int revPin;
            int pasos;
            int steps;
            int grados120;
            int grados90;
            int grados45;
            int tiempoLectora;
            int pausa;
};
#endif