package com.packt.infamous.game_objects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.packt.infamous.Alignment;

public class Civilian extends GenericObject{

    TextureRegion downTexture;
    TextureRegion healedTexture;

    boolean state = false; //Downed = false, Healed = true

    public Civilian(float x, float y, TextureRegion down, TextureRegion up){
        super(x, y, Alignment.ENEMY);
        downTexture = down;
        healedTexture = up;
        hitBox.width = down.getRegionWidth();
        hitBox.height = down.getRegionHeight();
        maxHealth = currentHealth = 20;
    }

    public void updateState(){

    }

    public void draw(SpriteBatch batch){
        TextureRegion currentTexture = downTexture;
        if(state){ currentTexture = healedTexture; }

        batch.draw(currentTexture, hitBox.x, hitBox.y, currentTexture.getRegionWidth(), currentTexture.getRegionHeight());
    }
}
