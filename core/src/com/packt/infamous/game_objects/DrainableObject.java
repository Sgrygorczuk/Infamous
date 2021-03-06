package com.packt.infamous.game_objects;

import com.packt.infamous.Alignment;

public class DrainableObject extends GenericObject{
    public DrainableObject(float x, float y, Alignment alignment) {
        super(x, y , alignment);
        currentEnergy = 10000;
    }

    public float getEnergy(){
        return currentEnergy;
    }

    //TODO: Drain energy slowly
    public float alterEnergy(int amount){
        currentEnergy -= amount;
        return 0;
    }
}
