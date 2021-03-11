package com.packt.infamous.game_objects;

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
    public boolean isFacingRight = false;
    public boolean shootBullet = false;
    protected float initialX;
    public Rectangle visionCone;
    protected static float visionWidth = COLE_WIDTH*6;
    protected static float visionHeight = COLE_HEIGHT/2;
    protected Rectangle lDetector;
    protected Rectangle rDetector;


    public Enemy(float x, float y, float distance, Alignment alignment, boolean direction){
        super(x, y, alignment);
        hitBox.height = COLE_HEIGHT;
        hitBox.width = COLE_WIDTH;
        walkingDistance = distance-COLE_WIDTH;
        initialX = x;
        isFacingRight = direction;
        currentHealth = maxHealth = 100;
        ammo = maxAmmo;
        lDetector = new Rectangle(x-COLE_WIDTH, y, COLE_WIDTH, COLE_HEIGHT);
        rDetector = new Rectangle(x+COLE_WIDTH, y, COLE_WIDTH, COLE_HEIGHT);
        visionCone = new Rectangle(x, y+visionHeight, visionWidth, visionHeight);
    }

    public void setUpSpriteSheet(TextureRegion[][] textureRegions){
        this.spriteSheet = textureRegions;
        setUpAnimations();
  }

    public void update(float delta){
        pathing(delta);
        action(delta);
        visionCone();
        if(!isFacingRight){ animationLeftTime += delta; }
        else{ animationRightTime += delta; }
    }

    public void action(float delta){

        if(reloading <= 0) {
            if (ammo <= 0 || (!inCombat && ammo < maxAmmo)) { // reloading action
                reloading = reloadTime;
            } else if(ammo > 0 && inCombat) { // shooting action
                if (shooting <= 0 && inCombat && !shootBullet) { // add conditional that enemy sees Cole.

                    // shoot a bullet here (throw a hitbox forward)
                    shootBullet = true;
                    ammo -= 1;
                    shooting = shootTime;
                } else {
                    shooting -= delta;
                }
            }
        } else {
            reloading -= delta;
            if(reloading <= 0){
                ammo = maxAmmo;
            }
        }
    }

    public void pathing(float delta){
        if(walkingDistance <= COLE_WIDTH || inCombat)return;
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
        lDetector.x = hitBox.x-COLE_WIDTH;
        rDetector.x = hitBox.x+COLE_WIDTH;
    }

    public void visionCone(){
        if(isFacingRight){
            visionCone.setX(hitBox.x + COLE_WIDTH);
        } else{
            visionCone.setX(hitBox.x - visionWidth);
        }
    }

    public void nearDetector(Cole player){
        if(lDetector.overlaps(player.hitBox)){
            isFacingRight = false;
        } else if (rDetector.overlaps(player.hitBox)){
            isFacingRight = true;
        }
    }

    public void drawDebug(ShapeRenderer shapeRenderer) {
        shapeRenderer.rect(hitBox.x, hitBox.y, hitBox.width, hitBox.height);
        shapeRenderer.rect(visionCone.x, visionCone.y, visionCone.width, visionCone.height);
        shapeRenderer.rect(lDetector.x, lDetector.y, lDetector.width, lDetector.height);
        shapeRenderer.rect(rDetector.x, rDetector.y, rDetector.width, rDetector.height);
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

    public void drawAnimations(SpriteBatch batch){
        TextureRegion currentFrame = spriteSheet[0][0];
        if (isFacingRight) {
            currentFrame = walkRightAnimation.getKeyFrame(animationRightTime);
        }
        else if(!isFacingRight){
            currentFrame = walkLeftAnimation.getKeyFrame(animationLeftTime);
        }

        batch.draw(currentFrame, isFacingRight ? hitBox.x  : hitBox.x + COLE_WIDTH, hitBox.y , isFacingRight ? COLE_WIDTH : -COLE_WIDTH, currentFrame.getRegionHeight());

    }

}
