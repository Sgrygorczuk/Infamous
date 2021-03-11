package com.packt.infamous.game_objects;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.packt.infamous.Alignment;

import static com.packt.infamous.Const.COLE_HEIGHT;
import static com.packt.infamous.Const.COLE_WIDTH;

public class Enemy extends  GenericObject{
    protected int ammo = 0;
    protected float reloading = 0;
    protected static int maxAmmo = 10;
    protected static float reloadTime = 1f;
    protected static float shooting = 0;
    protected static float shootTime = 0.3f;
    protected static float moveSpeed = 20f;
    protected  float walkingDistance;
    protected boolean inCombat = false; // True if they see cole
    private boolean isReloading = false;
    public boolean isFacingRight = false;
    public boolean shootBullet = false;
    protected float initialX;
    public Rectangle visionCone;
    protected static float visionWidth = COLE_WIDTH*6;
    protected static float visionHeight = COLE_HEIGHT/2;
    protected static float calloutTime = 1f;
    protected float callout = 0f;

    protected Animation<TextureRegion> reloadAnimation;
    protected float reloadTimer = 0;

    protected Animation<TextureRegion> deathAnimation;
    protected float deathTimer = 0;

    public Enemy(float x, float y, float distance, Alignment alignment){
        super(x, y, alignment);
        hitBox.height = COLE_HEIGHT;
        hitBox.width = COLE_WIDTH;
        walkingDistance = distance-COLE_WIDTH;
        initialX = x;
        currentHealth = maxHealth = 100;
        ammo = maxAmmo;
        visionCone = new Rectangle(x, y+visionHeight, visionWidth, visionHeight);
    }

    public void setUpSpriteSheet(TextureRegion[][] textureRegions, TextureRegion[][] deathSpriteSheet){
        this.spriteSheet = textureRegions;
        setUpAnimations();
        setUpReloadAnimation();
        deathAnimation = setUpAnimation(deathSpriteSheet, 1/5f, 0, Animation.PlayMode.NORMAL);
  }

    protected void setUpReloadAnimation(){
        reloadAnimation = new Animation<TextureRegion>(1/5f, spriteSheet[1][0], spriteSheet[1][2], spriteSheet[1][1],  spriteSheet[1][2], spriteSheet[1][0]);
        reloadAnimation.setPlayMode(Animation.PlayMode.LOOP);
    }

    public void update(float delta){
        if(currentHealth > 0) {
            pathing(delta);
            action(delta);
            visionCone();
        }

        if(!isFacingRight){ animationLeftTime += delta; }
        if(isFacingRight){ animationRightTime += delta; }
        if(isReloading){reloadTimer += delta; }
        if(currentHealth < 0){deathTimer += delta;}
    }

    public void action(float delta){
        callout -= delta;
        if(reloading <= 0) {
            if (ammo <= 0 || (!inCombat && ammo < maxAmmo)) { // reloading action
                System.out.println("Reloading!");
                callout = calloutTime;
                reloading = reloadTime;
                isReloading = true;
            } else if(ammo > 0 && inCombat) { // shooting action
                if (shooting <= 0 && inCombat && !shootBullet) { // add conditional that enemy sees Cole.
                    System.out.println("Engaging!");
                    callout = calloutTime;
                    // shoot a bullet here (throw a hitbox forward)
                    shootBullet = true;
                    ammo -= 1;
                    shooting = shootTime;
                } else {
                    shooting -= delta;
                }
                isReloading = false;
            } else{
                if(callout <= 0){
                    System.out.println("Standing By!");
                    callout = calloutTime;
                }
                isReloading = false;
            }
        } else {
            reloading -= delta;
            if(reloading <= 0){
                ammo = maxAmmo;
            }
            isReloading = true;
        }
    }

    public void pathing(float delta){
        if(inCombat)return;
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
    public void visionCone(){
        if(isFacingRight){
            visionCone.setX(hitBox.x + COLE_WIDTH);
        } else{
            visionCone.setX(hitBox.x - visionWidth);
        }
    }

    public void drawDebug(ShapeRenderer shapeRenderer) {
        shapeRenderer.rect(hitBox.x, hitBox.y, hitBox.width, hitBox.height);
        shapeRenderer.rect(visionCone.x, visionCone.y, visionCone.width, visionCone.height);
    }

    public void takeDamage(int damage){
        currentHealth -= damage;
        System.out.println("Enemy's New Health: "+ currentHealth);
    }

    public void setCombat(boolean combatState){
        inCombat = combatState;
    }

    public void setCombat(Cole player){
        inCombat = visionCone.overlaps(player.hitBox);
    }

    public boolean finishedDying(){return deathAnimation.isAnimationFinished(deathTimer);}

    public void drawAnimations(SpriteBatch batch){
        TextureRegion currentFrame = spriteSheet[0][0];

        if(currentHealth < 0){currentFrame = deathAnimation.getKeyFrame(deathTimer);}
        else if(isReloading){currentFrame = reloadAnimation.getKeyFrame(reloadTimer);}
        else if(inCombat){currentFrame = spriteSheet[1][0]; }
        else if (isFacingRight) { currentFrame = walkRightAnimation.getKeyFrame(animationRightTime); }
        else if(!isFacingRight){ currentFrame = walkLeftAnimation.getKeyFrame(animationLeftTime); }

        batch.draw(currentFrame, isFacingRight ? hitBox.x : hitBox.x + currentFrame.getRegionWidth() , hitBox.y , isFacingRight ? currentFrame.getRegionWidth() : -currentFrame.getRegionWidth(), currentFrame.getRegionHeight());
    }

}
