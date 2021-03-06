package com.packt.infamous.game_objects;

import com.badlogic.gdx.math.Rectangle;
import com.packt.infamous.Alignment;

public class DrainableObject extends GenericObject{
    public DrainableObject(float x, float y, Alignment alignment) {
        super(x, y , alignment);
        energy = 10000;
    }

    public float getEnergy(){
        return energy;
    }

    //TODO: Drain energy slowly
    public float alterEnergy(int amount){
        energy -= amount;
    }
}
