package com.packt.infamous.game_objects;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.packt.infamous.Alignment;

import static com.packt.infamous.Const.ACCELERATION;
import static com.packt.infamous.Const.COLE_HEIGHT;
import static com.packt.infamous.Const.FRICTION;
import static com.packt.infamous.Const.GRAVITY;
import static com.packt.infamous.Const.JUMP_PEAK;
import static com.packt.infamous.Const.MAX_VELOCITY;
import static com.packt.infamous.Const.WORLD_HEIGHT;

public class Cole extends GenericObject{

    private boolean touchPole = false;
    private boolean ridingPole = false;

    private DrainableObject previousDrainable = null;
    public boolean facingDirection = false; //false = Left, true = right

    private boolean isJumping = false;    //Used to tell that it's not touching a platform
    private boolean isFalling = false;    //Used to tell if Cole if falling off a platform
    private boolean isRising = false;     //Used to create a arc for the jump
    private boolean isDucking = false;    //Tells us if cole is ducking
    private float initialY;               //Where the jump starts from

    private float lastTouchedGroundX;
    private float lastTouchedGroundY;

    private final Rectangle meleeRangeBox;      //Used to tell if the player is in range to do melee attack

    private Array<String> attackNames;
    private int attackIndex = 0;

    private boolean canDrain = false;

    /* =========================== Movement Variables =========================== */

    protected float yAccel;     //value of jump speed
    protected float xAccel;     //value of increased speed for chosen direction
    protected float xDecel;     //value of decreased speed for chosen direction
    protected float xMaxVel;    //maximum x velocity allowed

    //============================ Constructor ========================================


    public Cole(float x, float y, Alignment alignment) {
        super(x, y, alignment);
        velocity.y = -GRAVITY;
        yAccel = GRAVITY;
        xAccel = ACCELERATION;
        xDecel = FRICTION;
        xMaxVel = MAX_VELOCITY;
        currentHealth = 60;
        currentEnergy = 80;
        maxEnergy = maxHealth = 100;

        setAttackNames();

        meleeRangeBox = new Rectangle(hitBox.x - hitBox.width, hitBox.y, hitBox.width * 3, hitBox.height * 2);
    }

    /**
     * Purpose: Sets up all the attacks that are available to Cole
     */
    private void setAttackNames(){
        attackNames = new Array<>();

        attackNames.add("Lighting Bolt");
        attackNames.add("Bomb");
        attackNames.add("Torpedo");
    }

    /**
     * Purpose: Central Update function for Cole all continuous updates come through here
     * @param levelWidth the end of the level
     */
    public void update(float levelWidth){
        checkIfWorldBound(levelWidth);

        if(!ridingPole){
            updateVelocityY();
            decelerate();
        }

        updateDucking();

        hitBox.y += velocity.y;
        hitBox.x += velocity.x;

        //Follow the hitBox
        meleeRangeBox.x = hitBox.x - hitBox.width;
        meleeRangeBox.y = hitBox.y;
    }

    //=============================== Movement =============================================

