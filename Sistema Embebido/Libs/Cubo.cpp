#include "Arduino.h"
#include "Cubo.h"


Cubo::Cubo(
         DRV8825 &arriba,  DRV8825 &abajo,  DRV8825 &Lateral/*,
            const int fwdPin,const  int revPin,const  int pasos,const  int steps, 
            const int grados120,const  int grados90,const  int grados45, 
            const int tiempoLectora,const  int pausa */
        )
{ 
    /*this->fwdPin = fwdPin;
    this->revPin = revPin;
    this->pasos = pasos;
    this->steps = steps;
    this->grados120 = grados120;
    this->grados90 = grados90;
    this->grados45 = grados45;s
    this->tiempoLectora = tiempoLectora;
    this->pausa = pausa;*/
}
/*
int Cubo::getFwdPin(){
    return this->fwdPin;
}
int Cubo::getRevPin(){
    return this->revPin;
}
int Cubo::getPasos(){
    return this->pasos;
}
int Cubo::getSteps(){
    return this->steps;
}
int Cubo::getGrados120(){
    return this->grados120;
}
int Cubo::getGrados90(){
    return this->grados90;
}
int Cubo::getGrados45(){
    return this->grados45;
}
int Cubo::getTiempoLectora(){
    return this->tiempoLectora;
}
int Cubo::getPausa(){
    return this->pausa;
}
*/
void Cubo::MovL(){
    Serial.println("MOVER L");
}

void Cubo::MovR(){
    Serial.println("MOVER R");
}

void Cubo::MovF(){
    Serial.println("MOVER F");
}

void Cubo::MovB(){
    Serial.println("MOVER B");
}

void Cubo::MovU(){
    Serial.println("MOVER U");
}
void Cubo::MovD(){
    Serial.println("MOVER D");
}

void Cubo::MovRA(){
    Serial.println("MOVER RA");
}

void Cubo::MovLA(){
    Serial.println("MOVER LA");
}

void Cubo::MovUA(){
    Serial.println("MOVER UA");
}

void Cubo::MovDA(){
    Serial.println("MOVER DA");
}

void Cubo::MovBA(){
    Serial.println("MOVER BA");
}

void Cubo::MovFA(){
    Serial.println("MOVER FA");
}

void Cubo::Mover( char c){
    switch(c){
        case RC:
            this->MovR();
            break;
        case LC:
            this->MovL();
            break;
        case UC:
            this->MovU();
            break;
        case DC:
            this->MovD();
            break;
        case FC:
            this->MovF();
            break;
        case BC:
            this->MovB();
            break;
        case RA:
            this->MovRA();
            break;
        case LA:
            this->MovLA();
            break;
        case FA:
            this->MovFA();
            break;
        case BA:
            this->MovBA();
            break;
        case UA:
            this->MovUA();
            break;
        case DA:
            this->MovDA();
            break;
    }

}
