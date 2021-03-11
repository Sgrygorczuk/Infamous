package com.packt.infamous.game_objects;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.packt.infamous.Alignment;

public class EndShard extends GenericObject {

    protected Animation<TextureRegion> shardAnimation;
    protected float shardTime = 0;

    public EndShard(float x, float y, TextureRegion[][] texture){
        super(x, y, Alignment.BACKGROUND);

        hitBox.width = 32;
        hitBox.height = 32;

        shardAnimation = setUpAnimation(texture, 1/3f, 0,Animation.PlayMode.LOOP_PINGPONG);

    }

    public void update(float delta){
        shardTime += delta;
    }

    @Override
    public void draw(SpriteBatch batch) {
        TextureRegion currentFrame = shardAnimation.getKeyFrame(shardTime);

        batch.draw(currentFrame, hitBox.x, hitBox.y, hitBox.width, hitBox.height);
    }
}
