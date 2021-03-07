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

public class GenericObject {

    protected Alignment align;
    protected Rectangle hitBox;
    protected Vector2 velocity;

    protected int maxHealth;
    protected int maxEnergy;
    protected int currentHealth;
    protected int currentEnergy;

    //Sprite sheet used
    protected TextureRegion[][] spriteSheet;
    protected Texture texture;

    protected Animation<TextureRegion> walkRightAnimation;
    protected Animation<TextureRegion> walkLeftAnimation;

    float animationFrameTime = 4;
    protected float animationRightTime = 0;
    protected float animationLeftTime = 0;


    GenericObject(float x , float y, Alignment aling){
        this.align = aling;
        this.hitBox = new Rectangle(x, y, TILED_WIDTH, TILED_HEIGHT);
        this.velocity = new Vector2(0, 0);
    }

    public void setTexture(Texture texture){
        this.texture = texture;
        this.hitBox.height = texture.getHeight();
        this.hitBox.width = texture.getWidth();
    }

    public void setWidth(float width){hitBox.width = width;}

    public void setHeight(float height){hitBox.height = height;}

    public Rectangle getHitBox(){return hitBox;}

    public float getX(){return hitBox.x;}

    public float getY(){return hitBox.y;}

    public void setY(float y){hitBox.y = y;}

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
        walkRightAnimation = setUpAnimation(1/animationFrameTime, 0, Animation.PlayMode.LOOP);
        walkLeftAnimation = setUpAnimation(1/animationFrameTime, 0, Animation.PlayMode.LOOP_REVERSED);
    }

    private Animation<TextureRegion> setUpAnimation(float duration, int row, Animation.PlayMode playMode){
        Animation<TextureRegion> animation = new Animation<>(duration, this.spriteSheet[row]);
        animation.setPlayMode(playMode);
        return animation;
    }


    public boolean isColliding(Rectangle other) { return this.hitBox.overlaps(other); }

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
