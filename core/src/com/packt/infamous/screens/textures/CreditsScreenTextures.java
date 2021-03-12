package com.packt.infamous.screens.textures;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;

public class CreditsScreenTextures {
    //============================================= Textures =======================================
    public Texture eTexture;

    public CreditsScreenTextures(){ showTextures(); }

    /**
     * Purpose: Sets up all of the textures
     */
    private void showTextures(){
        eTexture = new Texture(Gdx.files.internal("Sprites/E.png"));

    }
}
