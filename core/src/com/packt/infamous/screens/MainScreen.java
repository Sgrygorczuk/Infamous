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
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
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
import com.packt.infamous.game_objects.DrainableObject;
import com.packt.infamous.game_objects.GenericObject;
import com.packt.infamous.game_objects.Platforms;
import com.packt.infamous.game_objects.Pole;
import com.packt.infamous.game_objects.Rail;
import com.packt.infamous.game_objects.Water;
import com.packt.infamous.main.Infamous;
import com.packt.infamous.screens.textures.MainScreenTextures;
import com.packt.infamous.tools.DebugRendering;
import com.packt.infamous.tools.MusicControl;
import com.packt.infamous.tools.TextAlignment;
import com.packt.infamous.tools.TiledSetUp;

import static com.packt.infamous.Const.COLE_HEIGHT;
import static com.packt.infamous.Const.COLE_WIDTH;
import static com.packt.infamous.Const.DEVELOPER_TEXT_X;
import static com.packt.infamous.Const.DEVELOPER_TEXT_Y;
import static com.packt.infamous.Const.INSTRUCTIONS_Y_START;
import static com.packt.infamous.Const.INSTRUCTION_BUTTON_Y;
import static com.packt.infamous.Const.MENU_BUTTON_HEIGHT;
import static com.packt.infamous.Const.MENU_BUTTON_WIDTH;
import static com.packt.infamous.Const.MENU_BUTTON_Y_START;
import static com.packt.infamous.Const.TEXT_OFFSET;
import static com.packt.infamous.Const.UI_HEIGHT;
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
    private float xCameraDelta = 0;
    private float yCameraDelta = 0;

    //=================================== Miscellaneous Vars =======================================
    private final String[] menuButtonText = new String[]{"Restart", "Help", "Sound Off", "Main Menu", "Back", "Sound On"};

    //========================================= Game Objects ========================================
    private Cole cole;
    private final Array<Platforms> platforms = new Array<>();
    private final Array<Water> waters = new Array<>();
    private final Array<Pole> poles = new Array<>();
    private final Array<DrainableObject> drainables = new Array<>();
    private final Array<Rail> rails = new Array<>();

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
        cole.setWidth(COLE_WIDTH);
        cole.setHeight(COLE_HEIGHT);

        Array<Vector2> poleStartPositions = tiledSetUp.getLayerCoordinates("PoleStart");
        Array<Vector2> poleEndPositions = tiledSetUp.getLayerCoordinates("PoleEnd");
        for(int i = 0; i < poleStartPositions.size; i++){
            poles.add(new Pole(poleStartPositions.get(i).x, poleStartPositions.get(i).y, poleEndPositions.get(i).x, poleEndPositions.get(i).y));
        }

        Array<Vector2> platformsPositions = tiledSetUp.getLayerCoordinates("Platforms");
        Array<Vector2> platformsDimensions = tiledSetUp.getLayerDimensions("Platforms");
        for(int i = 0; i < platformsPositions.size; i++){
            platforms.add(new Platforms(platformsPositions.get(i).x, platformsPositions.get(i).y, Alignment.BACKGROUND));
            platforms.get(i).setWidth(platformsDimensions.get(i).x);
            platforms.get(i).setHeight(platformsDimensions.get(i).y);
        }

        Array<Vector2> waterPositions = tiledSetUp.getLayerCoordinates("Water");
        Array<Vector2> waterDimensions = tiledSetUp.getLayerDimensions("Water");
        for(int i = 0; i < waterPositions.size; i++){
            waters.add(new Water(waterPositions.get(i).x, waterPositions.get(i).y, Alignment.ENEMY));
            waters.get(i).setWidth(waterDimensions.get(i).x);
            waters.get(i).setHeight(waterDimensions.get(i).y);
        }

        Array<Vector2> railPositions = tiledSetUp.getLayerCoordinates("Rail");
        Array<Vector2> railDimensions = tiledSetUp.getLayerCoordinates("Rail");
        for(int i = 0; i < railPositions.size; i++){
            float x = railPositions.get(i).x;
            float y = railPositions.get(i).y;
            float width = railDimensions.get(i).x;
            float height = railDimensions.get(i).y;
            rails.add(new Rail(x, y, width, height,Alignment.BACKGROUND));
        }

        Array<Vector2> drainablePositions = tiledSetUp.getLayerCoordinates("ElePhone");
        for(int i = 0; i < drainablePositions.size; i++){
            drainables.add(new DrainableObject(drainablePositions.get(i).x,
                    drainablePositions.get(i).y, Alignment.BACKGROUND));

            drainables.get(i).setTexture(mainScreenTextures.telephoneBoxTexture);
        }

        int currDrainableNum = drainables.size;
        drainablePositions = tiledSetUp.getLayerCoordinates("EleJunc");
        for(int i = 0; i < drainablePositions.size; i++){
            drainables.add(new DrainableObject(drainablePositions.get(i).x,
                    drainablePositions.get(i).y, Alignment.BACKGROUND));

            drainables.get(i+currDrainableNum).setTexture(mainScreenTextures.junctionBoxTexture);
            }
        }


    /**
     * Purpose: Sets up objects such as debugger, musicControl, fonts and others
    */
    private void showObjects(){
        debugRendering = new DebugRendering(camera);
        debugRendering.setShapeRendererUserShapeType(ShapeRenderer.ShapeType.Filled);
        musicControl = new MusicControl(infamous.getAssetManager());

        if(infamous.getAssetManager().isLoaded("Fonts/Font.fnt")){bitmapFont = infamous.getAssetManager().get("Fonts/Font.fnt");}
        bitmapFont.getData().setScale(1f);
        bitmapFont.setColor(Color.WHITE);
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
        for(Water water : waters){ water.drawDebug(debugRendering.getShapeRenderEnemy()); }
        debugRendering.endEnemyRender();

        debugRendering.startUserRender();
        //Draws Melee Range
        debugRendering.setShapeRendererUserColor(Color.GOLD);
        cole.drawMeleeDebug(debugRendering.getShapeRendererUser());
        //Draws Cole
        debugRendering.setShapeRendererUserColor(Color.GREEN);
        cole.drawDebug(debugRendering.getShapeRendererUser());
        debugRendering.endUserRender();

        debugRendering.startBackgroundRender();
        for(Platforms platform : platforms){
            platform.drawDebug(debugRendering.getShapeRendererBackground());
        }
        for(Pole pole : poles){
            pole.drawDebug(debugRendering.getShapeRendererBackground());
        }
        for(DrainableObject drainable :  drainables){
            drainable.drawDebug(debugRendering.getShapeRendererBackground());
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
        updateCamera();
        isCollidingPlatform();
        isCollidingPoleStart();
        isCollidingPoleEnd();
        isCollidingDrainable();
        isCollidingWater();
        isCollidingRails();
        handleInput();
        cole.update(tiledSetUp.getLevelWidth());
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
     * Purpose: Check if it's touching any platforms
     */
    private void isCollidingPlatform() {
        //Checks if there is ground below him
        boolean hasGround = false;
        for (int i = 0; i < platforms.size; i++) {
           if(cole.updateCollision(platforms.get(i).getHitBox())){
               hasGround = true;                //Tells us that he's standing
               cole.setLastTouchedGround();     //Saves that position for respawn
           }
        }
        //If there is no ground below Cole he should fall
        if(!hasGround){cole.setFalling();}
    }

    private void isCollidingPoleStart(){
        for (Pole pole : poles) {
            if (cole.isColliding(pole.getStartHitBox())) {
                System.out.println("Pole Colliding");
                cole.setTouchPole(true);
                return;
            }
        }

        cole.setTouchPole(false);

    }

    private void isCollidingPoleEnd(){
        if(!cole.isRidingPole()){
            return;
        }

        for (Pole pole : poles) {
            if (cole.getY() > pole.getEndHitBox().y + pole.getEndHitBox().height) {
                System.out.println("Pole Colliding");
                cole.setRidingPole(false);
                return;
            }
        }
    }

    private void isCollidingWater(){
        for (Water water : waters) {
            if(cole.isColliding(water.getHitBox())){
                cole.touchedWater();
            }
        }
    }

    /**
     * Purpose: Check if colliding with a drainable object
     */
    private void isCollidingDrainable(){
        for (DrainableObject source : drainables) {
            if (cole.isCollidingMelee(source.getHitBox())) {
            }
                cole.setPreviousDrainableBox(source);
                cole.setCanDrain(true);
                return;
            }
    }

    /**
     * Purpose: Check if Cole is on rails
     */
    private void isCollidingRails(){
        boolean hasGround = false;
        for (Rail rail : rails){
            if(rail.rideRail(cole)){
                hasGround = true;
            }
        }

    }

    /**
     * Purpose: Actions that can only be done in developer mode, used for testing
     */
    private void handleDevInputs(){
        //Movement Vertically
        if (!cole.getIsJumping() && (Gdx.input.isKeyJustPressed(Input.Keys.W) ||
                Gdx.input.isKeyPressed(Input.Keys.UP))){
            cole.jump();
        }

        if (Gdx.input.isKeyPressed(Input.Keys.S) || Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            cole.setDucking(true);
        }
        else{ cole.setDucking(false);}

        //Movement Horizontally
        if (Gdx.input.isKeyPressed(Input.Keys.D) || Gdx.input.isKeyPressed(Input.Keys.RIGHT))
        { cole.moveHorizontally(1); }
        if (Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.LEFT))
        { cole.moveHorizontally(-1); }

        if(Gdx.input.isKeyJustPressed(Input.Keys.SHIFT_LEFT)){ cole.updateAttackIndex(); }


        if (Gdx.input.isKeyPressed(Input.Keys.E) && Gdx.input.isKeyPressed(Input.Keys.Q) ||
            Gdx.input.isKeyJustPressed(Input.Keys.NUM_1) && Gdx.input.isKeyJustPressed(Input.Keys.NUM_2))
        { cole.drainEnergy(); }

    }

    /**
     * Purpose: Resize the menuStage viewport in case the screen gets resized (Desktop)
     *          Moving the camera if that's part of the game
     */
    public void updateCamera() {
        //Resize the menu Stage if the screen changes size
        menuStage.getViewport().update(viewport.getScreenWidth(), viewport.getScreenHeight(), true);

        //Updates Camera if the X positions has changed
        if((cole.getX() > WORLD_WIDTH/2f) && (cole.getX() < tiledSetUp.getLevelWidth() - WORLD_WIDTH/2f)) {
            camera.position.set(cole.getX(), camera.position.y, camera.position.z);
            camera.update();
            tiledSetUp.updateCamera(camera);
        }

        //Updates the Camera if the Y positions has changed
        if((cole.getY() > 2 * WORLD_HEIGHT / 3f) && (cole.getY() < tiledSetUp.getLevelHeight() - 2 * WORLD_HEIGHT / 3f)){
            camera.position.set(camera.position.x, cole.getY(), camera.position.z);
            camera.update();
            tiledSetUp.updateCamera(camera);
        }

        //Updates the change of camera to keep the UI moving with the player
        xCameraDelta = camera.position.x - WORLD_WIDTH/2f;
        yCameraDelta = camera.position.y - WORLD_HEIGHT/2f;
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
        for (DrainableObject drainable : drainables){
            drainable.draw(batch);
        }
        batch.end();


        //======================== Draws Titled =============================
        tiledSetUp.drawTiledMap();

        //=================== Draws the Menu Background =====================
        drawUIBackground();
        drawHealthAndEnergy();

        batch.begin();
        drawUIText();
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
     * Purpose: Draws the big black square that severs as the UI background
     */
    private void drawUIBackground(){
        debugRendering.startUIRender();
        debugRendering.getShapeRendererUI().rect(xCameraDelta,yCameraDelta + WORLD_HEIGHT - UI_HEIGHT, WORLD_WIDTH, UI_HEIGHT);
        debugRendering.endUIRender();
    }

    /**
     * Purpose: Draws the Text that's displayed in the UI
     */
    private void drawUIText(){
        bitmapFont.getData().setScale(0.3f);
        bitmapFont.setColor(Color.WHITE);
        textAlignment.centerText(batch, bitmapFont, "Health", 20 + xCameraDelta,yCameraDelta + WORLD_HEIGHT - 5);
        textAlignment.centerText(batch, bitmapFont, "Energy", 20 + xCameraDelta,yCameraDelta + WORLD_HEIGHT - 15 - 5);

        textAlignment.centerText(batch, bitmapFont, cole.getCurrentAttack(), xCameraDelta + WORLD_WIDTH/2f + 80,yCameraDelta + WORLD_HEIGHT - 14);
    }

    /**
     * Purpose: Draws the Health, Energy bars and draws the Current Attack Box
     */
    private void drawHealthAndEnergy(){
        makeBar((int) cole.getCurrentHealth()/20, (int) cole.getMaxHealth()/20, Color.RED, 0);
        makeBar((int) cole.getCurrentEnergy()/20, (int) cole.getMaxEnergy()/20, Color.BLUE, 15);

        debugRendering.startBoarderRender();
        debugRendering.getShapeRendererBoarder().rect(xCameraDelta + WORLD_WIDTH/2f + 20,  yCameraDelta + WORLD_HEIGHT - 27 , 20, 20);
        debugRendering.endBoarderRender();
    }

    /**
     * Purpose: Actual process of making a bar
     */
    private void makeBar(int healthFilledBox, int healthBoarderBox, Color color, float offset){
        //Sets color
        debugRendering.setShapeRendererBarFillColor(color);

        //Creates the filled in boxes
        for(int i = 0; i < healthFilledBox; i++){
            debugRendering.startBarFillRender();
            debugRendering.getShapeRendererBarFill().rect(xCameraDelta + 40 + 15 * i,yCameraDelta + WORLD_HEIGHT - 13 - offset, 10, 10);
            debugRendering.endBarFillRender();
        }

        //Create the boarders around the boxes
        for(int i = 0; i < healthBoarderBox; i++){
            debugRendering.startBoarderRender();
            debugRendering.getShapeRendererBoarder().rect(xCameraDelta + 40 + 15 * i,  yCameraDelta + WORLD_HEIGHT - 13  - offset, 10, 10);
            debugRendering.endBoarderRender();
        }
    }

    /**
     * Input: Void
     * Output: Void
     * Purpose: Draws the text for instructions
     */
    private void drawInstructions() {
        textAlignment.centerText(batch, bitmapFont, "Health", WORLD_WIDTH/2f, WORLD_HEIGHT/2f);

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
        Gdx.gl.glClearColor(Color.BROWN.r, Color.BROWN.g, Color.BROWN.b, Color.BROWN.a); //Sets color to black
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
