package com.packt.infamous.game_objects.Projectiles;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.packt.infamous.Alignment;
import com.packt.infamous.Enum;

public class Bomb extends Projectile{
    public Bomb(float x, float y, Alignment align, int width, int height, int direction, float startVelocity, Enum type, TextureRegion[][] bulletSpriteSheet) {
        super(x, y, align, width, height, direction, startVelocity, Enum.BOMB, bulletSpriteSheet);
        isExplosive = true;
    }

    @Override
    public void update(float levelWidth, float levelHeight, float delta){
        checkIfWorldBound(levelWidth, levelHeight);
        derezTimer(delta);
        arcDown(delta);
        if(!isAttached){
            hitBox.y += velocity.y;
            hitBox.x += velocity.x;
        }
        else {
            hitBox.y = followObject.getY()+disjoint.x;
            hitBox.x = followObject.getX()+disjoint.y;
        }
    }

    /**
     Input: float delta
     Output: void
     Purpose: Arcs down bomb slowly
     */
    private void arcDown(float delta){
        velocity.y -= 0.1f;
    }

}
