package com.packt.infamous.game_objects;

import com.badlogic.gdx.math.Rectangle;
import com.packt.infamous.Alignment;

import static com.packt.infamous.Const.GRAVITY;

public class Cole extends GenericObject{

    protected boolean touchingPlatform = false;
    protected boolean touchPole = false;
    private boolean ridingPole = false;
    protected Rectangle previousCollisionBox = null;

    public Cole(float x, float y, Alignment alignment) {
        super(x, y, alignment);
        velocity.y = GRAVITY;
    }

    public void update(){
        hitBox.y += velocity.y;
        hitBox.x += velocity.x;
        if(!ridingPole){
            updateGravity();
        }
    }

    private void updateGravity(){
        if (touchingPlatform){
            velocity.y = 0;
        }
        else if(velocity.y > GRAVITY){
            velocity.y += GRAVITY;
        }
    }

    public void moveHorizontally(float speed){
        velocity.x = speed;
    }

    public boolean isTouchingPlatform(){
        if (previousCollisionBox == null) {return false;} //Return false if not colliding with anything
        return (isColliding(previousCollisionBox));
    }

    public void setTouchingPlatform(boolean state) {
        setTouchingPlatform(state, null);
    }

    public void setTouchingPlatform(boolean state, Rectangle hitBox) {
        touchingPlatform = state;
        previousCollisionBox = hitBox;
    }

    public void setTouchPole(boolean touchPole){this.touchPole = touchPole;}

    public boolean isTouchPole(){return touchPole;}

    public boolean isRidingPole(){return ridingPole;}

    public void setRidingPole(boolean ridingPole) { this.ridingPole = ridingPole; }

    public void setPoleVelocity(){
        velocity.x = 0;
        velocity.y = 5;
    }

    public void hover(){
        velocity.y = -0.5f;
    }

    public void jump(){
        velocity.y = 10f;
    }
}
