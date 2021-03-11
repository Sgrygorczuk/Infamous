package com.packt.infamous.game_objects;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.packt.infamous.Alignment;

import static com.packt.infamous.Const.ACCELERATION;
import static com.packt.infamous.Const.ATTACK_DELAY;
import static com.packt.infamous.Const.COLE_HEIGHT;
import static com.packt.infamous.Const.FRICTION;
import static com.packt.infamous.Const.FRICTION_AFTER_RIDE;
import static com.packt.infamous.Const.GRAVITY;
import static com.packt.infamous.Const.HOVER_GRAVITY;
import static com.packt.infamous.Const.INVINCIBILITY_TIME;
import static com.packt.infamous.Const.JUMP_PEAK;
import static com.packt.infamous.Const.MAX_VELOCITY;

import com.packt.infamous.Enum;

public class Cole extends GenericObject{
    private DrainableObject previousDrainable = null;

    private boolean isJumping = false;    //Used to tell that it's not touching a platform
    private boolean isFalling = false;    //Used to tell if Cole if falling off a platform
    private boolean isRising = false;     //Used to create a arc for the jump
    private boolean isDucking = false;    //Tells us if cole is ducking
    private boolean isRiding = false;
    private boolean ridingFriction = false;
    private boolean isFacingRight = true; //Tells us which way cole is facing
    private boolean isDraining = false;   //Tells us to use the drain animation
    private boolean invincibilityFlag = false; //Tells us if he's invincible
    protected boolean flashing = false;      //Tells the animation to flash or not
    private float initialY;               //Where the jump starts from

    private float lastTouchedGroundX;
    private float lastTouchedGroundY;

    protected TextureRegion[][] drainingParticleEffectSpriteSheet;
    protected Animation<TextureRegion> drainingParticleEffectAnimation;
    protected float drainingParticleEffectTime = 0;

    protected TextureRegion[][] railSparkEffectSpriteSheet;
    protected Animation<TextureRegion> railSparkEffectAnimation;
    protected float railSparkEffectTime = 0;

    protected TextureRegion[][] hoverEffectSpriteSheet;
    protected Animation<TextureRegion> hoverEffectAnimation;
    protected float hoverEffectTime = 0;


    protected TextureRegion[][] meleeEffectSpriteSheet;
    protected Animation<TextureRegion> meleeEffectAnimation;
    protected Animation<TextureRegion> meleeAnimation;
    protected float meleeEffectTime = 0;

    protected Animation<TextureRegion> shimmyAnimation;
    protected float shimmyTime = 0;

    private final Rectangle meleeRangeBox;      //Used to tell if the player is in range to do melee attack

    private Array<String> attackNames;
    private int attackIndex = 0;

    private boolean isHovering = false;
    private float relativeGravity;

    //Attack Related
    private boolean canDrain = false;
    private boolean isAttacking = false;
    private boolean hasAttacked = false;
    private boolean hasMeleeAttacked = false;
    private boolean canMelee = false;

    //Timer for delaying attacks
    private boolean canAttack = true;
    private float attackTimer = ATTACK_DELAY;

    //Timer counting down until player can be hit again
    private static final float INVINCIBILITY_TIME = 1.5F;
    private float invincibilityTimer = INVINCIBILITY_TIME;

    //Timer counting down until we turn the draw function on/Off
    private static final float FLASHING_TIME = 0.1F;
    private float flashingTimer = FLASHING_TIME;

    //Attack Animation Timer
    private static final float HAS_ATTACKED = 0.5F;
    private float hasAttackedTimer = HAS_ATTACKED;


    //============================= Climbing Stuff ===============================
    private boolean isTouchPole = false;
    private boolean isClimbingPole = false; //Tells us if Cole is climbing a pole
    private boolean isTouchingLedge = false;
    private boolean isHangingLedge = false; //Tells us if Cole is holding on a ledge

    private float poleMin;
    private float poleMax;

    private float ledgeMin;
    private float ledgeMax;

    /* =========================== Movement Variables =========================== */

    protected float yAccel;     //value of jump speed
    protected float xAccel;     //value of increased speed for chosen direction
    protected float xDecel;     //value of decreased speed for chosen direction
    protected float xMaxVel;    //maximum x velocity allowed

    //============================ Constructor ========================================

