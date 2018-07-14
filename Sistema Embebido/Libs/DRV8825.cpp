#include "Arduino.h"
#include "DRV8825.h"

DRV8825::DRV8825( int dir, int step, int enb )
{
    pinMode(dir, OUTPUT);
    pinMode(step, OUTPUT);
    pinMode(enb, OUTPUT);    
}