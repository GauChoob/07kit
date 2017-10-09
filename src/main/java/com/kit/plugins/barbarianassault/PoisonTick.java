package com.kit.plugins.barbarianassault;

//Represented by an int from 0 to 20, where 20 represents the very first passive poison tick damage of 4,
//1 represents the last passive poison tick damage of 1, and 0 represents no passive damage
class PoisonTick {
    //20 = 4x5, 1=1x1, 0=no poison. Do not use for >20 or <0
    static int PoisonDamage(int poisonTick){
        return (poisonTick + 4) / 5;
    }
    static int PoisonRemainingSameNumber(int poisonTick){
        return (poisonTick-1)%5+1;
    }

}
