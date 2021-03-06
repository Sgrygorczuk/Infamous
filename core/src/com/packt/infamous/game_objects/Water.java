package com.packt.infamous.game_objects;

import com.badlogic.gdx.math.Rectangle;

public class Water {
    public Rectangle hitbox;

    public Water(float x, float y, int width, int height){
        hitbox = new Rectangle(x, y, width, height);
    }

    public void inWater(Cole player, Rectangle otherHitbox){
        if(hitbox.overlaps(otherHitbox)){
            // drain player energy here
        }
    }
}