    /**
     Purpose: Performs the jumping/falling action
     */
    private void updateVelocityY(){
        //=================== Player Initiated Jumping============
        if(isJumping) {
            //Is rising towards peak
            if (isRising && hitBox.y < initialY + JUMP_PEAK) { velocity.y += GRAVITY; }
            //Checks if we reached peaked or Cole hit something above him
            else if (isRising) { isRising = false; }
            //Starts falling back down
            else if (velocity.y > -GRAVITY) { velocity.y -= GRAVITY; }
        }
        //================== Player Walked Off A Platform ===========
        else if(isFalling) {
            if (velocity.y > -GRAVITY) { velocity.y -= GRAVITY; }
        }
        //==================== Is Standing on a Platform =============
        else{
            velocity.y = 0;
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

    /**
     * Purpose: Initiate the Player jump action
     */
    public void jump(){
        isJumping = true;       //Tells us we're jumping
        isRising = true;        //Tells us we're going up
        initialY = hitBox.y;    //Grabs the initial place where we started so we can find the jump peak
    }

    /**
     * Purpose: Turns isFalling true whenever Cole dosen't have ground below him
     */
    public void setFalling(){isFalling = true;}

    /**
     * Purpose: To tell MainScreen that the use can click Jump
     * @return returns isJumping state
     */
    public boolean getIsJumping(){return isJumping;}

    //================================== Respawn ========================================

    public void setLastTouchedGround(){
        lastTouchedGroundX = hitBox.x;
        lastTouchedGroundY = hitBox.y;
    }

    public void touchedWater(){
        if(currentHealth - 20 > -20){
            currentHealth -= 20;
        }
        else{
            //Game Over
        }

        hitBox.x = lastTouchedGroundX;
        hitBox.y = lastTouchedGroundY;
    }

    //============================= Duck ================================================

    /**
     * Purpose: Have cole be at either full height or 2/3 height depending on if he's ducking
     */
    private void updateDucking(){
        if(!isDucking){ hitBox.height = COLE_HEIGHT; }
        else{ hitBox.height = 2 * COLE_HEIGHT / 3f; }
    }

    /**
     * Purpose: Allow use to change Cole's stance
     * @param isDucking sets if he's ducking or not
     */
    public void setDucking(boolean isDucking){this.isDucking = isDucking;}

    //=============================== Pole ===============================================

    public void setTouchPole(boolean touchPole){this.touchPole = touchPole;}

    public boolean isTouchPole(){return touchPole;}

    public boolean isRidingPole(){return ridingPole;}


    public void setRidingPole(boolean ridingPole) { this.ridingPole = ridingPole; }

    public void setPoleVelocity(){
        velocity.x = 0;
        velocity.y = 5;
    }

    /* ============================ Combat Functions =========================== */

    //TODO: Make attack work, switch between Melee and  Bolt
    public void attack(){
        if (currentEnergy > 0){
        }
    }

    public void attackMelee(){

    }

    public void attackBolt(){

    }

    public void updateAttackIndex(){
        attackIndex++;
        if(attackIndex == attackNames.size){ attackIndex = 0; }
    }

    public String getCurrentAttack(){return attackNames.get(attackIndex);}

    /**
     * Purpose: Allow Cole to drain energy
     * @param state sets if he can drain
     */
    public void setCanDrain(boolean state){this.canDrain = state;}

    /**
     * Purpose: Plays fail sound if Cole cannot drain energy or is full, otherwise restores energy
     */
    public void drainEnergy(){
        if (!canDrain || previousDrainable.getCurrentEnergy() == 0 || this.maxEnergy == this.currentEnergy) {
            //Play fail sound
        }

        else if (this.currentEnergy < this.maxEnergy){
            int source_energy = previousDrainable.removeEnergy();
            this.currentEnergy += source_energy;
        }
    }



    /* ============================ Utility Functions =========================== */


    /**
     * Purpose: Keeps Cole within the level
     * @param levelWidth tells where the map ends
     */
    private void checkIfWorldBound(float levelWidth) {
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
            isFalling = true;
        }
    }

    /**
     * Purpose: Check if Cole is touching any platform
     * @param rectangle the platform we're checking against
     * @return tells us if there is any platform below Cole
     */
    public boolean updateCollision(Rectangle rectangle){
        if(this.hitBox.overlaps(rectangle)){
            //Vertical hitBox collision happens first otherwise he'll get set to the other size of the map

            /* Breakdown of Collision
              this.hitBox.y < rectangle.y + rectangle.height - checks if we're dipping into the box from the top
              this.hitBox.y >= rectangle.y + rectangle.height * 0.8f - makes sure it's only the very top of the box we're going into
                       We do rectangle.y + rectangle.height * 0.8f not (rectangle.y + rectangle.height) * 0.8f because () will have a lower value on
                       lower platforms we only want to multiply the height so that the initial Y is still large
              hitBox.x + hitBox.width > rectangle.x && hitBox.x < rectangle.x + rectangle.width - makes sure to only interact with this platform
             */
            //=============== On Top Of the Colliding Platform ====================
            if(this.hitBox.y <= rectangle.y + rectangle.height
                    && this.hitBox.y >= rectangle.y + rectangle.height * 0.8f){
                this.hitBox.y = rectangle.y + rectangle.height;
                isJumping = false;  //Can jump again
                isFalling = false;  //Is no longer falling
                velocity.y = 0;
            }
            /* Breakdown of Collision
              this.hitBox.y + this.hitBox.height > rectangle.y- checks if we're dipping into the box from the bottom
              this.hitBox.y + this.hitBox.height * 0.8f < rectangle.y - makes sure that only on the bottom
              hitBox.x + hitBox.width > rectangle.x && hitBox.x < rectangle.x + rectangle.width - makes sure to only interact with this platform
             */
            //=============== Below the Colliding Platform ====================
            else if(this.hitBox.y + this.hitBox.height > rectangle.y
                    && this.hitBox.y < rectangle.y
                    && hitBox.x + hitBox.width >= rectangle.x + rectangle.width * 0.1f
                    && hitBox.x <= rectangle.x + rectangle.width * 0.9f){
                this.hitBox.y = rectangle.y - this.hitBox.height;
                isRising = false;   //Stop jump arc
            }

            /* Breakdown of Collision
              this.hitBox.x + this.hitBox.width > rectangle.x - checks if we're dipping into the box from the left
              hitBox.x < rectangle.x - makes sures we're coming from the left
              !(this.hitBox.y >= rectangle.y + rectangle.height) - makes sure we don't do it when on top of the block
              hitBox.y >= rectangle.y - so he doesn't teleports to edge of platform if he touches it from below
             */
            //=============== On the Left of the Colliding Platform ====================
            if(this.hitBox.x + this.hitBox.width >= rectangle.x
                    && hitBox.x < rectangle.x
                    && !(this.hitBox.y >= rectangle.y + rectangle.height)
                    && this.hitBox.y >= rectangle.y){
                this.hitBox.x = rectangle.x - this.hitBox.width;
                velocity.x = 0; //Stops movement
            }
            //=============== On the Right of the Colliding Platform ====================
            else if(this.hitBox.x <= rectangle.x + rectangle.width
                    && this.hitBox.x > rectangle.x
                    && !(this.hitBox.y >= rectangle.y + rectangle.height)
                    && this.hitBox.y >= rectangle.y){
                this.hitBox.x = rectangle.x + rectangle.width;
                velocity.x = 0; //Stop movement
            }
        }
        //Creates a second rectangle that's a little below Cole to see if there is any
        //Platform below him
        Rectangle fallCheckBox = new Rectangle(hitBox.x, hitBox.y - 1, hitBox.width, hitBox.height);
        return fallCheckBox.overlaps(rectangle);
    }

    /**
     * Purpose: Draws the melee range for Cole
     */
    public void drawMeleeDebug(ShapeRenderer shapeRenderer) {
        shapeRenderer.rect(meleeRangeBox.x, meleeRangeBox.y, meleeRangeBox.width, meleeRangeBox.height);
    }

    public boolean isCollidingMelee(Rectangle other) { return this.meleeRangeBox.overlaps(other); }

    public void setPreviousDrainableBox(DrainableObject drainable){
        previousDrainable = drainable;
    }

    /**
     * Purpose: Sets Cole to no longer allow recharge
     */
    private void updateDrainableRange(){
        if (previousDrainable == null || isCollidingMelee(previousDrainable.getHitBox()) == false){
            canDrain = false;
            previousDrainable = null;
        }
    }
}
