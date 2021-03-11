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

    public MenuScreenTextures(){ showTextures(); }

    /**
     * Purpose: Sets up all of the textures
     */
    private void showTextures(){
        backgroundTexture = new Texture(Gdx.files.internal("UI/MenuBackground.png"));
        controlsTexture = new Texture(Gdx.files.internal("UI/Instructions.png"));
        Texture menuButtonTexturePath = new Texture(Gdx.files.internal("UI/Button.png"));
        buttonSpriteSheet = new TextureRegion(menuButtonTexturePath).split(
                menuButtonTexturePath.getWidth()/2, menuButtonTexturePath.getHeight());
    }

}