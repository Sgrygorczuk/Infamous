package com.packt.infamous.game_objects.Projectiles;

import com.packt.infamous.Alignment;
import com.packt.infamous.game_objects.GenericObject;

import static com.packt.infamous.Const.BOLT_DAMAGE;
import static com.packt.infamous.Const.BOLT_SPEED;

public class Projectile extends GenericObject {
    protected int damage;
    protected float explosive_radius;
    public static boolean isExplosive = false;

    public Projectile(float x, float y, Alignment align, int width, int height, int direction) {
        super(x, y, align);

        velocity.x = BOLT_SPEED * direction;
        damage =  BOLT_DAMAGE;
        this.setWidth(width);
        this.setHeight(height);
    }


    public void update(float levelWidth){
        checkIfWorldBound(levelWidth);
        hitBox.y += velocity.y;
        hitBox.x += velocity.x;
    }

    public int getDamage() {return damage;}

}
