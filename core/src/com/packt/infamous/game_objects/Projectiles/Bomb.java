package com.packt.infamous.game_objects.Projectiles;

import com.packt.infamous.Alignment;
import com.packt.infamous.Enum;
import com.packt.infamous.game_objects.GenericObject;

public class Bomb extends Projectile{
    GenericObject followObject;

    public Bomb(float x, float y, Alignment align, int width, int height, int direction, Enum type) {
        super(x, y, align, width, height, direction, Enum.BOMB);
        isExplosive = true;
    }

    @Override
    public void update(float levelWidth, float delta){
        checkIfWorldBound(levelWidth);
        derezTimer(delta);
        arcDown(delta);
        if(!isAttached){
            hitBox.y += velocity.y;
            hitBox.x += velocity.x;
        }
        else {
            hitBox.y = followObject.getY();
            hitBox.x = followObject.getX();
        }
    }

    /**
     Input: float delta
     Output: void
     Purpose: Arcs down bomb slowly
     */
    private void arcDown(float delta){
        velocity.y -= 0.1f;
    }

}
