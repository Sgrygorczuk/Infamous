package com.packt.infamous.game_objects;

import com.packt.infamous.Alignment;

import static com.packt.infamous.Const.GRAVITY;

public class Cole extends GenericObject{


    public Cole(float x, float y, Alignment alignment) {
        super(x, y, alignment);
        velocity.y = GRAVITY;
    }

    public void update(){
        hitBox.y += velocity.y;
        hitBox.x = velocity.x;
        updateGravity();
    }

    private void updateGravity(){
        if(velocity.y > GRAVITY){
            velocity.y += GRAVITY;
        }
    }

    public void moveHorizontally(float speed){
        velocity.x = speed;
    }

    public void jump(){
        velocity.y = 4f;
    }

}
