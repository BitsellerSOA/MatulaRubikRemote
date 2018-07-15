#include "Arduino.h"
#include "Cubo.h"


Cubo::Cubo(
            const int dirArr,    const int stepArr,   const int enblArr,
            const int dirAbajo,  const int stepAbajo, const int enblAbajo,
            const int dirCost,   const int stepCost,  const int enblCost,
            const int fwdPin, const  int revPin, const int inputFinCarrera, const  int pasos,  
            const int grados180, const  int grados90, const  int grados45, 
            const int tiempoLectora, const  int pausa 
        )
{ 
    this->dirArr = dirArr;
    this->stepArr = stepArr;
    this->enblArr = enblArr;
    this->dirAbajo = dirAbajo;
    this->stepAbajo = stepAbajo;
    this->enblAbajo = enblAbajo;
    this->dirCost = dirCost;
    this->stepCost = stepCost;
    this->enblCost = enblCost;
    this->fwdPin = fwdPin;
    this->revPin = revPin;
    this->inputFinCarrera = inputFinCarrera;
    this->pasos = pasos;
    this->grados180 = grados180;
    this->grados90 = grados90;
    this->grados45 = grados45;
    this->tiempoLectora = tiempoLectora;
    this->pausa = pausa;
    this->value = 0;

    pinMode(fwdPin, OUTPUT);
    pinMode(revPin, OUTPUT);
    pinMode(inputFinCarrera, INPUT);
}

