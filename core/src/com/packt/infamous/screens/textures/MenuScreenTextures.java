package com.packt.infamous.screens.textures;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class MenuScreenTextures {

    //============================================= Textures =======================================
    public Texture backgroundTexture;
    public Texture menuBackgroundTexture;   //Pop up menu to show menu buttons and Help screen
    public TextureRegion[][] buttonSpriteSheet;
    public Texture controlsTexture;

    public TextureRegion[][] collectibleSpriteSheet;
    public TextureRegion[][] peopleUpSpriteSheet;

    public MenuScreenTextures(){ showTextures(); }

    /**
     * Purpose: Sets up all of the textures
     */
    private void showTextures(){
        backgroundTexture = new Texture(Gdx.files.internal("UI/MenuBackground.png"));
        menuBackgroundTexture = new Texture(Gdx.files.internal("UI/BoarderBox.png"));
        controlsTexture = new Texture(Gdx.files.internal("UI/Instructions.png"));
        Texture menuButtonTexturePath = new Texture(Gdx.files.internal("UI/Button.png"));
        buttonSpriteSheet = new TextureRegion(menuButtonTexturePath).split(
                menuButtonTexturePath.getWidth()/2, menuButtonTexturePath.getHeight());


        Texture peopleUpTexturePath = new Texture(Gdx.files.internal("Sprites/PeopleHealed.png"));
        peopleUpSpriteSheet = new TextureRegion(peopleUpTexturePath).split(
                peopleUpTexturePath.getWidth()/5, peopleUpTexturePath.getHeight());

        Texture collectibleTexturePath = new Texture(Gdx.files.internal("Sprites/BlastShard.png"));
        collectibleSpriteSheet = new TextureRegion(collectibleTexturePath).split(
                collectibleTexturePath.getWidth()/3, collectibleTexturePath.getHeight());
    }

}