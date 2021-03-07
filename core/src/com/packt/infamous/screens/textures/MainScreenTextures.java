package com.packt.infamous.screens.textures;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;

public class MainScreenTextures {

    //============================================= Textures =======================================
    public Texture backgroundTexture;
    public Texture menuBackgroundTexture;   //Pop up menu to show menu buttons and Help screen
    public Texture junctionBoxTexture;
    public Texture telephoneBoxTexture;

    public MainScreenTextures(){ showTextures(); }

    /**
     * Purpose: Sets up all of the textures
     */
    private void showTextures(){
        backgroundTexture = new Texture(Gdx.files.internal("UI/TestBackground.png"));
        menuBackgroundTexture = new Texture(Gdx.files.internal("UI/TestPopUp.png"));
        junctionBoxTexture = new Texture(Gdx.files.internal("Sprites/junc_box.png"));
        telephoneBoxTexture = new Texture(Gdx.files.internal("Sprites/phone_booth.png"));
    }

}
