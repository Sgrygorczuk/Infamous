package com.packt.infamous.game_objects;

import com.packt.infamous.Alignment;

import static com.packt.infamous.Const.BOLT_DAMAGE;
import static com.packt.infamous.Const.BOLT_SPEED;

public class PlayerProjectile extends GenericObject {
    protected int damage;
    protected boolean explosive = false;
    protected float explosive_radius;

    //Standard projectiles
    public PlayerProjectile(float x, float y, Alignment align, int width, int height, int direction) {
        this(x, y, align, width, height, direction, false, 0);
    }

    //For explosive projectiles
    public PlayerProjectile(float x, float y, Alignment align, int width, int height, int direction,
                     boolean explosive, float radius) {
        super(x, y, align);

        velocity.x = BOLT_SPEED * direction;
        damage =  BOLT_DAMAGE;

        this.setWidth(width);
        this.setHeight(height);

        this.explosive = explosive;
        this.explosive_radius = radius;
    }


    public void update(float levelWidth){
        checkIfWorldBound(levelWidth);
        hitBox.y += velocity.y;
        hitBox.x += velocity.x;
    }

    public int getDamage() {return damage;}

    public boolean isExplosive(){return explosive;}

}
