package com.packt.infamous.game_objects;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.packt.infamous.Alignment;

public class DrainableObject extends GenericObject{
    public DrainableObject(float x, float y, Alignment alignment) {
        super(x, y , alignment);
        maxEnergy = currentEnergy = 500;
    }

    public void setTexture(Texture texture){
        this.texture = texture;
        this.hitBox.height = texture.getHeight()/2f;
        this.hitBox.width = texture.getWidth()/2f;
    }

    public int removeEnergy() {
        if (this.currentEnergy > 0) {
            currentEnergy -= 5;
            return 5;
        }
        else { return 0; } //Play fail sound
    }


    public void draw(SpriteBatch batch){
        Sprite sprite = new Sprite(texture);
        if(currentEnergy == 0){ sprite.setColor(Color.GRAY); }
        batch.draw(sprite.getTexture(), hitBox.x, hitBox.y, hitBox.width, hitBox.height);
    }
}