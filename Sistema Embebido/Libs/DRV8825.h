#ifndef DRV8825_h
#define DRV8825_h

#include "Arduino.h"

class DRV8825
{
    public:
        DRV8825( int dir, int step, int enable );
        int GetDir();
        int GetStep();
        int GetEnable();

    private:
        int dir;
        int step;
        int enable;
};
#endif