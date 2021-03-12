package com.packt.infamous.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.assets.loaders.BitmapFontLoader;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.packt.infamous.main.Infamous;
import com.packt.infamous.screens.textures.CreditsScreenTextures;
import com.packt.infamous.screens.textures.LoadingScreenTextures;
import com.packt.infamous.tools.DebugRendering;
import com.packt.infamous.tools.TextAlignment;

import static com.packt.infamous.Const.LOADING_HEIGHT;
import static com.packt.infamous.Const.LOADING_OFFSET;
import static com.packt.infamous.Const.LOADING_WIDTH;
import static com.packt.infamous.Const.LOADING_Y;
import static com.packt.infamous.Const.LOGO_HEIGHT;
import static com.packt.infamous.Const.LOGO_WIDTH;
import static com.packt.infamous.Const.TEXT_OFFSET;
import static com.packt.infamous.Const.WORLD_HEIGHT;
import static com.packt.infamous.Const.WORLD_WIDTH;


public class CreditsScreen extends ScreenAdapter{

    //=========================================== Variable =========================================

    //================================== Image processing ===========================================
    private final SpriteBatch batch = new SpriteBatch();
    private Viewport viewport;
    private Camera camera;

    //===================================== Tools ==================================================
    private final Infamous infamous;
    private final TextAlignment textAlignment = new TextAlignment();
    private CreditsScreenTextures creditsScreenTextures;

    //====================================== Fonts =================================================
    private BitmapFont bitmapFont = new BitmapFont();

    private Array<String> credits = new Array<>();
    private float position =  -TEXT_OFFSET;




    /**
     * Purpose: The Constructor used when loading up the game for the first time showing off the logo
     * @param infamous game object with data
     */
    public CreditsScreen(Infamous infamous) {
        this.infamous = infamous;
    }

    /**
     Purpose: Updates the dimensions of the screen
     Input: The width and height of the screen
     */
    @Override
    public void resize(int width, int height) { viewport.update(width, height); }

    //===================================== Show ===================================================

    /**
     * Purpose: Central function for initializing the screen
     */
    @Override
    public void show() {
        //Sets up the camera
        showCamera();           //Sets up camera through which objects are draw through
        showObjects();
        showCredits();           //Loads the stuff into the asset manager
        creditsScreenTextures = new CreditsScreenTextures();
    }

    /**
     * Purpose: Sets up the camera through which all the objects are view through
     */
    private void showCamera(){
        camera = new OrthographicCamera();									//Sets a 2D view
        camera.position.set(WORLD_WIDTH / 2, WORLD_HEIGHT / 2, 0);	//Places the camera in the center of the view port
        camera.update();													//Updates the camera
        viewport = new StretchViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);		//
    }

    /**
     * Purpose: Sets up objects such as debugger, musicControl, fonts and others
     */
    private void showObjects(){
        bitmapFont.setColor(Color.BLACK);
        if(infamous.getAssetManager().isLoaded("Fonts/Font.fnt")){bitmapFont = infamous.getAssetManager().get("Fonts/Font.fnt");}
        bitmapFont.getData().setScale(0.45f);

    }

    private void showCredits(){
        credits.add("inFamous NES Demake");
        credits.add("Programming");
        credits.add("Pr1");
        credits.add("Pr2");
        credits.add("Pr3");
        credits.add(" ");
        credits.add("Art");
        credits.add("Ar1");
        credits.add("Ar2");
        credits.add(" ");
        credits.add("Music");
        credits.add("Ar1");
        credits.add("Ar2");
        credits.add("Level Music");
        credits.add("AKA Dramatic Music");
        credits.add("by PureDesignGirl");
        credits.add(" ");
        credits.add("SFX");
        credits.add("Ar1");
        credits.add("Ar2");
        credits.add("Bolt SFX");
        credits.add("Pure Audio Ninja");
        credits.add(" ");
        credits.add("Punch + Electricty SFX");
        credits.add("ShareSynth & Damnsatinist");
        credits.add(" ");
        credits.add("Death SFX");
        credits.add("tonsil5");
        credits.add(" ");
        credits.add("Water SFX");
        credits.add("InspectorJ");
        credits.add(" ");
        credits.add("Menu Move SFX");
        credits.add("DrMrSir");
        credits.add(" ");
        credits.add("Confirm/Deconfirm SFX");
        credits.add("Raclure");
        credits.add(" ");
        credits.add("Hover SFX");
        credits.add("Jagadamba");
        credits.add(" ");
        credits.add("Collectible SFX");
        credits.add("Free-Rush");
        credits.add(" ");
        credits.add("Checkpoint SFX");
        credits.add("Scrampunk");
        credits.add(" ");
        credits.add("Level Complete SFX");
        credits.add("_MC5_");
        credits.add(" ");
        credits.add("Grip SFX");
        credits.add("Aquafeniz");
        credits.add(" ");
        credits.add("freesound.org");
        credits.add(" ");
        credits.add(" ");
        credits.add(" ");
        credits.add("Thank You");
        credits.add("Game Jam");

    }


    //=================================== Execute Time Methods =====================================

    /**
     Purpose: Central function for the game, everything that updates run through this function
     */
    @Override
    public void render(float delta) {
        update(delta);
        if( Gdx.input.isKeyJustPressed(Input.Keys.E) || Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE) ||
        position - TEXT_OFFSET * credits.size > WORLD_HEIGHT){
            infamous.setScreen(new LoadingScreen(infamous, 0));
        }
        draw();
    }

    //=================================== Updating Methods =========================================

    /**
     * Purpose: Updates the variable of the progress bar, when the whole thing is load it turn on game screen
     * @param delta timing variable
     */
    private void update(float delta) {
        position += 1;
    }



    //========================================== Drawing ===========================================

    /**
     * Purpose: Central drawing Function
     */
    private void draw() {
        //================== Clear Screen =======================
        clearScreen();

        //==================== Set Up Camera =============================
        batch.setProjectionMatrix(camera.projection);
        batch.setTransformMatrix(camera.view);

        batch.begin();
        bitmapFont.getData().setScale(0.5f);
        for(int i = 0; i < credits.size; i++){
            textAlignment.centerText(batch, bitmapFont, credits.get(i), WORLD_WIDTH/2f, position - TEXT_OFFSET * i);
        }
        batch.draw(creditsScreenTextures.eTexture, 220, 7);
        bitmapFont.getData().setScale(0.3f);
        textAlignment.centerText(batch, bitmapFont, "End", 240, 15 );

        batch.end();
    }

    /**
     *  Purpose: Sets screen color
     */
    private void clearScreen() {
        Gdx.gl.glClearColor(Color.BLACK.r, Color.BLACK.g, Color.BLACK.b, Color.BLACK.a);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    }

    /**
     * Purpose: Gets rid of all visuals
     */
    @Override
    public void dispose() {
    }
}