    public Cole(float x, float y, Alignment alignment) {
        super(x, y, alignment);
        relativeGravity = GRAVITY;
        velocity.y = -relativeGravity;
        yAccel = relativeGravity;
        xAccel = ACCELERATION;
        xDecel = FRICTION;
        xMaxVel = MAX_VELOCITY;
        maxEnergy = maxHealth = currentHealth = currentEnergy = 100;

        setAttackNames();

        meleeRangeBox = new Rectangle(hitBox.x - hitBox.width, hitBox.y, hitBox.width * 3, hitBox.height * 2);
    }


    public void setUpSpriteSheet(TextureRegion[][] textureRegions, TextureRegion[][] chargeTexture,
                                 TextureRegion[][] railSparkEffectSpriteSheet, TextureRegion[][] hoverSpriteSheet,
                                 TextureRegion[][] meleeEffectSpriteSheet){
        this.spriteSheet = textureRegions;
        this.drainingParticleEffectSpriteSheet = chargeTexture;
        this.railSparkEffectSpriteSheet = railSparkEffectSpriteSheet;
        this.hoverEffectSpriteSheet = hoverSpriteSheet;
        this.meleeEffectSpriteSheet = meleeEffectSpriteSheet;

        setUpAnimations();
        setUpShimmyAnimation();
        drainingParticleEffectAnimation = setUpAnimation(drainingParticleEffectSpriteSheet, 1/4f,0, Animation.PlayMode.LOOP_PINGPONG);
        hoverEffectAnimation = setUpAnimation(hoverSpriteSheet, 1/6f, 0, Animation.PlayMode.LOOP);
        railSparkEffectAnimation = setUpAnimation(railSparkEffectSpriteSheet, 1/6f,0, Animation.PlayMode.LOOP_PINGPONG);
        setUpMeleeAnimation();
    }

    protected void setUpShimmyAnimation(){
        shimmyAnimation =new Animation<TextureRegion>(1/6f, spriteSheet[2][0], spriteSheet[2][1], spriteSheet[2][2]);
        shimmyAnimation.setPlayMode(Animation.PlayMode.LOOP_PINGPONG);
    }

    protected void setUpMeleeAnimation(){
        meleeAnimation = new Animation<TextureRegion>(1/9f, spriteSheet[3][1], spriteSheet[3][2], spriteSheet[3][3], spriteSheet[4][0], spriteSheet[4][1]);
        meleeAnimation.setPlayMode(Animation.PlayMode.LOOP);

        meleeEffectAnimation = setUpAnimation(meleeEffectSpriteSheet, 1/9f, 0, Animation.PlayMode.LOOP);
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

    public void update(float levelWidth, float levelHeight, float delta){
        assertWorldBound(levelWidth, levelHeight);
        updateDrainableRange();
        updateVelocityY();
        decelerate();
        attackTimer(delta);

        if(velocity.x > 0){
            animationLeftTime += delta;
            isFacingRight = false;
        }
        if(velocity.x < 0){
            animationRightTime += delta;
            isFacingRight = true;
        }

        if(isDraining){ drainingParticleEffectTime += delta; }
        if(isRiding){railSparkEffectTime += delta;}
        if(isHovering){hoverEffectTime += delta;}
        if(hasAttacked && hasMeleeAttacked){meleeEffectTime += delta;}

        if(hasAttacked) {
            hasAttackedTimer -= delta;
            if (hasAttackedTimer <= 0) {
                hasAttackedTimer = HAS_ATTACKED;
                hasAttacked = false;
                hasMeleeAttacked = false;
            }
        }

        updateDucking();

        hitBox.y += velocity.y;
        hitBox.x += velocity.x;

        //Follow the hitBox
        meleeRangeBox.height = hitBox.height;
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
            if (isRising && hitBox.y < initialY + JUMP_PEAK) { velocity.y += relativeGravity; }
            //Checks if we reached peaked or Cole hit something above him
            else if (isRising) { isRising = false; }
            //Starts falling back down
            else if (velocity.y > -relativeGravity) { velocity.y -= relativeGravity; }
        }
        //=========================== Hovering ==========================
        else if(isHovering){velocity.y = -HOVER_GRAVITY;}
        //================== Is holding onto something ======================
        else if(isClimbingPole || isHangingLedge || isRiding){
            velocity.y = 0;
        }
        //================== Player Walked Off A Platform ===========
        else if(isFalling) {
            if (velocity.y > -relativeGravity) { velocity.y -= relativeGravity; }
        }
        //==================== Is Standing on a Platform =============
        else{
            velocity.y = 0;
        }
    }

