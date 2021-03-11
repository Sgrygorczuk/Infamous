package com.packt.infamous.game_objects;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.packt.infamous.Alignment;

public class Water extends GenericObject{

    private float yMin;
    private float yMax;

    private boolean direction = false; //True = Up | False = Down

    public Water(float x, float y, Alignment alignment){
        super(x, y, alignment);
    }

    public void setTexture(Texture texture){
        this.texture = texture;
        this.hitBox.height = texture.getHeight();
        this.hitBox.width = texture.getWidth();

        yMin = hitBox.y - MathUtils.random(6,10);
        yMax = hitBox.y - 2;
    }

    public void updatePosition(){
        if(direction){
            if(hitBox.y > yMin) { hitBox.y -= 0.1; }
            else{ direction = !direction; }
        }
        else{
            if(hitBox.y < yMax) { hitBox.y += 0.1f; }
            else{ direction = !direction; }
        }
    }

}
