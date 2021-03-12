package com.packt.infamous.game_objects;

import com.badlogic.gdx.graphics.Texture;
import com.packt.infamous.Alignment;

public class CheckpointObject extends GenericObject {
    public CheckpointObject(float x, float y, Alignment align,  Texture texture) {
        super(x, y, align);
        this.texture = texture;
        hitBox.width = 16;
        hitBox.height =  32;
    }
}