    private void decelerate(){
        float deceleration = xDecel;
        if(ridingFriction){deceleration = FRICTION_AFTER_RIDE;}

        if (velocity.x > deceleration)
            velocity.x = velocity.x - deceleration;
        else if (velocity.x < -deceleration)
            velocity.x = velocity.x + deceleration;
        else
            velocity.x = 0;

    }

    public void moveHorizontally(int direction){
        if ((velocity.x < xMaxVel) && (velocity.x > -xMaxVel))
            velocity.x += direction*xAccel;
    }

    public void setFriction(boolean friction){ridingFriction = friction;}

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
    public void setFalling(boolean falling){isFalling = falling;}

    public boolean getIsFalling(){return isFalling;}

    /**
     * Purpose: To tell MainScreen that the use can click Jump
     * @return returns isJumping state
     */
    public boolean getIsJumping(){return isJumping;}

    public void setIsJumping(boolean isJumping){this.isJumping = isJumping;}

    /**
     * Purpose: If cole is doing something that stops him from walking, it disables users
     * ability to move
     * @return tells us if Cole can move
     */
    public boolean canColeMove(){
        return !isClimbingPole && !isHangingLedge;
    }

    //====================== Rail Riding ==============================================

    public void setIsRiding(boolean isRiding){ this.isRiding = isRiding;}

    public boolean getIsRiding(){return isRiding;}

    //================================== Respawn ========================================

    public void setLastTouchedGround(){
        lastTouchedGroundX = hitBox.x;
        lastTouchedGroundY = hitBox.y;
    }

