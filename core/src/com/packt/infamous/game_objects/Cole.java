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
    protected boolean touchPole = false;
    private boolean ridingPole = false;
    protected Rectangle previousCollisionBox = null;
    protected boolean isJumping = false;
    public boolean isFloating = false;
    boolean isRising = false;
    float initialY;


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

    public void update(float delta){
        hitBox.y += velocity.y;
        hitBox.x += velocity.x;

        if(!ridingPole){
            updateGravity();
            decelerate();
        }
    }

    private void updateGravity(){
        if(isRising && hitBox.y < initialY + 20){
            velocity.y += JUMP;
        }
        else if(isRising){isRising = false;}
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
        if (!isJumping) {
            isJumping = true;
            isRising = true;
            initialY = hitBox.y;
            velocity.y += yAccel;
        }
    }

    /* ============================ Utility Functions =========================== */

    public void updateCollision(Rectangle rectangle){
        if(this.hitBox.overlaps(rectangle)){
            /* Breakdown of Collision
              this.hitBox.y < rectangle.y + rectangle.height - checks if we're dipping into the box from the top
              this.hitBox.y >= rectangle.y + rectangle.height * 0.8f - makes sure it's only the very top of the box we're going into
             */
            //=============== On Top Of the Colliding Platform ====================
            if(this.hitBox.y <= rectangle.y + rectangle.height
                    && this.hitBox.y >= (rectangle.y + rectangle.height) * 0.8f){
                this.hitBox.y = rectangle.y + rectangle.height;
                isJumping = false;
                isFloating = false;
            }
            /* Breakdown of Collision
              this.hitBox.y + this.hitBox.height > rectangle.y- checks if we're dipping into the box from the bottom
              this.hitBox.y < rectangle.y < rectangle.y - makes sure that only on the bottom
             */
            //=============== Below the Colliding Platform ====================
            else if(this.hitBox.y + this.hitBox.height > rectangle.y
                    && this.hitBox.y < rectangle.y){
                this.hitBox.y = rectangle.y - this.hitBox.height;
            }

            /* Breakdown of Collision
              this.hitBox.x + this.hitBox.width > rectangle.x - checks if we're dipping into the box from the left
              hitBox.x < rectangle.x - makes sures we're coming from the left
              !(this.hitBox.y >= rectangle.y + rectangle.height) - makes sure we don't do it when on top of the block
              hitBox.y >= rectangle.y - can't fully remember why
             */
            //=============== On the Left of the Colliding Platform ====================
            if(this.hitBox.x + this.hitBox.width > rectangle.x
                    && hitBox.x < rectangle.x
                    && !(this.hitBox.y >= rectangle.y + rectangle.height)
                    && hitBox.y >= rectangle.y){
                this.hitBox.x = rectangle.x - this.hitBox.width;
            }
            //=============== On the Right of the Colliding Platform ====================
            else if(this.hitBox.x < rectangle.x + rectangle.width
                    && this.hitBox.x > rectangle.x
                    && !(this.hitBox.y >= rectangle.y + rectangle.height)
                    && hitBox.y >= rectangle.y){
                this.hitBox.x = rectangle.x + rectangle.width;
            }
        }
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
        isFloating = true;
    }
}
