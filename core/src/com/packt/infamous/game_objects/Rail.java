package com.packt.infamous.game_objects;


import com.badlogic.gdx.math.Rectangle;
import com.packt.infamous.Alignment;

public class Rail extends Platforms{
    public Rail(float x, float y, float width, float height, Alignment alignment){
        super(x,y,alignment);
        this.setWidth(width);
        this.setHeight(height);
    }
    public boolean rideRail(Cole player){
        System.out.println("Player Facing: "+player.facingDirection);
        if(hitBox.overlaps(player.getHitBox())){
            int xVelocity;
            if ( player.facingDirection ){
                xVelocity = -5;
            } else {
                xVelocity = 5;
            }
            player.setVelocity(xVelocity, 0);
            return true;
        }
        return false;
    }
}
