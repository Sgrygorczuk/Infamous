package com.packt.infamous.game_objects;

import com.badlogic.gdx.math.Rectangle;
import com.packt.infamous.Alignment;

import static com.packt.infamous.Const.COLE_HEIGHT;
import static com.packt.infamous.Const.COLE_WIDTH;

public class Enemy extends  GenericObject{
    protected int ammo = 0;
    protected float reloading = 0;
    protected static float reloadTime = 3f;
    protected static float shooting = 0;
    protected static float shootTime = 1f;
    protected static float walkingDistance = 50f;
    protected static float moveSpeed = 15f;
    protected boolean isFacingRight = false;
    protected float initialX;


    public Enemy(float x, float y, Alignment alignment){
        super(x, y, alignment);
        hitBox.height = COLE_HEIGHT;
        hitBox.width = COLE_WIDTH;
        initialX = x;
        currentHealth = maxHealth = 100;
        ammo = 10;
    }

    public void update(float delta){
        pathing(delta);
        action(delta);
    }

    public void action(float delta){
        if(reloading <= 0) {
            // TOOD: or if enemy doesn't see cole, reload
            if (ammo <= 0) { // reloading action
                reloading -= delta;
            } else { // shooting action
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

    public void pathing(float delta){
        System.out.println("Enemy at "+hitBox.x+". "+hitBox.y);
        if(isFacingRight){
            if(hitBox.x >= initialX + walkingDistance){
                isFacingRight = false;
            } else {
                hitBox.x += delta*moveSpeed;
            }
        } else {
            if (hitBox.x <= initialX){
                isFacingRight = true;
            } else {
                hitBox.x -= delta*moveSpeed;
            }
        }
    }
}
