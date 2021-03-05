package com.packt.infamous.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.packt.infamous.Alignment;
import com.packt.infamous.game_objects.Cole;
import com.packt.infamous.game_objects.Platforms;
import com.packt.infamous.main.Infamous;
import com.packt.infamous.screens.textures.MainScreenTextures;
import com.packt.infamous.tools.DebugRendering;
import com.packt.infamous.tools.MusicControl;
import com.packt.infamous.tools.TextAlignment;
import com.packt.infamous.tools.TiledSetUp;

import java.util.ArrayList;

import static com.packt.infamous.Const.DEVELOPER_TEXT_X;
import static com.packt.infamous.Const.DEVELOPER_TEXT_Y;
import static com.packt.infamous.Const.INSTRUCTIONS_Y_START;
import static com.packt.infamous.Const.INSTRUCTION_BUTTON_Y;
import static com.packt.infamous.Const.MENU_BUTTON_HEIGHT;
import static com.packt.infamous.Const.MENU_BUTTON_WIDTH;
import static com.packt.infamous.Const.MENU_BUTTON_Y_START;
import static com.packt.infamous.Const.TEXT_OFFSET;
import static com.packt.infamous.Const.WORLD_HEIGHT;
import static com.packt.infamous.Const.WORLD_WIDTH;


//TODO We can only use 8 buttons, directional,  Select/Start and A B

//TODO The NES aspect ratio is 256 horizontal pixels by 240 vertical pixels

//TODO : Since we're following NES no mouse controls for the buttons so we need to rework those
//TODO cont: to use WASD/Arrow Keys

//TODO : Seba will focus on platforming, I want to do climbing, rail riding, hover float, and
//TODO cont : rail climbing and try to make it feel good.

class MainScreen extends ScreenAdapter {

    //=========================================== Variable =========================================

    //================================== Image processing ===========================================
    private Viewport viewport;			                     //The screen where we display things
    private Camera camera;				                     //The camera viewing the viewport
    private final SpriteBatch batch = new SpriteBatch();	 //Batch that holds all of the textures

    //=================================== Buttons ==================================================

    private Stage menuStage;
    private ImageButton[] menuButtons; //Used for the menu selection
    private ImageButton backButton;    //Used to go back from the Help Screen to Menu

    //===================================== Tools ==================================================
    private final Infamous infamous;      //Game object that holds the settings
    private DebugRendering debugRendering;        //Draws debug hit-boxes
    private MusicControl musicControl;            //Plays Music
    private final TextAlignment textAlignment = new TextAlignment();
    private TiledSetUp tiledSetUp;

    //=========================================== Text =============================================
    //Font used for the user interaction
    private BitmapFont bitmapFont = new BitmapFont();             //Font used for the user interaction
    private final BitmapFont bitmapFontDeveloper = new BitmapFont();    //Font for viewing phone stats in developer mode
    private MainScreenTextures mainScreenTextures;

    //============================================= Flags ==========================================
    private boolean developerMode = true;      //Developer mode shows hit boxes and phone data
    private boolean pausedFlag = false;         //Stops the game from updating
    private boolean endFlag = false;            //Tells us game has been lost
    private boolean helpFlag = false;           //Tells us if help flag is on or off

    //=================================== Miscellaneous Vars =======================================
    private final String[] menuButtonText = new String[]{"Restart", "Help", "Sound Off", "Main Menu", "Back", "Sound On"};

    //========================================= Game Objects ========================================
    private Cole cole;
    private final Array<Platforms> platforms = new Array<>();

    //================================ Set Up ======================================================

    /**
     * Purpose: Grabs the info from main screen that holds asset manager
     * Input: Infamous
    */
    MainScreen(Infamous infamous) { this.infamous = infamous;}


    /**
    Purpose: Updates the dimensions of the screen
    Input: The width and height of the screen
    */
    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    //===================================== Show ===================================================

    /**
     * Purpose: Central function for initializing the screen
     */
    @Override
    public void show() {
        showCamera();       //Set up the camera
        showObjects();      //Sets up player and font
        mainScreenTextures = new MainScreenTextures();
        showButtons();      //Sets up the buttons
        musicControl.showMusic(0);
        showTiled();
        if(developerMode){debugRendering.showRender();}    //If in developer mode sets up the redners
    }

