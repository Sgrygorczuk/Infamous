package com.packt.infamous.game_objects;


import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;

import static com.packt.infamous.Const.TILED_HEIGHT;
import static com.packt.infamous.Const.TILED_WIDTH;

public class Pole {
    Rectangle start;
    Rectangle end;

    public Pole(float xStart, float yStart, float xEnd, float yEnd){
        start = new Rectangle(xStart, yStart, TILED_WIDTH, TILED_HEIGHT);
        end = new Rectangle(xEnd, yEnd, TILED_WIDTH, TILED_HEIGHT);
    }

    public Rectangle getStartHitBox(){return start;}

    public Rectangle getEndHitBox(){return end;}

    /**
     * Purpose: Draws the circle on the screen using render
     */
    public void drawDebug(ShapeRenderer shapeRenderer) {
        shapeRenderer.rect(start.x, start.y, start.width, start.height);
        shapeRenderer.rect(end.x, end.y, end.width, end.height);
    }
}
