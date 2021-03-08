package com.packt.infamous.game_objects.Projectiles;

import com.packt.infamous.Alignment;
import com.packt.infamous.game_objects.GenericObject;

import static com.packt.infamous.Const.BOLT_DAMAGE;
import static com.packt.infamous.Const.BOLT_SPEED;
import static com.packt.infamous.Const.PROJ_TIME;

public class Projectile extends GenericObject {
    protected int damage;

    public static boolean isExplosive = false;

    //Timer counting down to destroy the projectile
    private float projectileTimer;
    private boolean projectileTimer_enabled = true;

    private boolean destroy = false;

    public Projectile(float x, float y, Alignment align, int width, int height, int direction) {
        super(x, y, align);

        this.setWidth(width);
        this.setHeight(height);

        velocity.x = BOLT_SPEED * direction;
        damage =  BOLT_DAMAGE;

        projectileTimer = PROJ_TIME;
    }

    public void update(float levelWidth, float delta){
        checkIfWorldBound(levelWidth);
        derezTimer(delta);
        hitBox.y += velocity.y;
        hitBox.x += velocity.x;
    }

    /**
     Input: Float delta
     Output: Void
     Purpose: Ticks down to flag for removal
     */
    public void derezTimer(float delta){
        if (projectileTimer_enabled){
            projectileTimer -= delta;

            if (projectileTimer <= 0) {
                destroy = true;
            }
        }
    }

    public int getDamage() {return damage;}

    public boolean canDestroy() {return destroy;}

}