    /**
     * Purpose: Sets up the camera through which all the objects are view through
    */
    private void showCamera(){
        camera = new OrthographicCamera();									//Sets a 2D view
        camera.position.set(WORLD_WIDTH / 2, WORLD_HEIGHT / 2, 0);	//Places the camera in the center of the view port
        camera.update();													//Updates the camera
        viewport = new StretchViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);  //Stretches the image to fit the screen
    }

    /**
     * Purpose: Sets up the button
    */
    private void showButtons(){
        //Sets the menu stage to go over what we're looking at
        menuStage = new Stage(new StretchViewport(WORLD_WIDTH, WORLD_HEIGHT));
        Gdx.input.setInputProcessor(menuStage); //Gives control to the stage for clicking on buttons
        setUpMenuButtons();                     //Sets up the button in the menu
        setUpBackButton();
    }

    /**
     * Purpose: Sets up the buttons in the main menu, Restart, Help, Sound Off/On and Main Menu
     */
    private void setUpMenuButtons() {
        //Sets up 5 Buttons
        menuButtons = new ImageButton[menuButtonText.length-1];

        //Sets up the texture
        Texture menuButtonTexturePath = new Texture(Gdx.files.internal("UI/Button.png"));
        TextureRegion[][] buttonSpriteSheet = new TextureRegion(menuButtonTexturePath).split(
                menuButtonTexturePath.getWidth()/2, menuButtonTexturePath.getHeight());


        for (int i = 0; i < menuButtonText.length-1; i++) {
            menuButtons[i] = new ImageButton(new TextureRegionDrawable(buttonSpriteSheet[0][0]), new TextureRegionDrawable(buttonSpriteSheet[0][1]));

            menuButtons[i].setPosition(WORLD_WIDTH / 2f - MENU_BUTTON_WIDTH / 2f, MENU_BUTTON_Y_START - (10 + MENU_BUTTON_HEIGHT) * i);
            menuStage.addActor(menuButtons[i]);
            menuButtons[i].setVisible(false);       //Initially all the buttons are off

            menuButtons[i].setWidth(MENU_BUTTON_WIDTH);
            menuButtons[i].setHeight(MENU_BUTTON_HEIGHT);

            //Sets up each buttons function
            final int finalI = i;
            menuButtons[i].addListener(new ActorGestureListener() {
                @Override
                public void tap(InputEvent event, float x, float y, int count, int button) {
                    super.tap(event, x, y, count, button);
                    musicControl.playSFX(0);
                    //Restarts the game
                    if (finalI == 0) { restart(); }
                    //Turns on the help menu
                    else if (finalI == 1) {
                        helpFlag = true;
                        //Turns off all the buttons
                        for (ImageButton imageButton : menuButtons) { imageButton.setVisible(false); }
                        backButton.setVisible(true);
                    }
                    //Turns sound on and off
                    else if (finalI == 2) { musicControl.soundOnOff(); }
                    //Back to Main Menu Screen
                    else if(finalI == 3){
                        musicControl.stopMusic();
                        infamous.setScreen(new LoadingScreen(infamous, 0));
                    }
                    //Back to the game button
                    else{
                        pausedFlag = !pausedFlag;
                        for (ImageButton imageButton : menuButtons) { imageButton.setVisible(false); }
                    }
                }
            });
        }
    }

    /**
     * Purpose: Sets up the button to leave the help menu
     */
    private void setUpBackButton(){
        //Sets up the texture
        Texture menuButtonTexturePath = new Texture(Gdx.files.internal("UI/Button.png"));
        TextureRegion[][] buttonSpriteSheet = new TextureRegion(menuButtonTexturePath).split(
                menuButtonTexturePath.getWidth()/2, menuButtonTexturePath.getHeight());

        backButton = new ImageButton(new TextureRegionDrawable(buttonSpriteSheet[0][0]), new TextureRegionDrawable(buttonSpriteSheet[0][1]));

        backButton.setPosition(WORLD_WIDTH / 2f - MENU_BUTTON_WIDTH / 2f, INSTRUCTION_BUTTON_Y);
        menuStage.addActor(backButton);
        backButton.setVisible(false);       //Initially all the buttons are off

        backButton.setWidth(MENU_BUTTON_WIDTH);
        backButton.setHeight(MENU_BUTTON_HEIGHT);

        backButton.addListener(new ActorGestureListener() {
            @Override
            public void tap(InputEvent event, float x, float y, int count, int button) {
                super.tap(event, x, y, count, button);
                helpFlag = false;
                //Turns off all the buttons
                for (ImageButton imageButton : menuButtons) { imageButton.setVisible(true); }
                backButton.setVisible(false);
            }
        });
    }

    /**
     * Purpose: Sets up all the objects imported from tiled
     */
    private void showTiled(){
        tiledSetUp = new TiledSetUp(infamous.getAssetManager(), batch, "Tiled/InfamousMapPlaceHolder.tmx");

        Array<Vector2> colePosition = tiledSetUp.getLayerCoordinates("Cole");
        cole = new Cole(colePosition.get(0).x, colePosition.get(0).y, Alignment.PLAYER);


        Array<Vector2> platformsPositions = tiledSetUp.getLayerCoordinates("Platforms");
        Array<Vector2> platformsDimensions = tiledSetUp.getLayerDimensions("Platforms");
        for(int i = 0; i < platformsPositions.size; i++){
            platforms.add(new Platforms(platformsPositions.get(i).x, platformsPositions.get(i).y, Alignment.BACKGROUND));
            platforms.get(i).setWidth(platformsDimensions.get(i).x);
            platforms.get(i).setHeight(platformsDimensions.get(i).y);
        }

    }


    /**
     * Purpose: Sets up objects such as debugger, musicControl, fonts and others
    */
    private void showObjects(){
        debugRendering = new DebugRendering(camera);
        musicControl = new MusicControl(infamous.getAssetManager());

        if(infamous.getAssetManager().isLoaded("Fonts/Font.fnt")){bitmapFont = infamous.getAssetManager().get("Fonts/Font.fnt");}
        bitmapFont.getData().setScale(1f);
    }

    //=================================== Execute Time Methods =====================================

    /**
    Purpose: Central function for the game, everything that updates run through this function
    */
    @Override
    public void render(float delta) {
        if(!pausedFlag) { update(delta); }      //If the game is not paused update the variables
        draw();                                 //Draws everything
        if (developerMode) { debugRender(); }   //If developer mode is on draws hit-boxes
    }

    //===================================== Debug ==================================================

    /**
     Purpose: Draws hit-boxes for all the objects
     */
    private void debugRender(){
        debugRendering.startEnemyRender();
        //TODO set up enemies to render
        debugRendering.endEnemyRender();

        debugRendering.startUserRender();
        cole.drawDebug(debugRendering.getShapeRendererUser());
        debugRendering.endUserRender();

        debugRendering.startBackgroundRender();
        for(Platforms platform : platforms){
            platform.drawDebug(debugRendering.getShapeRendererBackground());
        }
        debugRendering.endBackgroundRender();

        debugRendering.startCollectibleRender();
        //TODO set up collectibles to render
        debugRendering.endCollectibleRender();
    }

    /**
     * Purpose: Draws the info for dev to test the game
    */
    private void debugInfo(){
        //Batch setting up texture
        textAlignment.centerText(batch, bitmapFontDeveloper, "Hello Dev", DEVELOPER_TEXT_X, DEVELOPER_TEXT_Y);
        textAlignment.centerText(batch, bitmapFontDeveloper, "This is Dev Info ", DEVELOPER_TEXT_X, DEVELOPER_TEXT_Y - TEXT_OFFSET);
        textAlignment.centerText(batch, bitmapFontDeveloper, "Bye", DEVELOPER_TEXT_X, DEVELOPER_TEXT_Y - 2 * TEXT_OFFSET);
    }

    //=================================== Updating Methods =========================================

    /**
    Purpose: Updates all the moving components and game variables
    Input: @delta - timing variable
    */
    private void update(float delta){
        cole.update();
        handleInput();
        updateCamera();
    }


    /**
     * Purpose: Central Input Handling function
     */
    private void handleInput() {
        //TODO add user inputs
        //Pause and un-pause the game with ESC
        handlePause();
        //Allows user to turn on dev mode
        if (Gdx.input.isKeyJustPressed(Input.Keys.TAB)) { developerMode = !developerMode; }
        if (developerMode) { handleDevInputs(); }
    }

    /**
     * Pauses and un-pauses the game with Esc key
     */
    private void handlePause(){
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            pausedFlag = !pausedFlag;
            for (ImageButton imageButton : menuButtons) { imageButton.setVisible(true); }
        }
    }

    /**
     * Purpose: Actions that can only be done in developer mode, used for testing
     */
    private void handleDevInputs(){
        //Movement
        if (Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            cole.moveHorizontally(-1);
        }
        else{ cole.moveHorizontally(0); }

        if(Gdx.input.isKeyPressed(Input.Keys.W) || Gdx.input.isKeyPressed(Input.Keys.UP)){

        }

        if(Gdx.input.isKeyPressed(Input.Keys.D) || Gdx.input.isKeyPressed(Input.Keys.RIGHT)){
            cole.moveHorizontally(1);
        }
        else{ cole.moveHorizontally(0); }

        if(Gdx.input.isKeyPressed(Input.Keys.S) || Gdx.input.isKeyPressed(Input.Keys.DOWN)){

        }
        //Jumping
        else if(Gdx.input.isKeyPressed(Input.Keys.SPACE)){
            cole.jump();
        }
        //Interact
        else if(Gdx.input.isKeyJustPressed(Input.Keys.E)){

        }


    }

    /**
     * Purpose: Resize the menuStage viewport in case the screen gets resized (Desktop)
     *          Moving the camera if that's part of the game
     */
    public void updateCamera() {
        //Resize the menu Stage if the screen changes size
        menuStage.getViewport().update(viewport.getScreenWidth(), viewport.getScreenHeight(), true);
    }

    /**
    Purpose: Puts the game in end game state
    */
    private void endGame(){ endFlag = true; }

    /**
     Purpose: Restarts the game to it's basic settings
     */
    private void restart(){
    }

    //========================================== Drawing ===========================================

    /**
     * Purpose: Central drawing function, from here everything gets drawn
    */
    private void draw(){
        //================== Clear Screen =======================
        clearScreen();

        //==================== Set Up Camera =============================
        batch.setProjectionMatrix(camera.projection);
        batch.setTransformMatrix(camera.view);

        //======================== Draws ==============================
        batch.begin();
        batch.draw(mainScreenTextures.backgroundTexture, 0, 0, WORLD_WIDTH, WORLD_HEIGHT);
        if(developerMode){debugInfo();}        //If dev mode is on draw hit boxes and phone stats
        batch.end();


        //======================== Draws Titled =============================
        tiledSetUp.drawTiledMap();

        //=================== Draws the Menu Background =====================
        batch.begin();
        drawPopUpMenu();
        batch.end();

        //=================== Draws the Menu Buttons =========================
        if(pausedFlag || endFlag || helpFlag){menuStage.draw();}

        //================= Draw Menu Button Text =============================
        batch.begin();
        if(pausedFlag && !helpFlag){drawButtonText();}
        else if(pausedFlag){drawBackButtonText();}
        batch.end();
    }

    /**
     * Input: Void
     * Output: Void
     * Purpose: Draws the menu background and instructions
     */
    private void drawPopUpMenu() {
        bitmapFont.getData().setScale(0.3f);
        if (pausedFlag || endFlag || helpFlag) {
            batch.draw(mainScreenTextures.menuBackgroundTexture, WORLD_WIDTH / 2f - 200 / 2f, WORLD_HEIGHT / 2 - 300 / 2f, 200, 300);
            if (helpFlag) { drawInstructions();}
        }
    }

    /**
     * Input: Void
     * Output: Void
     * Purpose: Draws the text for instructions
     */
    private void drawInstructions() {
        bitmapFont.getData().setScale(.5f);
        textAlignment.centerText(batch, bitmapFont, "Instruction", WORLD_WIDTH / 2f, INSTRUCTIONS_Y_START);
        bitmapFont.getData().setScale(.35f);

        textAlignment.centerText(batch, bitmapFont, "Move - WASD", WORLD_WIDTH / 2f, INSTRUCTIONS_Y_START - TEXT_OFFSET);
        textAlignment.centerText(batch, bitmapFont, "Action #2", WORLD_WIDTH / 2f, INSTRUCTIONS_Y_START - 2 * TEXT_OFFSET);
        textAlignment.centerText(batch, bitmapFont, "Action #3", WORLD_WIDTH / 2f, INSTRUCTIONS_Y_START  - 3 * TEXT_OFFSET);
        textAlignment.centerText(batch, bitmapFont, "Actions #4", WORLD_WIDTH / 2f, INSTRUCTIONS_Y_START - 4 * TEXT_OFFSET);
    }

    /**
        Purpose: Draws text over the menu buttons, Restart, Help, Sound Off/On and Main Menu
     */
    private void drawButtonText() {
        bitmapFont.setColor(Color.BLACK);
        String string;
        for (int i = 0; i < menuButtonText.length - 1; i++) {
            string = menuButtonText[i];
            //If the volume is off draw Sound On else Sound off
            if (i == 2 && musicControl.getSFXVolume() == 0) { string = menuButtonText[menuButtonText.length - 1]; }
            textAlignment.centerText(batch, bitmapFont, string, WORLD_WIDTH / 2f,  MENU_BUTTON_Y_START + MENU_BUTTON_HEIGHT/2f - (10 + MENU_BUTTON_HEIGHT) * i);
        }
    }

    /**
     Purpose: Draws text over the back button in help menu
     */
    private void drawBackButtonText(){
        textAlignment.centerText(batch, bitmapFont, menuButtonText[4], WORLD_WIDTH / 2f,  INSTRUCTION_BUTTON_Y + MENU_BUTTON_HEIGHT/2f);
    }

    /**
     * Purpose: Set the screen to black so we can draw on top of it again
    */
    private void clearScreen() {
        Gdx.gl.glClearColor(Color.BLACK.r, Color.BLACK.g, Color.BLACK.b, Color.BLACK.a); //Sets color to black
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);										 //Sends it to the buffer
    }

    /**
     * Purpose: Destroys everything once we move onto the new screen
    */
    @Override
    public void dispose() {
        menuStage.dispose();
    }
}