    public void touchedWater(){
        if(currentHealth - 20 > 0){
           takeDamage(20);
        }
        else{
            //TODO: Game Over
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


    public boolean getIsDucking(){return !isDucking;}

    /**
     * Purpose: Allow use to change Cole's stance
     * @param isDucking sets if he's ducking or not
     */
    public void setDucking(boolean isDucking){this.isDucking = isDucking;}

    //=============================== Pole ===============================================

    public boolean getIsClimbingPole(){return isClimbingPole;}

    public boolean getIsTouchingPole(){return isTouchPole;}

    public void setIsTouchingPole(boolean touchingPole){this.isTouchPole = touchingPole; }

    public void setIsClimbingPole(boolean climbingPole){this.isClimbingPole = climbingPole; }

    public void setPoleLimits(float min, float max){
        poleMin = min;
        poleMax = max;
    }

    public void climbPole(boolean direction, float delta){
        if(direction && hitBox.y + hitBox.height < poleMax){ hitBox.y += 1; }
        else if(!direction && hitBox.y > poleMin){ hitBox.y -= 1; }
        shimmyTime += delta;
    }

    //================================= Ledge ========================================

    public boolean getIsHangingLedge(){return isHangingLedge;}

    public boolean getIsTouchingLedge(){return isTouchingLedge;}

    public void setIsTouchingLedge(boolean touchingLedge){this.isTouchingLedge = touchingLedge; }

    public void setIsHangingLedge(boolean hangingLedge){this.isHangingLedge = hangingLedge; }

    public void setLedgeLimits(float min, float max){
        ledgeMin = min;
        ledgeMax = max;
    }

    public void shimmyLedge(boolean direction, float delta){
        if(direction && hitBox.x + hitBox.width < ledgeMax){ hitBox.x += 1;}
        else if(!direction && hitBox.x > ledgeMin){ hitBox.x -= 1; }
        shimmyTime += delta;
    }


    /* ============================ Combat Functions =========================== */

    /**
     Input: Float delta
     Output: Void
     Purpose: Ticks down to turn off invincibility
     */
    public void invincibilityTimer(float delta){
        invincibilityTimer -= delta;
        flashingTimer -= delta;

        if (flashingTimer <= 0) {
            flashingTimer = FLASHING_TIME;
            flashing = !flashing;
        }

        if (invincibilityTimer <= 0) {
            invincibilityTimer = INVINCIBILITY_TIME;
            invincibilityFlag = false;
            flashing = false;
        }
    }

    public boolean getInvincibility(){return invincibilityFlag;}

    public void setInvincibility(boolean invincibility){this.invincibilityFlag = invincibility;}

    /**
     Input: Float delta
     Output: Void
     Purpose: Ticks down to delay attacks
     */
    public void attackTimer(float delta){
        if (!canAttack) {
            attackTimer -= delta;

            if (attackTimer <= 0) {
                canAttack = true;
            }
        }
    }

    /**
     * Purpose: Sets isAttacking to true, allows MainScreen to create projectile instance
     */
    public void attack(){
        ///If attack timer not complete, do not not allow attack
        //If out of melee range, projectile: uses energy
        if (!canAttack){
            return;
        }
        else if(canMelee){
            isAttacking = true;
            hasMeleeAttacked = true;
        }
        else if (currentEnergy > 0){
            switch(Enum.fromInteger(attackIndex)){
                case BOLT:
                    currentEnergy -= 5;
                    break;
                case BOMB:
                    currentEnergy -= 10;
                    break;
                case TORPEDO:
                    currentEnergy -= 15;
                    break;
            }
            isAttacking = true;
        }
        hasAttacked = isAttacking;
    }

    public void updateAttackIndex(){
        attackIndex++;
        if(attackIndex == attackNames.size){ attackIndex = 0; }
    }

    public int getAttackIndex() {
        return attackIndex;
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
    public void drainEnergy(float delta){
        isDraining = false;
        if (!canDrain || previousDrainable.getCurrentEnergy() == 0) {
            //Play fail sound
        }
        else if (this.currentEnergy < this.maxEnergy || this.currentHealth < this.maxHealth) {
            isDraining = true;
            int source_energy = previousDrainable.removeEnergy();
            if (this.currentHealth < this.maxHealth) {
                this.currentHealth += source_energy;
            }
            if (this.currentEnergy < this.maxEnergy) {
                currentEnergy += source_energy;
            }
            else {
                //Play fail sound
            }
        }
    }

    public void setDraining(boolean draining){this.isDraining = draining;}


    /* ============================ Utility Functions =========================== */
    /**
     * Purpose: Keeps Cole within the level
     * @param levelWidth tells where the map ends
     */

    private void assertWorldBound(float levelWidth, float levelHeight) {
        checkIfWorldBound(levelWidth, levelHeight);
        if (touchedCeiling) {isFalling = true; touchedCeiling = false;}
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
                    && this.hitBox.y >= rectangle.y + rectangle.height * 0.7f){
                this.hitBox.y = rectangle.y + rectangle.height;
                isJumping = false;  //Can jump again
                isFalling = false;  //Is no longer falling
                setIsHovering(false);
                ridingFriction = false;
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
                    && hitBox.x <= rectangle.x + rectangle.width * 0.9f
                    && !isRiding){
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
                    && this.hitBox.y >= rectangle.y
                    && !isRiding){
                this.hitBox.x = rectangle.x - this.hitBox.width;
                velocity.x = 0; //Stops movement
            }
            //=============== On the Right of the Colliding Platform ====================
            else if(this.hitBox.x <= rectangle.x + rectangle.width
                    && this.hitBox.x > rectangle.x
                    && !(this.hitBox.y >= rectangle.y + rectangle.height)
                    && this.hitBox.y >= rectangle.y
                    && !isRiding){
                this.hitBox.x = rectangle.x + rectangle.width;
                velocity.x = 0; //Stop movement
            }
        }
        //Creates a second rectangle that's a little below Cole to see if there is any
        //Platform below him
        Rectangle fallCheckBox = new Rectangle(hitBox.x, hitBox.y - 1, hitBox.width, hitBox.height * 0.1f);
        return fallCheckBox.overlaps(rectangle);
    }

    /**
     * Purpose: Draws the melee range for Cole
     */
    public void drawMeleeDebug(ShapeRenderer shapeRenderer) {
        shapeRenderer.rect(meleeRangeBox.x, meleeRangeBox.y, meleeRangeBox.width, meleeRangeBox.height);
    }

    public boolean isCollidingMelee(Rectangle other) { return this.meleeRangeBox.overlaps(other); }

    public void setPreviousDrainable(DrainableObject drainable){
        previousDrainable = drainable;
    }

    public DrainableObject getPreviousDrainable(){
        return previousDrainable;
    }

    /**
     * Purpose: Sets Cole to no longer allow recharge
     */
    private void updateDrainableRange(){
        if (previousDrainable == null ||
                isCollidingMelee(previousDrainable.getHitBox()) == false ||
                previousDrainable.getCurrentEnergy() < 0){
            canDrain = false;
            previousDrainable = null;
        }
    }

    public void drawAnimations(SpriteBatch batch){
        TextureRegion currentFrame = spriteSheet[0][0];
        if(!flashing) {
            //=========================== Cole ============================================
            if (isHovering) {
                currentFrame = spriteSheet[3][0];
            } else if (isRiding) {
                currentFrame = spriteSheet[2][3];
            } else if (isClimbingPole || isHangingLedge) {
                currentFrame = shimmyAnimation.getKeyFrame(shimmyTime);
            } else if (isDraining) {
                currentFrame = spriteSheet[1][3];
            } else if (isJumping || isFalling) {
                currentFrame = spriteSheet[1][1];
            } else if(hasAttacked && hasMeleeAttacked){
                currentFrame = meleeAnimation.getKeyFrame(meleeEffectTime);
            } else if (hasAttacked) {
                currentFrame = spriteSheet[1][2];
            } else if (isDucking) {
                currentFrame = spriteSheet[1][0];
            } else if (isFacingRight) {
                if (velocity.x != 0) {
                    currentFrame = walkRightAnimation.getKeyFrame(animationRightTime);
                }
            } else if (!isFacingRight) {
                if (velocity.x != 0) {
                    currentFrame = walkLeftAnimation.getKeyFrame(animationLeftTime);
                }
            }

            batch.draw(currentFrame, isFacingRight ? hitBox.x + currentFrame.getRegionWidth() : hitBox.x, hitBox.y, isFacingRight ? -currentFrame.getRegionWidth() : currentFrame.getRegionWidth(), currentFrame.getRegionHeight());

            //========================== Particle Effects =================================
            TextureRegion currentParticleFrame;

            if(hasAttacked && hasMeleeAttacked){
                currentParticleFrame = meleeEffectAnimation.getKeyFrame(meleeEffectTime);
                batch.draw(currentParticleFrame, isFacingRight ? hitBox.x + 2 * hitBox.width : hitBox.x - hitBox.width, hitBox.y, isFacingRight ? -hitBox.width * 3 : hitBox.width * 3, hitBox.height);
            }
            if (isDraining) {
                currentParticleFrame = drainingParticleEffectAnimation.getKeyFrame(drainingParticleEffectTime);
                batch.draw(currentParticleFrame, isFacingRight ? hitBox.x + 2 * hitBox.width : hitBox.x - hitBox.width, hitBox.y, isFacingRight ? -hitBox.width * 3 : hitBox.width * 3, hitBox.height);

            } else if (isRiding) {
                currentParticleFrame = railSparkEffectAnimation.getKeyFrame(railSparkEffectTime);
                batch.draw(currentParticleFrame, isFacingRight ? hitBox.x + 2 * hitBox.width : hitBox.x - hitBox.width, hitBox.y, isFacingRight ? -hitBox.width * 2 : hitBox.width * 2, hitBox.height);
            } else if (isHovering) {
                currentParticleFrame = hoverEffectAnimation.getKeyFrame(hoverEffectTime);
                batch.draw(currentParticleFrame, isFacingRight ? hitBox.x + hitBox.width : hitBox.x, hitBox.y, isFacingRight ? -hitBox.width : hitBox.width, hitBox.height);

            }
        }
    }

    public void setIsHovering(boolean enabled) { isHovering = enabled;}
    public boolean getIsRising() {return isRising;}
    public boolean getIsHovering() {return isHovering;}
    public boolean getIsFacingRight(){ return isFacingRight;}
    public boolean isIsAttacking() {return isAttacking;}
    public boolean isCanMelee() {return canMelee;}
    public void setCanMelee(boolean state) {canMelee = state;}
    public boolean getCanAttack() {return canAttack;}
    public void resetAttackTimer(){attackTimer = ATTACK_DELAY; canAttack = false;}
    public void setIsAttacking(boolean state) {isAttacking = state;}

}