int Cubo::getFwdPin(){
    return this->fwdPin;
}
int Cubo::getRevPin(){
    return this->revPin;
}
int Cubo::getInputFinDeCarrera(){
    return this->inputFinCarrera;
}
int Cubo::getPasos(){
    return this->pasos;
}
int Cubo::getGrados180(){
    return this->grados180;
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

void Cubo::MovL(){
    Serial.println("MOVER L");
    //girar brazo costado 45 grados antihorario
    //poner Brazo costado
    //girar brazo costado 90 grados horario
    //sacar brazo
    //girar brazo costado 45 grados antihorario
    
    girarBrazoCostadoAntihorario(grados45);   
    ponerBrazoCostado();   
    girarBrazoCostadoHorario(grados90);
    sacarBrazoCostado();   
    girarBrazoCostadoAntihorario(grados45);

}

void Cubo::MovU(){
    // pongo el brazo de costado con la mano horizontal; 
    // giro arriba horario 90 grados;
    // saco el brazo;
    digitalWrite(dirArr, HIGH);  // Establezco una dirección
    
    ponerBrazoCostado();
    
    for (int x = 0; x < grados90 ; x++) { // giro arriba horario 90 grados;
      digitalWrite(stepArr, HIGH);
      delayMicroseconds(pausa);
      digitalWrite(stepArr, LOW);
      delayMicroseconds(pausa);
    }
    
    sacarBrazoCostado(); 
}

void Cubo::MovD(){
    Serial.println("MOVER D");
    // pongo el brazo de costado con la mano horizontal; 
    // giro abajo horario 90 grados;
    // saco el brazo;
    digitalWrite(dirAbajo, HIGH);  // Establezco una dirección
    
    ponerBrazoCostado();
    
    for (int x = 0; x < grados90 ; x++) {
      digitalWrite(stepAbajo, HIGH);
      delayMicroseconds(pausa);
      digitalWrite(stepAbajo, LOW);
      delayMicroseconds(pausa);
    }
    
    sacarBrazoCostado(); 
}

void Cubo::MovX(){
    // giro el motor de arriba y el motor de abajo 90grados en sentido horario
    Serial.println("MOVER X");
    digitalWrite(dirArr, HIGH);  // Establezco una dirección
    digitalWrite(dirAbajo, HIGH);
    
    for (int x = 0; x < grados90 ; x++) {
      digitalWrite(stepArr, HIGH); 
      digitalWrite(stepAbajo, HIGH);
      delayMicroseconds(pausa);
      digitalWrite(stepArr, LOW);
      digitalWrite(stepAbajo, LOW);
      delayMicroseconds(pausa);
    }
}


void Cubo::MovLA(){
    Serial.println("MOVER LA");
    //girar brazo costado 45 grados horario
    //poner Brazo costado
    //girar brazo costado 90 grados antihorario
    //sacar brazo
    //girar brazo costado 45 grados horario
    
    girarBrazoCostadoHorario(grados45);   
    ponerBrazoCostado();   
    girarBrazoCostadoAntihorario(grados90);
    sacarBrazoCostado();   
    girarBrazoCostadoHorario(grados45);
}

void Cubo::MovUA(){
    Serial.println("MOVER UA");
    // pongo el brazo de costado con la mano horizontal; 
    // giro arriba antihorario 90 grados;
    // saco el brazo;
    digitalWrite(dirArr, LOW);  // Establezco una dirección
    
    ponerBrazoCostado();
    
    for (int x = 0; x < grados90 ; x++) { // giro arriba antihorario 90 grados;
      digitalWrite(stepArr, HIGH);
      delayMicroseconds(pausa);
      digitalWrite(stepArr, LOW);
      delayMicroseconds(pausa);
    }

    sacarBrazoCostado();
}

void Cubo::MovDA(){
    Serial.println("MOVER DA");
    // pongo el brazo de costado con la mano horizontal; 
                        // giro abajo antihorario 90 grados;
                        // saco el brazo;
    digitalWrite(dirAbajo, LOW);  // Establezco una dirección
    
    ponerBrazoCostado();
    
    for (int x = 0; x < grados90 ; x++) {
      digitalWrite(stepAbajo, HIGH);
      delayMicroseconds(pausa);
      digitalWrite(stepAbajo, LOW);
      delayMicroseconds(pausa);
    }
    
    sacarBrazoCostado(); 
}

/*
void Cubo::MovXA(){
    Serial.println("MOVER XA");
    // giro el motor de arriba y el motor de abajo 90grados en sentido antihorario    
    digitalWrite(dirArr, LOW);  // Establezco una dirección
    digitalWrite(dirAbajo, LOW);
    
    for (int x = 0; x < grados90 ; x++) {
      digitalWrite(stepArr, HIGH);
      digitalWrite(stepAbajo, HIGH);
      delayMicroseconds(pausa);
      digitalWrite(stepArr, LOW);
      digitalWrite(stepAbajo, LOW);
      delayMicroseconds(pausa);
    } 
}
*/


void Cubo::ponerBrazoCostado(){
    
    digitalWrite(revPin, HIGH);
    digitalWrite(fwdPin, LOW);
    
    value = digitalRead(inputFinCarrera);
    while( value == LOW ){

      value = digitalRead(inputFinCarrera);
    }
    
    digitalWrite(revPin, LOW);
    digitalWrite(fwdPin, LOW);
}
  
void Cubo::sacarBrazoCostado(){
    digitalWrite(revPin, LOW);
    digitalWrite(fwdPin, HIGH);
    
    delay(tiempoLectora);
    
    digitalWrite(revPin, LOW);
    digitalWrite(fwdPin, LOW);
}
  
void Cubo::girarBrazoCostadoHorario(int cantidadDePasos){
    digitalWrite(dirCost, HIGH);  // Establezco una dirección
    
    for (int x = 0; x < cantidadDePasos ; x++) { // giro costado horario 90 grados;
      digitalWrite(stepCost, HIGH);
      delayMicroseconds(pausa);
      digitalWrite(stepCost, LOW);
      delayMicroseconds(pausa);
    }
}
  
void Cubo::girarBrazoCostadoAntihorario(int cantidadDePasos){
    digitalWrite(dirCost, LOW);  // Establezco una dirección
    
    for (int x = 0; x < cantidadDePasos ; x++) { // giro costado horario 90 grados;
      digitalWrite(stepCost, HIGH);
      delayMicroseconds(pausa);
      digitalWrite(stepCost, LOW);
      delayMicroseconds(pausa);
    }
}


void Cubo::Mover( char c){
    switch(c){

        case LC:
            this->MovL();
            break;
        case UC:
            this->MovU();
            break;
        case DC:
            this->MovD();
            break;
        case LA:
            this->MovLA();
            break;
        case UA:
            this->MovUA();
            break;
        case DA:
            this->MovDA();
            break;
        case XC:
            this->MovX();
            break;
       
/*        case XA
            this->MovXA();
            break; */
    }

}
