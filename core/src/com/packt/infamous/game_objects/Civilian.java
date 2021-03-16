package com.packt.infamous.game_objects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.packt.infamous.Alignment;

public class Civilian extends GenericObject{

    TextureRegion downTexture;
    TextureRegion healedTexture;
    TextureRegion deadTexture;

    int state = 0; //Downed = 0, Healed = 1, Dead = 2

    public Civilian(float x, float y, TextureRegion down, TextureRegion up, TextureRegion dead){
        super(x, y, Alignment.ENEMY);
        downTexture = down;
        healedTexture = up;
        deadTexture = dead;
        hitBox.width = down.getRegionWidth();
        hitBox.height = down.getRegionHeight();
        maxHealth = currentHealth = 20;
    }

    public int getState(){return state;}

    public void healed(){ state = 1; }

    public void kill(){state = 2;}

    public void draw(SpriteBatch batch){
        TextureRegion currentTexture = downTexture;
        if(state == 1){ currentTexture = healedTexture; }
        else if(state == 2){currentTexture = deadTexture;}

        batch.draw(currentTexture, hitBox.x, hitBox.y, currentTexture.getRegionWidth(), currentTexture.getRegionHeight());
    }
}
