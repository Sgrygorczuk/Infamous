package com.packt.infamous.game_objects.platforms;


import com.packt.infamous.Alignment;
import com.packt.infamous.game_objects.Cole;

public class Rail extends Platforms{
    public Rail(float x, float y, float width, float height, Alignment alignment){
        super(x,y,alignment);
        this.setWidth(width);
        this.setHeight(height);
    }
    public boolean rideRail(Cole player){
//        System.out.println("Player Facing: "+player.getIsFacingRight());
        if(hitBox.overlaps(player.getHitBox())){
            int xVelocity;
            if ( player.getIsFacingRight() ){
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
