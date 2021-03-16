package com.packt.infamous.screens.textures;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;

public class LoadingScreenTextures {
    //============================================= Textures =======================================
    public Texture backgroundTexture;
    public Texture loadingBarTexture;
    public Texture loadingProgressTexture;
    public Texture logoTexture;   //Pop up menu to show menu buttons and Help screen


    public LoadingScreenTextures(){ showTextures(); }

    /**
     * Purpose: Sets up all of the textures
     */
    private void showTextures(){
        backgroundTexture = new Texture(Gdx.files.internal("Sprites/Bricks.png"));
        loadingBarTexture = new Texture(Gdx.files.internal("UI/LoadingBar.png"));
        loadingProgressTexture = new Texture(Gdx.files.internal("UI/LoadingBackground.png"));
        logoTexture = new Texture(Gdx.files.internal("Sprites/Logo.png"));
    }

}
