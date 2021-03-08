package com.packt.infamous.game_objects;

import com.badlogic.gdx.math.Rectangle;
import com.packt.infamous.Alignment;

public class Enemy extends  GenericObject{
    protected int ammo = 0;
    protected float reloading = 0;
    protected static float reloadTime = 3f;
    protected static float shooting = 0;
    protected static float shootTime = 1f;


    public Enemy(float x, float y, Alignment alignment){
        super(x, y, alignment);
        currentHealth = maxHealth = 100;
        ammo = 10;
    }

    public void update(float delta){
        if(reloading <= 0) {
            // TOOD: or if enemy doesn't see cole, reload
            if (ammo <= 0) {
                reload(delta);
            } else {
                // TODO: if enemy sees cole, do this
                if (shooting == 0) { // add conditional that enemy sees Cole.
                    // shoot a bullet here (throw a hitbox forward)
                    ammo -= 1;
                    shooting = shootTime;
                } else {
                    shooting -= delta;
                }
            }
        }
    }

    public void reload(float delta){
        if(reloading > 0){
            reloading -= delta;
        }
    }
}
