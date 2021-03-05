package com.packt.infamous.screens;

import com.badlogic.gdx.Gdx;
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
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.packt.infamous.main.Infamous;
import com.packt.infamous.screens.textures.LoadingScreenTextures;
import com.packt.infamous.tools.DebugRendering;
import com.packt.infamous.tools.TextAlignment;

import static com.packt.infamous.Const.LOADING_HEIGHT;
import static com.packt.infamous.Const.LOADING_OFFSET;
import static com.packt.infamous.Const.LOADING_WIDTH;
import static com.packt.infamous.Const.LOADING_Y;
import static com.packt.infamous.Const.LOGO_HEIGHT;
import static com.packt.infamous.Const.LOGO_WIDTH;
import static com.packt.infamous.Const.WORLD_HEIGHT;
import static com.packt.infamous.Const.WORLD_WIDTH;


public class LoadingScreen extends ScreenAdapter{

    //=========================================== Variable =========================================

    //================================== Image processing ===========================================
    private final SpriteBatch batch = new SpriteBatch();
    private Viewport viewport;
    private Camera camera;

    //===================================== Tools ==================================================
    private final Infamous infamous;
    private LoadingScreenTextures loadingScreenTextures;
    private final TextAlignment textAlignment = new TextAlignment();
    private DebugRendering debugRendering;

    //====================================== Fonts =================================================
    private BitmapFont bitmapFont = new BitmapFont();

    //=================================== Miscellaneous Vars =======================================
    private final int screenPath; //Tells us which screen to go to from here

    //Timing variables, keeps the logo on for at least 2 second
    private boolean loadingFirstTime = false;
    private boolean logoDoneFlag = false;
    private static final float LOGO_TIME = 2F;
    private float logoTimer = LOGO_TIME;

    //State of the progress bar
    private float progress = 0;

    private static final float LOADING_TIME = .2F;
    private float loadTimer = LOADING_TIME;
    private String loadingString = "Loading";


    /**
     * Purpose: The Constructor used when loading up the game for the first time showing off the logo
     * @param infamous game object with data
     */
    public LoadingScreen(Infamous infamous) {
        this.infamous = infamous;
        this.screenPath = 0;
        this.loadingFirstTime = true;
    }

    /**
     * Purpose: General Constructor for moving between screens
     * @param infamous game object with data
     * @param screenPath tells us which screen to go to from here
     */
    public LoadingScreen(Infamous infamous, int screenPath) {
        this.infamous = infamous;
        this.screenPath = screenPath;
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
        loadingScreenTextures = new LoadingScreenTextures();
        debugRendering = new DebugRendering(camera);
        debugRendering.setShapeRendererBackgroundShapeType(ShapeRenderer.ShapeType.Filled);
        showObjects();
        loadAssets();           //Loads the stuff into the asset manager
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

    /**
     * Purpose: Loads all the data needed for the asset manager, and set up logo to be displayed
    */
    private void loadAssets(){
        //===================== Load Fonts to Asset Manager ========================================
        BitmapFontLoader.BitmapFontParameter bitmapFontParameter = new BitmapFontLoader.BitmapFontParameter();
        bitmapFontParameter.atlasName = "font_assets.atlas";
        infamous.getAssetManager().load("Fonts/Font.fnt", BitmapFont.class, bitmapFontParameter);

        //=================== Load Music to Asset Manager ==========================================
        infamous.getAssetManager().load("Music/TestMusic.wav", Music.class);

        //========================== Load SFX to Asset Manager =====================================
        infamous.getAssetManager().load("SFX/TestButton.wav", Sound.class);

        //========================= Load Tiled Maps ================================================
        infamous.getAssetManager().load("Tiled/InfamousMapPlaceHolder.tmx", TiledMap.class);
    }

    //=================================== Execute Time Methods =====================================

    /**
     Purpose: Central function for the game, everything that updates run through this function
     */
    @Override
    public void render(float delta) {
        update(delta);
        draw();
    }

    //=================================== Updating Methods =========================================

    /**
     * Purpose: Updates the variable of the progress bar, when the whole thing is load it turn on game screen
     * @param delta timing variable
     */
    private void update(float delta) {
        //If everything is loaded go to the new screen
        if (infamous.getAssetManager().update() && logoDoneFlag) { goToNewScreen();}
        //Else keep loading
        else { progress = infamous.getAssetManager().getProgress();}

        updateTimer(delta);
        updateLoadingString(delta);
    }

    /**
     * Purpose: Counts down until the logo has been on screen long enough
     * @param delta timer to count down
     */
    private void updateTimer(float delta) {
        logoTimer -= delta;
        if (logoTimer <= 0) {
            logoTimer = LOGO_TIME;
            logoDoneFlag = true;
        }
    }

    /**
     * Purpose: Makes dot appear while loading so it looks like something is happening
     * @param delta timer to count down
     */
    private void updateLoadingString(float delta){
        loadTimer -= delta;
        //Add a "." if the length is short enough
        if(loadingString.length() < 10 && loadTimer <= 0){
            loadingString += ".";
            loadTimer = LOADING_TIME;
        }
        //Reset to default
        else if(loadTimer <= 0){
            loadingString = "Loading";
            loadTimer = LOADING_TIME;
        }
    }

    /**
     * Purpose: Allows us to go to a different screen each time we enter the LoadingScreen
     */
    private void goToNewScreen(){
        switch (screenPath){
            case 0:{
                infamous.setScreen(new MenuScreen(infamous));
                break;
            }
            case 1:{
                infamous.setScreen(new MainScreen(infamous));
                break;
            }
            default:{
                infamous.setScreen(new MenuScreen(infamous));
            }
        }
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

        //======================== Draws ==============================
        //Logo First Time We boot up
        if(loadingFirstTime){
            batch.begin();
            batch.draw(loadingScreenTextures.logoTexture, WORLD_WIDTH/2f - LOGO_WIDTH/2f, WORLD_HEIGHT/2f - LOGO_HEIGHT/2f,   LOGO_WIDTH, LOGO_HEIGHT);
            batch.end();
        }
        //Loading Screen with Progress bar
        else{
            batch.begin();
            batch.draw(loadingScreenTextures.backgroundTexture, 0, 0, WORLD_WIDTH, WORLD_HEIGHT);
            batch.draw(loadingScreenTextures.loadingBarTexture, WORLD_WIDTH/2 - LOADING_WIDTH/2f, LOADING_Y + LOADING_HEIGHT/2f, LOADING_WIDTH, LOADING_HEIGHT);
            batch.end();

            debugRendering.startBackgroundRender();
            debugRendering.getShapeRendererBackground().rect(WORLD_WIDTH/2 - LOADING_WIDTH/2f + LOADING_OFFSET, LOADING_Y + LOADING_HEIGHT/2f + LOADING_OFFSET,
                    progress * (LOADING_WIDTH - 2 * LOADING_OFFSET), LOADING_HEIGHT -  2 * LOADING_OFFSET);
            debugRendering.endBackgroundRender();

            batch.begin();
            textAlignment.centerText(batch, bitmapFont, loadingString, WORLD_WIDTH/2f,  LOADING_Y + 1.1f * LOADING_HEIGHT);
            batch.end();}

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