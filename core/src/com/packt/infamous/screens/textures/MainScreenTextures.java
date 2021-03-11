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
    public Texture waterTexture;
    public Texture eTexture;
    public Texture qTexture;

    public TextureRegion[][] buttonSpriteSheet;

    //================================= Cole ===========================
    public TextureRegion[][] coleSpriteSheet;
    public TextureRegion[][] drainSpriteSheet;
    public TextureRegion[][] railSparkSpriteSheet;
    public TextureRegion[][] hoverSpriteSheet;
    public TextureRegion[][] meleeSpriteSheet;

    public TextureRegion[][] bulletSpriteSheet;
    public TextureRegion[][] collectibleSpriteSheet;

    public TextureRegion[][] peopleDownSpriteSheet;
    public TextureRegion[][] peopleUpSpriteSheet;

    public TextureRegion[][] enemySpriteSheet;
    public TextureRegion[][] enemyDeathSpriteSheet;

    public MainScreenTextures(){ showTextures(); }

    /**
     * Purpose: Sets up all of the textures
     */
    private void showTextures(){
        backgroundTexture = new Texture(Gdx.files.internal("UI/TestBackground.png"));
        menuBackgroundTexture = new Texture(Gdx.files.internal("UI/TestPopUp.png"));
        junctionBoxTexture = new Texture(Gdx.files.internal("Sprites/junc_box.png"));
        telephoneBoxTexture = new Texture(Gdx.files.internal("Sprites/phone_booth.png"));
        waterTexture = new Texture(Gdx.files.internal("Sprites/Water.png"));
        eTexture = new Texture(Gdx.files.internal("Sprites/E.png"));
        qTexture = new Texture(Gdx.files.internal("Sprites/Q.png"));

        Texture menuButtonTexturePath = new Texture(Gdx.files.internal("UI/Button.png"));
        buttonSpriteSheet = new TextureRegion(menuButtonTexturePath).split(
                menuButtonTexturePath.getWidth()/2, menuButtonTexturePath.getHeight());

        Texture coleTexturePath = new Texture(Gdx.files.internal("Sprites/ColeSpriteSheet.png"));
        coleSpriteSheet = new TextureRegion(coleTexturePath).split(
                coleTexturePath.getWidth()/4, coleTexturePath.getHeight()/5);

        Texture drainTexturePath = new Texture(Gdx.files.internal("Sprites/Charging.png"));
        drainSpriteSheet = new TextureRegion(drainTexturePath).split(
                drainTexturePath.getWidth()/2, drainTexturePath.getHeight());

        Texture hoverTexturePath = new Texture(Gdx.files.internal("Sprites/FloatAction.png"));
        hoverSpriteSheet = new TextureRegion(hoverTexturePath).split(
                hoverTexturePath.getWidth()/3, hoverTexturePath.getHeight());


        Texture railSparkTexturePath = new Texture(Gdx.files.internal("Sprites/RailSparks.png"));
        railSparkSpriteSheet = new TextureRegion(railSparkTexturePath).split(
                railSparkTexturePath.getWidth()/3, railSparkTexturePath.getHeight());

        Texture meleeTexturePath = new Texture(Gdx.files.internal("Sprites/MeleeEffect.png"));
        meleeSpriteSheet = new TextureRegion(meleeTexturePath).split(
                meleeTexturePath.getWidth()/5, meleeTexturePath.getHeight());

        Texture bulletTexturePath = new Texture(Gdx.files.internal("Sprites/Bullet.png"));
        bulletSpriteSheet = new TextureRegion(bulletTexturePath).split(
                bulletTexturePath.getWidth()/3, bulletTexturePath.getHeight());

        Texture collectibleTexturePath = new Texture(Gdx.files.internal("Sprites/BlastShard.png"));
        collectibleSpriteSheet = new TextureRegion(collectibleTexturePath).split(
                collectibleTexturePath.getWidth()/3, collectibleTexturePath.getHeight());

        Texture peopleDownTexturePath = new Texture(Gdx.files.internal("Sprites/InjuredPeople.png"));
        peopleDownSpriteSheet = new TextureRegion(peopleDownTexturePath).split(
                peopleDownTexturePath.getWidth(), peopleDownTexturePath.getHeight()/4);

        Texture peopleUpTexturePath = new Texture(Gdx.files.internal("Sprites/PeopleHealed.png"));
        peopleUpSpriteSheet = new TextureRegion(peopleUpTexturePath).split(
                peopleUpTexturePath.getWidth()/5, peopleUpTexturePath.getHeight());

        Texture enemyTexturePath = new Texture(Gdx.files.internal("Sprites/Enemy.png"));
        enemySpriteSheet = new TextureRegion(enemyTexturePath).split(
                enemyTexturePath.getWidth()/4, enemyTexturePath.getHeight()/2);

        Texture enemyDeathTexturePath = new Texture(Gdx.files.internal("Sprites/EnemyDeath.png"));
        enemyDeathSpriteSheet = new TextureRegion(enemyDeathTexturePath).split(
                enemyDeathTexturePath.getWidth()/5, enemyDeathTexturePath.getHeight());


    }

}
