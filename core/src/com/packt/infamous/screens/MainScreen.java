package com.packt.infamous.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.packt.infamous.Alignment;
import com.packt.infamous.game_objects.Cole;
import com.packt.infamous.game_objects.DrainableObject;
import com.packt.infamous.game_objects.Enemy;
import com.packt.infamous.game_objects.Platforms;
import com.packt.infamous.game_objects.Projectiles.Projectile;
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
import static com.packt.infamous.Const.NUM_BUTTONS_MAIN_SCREEN;
import static com.packt.infamous.Const.TEXT_OFFSET;
import static com.packt.infamous.Const.UI_HEIGHT;
import static com.packt.infamous.Const.WORLD_HEIGHT;
import static com.packt.infamous.Const.WORLD_WIDTH;

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
    private int buttonIndex = 0;    //Tells us which button we're currently looking at

    //=================================== Miscellaneous Vars =======================================
    private final String[] menuButtonText = new String[]{"Help", "Sound Off", "Main Menu", "Back", "Sound On"};


    //========================================= Game Objects ========================================
    private Cole cole;
    private final Array<Platforms> platforms = new Array<>();
    private final Array<Water> waters = new Array<>();
    private final Array<Pole> poles = new Array<>();
    private final Array<DrainableObject> drainables = new Array<>();
    private final Array<Rail> rails = new Array<>();
    private final Array<Enemy> enemies = new Array<>();
    private final Array<Projectile> projectiles = new Array<>();

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
     * Purpose: Sets up all the objects imported from tiled
     */
    private void showTiled(){
        tiledSetUp = new TiledSetUp(infamous.getAssetManager(), batch, "Tiled/InfamousMapPlaceHolder.tmx");

        Array<Vector2> colePosition = tiledSetUp.getLayerCoordinates("Cole");
        cole = new Cole(colePosition.get(0).x, colePosition.get(0).y, Alignment.PLAYER);
        cole.setWidth(COLE_WIDTH);
        cole.setHeight(COLE_HEIGHT);
        cole.setUpSpriteSheet(mainScreenTextures.coleSpriteSheet);

        /*
        Array<Vector2> poleStartPositions = tiledSetUp.getLayerCoordinates("PoleStart");
        Array<Vector2> poleEndPositions = tiledSetUp.getLayerCoordinates("PoleEnd");
        for(int i = 0; i < poleStartPositions.size; i++){
            poles.add(new Pole(poleStartPositions.get(i).x, poleStartPositions.get(i).y, poleEndPositions.get(i).x, poleEndPositions.get(i).y));
        }
        */


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
        else{ menuInputHandling(); }
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
        for(Projectile proj : projectiles){ proj.drawDebug(debugRendering.getShapeRenderEnemy()); }
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
        updateProjectiles(tiledSetUp.getLevelWidth());
        handleInput();
        cole.update(tiledSetUp.getLevelWidth(), delta);
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
        handleDevInputs();
    }

    /**
     * Pauses and un-pauses the game with Esc key
     */
    private void handlePause(){
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            pausedFlag = !pausedFlag;
//            for (ImageButton imageButton : menuButtons) { imageButton.setVisible(true); }
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
        if (cole.getPreviousDrainable() == null) {
            for (DrainableObject source : drainables) {
                if (cole.isCollidingMelee(source.getHitBox()) && source.getCurrentEnergy() > 0) {
                    System.out.println("Found drainable: " + source);
                    cole.setPreviousDrainable(source);
                    cole.setCanDrain(true);
                    return;
                }
            }
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
     * Purpose: Checks for collision between enemies and player-made projectiles
     */
    private void processProjectileCollision(){
        for (int i = 0; i < projectiles.size; i++){
            Projectile proj = projectiles.get(i);
            boolean removeProjectile = false;
            //If projectile is no longer moving, or collided with world barrier
            if (proj.getVelocity().x == 0 && proj.getVelocity().y == 0 ||
                    proj.isTouchingCeiling()){
                removeProjectile = true;
            }
            else {
                //Check if Projectile is colliding with an enemy
                for (Enemy enemy : enemies){
                    if (proj.isColliding(enemy.getHitBox())){
                        removeProjectile = true;
                        enemy.takeDamage(proj.getDamage());
                    }
                }
            }
            if (removeProjectile){
                if (proj.isExplosive){
                    //Explode
                }
                projectiles.removeValue(proj, true);
            }
        }
    }

    //========================= Player-Attack Related =========================

    /**
     * Purpose: Updates projectile position each tick, processes collisions from projectiles
     * @param levelWidth the end of the level
     */
    private void updateProjectiles(float levelWidth){
        processProjectileCollision();
        for (Projectile proj : projectiles){
            proj.update(levelWidth);
        }
    }

    private void createProjectile(){
        int direction = -1;
        if (!cole.getIsFacingRight()){
            direction = 1;
        }
        projectiles.add(new Projectile(cole.getX(), cole.getY() + cole.getHeight() * (2/3f), Alignment.PLAYER,
                1, 1, direction));
        System.out.println(projectiles.size);
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


        else if (Gdx.input.isKeyPressed(Input.Keys.Q) || Gdx.input.isKeyPressed(Input.Keys.NUM_1)){
            cole.attack();
            if (cole.isAttacking){
                createProjectile();
                cole.isAttacking = false;
            }
        }


    }

    private void menuInputHandling(){
        if(!helpFlag) {
            //Movement Vertically
            if (Gdx.input.isKeyJustPressed(Input.Keys.W) || Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
                buttonIndex--;
                if (buttonIndex <= -1) {
                    buttonIndex = NUM_BUTTONS_MAIN_SCREEN - 1;
                }
            }

            if (Gdx.input.isKeyJustPressed(Input.Keys.S) || Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
                buttonIndex++;
                if (buttonIndex >= NUM_BUTTONS_MAIN_SCREEN) {
                    buttonIndex = 0;
                }
            }

            if (Gdx.input.isKeyJustPressed(Input.Keys.E)) {
                //Launches the game
                if (buttonIndex == 0) { helpFlag = true; }
                //Turns on the help menu
                else if (buttonIndex == 1) { musicControl.soundOnOff(); }
                //Turns on the credits menu
                else if(buttonIndex == 2){
                    musicControl.stopMusic();
                    infamous.setScreen(new LoadingScreen(infamous, 0));
                }
                else{ pausedFlag = false; }
            }
        }
        else{
            if (Gdx.input.isKeyJustPressed(Input.Keys.E)) { helpFlag = false; }
        }
    }

    /**
     * Purpose: Resize the menuStage viewport in case the screen gets resized (Desktop)
     *          Moving the camera if that's part of the game
     */
    public void updateCamera() {
        //Updates Camera if the X positions has changed
        if((cole.getX() > WORLD_WIDTH/2f) && (cole.getX() < tiledSetUp.getLevelWidth() - WORLD_WIDTH/2f)) {
            camera.position.set(cole.getX(), camera.position.y, camera.position.z);
            camera.update();
            tiledSetUp.updateCamera(camera);
        }

        //Updates the Camera if the Y positions has changed
        if((cole.getY() >  WORLD_HEIGHT / 2) && (cole.getY() < tiledSetUp.getLevelHeight() - WORLD_HEIGHT / 2)){
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
        cole.drawAnimations(batch);
        batch.end();


        //======================== Draws Titled =============================
        tiledSetUp.drawTiledMap();

        //=================== Draws the Menu Background =====================
        drawUIBackground();
        drawHealthAndEnergy();

        batch.begin();
        drawUIText();
        drawPopUpMenu();

        //=================== Draws the Menu Buttons =========================
        if(pausedFlag || endFlag || helpFlag){drawMainButtons();}

        //================= Draw Menu Button Text =============================
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
            batch.draw(mainScreenTextures.menuBackgroundTexture, xCameraDelta + WORLD_WIDTH / 2f - WORLD_WIDTH / 4, yCameraDelta + WORLD_HEIGHT / 2 - 5 * WORLD_HEIGHT / 12, WORLD_WIDTH / 2, 5 * WORLD_HEIGHT / 6);
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
        textAlignment.centerText(batch, bitmapFont, "Health", xCameraDelta + WORLD_WIDTH/2f, yCameraDelta + WORLD_HEIGHT/2f);

        bitmapFont.getData().setScale(.5f);
        textAlignment.centerText(batch, bitmapFont, "Instruction", xCameraDelta + WORLD_WIDTH / 2f, yCameraDelta + INSTRUCTIONS_Y_START);
        bitmapFont.getData().setScale(.35f);

        textAlignment.centerText(batch, bitmapFont, "Move - WASD", xCameraDelta + WORLD_WIDTH / 2f, yCameraDelta + INSTRUCTIONS_Y_START - TEXT_OFFSET);
        textAlignment.centerText(batch, bitmapFont, "Action #2", xCameraDelta + WORLD_WIDTH / 2f, yCameraDelta + INSTRUCTIONS_Y_START - 2 * TEXT_OFFSET);
        textAlignment.centerText(batch, bitmapFont, "Action #3", xCameraDelta + WORLD_WIDTH / 2f, yCameraDelta + INSTRUCTIONS_Y_START  - 3 * TEXT_OFFSET);
        textAlignment.centerText(batch, bitmapFont, "Actions #4", xCameraDelta + WORLD_WIDTH / 2f, yCameraDelta + INSTRUCTIONS_Y_START - 4 * TEXT_OFFSET);
    }


    /**
     * Purpose: Draws the main buttons on the screen
     */
    private void drawMainButtons(){
        for(int i = 0; i < NUM_BUTTONS_MAIN_SCREEN; i++){
            if(i == buttonIndex){
                batch.draw(mainScreenTextures.buttonSpriteSheet[0][1], xCameraDelta + WORLD_WIDTH / 2f - MENU_BUTTON_WIDTH / 2f, yCameraDelta +MENU_BUTTON_Y_START - 15 - (10 + MENU_BUTTON_HEIGHT) * i, MENU_BUTTON_WIDTH, MENU_BUTTON_HEIGHT);
            }
            else{
                batch.draw(mainScreenTextures.buttonSpriteSheet[0][0], xCameraDelta + WORLD_WIDTH / 2f - MENU_BUTTON_WIDTH / 2f, yCameraDelta + MENU_BUTTON_Y_START -  15 - (10 + MENU_BUTTON_HEIGHT) * i, MENU_BUTTON_WIDTH, MENU_BUTTON_HEIGHT);
            }
        }
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
            if (i ==1 && musicControl.getSFXVolume() == 0) { string = menuButtonText[menuButtonText.length - 1]; }
            textAlignment.centerText(batch, bitmapFont, string, xCameraDelta + WORLD_WIDTH / 2f,  yCameraDelta - 10 + MENU_BUTTON_Y_START + MENU_BUTTON_HEIGHT/2f - (10 + MENU_BUTTON_HEIGHT) * i);
        }
    }

    /**
     Purpose: Draws text over the back button in help menu
     */
    private void drawBackButtonText(){
        textAlignment.centerText(batch, bitmapFont, menuButtonText[4], xCameraDelta + WORLD_WIDTH / 2f,  yCameraDelta - 10 + INSTRUCTION_BUTTON_Y + MENU_BUTTON_HEIGHT/2f);
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
