#include "Arduino.h"
#include "DRV8825.h"

DRV8825::DRV8825( int dir, int step, int enb )
{
    this->dir = dir;
    this->step = step;
    this->enable = enb;
    pinMode(dir, OUTPUT);
    pinMode(step, OUTPUT);
    pinMode(enb, OUTPUT);    
}

int DRV8825::GetDir(){
    return this->dir;
}
int DRV8825::GetStep(){
    return this->step;
}
int DRV8825::GetEnable(){
    return this->enable;
}