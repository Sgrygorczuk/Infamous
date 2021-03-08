package com.packt.infamous.game_objects.Projectiles;

import com.packt.infamous.Alignment;

import static com.packt.infamous.Const.BOLT_DAMAGE;
import static com.packt.infamous.Const.BOLT_SPEED;

public class Explosive extends Projectile{

    protected float explosive_radius;

    //For explosive projectiles
    public Explosive(float x, float y, Alignment align, int width, int height, int direction,
                      float radius) {
        super(x, y, align, width, height, direction);
        this.explosive_radius = radius;
        isExplosive = true;
    }
}
