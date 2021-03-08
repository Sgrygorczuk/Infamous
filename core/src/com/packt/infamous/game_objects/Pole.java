package com.packt.infamous.game_objects;


import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;

import static com.packt.infamous.Const.TILED_HEIGHT;
import static com.packt.infamous.Const.TILED_WIDTH;

public class Pole extends GenericObject{

    public Pole(float xStart, float yStart){
        hitBox = new Rectangle(xStart, yStart, TILED_WIDTH, TILED_HEIGHT);
    }

    public Rectangle getHitBox(){return hitBox;}


    /**
     * Purpose: Draws the circle on the screen using render
     */
    public void drawDebug(ShapeRenderer shapeRenderer) {
        shapeRenderer.rect(hitBox.x, hitBox.y, hitBox.width, hitBox.height);
    }
}
