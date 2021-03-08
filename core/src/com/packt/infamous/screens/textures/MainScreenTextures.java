package com.packt.infamous.screens.textures;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class MainScreenTextures {

    //============================================= Textures =======================================
    public Texture backgroundTexture;
    public Texture menuBackgroundTexture;   //Pop up menu to show menu buttons and Help screen
    public Texture junctionBoxTexture;
    public Texture telephoneBoxTexture;
    public TextureRegion[][] buttonSpriteSheet;
    public TextureRegion[][] coleSpriteSheet;
    public TextureRegion[][] drainSpriteSheet;

    public MainScreenTextures(){ showTextures(); }

    /**
     * Purpose: Sets up all of the textures
     */
    private void showTextures(){
        backgroundTexture = new Texture(Gdx.files.internal("UI/TestBackground.png"));
        menuBackgroundTexture = new Texture(Gdx.files.internal("UI/TestPopUp.png"));
        junctionBoxTexture = new Texture(Gdx.files.internal("Sprites/junc_box.png"));
        telephoneBoxTexture = new Texture(Gdx.files.internal("Sprites/phone_booth.png"));

        Texture menuButtonTexturePath = new Texture(Gdx.files.internal("UI/Button.png"));
        buttonSpriteSheet = new TextureRegion(menuButtonTexturePath).split(
                menuButtonTexturePath.getWidth()/2, menuButtonTexturePath.getHeight());

        Texture coleTexturePath = new Texture(Gdx.files.internal("Sprites/ColeSpriteSheet.png"));
        coleSpriteSheet = new TextureRegion(coleTexturePath).split(
                coleTexturePath.getWidth()/4, coleTexturePath.getHeight()/2);

        Texture drainTexturePath = new Texture(Gdx.files.internal("Sprites/Charging.png"));
        drainSpriteSheet = new TextureRegion(drainTexturePath).split(
                drainTexturePath.getWidth()/2, drainTexturePath.getHeight());

    }

}
