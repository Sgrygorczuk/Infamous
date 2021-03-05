package com.packt.infamous.game_objects;

import com.badlogic.gdx.math.Rectangle;
import com.packt.infamous.Alignment;

import static com.packt.infamous.Const.ACCELERATION;
import static com.packt.infamous.Const.FRICTION;
import static com.packt.infamous.Const.GRAVITY;
import static com.packt.infamous.Const.JUMP;
import static com.packt.infamous.Const.MAX_VELOCITY;

public class Cole extends GenericObject{

    protected boolean touchingPlatform = false;
    protected Rectangle previousCollisionBox = null;

    
    /* =========================== Movement Variables =========================== */

    protected float yAccel;     //value of jump speed
    protected float xAccel;     //value of increased speed for chosen direction
    protected float xDecel;     //value of decreased speed for chosen direction
    protected float xMaxVel;    //maximum x velocity allowed

    public Cole(float x, float y, Alignment alignment) {
        super(x, y, alignment);
        velocity.y = GRAVITY;
        yAccel = JUMP;
        xAccel = ACCELERATION;
        xDecel = FRICTION;
        xMaxVel = MAX_VELOCITY;
    }

    public void update(){
        hitBox.y += velocity.y;
        hitBox.x += velocity.x;
        updateGravity();
        decelerate();
    }

    private void updateGravity(){
        if (touchingPlatform){
            velocity.y = 0;
        }
        else if(velocity.y > GRAVITY){
            velocity.y += GRAVITY;
        }
    }

    private void decelerate(){
        if (velocity.x > xDecel)
            velocity.x = velocity.x - xDecel;
        else if (velocity.x < -xDecel)
            velocity.x = velocity.x + xDecel;
        else
            velocity.x = 0;
    }

    public void moveHorizontally(int direction){
        if ((velocity.x < xMaxVel) && (velocity.x > -xMaxVel))
            velocity.x += direction*xAccel;
    }
 
    public void jump(){
        if (isTouchingPlatform())
            velocity.y = yAccel;
    }

    /* ============================ Utility Functions =========================== */

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


}
