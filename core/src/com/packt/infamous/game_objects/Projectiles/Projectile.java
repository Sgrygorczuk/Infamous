package com.packt.infamous.game_objects.Projectiles;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.packt.infamous.Alignment;
import com.packt.infamous.Enum;
import com.packt.infamous.game_objects.GenericObject;

import static com.packt.infamous.Const.BOLT_DAMAGE;
import static com.packt.infamous.Const.BOLT_SPEED;
import static com.packt.infamous.Const.BULLET_TIME;
import static com.packt.infamous.Const.EXPLOSIVE_DAMAGE;
import static com.packt.infamous.Const.EXPLOSIVE_LINGER;
import static com.packt.infamous.Const.MELEE_TIME;
import static com.packt.infamous.Const.PROJ_TIME;

public class Projectile extends GenericObject {
    protected int damage;

    protected Enum type = Enum.BOLT;
    protected static boolean isExplosive = false;

    //Timer counting down to destroy the projectile
    private float projectileTimer;
    private boolean projectileTimer_enabled = true;

    private boolean destroy = false;

    protected TextureRegion[][] bulletSpriteSheet;
    protected Animation<TextureRegion> bulletAnimation;
    protected float bulletTime = 0;


    //For Bombs
    protected boolean isAttached;
    GenericObject followObject;
    Vector2 disjoint;

    public Projectile(float x, float y, Alignment align, int width, int height,
                      int direction, float startVelocity, Enum type, TextureRegion[][] bulletSpriteSheet) {
        super(x, y, align);

        this.setWidth(width);
        this.setHeight(height);

        velocity.x = startVelocity * direction;

        damage =  BOLT_DAMAGE;

        if (type == Enum.TORPEDO || type == Enum.BOMB){
            damage = 1;
        }


        projectileTimer = PROJ_TIME;
        this.type = type;

        if(type == Enum.BULLET){
            projectileTimer = BULLET_TIME;
            damage = 10;
        } else if (type != Enum.BOLT && type != Enum.EXPLOSION){
            isExplosive = true;
            if (type == Enum.BOMB){
                projectileTimer_enabled = false;
            }
        }
        else if (type == Enum.EXPLOSION){
            isExplosive = false;
            velocity.x = 0;
            velocity.y = 0;
            damage = EXPLOSIVE_DAMAGE;
            projectileTimer = EXPLOSIVE_LINGER;
            projectileTimer_enabled = true;
        }

        this.bulletSpriteSheet = bulletSpriteSheet;
        this.bulletAnimation = setUpAnimation(bulletSpriteSheet, 5, 0, Animation.PlayMode.NORMAL);
    }

    public Projectile(float x, float y, Alignment alignment, float width, float height, int direction, float startVelocity){
        super(x, y, alignment);

        this.setWidth(width);
        this.setHeight(height);

        type = Enum.MELEE;
        projectileTimer = MELEE_TIME;

        velocity.x = (startVelocity + BOLT_SPEED) * direction;
        damage = 40;
    }

    public void update(float levelWidth, float levelHeight, float delta){
        checkIfWorldBound(levelWidth, levelHeight);
        derezTimer(delta);
        hitBox.y += velocity.y;
        hitBox.x += velocity.x;

        bulletTime += delta;
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
        followObject = obj;
        isAttached = true;
        disjoint = new Vector2(hitBox.x - obj.getX(), hitBox.y - obj.getY());
    }

    public int getDamage() {return damage;}
    public Enum getType() {return type;}
    public boolean isIsExplosive() {return isExplosive;}

    public void setDerezTimer(boolean state) {projectileTimer_enabled = state;}
    public void setDestroy(boolean state) {destroy = state;}
    public boolean canDestroy() {return destroy;}

    public void drawAnimation(SpriteBatch batch){
        if(this.type != Enum.MELEE) {

            TextureRegion currentFrame = bulletAnimation.getKeyFrame(bulletTime);
            batch.draw(currentFrame, hitBox.x, hitBox.y, hitBox.width, hitBox.height);
        }
    }

}
