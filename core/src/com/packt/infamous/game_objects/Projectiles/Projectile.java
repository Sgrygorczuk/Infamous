package com.packt.infamous.game_objects.Projectiles;

import com.packt.infamous.Alignment;
import com.packt.infamous.Enum;
import com.packt.infamous.game_objects.GenericObject;

import static com.packt.infamous.Const.BOLT_DAMAGE;
import static com.packt.infamous.Const.BOLT_SPEED;
import static com.packt.infamous.Const.EXPLOSIVE_LINGER;
import static com.packt.infamous.Const.PROJ_TIME;

public class Projectile extends GenericObject {
    protected int damage;

    protected Enum type = Enum.BOLT;
    protected static boolean isExplosive = false;

    //Timer counting down to destroy the projectile
    private float projectileTimer;
    private boolean projectileTimer_enabled = true;

    private boolean destroy = false;

    //For Bombs
    protected boolean isAttached;
    GenericObject followObject;

    public Projectile(float x, float y, Alignment align, int width, int height,
                      int direction, Enum type) {
        super(x, y, align);

        this.setWidth(width);
        this.setHeight(height);

        velocity.x = BOLT_SPEED * direction;
        damage =  BOLT_DAMAGE;

        projectileTimer = PROJ_TIME;
        this.type = type;

        if (type != Enum.BOLT && type != Enum.EXPLOSION){
            isExplosive = true;
            if (type == Enum.BOMB){
                projectileTimer_enabled = false;
            }
        }
        else if (type == Enum.EXPLOSION){
            isExplosive = false;
            velocity.x = 0;
            velocity.y = 0;
            projectileTimer = EXPLOSIVE_LINGER;
            projectileTimer_enabled = true;
        }
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

    public void setAttached(GenericObject obj) {
        isAttached = true;
        followObject = obj;
    }

    public int getDamage() {return damage;}
    public Enum getType() {return type;}
    public boolean isIsExplosive() {return isExplosive;}

    public void setDerezTimer(boolean state) {projectileTimer_enabled = state;}
    public void setDestroy(boolean state) {destroy = state;}
    public boolean canDestroy() {return destroy;}

}
