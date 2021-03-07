package com.packt.infamous.game_objects;

import com.packt.infamous.Alignment;

public class DrainableObject extends GenericObject{
    public DrainableObject(float x, float y, Alignment alignment) {
        super(x, y , alignment);
        maxEnergy = currentEnergy = 10000;
    }

    public int removeEnergy() {
        if (this.currentEnergy > 0) {
            currentEnergy -= 10;
            return 10;
        }
        else { return 0; } //Play fail sound
    }
}
