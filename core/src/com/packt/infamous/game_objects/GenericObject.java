package com.packt.infamous.game_objects;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.packt.infamous.Alignment;

import static com.packt.infamous.Const.TILED_HEIGHT;
import static com.packt.infamous.Const.TILED_WIDTH;
import static com.packt.infamous.Const.WORLD_HEIGHT;

public class GenericObject {

    protected Alignment align;
    protected Rectangle hitBox;
    protected Vector2 velocity;

    protected int maxHealth;
    protected int maxEnergy;
    protected int currentHealth;
    protected int currentEnergy;


    protected Texture texture;
    //Sprite sheet used
    protected TextureRegion[][] spriteSheet;

    protected Animation<TextureRegion> walkRightAnimation;
    protected Animation<TextureRegion> walkLeftAnimation;

    float animationFrameTime = 6;
    protected float animationRightTime = 0;
    protected float animationLeftTime = 0;

    protected boolean touchedCeiling;


    public GenericObject(float x , float y, Alignment align){
        this.align = align;
        this.hitBox = new Rectangle(x, y, TILED_WIDTH, TILED_HEIGHT);
        this.velocity = new Vector2(0, 0);
    }

    public GenericObject() {
    }

    public void setTexture(Texture texture){
        this.texture = texture;
        this.hitBox.height = texture.getHeight()/2f;
        this.hitBox.width = texture.getWidth()/2f;
    }

    public void setWidth(float width){hitBox.width = width;}

    public void setHeight(float height){hitBox.height = height;}

    public Rectangle getHitBox(){return hitBox;}

    public float getX(){return hitBox.x;}

    public float getY(){return hitBox.y;}

    public float getWidth(){return hitBox.width;}

    public void setY(float y){hitBox.y = y;}
    public void setX(float x){hitBox.x = x;}

    public void setX(float x){hitBox.x = x;}

    public float getHeight(){return hitBox.height;}

    public float getMaxHealth(){return maxHealth;}
    public float getCurrentHealth(){return currentHealth;}

    public float getMaxEnergy(){return maxEnergy;}
    public float getCurrentEnergy(){return currentEnergy;}

    public void setVelocity(float x, float y){
        velocity.x = x;
        velocity.y = y;
    }

    public Vector2 getVelocity(){return velocity;}

    /**
     * Purpose: Sets up the animation loops in all of the directions
     */
    protected void setUpAnimations() {
        walkRightAnimation = setUpAnimation(spriteSheet, 1/animationFrameTime, 0, Animation.PlayMode.LOOP);
        walkLeftAnimation = setUpAnimation(spriteSheet, 1/animationFrameTime, 0, Animation.PlayMode.LOOP_REVERSED);
    }

    protected Animation<TextureRegion> setUpAnimation(TextureRegion[][] textureRegion, float duration, int row, Animation.PlayMode playMode){
        Animation<TextureRegion> animation = new Animation<>(duration, textureRegion[row]);
        animation.setPlayMode(playMode);
        return animation;
    }


    public boolean isColliding(Rectangle other) { return this.hitBox.overlaps(other); }


    /**
     * Purpose: Keeps Object within the level
     * @param levelWidth tells where the map ends
     */
    protected void checkIfWorldBound(float levelWidth) {
        //Makes sure we're bound by x
        if (hitBox.x < 0) {
            hitBox.x = 0;
            velocity.x = 0;
        }
        else if (hitBox.x + hitBox.width > levelWidth) {
            hitBox.x = (int) (levelWidth - hitBox.width);
            velocity.x = 0;
        }

        //Makes sure that we stop moving down when we hit the ground
        if (hitBox.y < 0) {
            hitBox.y = 0;
            velocity.y = 0;
        }
        else if (hitBox.y + hitBox.height > WORLD_HEIGHT){
            hitBox.y = WORLD_HEIGHT - hitBox.height;
            touchedCeiling = true;
        }
    }

    public void takeDamage(float damage){
        currentHealth -= damage;
    }

    public boolean isTouchingCeiling(){ return touchedCeiling;}

    /**
     * Purpose: Draws the circle on the screen using render
     */
    public void drawDebug(ShapeRenderer shapeRenderer) {
        shapeRenderer.rect(hitBox.x, hitBox.y, hitBox.width, hitBox.height);
    }

    public void draw(SpriteBatch batch){
        batch.draw(texture, hitBox.x, hitBox.y, hitBox.width, hitBox.height);
    }

}
