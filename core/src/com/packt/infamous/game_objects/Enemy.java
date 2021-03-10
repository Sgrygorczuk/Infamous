package com.packt.infamous.game_objects;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
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
    protected static float moveSpeed = 20f;
    protected  float walkingDistance;
    protected boolean isFacingRight = false;
    protected float initialX;
    public Rectangle visionCone;
    protected static float visionWidth = COLE_WIDTH*6;
    protected static float visionHeight = COLE_HEIGHT/2;


    public Enemy(float x, float y, float distance, Alignment alignment){
        super(x, y, alignment);
        hitBox.height = COLE_HEIGHT;
        hitBox.width = COLE_WIDTH;
        walkingDistance = distance-COLE_WIDTH;
        initialX = x;
        currentHealth = maxHealth = 100;
        ammo = 10;
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
            // TODO: or if enemy doesn't see cole, reload
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
        } else {
            reloading -= delta;
            if(reloading <= 0){
                ammo = maxAmmo;
            }
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

    public void drawAnimations(SpriteBatch batch){
        TextureRegion currentFrame = spriteSheet[0][0];


        if (isFacingRight) {
            currentFrame = walkRightAnimation.getKeyFrame(animationRightTime);
        }
        else if(!isFacingRight){
            currentFrame = walkLeftAnimation.getKeyFrame(animationLeftTime);
        }

        batch.draw(currentFrame, isFacingRight ? hitBox.x + currentFrame.getRegionWidth() : hitBox.x , hitBox.y , isFacingRight ? currentFrame.getRegionWidth() : -currentFrame.getRegionWidth(), currentFrame.getRegionHeight());
    }

}
