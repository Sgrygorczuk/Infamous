package com.packt.infamous.game_objects;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.packt.infamous.Alignment;

public class Collectible extends GenericObject{

    protected TextureRegion[][] collectibleSpriteSheet;
    protected Animation<TextureRegion> collectibleAnimation;
    protected float collectibleTime = 0;

    public Collectible(float x, float y, TextureRegion[][] textureRegions){
        super(x, y, Alignment.BACKGROUND);

        hitBox.width = 16;
        hitBox.height = 16;

        collectibleSpriteSheet = textureRegions;
        collectibleAnimation = setUpAnimation(collectibleSpriteSheet, 1/3f, 0, Animation.PlayMode.LOOP_PINGPONG);

    }

    public void update(float delta){
        collectibleTime += delta;
    }

    @Override
    public void draw(SpriteBatch batch) {
        TextureRegion currentFrame = collectibleAnimation.getKeyFrame(collectibleTime);

        batch.draw(currentFrame, hitBox.x, hitBox.y, hitBox.width, hitBox.height);
    }
}
