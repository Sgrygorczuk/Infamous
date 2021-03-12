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
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.packt.infamous.Alignment;
import com.packt.infamous.game_objects.Checkpoint;
import com.packt.infamous.game_objects.CheckpointObject;
import com.packt.infamous.game_objects.Civilian;
import com.packt.infamous.game_objects.Cole;
import com.packt.infamous.game_objects.Collectible;
import com.packt.infamous.game_objects.DrainableObject;
import com.packt.infamous.game_objects.EndShard;
import com.packt.infamous.game_objects.Enemy;
import com.packt.infamous.game_objects.platforms.Ledge;
import com.packt.infamous.game_objects.platforms.Platforms;
import com.packt.infamous.game_objects.Projectiles.Bomb;
import com.packt.infamous.game_objects.Projectiles.Projectile;
import com.packt.infamous.game_objects.platforms.Pole;
import com.packt.infamous.game_objects.platforms.Rail;
import com.packt.infamous.game_objects.Water;
import com.packt.infamous.main.Infamous;
import com.packt.infamous.screens.textures.MainScreenTextures;
import com.packt.infamous.tools.DebugRendering;
import com.packt.infamous.tools.MusicControl;
import com.packt.infamous.tools.TextAlignment;
import com.packt.infamous.tools.TiledSetUp;
import com.packt.infamous.Enum;

import static com.packt.infamous.Const.BOLT_HEIGHT;
import static com.packt.infamous.Const.BOLT_SPEED;
import static com.packt.infamous.Const.BOLT_WIDTH;
import static com.packt.infamous.Const.BOMB_HEIGHT;
import static com.packt.infamous.Const.BOMB_SPEED;
import static com.packt.infamous.Const.BOMB_WIDTH;
import static com.packt.infamous.Const.COLE_HEIGHT;
import static com.packt.infamous.Const.COLE_WIDTH;
import static com.packt.infamous.Const.DEVELOPER_TEXT_X;
import static com.packt.infamous.Const.DEVELOPER_TEXT_Y;
import static com.packt.infamous.Const.EXPLOSIVE_RADIUS;
import static com.packt.infamous.Const.INSTRUCTION_BUTTON_Y;
import static com.packt.infamous.Const.MENU_BUTTON_HEIGHT;
import static com.packt.infamous.Const.MENU_BUTTON_WIDTH;
import static com.packt.infamous.Const.MENU_BUTTON_Y_START;
import static com.packt.infamous.Const.NUM_BUTTONS_MAIN_SCREEN;
import static com.packt.infamous.Const.TEXT_OFFSET;
import static com.packt.infamous.Const.TORPEDO_HEIGHT;
import static com.packt.infamous.Const.TORPEDO_SPEED;
import static com.packt.infamous.Const.TORPEDO_WIDTH;
import static com.packt.infamous.Const.UI_HEIGHT;
import static com.packt.infamous.Const.WORLD_HEIGHT;
import static com.packt.infamous.Const.WORLD_WIDTH;

class MainScreen extends ScreenAdapter {

    //=========================================== Variable =========================================

    //================================== Image processing ===========================================
    private Viewport viewport;			                     //The screen where we display things
    private Camera camera;				                     //The camera viewing the viewport
    private final SpriteBatch batch = new SpriteBatch();	 //Batch that holds all of the textures

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
    private boolean helpFlag = false;           //Tells us if help flag is on or off
    private boolean skinFlag = false;
    private float xCameraDelta = 0;
    private float yCameraDelta = 0;
    private int buttonIndex = 0;    //Tells us which button we're currently looking at
    private float polePosition = 0;
    private float ledgePosition = 0;
    private int collectibleSum = 0;

    private int touchedPersonIndex;
    private boolean isTouchingPerson = false;
    private float killText = -1;
    private float healText = 1;
    private boolean textDirection = false;
    private int healed = 0;
    private int killed = 0;

    private int skinIndex = 0;
    private boolean skinExit = false;

    private boolean endFlag = false;            //Tells us the player touched the endShard
    private int exitIndex = 1;

    //=================================== Miscellaneous Vars =======================================
    private Array<String> levelNames = new Array<>();
    private int tiledSelection;
    private final String[] menuButtonText = new String[]{"Controls", "Skins", "Sound Off", "Main Menu", "Back", "Sound On"};


    //========================================= Game Objects ========================================
    private Cole cole;
    private final Array<Platforms> platforms = new Array<>();
    private final Array<Water> waters = new Array<>();
    private final Array<Pole> poles = new Array<>();
    private final Array<Ledge> ledges = new Array<>();
    private final Array<DrainableObject> drainables = new Array<>();
    private final Array<Rail> rails = new Array<>();
    private final Array<Enemy> enemies = new Array<>();
    private final Array<Projectile> projectiles = new Array<>();
    private final Array<Collectible> collectibles = new Array<>();
    private final Array<Civilian> civilians = new Array<>();
    private final Array<EndShard> endShards = new Array<>();
    private final Array<CheckpointObject> checkpoints = new Array<>();

    private Checkpoint checkpoint;
    private boolean isCheckpointed = false;

    //================================ Set Up ======================================================

    /**
     * Purpose: Grabs the info from main screen that holds asset manager
     * @param infamous game, checkpoint if reloading level
     * @param tiledSelection stores the map choice
    */
    MainScreen(Infamous infamous, int tiledSelection) {
        this.infamous = infamous;

        this.tiledSelection = tiledSelection;
        levelNames.add("Tiled/SebaLevelOne.tmx");
        levelNames.add("Tiled/LevelVertical.tmx");
        levelNames.add("Tiled/SebaLevelThree.tmx");
        levelNames.add("Tiled/Paul_Level.tmx");
        levelNames.add("Tiled/SebaLevelTwo.tmx");
    }

    MainScreen(Infamous infamous, int tiledSelection, Checkpoint checkpoint) {
        this(infamous, tiledSelection);
        this.checkpoint = checkpoint;
        isCheckpointed = true;

    }


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
        if (isCheckpointed){
            cole.setX(checkpoint.getLocation_x());
            cole.setY(checkpoint.getLocation_y());
            cole.setCurrentEnergy(checkpoint.getEnergy());
            cole.setCurrentHealth(checkpoint.getHealth());
        }
        if(developerMode){debugRendering.showRender();}    //If in developer mode sets up the renders
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
        tiledSetUp = new TiledSetUp(infamous.getAssetManager(), batch, levelNames.get(tiledSelection));

        //======================================== Cole =========================================
        Array<Vector2> colePosition = tiledSetUp.getLayerCoordinates("Cole");
        cole = new Cole(colePosition.get(0).x, colePosition.get(0).y, Alignment.PLAYER, infamous.getTextureChoice());
        cole.setWidth(COLE_WIDTH);
        cole.setHeight(COLE_HEIGHT);
        cole.setUpSpriteSheet(mainScreenTextures.coleSpriteSheet, mainScreenTextures.drainSpriteSheet,
                mainScreenTextures.railSparkSpriteSheet, mainScreenTextures.hoverSpriteSheet,
                mainScreenTextures.meleeSpriteSheet);

        //========================================= Civilians ======================================
        Array<Vector2> peoplePosition = tiledSetUp.getLayerCoordinates("People");
        for(int i = 0; i < peoplePosition.size; i++){
            int choice = MathUtils.random(0,3);
            civilians.add(new Civilian(peoplePosition.get(i).x, peoplePosition.get(i).y,
                    mainScreenTextures.peopleDownSpriteSheet[choice][0],
                    mainScreenTextures.peopleUpSpriteSheet[0][choice],
                    mainScreenTextures.peopleUpSpriteSheet[0][4]));
        }


        //========================================= Pole ======================================
        Array<Vector2> polePositions = tiledSetUp.getLayerCoordinates("Pole");
        Array<Vector2> poleDimensions = tiledSetUp.getLayerDimensions("Pole");
        for(int i = 0; i < polePositions.size; i++){
            poles.add(new Pole(polePositions.get(i).x + poleDimensions.get(i).x/4f, polePositions.get(i).y));
            poles.get(i).setWidth(poleDimensions.get(i).x/2f);
            poles.get(i).setHeight(poleDimensions.get(i).y);
        }

        //======================================= Ledge ==============================================
        Array<Vector2> ledgePositions = tiledSetUp.getLayerCoordinates("Ledge");
        Array<Vector2> ledgeDimensions = tiledSetUp.getLayerDimensions("Ledge");
        for(int i = 0; i < ledgePositions.size; i++){
            ledges.add(new Ledge(ledgePositions.get(i).x, ledgePositions.get(i).y + ledgeDimensions.get(i).y/4f));
            ledges.get(i).setWidth(ledgeDimensions.get(i).x);
            ledges.get(i).setHeight(ledgeDimensions.get(i).y/2f);
        }

        //================================= Collectible ===================================
        Array<Vector2> collectiblePositions = tiledSetUp.getLayerCoordinates("BlastShard");
        for(int i = 0; i < collectiblePositions.size; i++){
            collectibles.add(new Collectible(collectiblePositions.get(i).x, collectiblePositions.get(i).y, mainScreenTextures.collectibleSpriteSheet));
        }
        collectibleSum = collectibles.size;

        Array<Vector2> endPositions = tiledSetUp.getLayerCoordinates("EndShard");
        for(int i = 0; i < endPositions.size; i++){
            endShards.add(new EndShard(endPositions.get(i).x, endPositions.get(i).y, mainScreenTextures.collectibleSpriteSheet));
        }

        //================================= Platforms =======================================
        Array<Vector2> platformsPositions = tiledSetUp.getLayerCoordinates("Platforms");
        Array<Vector2> platformsDimensions = tiledSetUp.getLayerDimensions("Platforms");
        for(int i = 0; i < platformsPositions.size; i++){
            platforms.add(new Platforms(platformsPositions.get(i).x, platformsPositions.get(i).y, Alignment.BACKGROUND));
            platforms.get(i).setWidth(platformsDimensions.get(i).x);
            platforms.get(i).setHeight(platformsDimensions.get(i).y);
        }

        //============================ Water ============================================
        Array<Vector2> waterPositions = tiledSetUp.getLayerCoordinates("Water");
        Array<Vector2> waterDimensions = tiledSetUp.getLayerDimensions("Water");
        for(int i = 0; i < waterPositions.size; i++){
            waters.add(new Water(waterPositions.get(i).x, waterPositions.get(i).y, Alignment.ENEMY));
            waters.get(i).setWidth(waterDimensions.get(i).x);
            waters.get(i).setHeight(waterDimensions.get(i).y);
            waters.get(i).setTexture(mainScreenTextures.waterTexture);
        }

        //=============================== Rails ===========================================
        Array<Vector2> railPositions = tiledSetUp.getLayerCoordinates("Rail");
        Array<Vector2> railDimensions = tiledSetUp.getLayerDimensions("Rail");
        for(int i = 0; i < railPositions.size; i++){
            float x = railPositions.get(i).x;
            float y = railPositions.get(i).y +  railDimensions.get(i).y/2f;
            float width = railDimensions.get(i).x;
            float height = railDimensions.get(i).y/2f;
            rails.add(new Rail(x, y+height, width, height,Alignment.BACKGROUND));
            Platforms rail_platform = new Platforms(x + 5, y, Alignment.BACKGROUND);
            rail_platform.setWidth(width - 10);
            rail_platform.setHeight(height);
            platforms.add(rail_platform);
        }

        //========================= Checkpoints ==========================================
        Array<Vector2> checkpointPositions = tiledSetUp.getLayerCoordinates("Checkpoint");
        for (Vector2 position : checkpointPositions){
            checkpoints.add(new CheckpointObject(position.x, position.y, Alignment.BACKGROUND, mainScreenTextures.checkpointTexture));
        }

        //========================= Drainables ===========================================
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

        Array<Vector2> enemyPositions = tiledSetUp.getLayerCoordinates("Enemy");
        Array<Vector2> enemyDimensions = tiledSetUp.getLayerDimensions("Enemy");
        for(int i = 0; i < enemyPositions.size; i++){
            Enemy newEnemy = new Enemy(enemyPositions.get(i).x, enemyPositions.get(i).y, enemyDimensions.get(i).x, Alignment.ENEMY, true);
            newEnemy.setUpSpriteSheet(mainScreenTextures.enemySpriteSheet,
                    mainScreenTextures.enemyDeathSpriteSheet);
            enemies.add(newEnemy);
        }

        Array<Vector2> stationaryL = tiledSetUp.getLayerCoordinates("EnemyStationaryL");
        Array<Vector2> stationaryR = tiledSetUp.getLayerCoordinates("EnemyStationaryR");
        for(int i = 0; i < stationaryL.size; i++){
            Enemy newEnemy = new Enemy(stationaryL.get(i).x, stationaryL.get(i).y, 0, Alignment.ENEMY, false);
            newEnemy.setUpSpriteSheet(mainScreenTextures.enemySpriteSheet,
                    mainScreenTextures.enemyDeathSpriteSheet);
            enemies.add(newEnemy);
        }
        for(int i = 0; i < stationaryR.size; i++){
            Enemy newEnemy = new Enemy(stationaryR.get(i).x, stationaryR.get(i).y, 0, Alignment.ENEMY, true);
            newEnemy.setUpSpriteSheet(mainScreenTextures.enemySpriteSheet,
                    mainScreenTextures.enemyDeathSpriteSheet);
            enemies.add(newEnemy);
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
        for(Civilian civilian : civilians){civilian.drawDebug(debugRendering.getShapeRenderEnemy());}
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
        debugRendering.setShapeRendererBackgroundColor(Color.GRAY);
        for(Platforms platform : platforms){
            platform.drawDebug(debugRendering.getShapeRendererBackground());
        }
        debugRendering.setShapeRendererBackgroundColor(Color.BROWN);
        for(Pole pole : poles){
            pole.drawDebug(debugRendering.getShapeRendererBackground());
        }
        for(Ledge ledge : ledges){
            ledge.drawDebug(debugRendering.getShapeRendererBackground());
        }
        debugRendering.setShapeRendererBackgroundColor(Color.BLUE);
        for(DrainableObject drainable :  drainables){
            drainable.drawDebug(debugRendering.getShapeRendererBackground());
        }

        debugRendering.setShapeRendererBackgroundColor(Color.MAGENTA);
        for(CheckpointObject checkpoint : checkpoints){checkpoint.drawDebug(debugRendering.getShapeRendererBackground());}

        debugRendering.setShapeRendererBackgroundColor(Color.YELLOW);
        for(Rail rail : rails){
            rail.drawDebug(debugRendering.getShapeRendererBackground());
        }

        for(Enemy enemy : enemies){
            debugRendering.setShapeRendererBackgroundColor(Color.RED);
            enemy.drawDebug(debugRendering.getShapeRendererBackground());
            debugRendering.setShapeRendererBackgroundColor(Color.GOLDENROD);

        }
        debugRendering.endBackgroundRender();

        debugRendering.startCollectibleRender();
        for(Collectible collectible : collectibles){collectible.drawDebug(debugRendering.getShapeRendererCollectible());}
        for(EndShard endShard : endShards){endShard.drawDebug(debugRendering.getShapeRendererCollectible());}
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
        updateColliding();
        updateProjectiles(tiledSetUp.getLevelWidth(), tiledSetUp.getLevelHeight(), delta);
        handleInput(delta);
        updateEnemies(delta);
        updateIfDead();
        cole.update(tiledSetUp.getLevelWidth(), tiledSetUp.getLevelHeight(), delta);
        if(cole.getInvincibility()){cole.invincibilityTimer(delta);}
        for(Collectible collectible : collectibles){collectible.update(delta);}
        for(EndShard endShard : endShards){endShard.update(delta);}
        for(Water water : waters){water.updatePosition();}

        if(textDirection){
            healText += 0.5;
            killText -= 0.5;
            if(healText == 10){
                textDirection = false;
            }
        }
        else{
            healText -= 0.5;
            killText += 0.5;
            if(killText == 10){
                textDirection = true;
            }
        }
    }

    //======================= Input Handling ======================================================

    /**
     * Purpose: Central Input Handling function
     */
    private void handleInput(float delta) {
        //Pause and un-pause the game with ESC
        handlePause();
        //Allows user to turn on dev mode
        if (Gdx.input.isKeyJustPressed(Input.Keys.TAB)) { developerMode = !developerMode; }
        handleInputs(delta);
    }

    /**
     * Pauses and un-pauses the game with Esc key
     */
    private void handlePause(){
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            pausedFlag = !pausedFlag;
        }
    }

    /**
     * Purpose: Actions that can only be done in developer mode, used for testing
     */
    private void handleInputs(float delta){
        //======================== Movement Vertically ====================================
        if(cole.canColeMove() && !cole.getIsJumping() && !cole.getIsFalling() && !cole.getIsHovering() && (Gdx.input.isKeyJustPressed(Input.Keys.W) || Gdx.input.isKeyPressed(Input.Keys.UP))){
            cole.jump();
        }
        else if (cole.getIsJumping() && !cole.getIsHovering() && (Gdx.input.isKeyJustPressed(Input.Keys.W) || Gdx.input.isKeyPressed(Input.Keys.UP))){
            cole.setIsHovering(true);
            cole.setIsJumping(false);
        }
        else if(!cole.canColeMove() && cole.getIsClimbingPole() && (Gdx.input.isKeyPressed(Input.Keys.W) || Gdx.input.isKeyPressed(Input.Keys.UP))){
            cole.climbPole(true, delta);
        }
        else if(!cole.canColeMove() && cole.getIsHangingLedge() && (Gdx.input.isKeyPressed(Input.Keys.W) || Gdx.input.isKeyPressed(Input.Keys.UP))){
            cole.jump();
            cole.setIsHangingLedge(false);
        }

        if(cole.getIsHovering() && Gdx.input.isKeyPressed(Input.Keys.S)){
            cole.setIsHovering(false);
        }
        if(cole.canColeMove() && !cole.getIsJumping() && (Gdx.input.isKeyPressed(Input.Keys.S) || Gdx.input.isKeyPressed(Input.Keys.DOWN))){
            cole.setDucking(true);
        }
        else if(!cole.canColeMove() && cole.getIsClimbingPole() && (Gdx.input.isKeyPressed(Input.Keys.S) || Gdx.input.isKeyPressed(Input.Keys.DOWN))){
            cole.climbPole(false, delta);
        }
        else{
            cole.setDucking(false);
        }

        //==================== Movement Horizontally ======================================
        if (cole.getIsDucking() && cole.canColeMove() && (Gdx.input.isKeyPressed(Input.Keys.D) || Gdx.input.isKeyPressed(Input.Keys.RIGHT)))
        { cole.moveHorizontally(1); }
        else if(!cole.canColeMove() && cole.getIsClimbingPole() && (Gdx.input.isKeyPressed(Input.Keys.D) || Gdx.input.isKeyPressed(Input.Keys.RIGHT))){
            cole.jump();
            cole.setIsClimbingPole(false);
        }
        else if(!cole.canColeMove() && cole.getIsHangingLedge() && (Gdx.input.isKeyPressed(Input.Keys.D) || Gdx.input.isKeyPressed(Input.Keys.RIGHT))){
            cole.shimmyLedge(true, delta);
        }

        if (cole.getIsDucking() && cole.canColeMove() && (Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.LEFT)))
        { cole.moveHorizontally(-1); }
        else if(!cole.canColeMove() && cole.getIsClimbingPole() && (Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.LEFT))){
            cole.jump();
            cole.setIsClimbingPole(false);
        }
        else if(!cole.canColeMove() && cole.getIsHangingLedge() && (Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.LEFT))){
            cole.shimmyLedge(false, delta);
        }

        //=========================== Switch Power ========================================
        if(Gdx.input.isKeyJustPressed(Input.Keys.SHIFT_LEFT)){ cole.updateAttackIndex(); }

        //================================= Drain =========================================
        if(isTouchingPerson){
            if(Gdx.input.isKeyPressed(Input.Keys.E)){
                civilians.get(touchedPersonIndex).healed();
                healed++;
            }
            else if(Gdx.input.isKeyPressed(Input.Keys.Q)){
                civilians.get(touchedPersonIndex).kill();
                killed++;
            }
        }
        else if (Gdx.input.isKeyPressed(Input.Keys.E) && Gdx.input.isKeyPressed(Input.Keys.Q) ||
                Gdx.input.isKeyJustPressed(Input.Keys.NUM_1) && Gdx.input.isKeyJustPressed(Input.Keys.NUM_2))
        { cole.drainEnergy(delta); }

        //================================== Attacks ======================================
        else if (Gdx.input.isKeyPressed(Input.Keys.Q) || Gdx.input.isKeyPressed(Input.Keys.NUM_1)){
            cole.attack();
            if (cole.isIsAttacking()){
                createProjectile(Alignment.PLAYER, cole.getIsFacingRight(), null);
            }
        }

        //================================ Interact =================================================
        else if (Gdx.input.isKeyJustPressed(Input.Keys.E) || Gdx.input.isKeyJustPressed(Input.Keys.NUM_2)){
            if(cole.getIsTouchingPole() && !cole.getIsClimbingPole()){
                cole.setIsClimbingPole(true);
                cole.setX(polePosition);
                cole.setIsJumping(false);
                cole.setIsHovering(false);
                cole.setVelocity(0,0);
            }
            else if(cole.getIsTouchingPole() && cole.getIsClimbingPole()){
                cole.setIsClimbingPole(false);
            }

            if(cole.getIsTouchingLedge() && !cole.getIsHangingLedge()){
                cole.setIsHangingLedge(true);
                cole.setY(ledgePosition);
                cole.setIsJumping(false);
                cole.setIsHovering(false);
                cole.setVelocity(0,0);
            }
            else if(cole.getIsTouchingLedge() && cole.getIsHangingLedge()){
                cole.setIsHangingLedge(false);
            }
        }
        else {
            cole.setDraining(false);
        }

    }

    /**
     * Purpose: allows user to control the menus
     */
    private void menuInputHandling(){
        //================================= General Menu ==========================
        if(!helpFlag && !skinFlag && !endFlag) {
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
                else if(buttonIndex == 1){ skinFlag = true;}
                //Turns on the help menu
                else if (buttonIndex == 2) { musicControl.soundOnOff(); }
                //Turns on the credits menu
                else if(buttonIndex == 3){
                    musicControl.stopMusic();
                    infamous.setScreen(new LoadingScreen(infamous, 0));
                }
                else{
                    pausedFlag = false;
                    buttonIndex = 0;
                }
            }

            if(Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)){
                pausedFlag = false;
                buttonIndex = 0;
            }
        }
        //========================= Controls ===============================
        else if(helpFlag){
            if (Gdx.input.isKeyJustPressed(Input.Keys.E) || Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) { helpFlag = false; }
        }
        //===================== Skin Menu ==================================
        else if(skinFlag){
            if((Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE))){
                skinFlag = false;
                skinExit = false;
                skinIndex = 0;
            }
            if(skinExit){
                if (Gdx.input.isKeyJustPressed(Input.Keys.W) || Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
                    skinExit = false;
                }
                else if((Gdx.input.isKeyJustPressed(Input.Keys.E))){
                    skinFlag = false;
                    skinExit = false;
                    skinIndex = 0;
                }
            }
            else{
                if (Gdx.input.isKeyJustPressed(Input.Keys.S) || Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
                    skinExit = true;
                }
                else if(Gdx.input.isKeyPressed(Input.Keys.E)){
                    if((skinIndex == 1 && infamous.getIfCollectibleComplete())
                    || (skinIndex == 2 && infamous.getIfHealsComplete())
                    || (skinIndex == 3 && infamous.getIfKillsComplete())){
                        updateTextureChoice(skinIndex);
                    }
                }
                else if(skinIndex < 3 && (Gdx.input.isKeyJustPressed(Input.Keys.D) || Gdx.input.isKeyJustPressed(Input.Keys.RIGHT))){
                    skinIndex ++;
                }
                else if(skinIndex > 0 && (Gdx.input.isKeyJustPressed(Input.Keys.A) || Gdx.input.isKeyJustPressed(Input.Keys.LEFT))){
                    skinIndex --;
                }
            }
        }
        //================== Exit Menu ==================
        else if(endFlag){
            //Move between buttons
            if(exitIndex == 0 && (Gdx.input.isKeyJustPressed(Input.Keys.D) || Gdx.input.isKeyJustPressed(Input.Keys.RIGHT))){
                exitIndex++;
            }
            else if(exitIndex == 1 && (Gdx.input.isKeyJustPressed(Input.Keys.A) || Gdx.input.isKeyJustPressed(Input.Keys.LEFT))){
                exitIndex--;
            }

            //Select the action
            if (exitIndex == 0 && Gdx.input.isKeyJustPressed(Input.Keys.E)) {
                musicControl.stopMusic();
                infamous.setScreen(new LoadingScreen(infamous, 0));
            }
            else if(exitIndex == 1 && Gdx.input.isKeyJustPressed(Input.Keys.E)){
                toNextLevel();
            }
        }
    }

    /**
     * Purpose: Change the animation effect around cole based on users choice
     * @param textureChoice the texture row we want to use
     */
    private void updateTextureChoice(int textureChoice){
        infamous.setTextureChoice(textureChoice);
        cole.updateTextureChoice(infamous.getTextureChoice());
    }


    //============================== Collision ============================================

    /**
     * Purpose: Central Colliding function, to make it update less cluttered
     */
    private void updateColliding(){
        isCollidingPlatform();
        isCollidingDrainable();
        isCollidingWater();
        isCollidingRails();
        isCollidingPole();
        isCollidingLedge();
        isCollidingCollectibles();
        isCollidingMelee();
        isCollidingWithPerson();
        isCollidingEndShard();
        isCollidingCheckpoint();
    }

    /**
     * Purpose: Check if cole is touching a checkpoint
     *
     */
    private void isCollidingCheckpoint(){
        for (CheckpointObject checkpoint : checkpoints){
            if (checkpoint.isColliding(cole.getHitBox())){
                isCheckpointed = true;
                this.checkpoint = new Checkpoint(cole.getCurrentHealth(), cole.getCurrentEnergy(),
                        cole.getX(), cole.getY());
            }
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
               if(!cole.getIsRiding()
                       &&cole.getX() >= platforms.get(i).getX()
                       && cole.getX() + cole.getWidth() <= platforms.get(i).getX() + platforms.get(i).getWidth()) {
                   cole.setLastTouchedGround();     //Saves that position for respawn
               }
           }
        }
        //If there is no ground below Cole he should fall
        if(!hasGround){cole.setFalling(true);}
    }

    /**
     *.]
     */
    private void isCollidingPole(){
        boolean touching = false;
        for (Pole pole : poles) {
            if(cole.isColliding(pole.getHitBox())){
                touching = true;
                polePosition = pole.getX() - pole.getWidth()/2f;
                cole.setPoleLimits(pole.getY(), pole.getY() + pole.getHeight());
            }
        }
        cole.setIsTouchingPole(touching);
    }


    /**
     * Checks if cole hits water.
     */
    private void isCollidingLedge(){
        boolean touching = false;
        for (Ledge ledge : ledges) {
            if(cole.isColliding(ledge.getHitBox())){
                touching = true;
                ledgePosition = ledge.getY() - 2.5f * ledge.getHeight();
                cole.setLedgeLimits(ledge.getX(), ledge.getX() + ledge.getWidth());
            }
        }
        cole.setIsTouchingLedge(touching);
    }

    /**
     * Checks if cole hits water.
     */
    private void isCollidingWater(){
        for (Water water : waters) {
            if(cole.isColliding(water.getHitBox())){
                cole.touchedWater();
                updateCamera();
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
        boolean riding = false;
        for (Rail rail : rails){
            if(rail.rideRail(cole)){
                riding = true;
                cole.setFalling(false);
                cole.setFriction(true);
            }
        }
        cole.setIsRiding(riding);
    }

    /**
     * Purpose: Check if Cole can Melee an Enemy
     */
    private void isCollidingMelee(){
        boolean isMelee = false;
        for (Enemy enemy : enemies){
            if(cole.isCollidingMelee(enemy.getHitBox())){
                isMelee = true;
            }
        }
        cole.setCanMelee(isMelee);
    }

    private void isCollidingWithPerson(){
        isTouchingPerson = false;
        for (Civilian civilian : civilians){
            if(cole.isCollidingMelee(civilian.getHitBox()) && civilian.getState() == 0){
                isTouchingPerson = true;
                touchedPersonIndex = civilians.indexOf(civilian, true);
            }
        }
    }

    private void isCollidingEndShard() {
        for (EndShard endShard : endShards) {
            if (cole.isColliding(endShard.getHitBox())) {
                pausedFlag = true;
                endFlag = true;
                //Updates all the data for unlocks
                if(!infamous.getKilled(tiledSelection) && killed  == civilians.size ){
                    infamous.setKilled(tiledSelection);
                }
                if(!infamous.getHealed(tiledSelection) && healed  == civilians.size ) {
                    infamous.setHealed(tiledSelection);
                }
                if(!infamous.getCollected(tiledSelection) && (collectibleSum-collectibles.size) == collectibleSum){
                    infamous.setCollected(tiledSelection);
                }
            }
        }
    }

    private void toNextLevel(){
        if(tiledSelection + 1 < levelNames.size)
        {
            musicControl.stopMusic();
            infamous.setScreen(new LoadingScreen(infamous, 1, tiledSelection + 1));
        }
        else{
            musicControl.stopMusic();
            infamous.setScreen(new LoadingScreen(infamous, 2));

        }
    }


    /**
     * Purpose: Check if Cole is on rails
     */
    private void isCollidingCollectibles(){
        Collectible touchedCollectible =  new Collectible(0,0, mainScreenTextures.collectibleSpriteSheet);
        for (Collectible collectible : collectibles){
            if(collectible.isColliding(cole.getHitBox())){
                touchedCollectible = collectible;
            }
        }
        collectibles.removeValue(touchedCollectible, true);
    }

    //========================= Player-Attack Related =========================
    /**
     * Purpose: Updates projectile position each tick, processes collisions from projectiles
     * @param levelWidth the end of the level
     * @param delta engine-defined time keeping
     */
    private void updateProjectiles(float levelWidth, float levelHeight, float delta){
        for (Projectile proj : projectiles){
            if (proj.canDestroy()){
                projectileRemove(proj);
            }
//            System.out.println(proj.getType());
            proj.update(levelWidth, levelHeight, delta);
            //Check if colliding with a platform
            for (Platforms platform : platforms){
                if (proj.isColliding(platform.getHitBox())){
                    proj.setVelocity(0, 0);
                    if (proj.getType() == Enum.BOMB){
                        proj.setDerezTimer(true);
                    }
                }
            }
            //Check if Projectile is colliding with an enemy
            for (Enemy enemy : enemies){
                if(proj.getAlignment() == Alignment.ENEMY) continue;
                if (proj.isColliding(enemy.getHitBox()) && proj.getType() == Enum.BOMB){
                    proj.setVelocity(0, 0);
                    proj.setAttached(enemy);
                    proj.setDerezTimer(true);
                }
                else if (proj.isColliding(enemy.getHitBox())){
                    enemy.takeDamage(proj.getDamage());
                    proj.setDestroy(true);
                }
            }
            if(proj.isColliding(cole.getHitBox()) && proj.getAlignment() == Alignment.ENEMY){
                if(!cole.getInvincibility()){
                    cole.takeDamage(proj.getDamage());
                    cole.setInvincibility(true);
                }
                proj.setDestroy(true);
            }

        }
    }

    /**
     * Purpose: Removes projectiles from the vector, if proj isExplosive,
     * then create explosive projectile first.
     * @param proj
     */
    private void projectileRemove(Projectile proj){
        //If an explosive, create temporary bullet
        TextureRegion[][] spriesheet;
        if(cole.getCurrentAttack() == "Bomb"){
            spriesheet = mainScreenTextures.bombSpriteSheet;
        }
        else if(cole.getCurrentAttack() == "Torpedo"){
            spriesheet = mainScreenTextures.torpedoSpriteSheet;
        }
        else{
            spriesheet = mainScreenTextures.bulletSpriteSheet;
        }

        if (proj.isIsExplosive()){
            projectiles.add(new Projectile(proj.getX(), proj.getY(), Alignment.PLAYER,
                    EXPLOSIVE_RADIUS, EXPLOSIVE_RADIUS, 1, cole.getVelocity().x, Enum.EXPLOSION, spriesheet));
        }
        projectiles.removeValue(proj, true);
    }

    /**
     * Purpose: Adds projectile to vector with specified properties from the ``cole``  instance.
     */
    private void createProjectile(Alignment alignment, boolean facing_direction, Rectangle shooter){
        int direction = -1;
        if (alignment == Alignment.PLAYER){
            if (!cole.getIsFacingRight()){
                direction = 1;
            }

            //Assign Projectile Characteristics
            Enum attackIndex = Enum.fromInteger(cole.getAttackIndex());
            int projWidth = 0;
            int projHeight = 0;
            float projVel = Math.abs(cole.getVelocity().x);
            switch(attackIndex){
                case BOLT:
                    projWidth = BOLT_WIDTH;
                    projHeight = BOLT_HEIGHT;
                    projVel += BOLT_SPEED;
                    break;
                case BOMB:
                    projWidth = BOMB_WIDTH;
                    projHeight =  BOMB_HEIGHT;
                    projVel += BOMB_SPEED;
                    break;
                case TORPEDO:
                    projWidth = TORPEDO_WIDTH;
                    projWidth = TORPEDO_HEIGHT;
                    projVel += TORPEDO_SPEED;
                    break;
            }

            if (cole.isCanMelee()){
                projectiles.add(new Projectile(cole.getIsFacingRight() ? cole.getX() : cole.getX() + cole.getWidth(), cole.getY() + cole.getHeight() * (2/3f), Alignment.PLAYER,
                        (int)cole.getHitBox().width, (int)cole.getHitBox().height, direction, cole.getVelocity().x));
            }
            else if (Enum.fromInteger(cole.getAttackIndex()) == Enum.BOMB){
                projectiles.add(new Bomb(cole.getIsFacingRight() ? cole.getX() : cole.getX() + cole.getWidth(), cole.getY() + cole.getHeight() * (2/3f), Alignment.PLAYER,
                        projWidth, projHeight, direction, projVel, Enum.fromInteger(cole.getAttackIndex()), mainScreenTextures.bombSpriteSheet));
            }
            else {
                projectiles.add(new Projectile(cole.getIsFacingRight() ? cole.getX() : cole.getX() + cole.getWidth(), cole.getY() + cole.getHeight() * (2/3f), Alignment.PLAYER,
                        projWidth, projHeight, direction, projVel, Enum.fromInteger(cole.getAttackIndex()), mainScreenTextures.bulletSpriteSheet));
            }
            cole.setIsAttacking(false);
            cole.resetAttackTimer();
        } else {
            float bulletX = shooter.x-5;
            float bulletY = shooter.y+shooter.height*(2/3f);
            if(facing_direction) {
                direction = 1;
                bulletX += shooter.width+5;
            }
            System.out.println("direction"+direction);
            projectiles.add(new Projectile(bulletX, bulletY, Alignment.ENEMY, 5,5, direction, 1f, Enum.BULLET, mainScreenTextures.bulletSpriteSheet));
        }
    }
    /**
     * Purpose: Update enemy pathing
     */
    public void updateEnemies(float delta){
        Array<Enemy> removeEnemies = new Array<>();

        for(Enemy enemy : enemies){
            //Removes enemies on death
            if (enemy.getCurrentHealth() < 0){
                enemies.removeValue(enemy, true);
            }
            enemy.update(delta);
            enemy.setCombat(cole);
            enemy.nearDetector(cole);
            if(enemy.shootBullet){
                enemy.shootBullet = false;
                createProjectile(Alignment.ENEMY, enemy.isFacingRight, enemy.getHitBox());
            }
            if(enemy.getCurrentHealth() < 0){
                removeEnemies.add(enemy);
            }
        }

        for(Enemy enemy : removeEnemies){
            if(enemy.finishedDying()){enemies.removeValue(enemy, true);}
        }
    }

    /**
     * Purpose: Checks if cole is dead yet, then reloads checkpoint if possible (or back to start)
     */
    public void updateIfDead(){
        if (cole.getCurrentHealth() < 0){
            if (this.isCheckpointed){
                infamous.setScreen(new MainScreen(infamous, tiledSelection, this.checkpoint));
            }
            //Reload screen if died with no checkpoint
            else {
                infamous.setScreen(new MainScreen(infamous, tiledSelection));
            }
        }
    }

    /**
     * Purpose: Resize the menuStage viewport in case the screen gets resized (Desktop)
     *          Moving the camera if that's part of the game
     */
    public void updateCamera() {
        if(cole.getX() < xCameraDelta - WORLD_WIDTH/2f){
            camera.position.set(cole.getX(), camera.position.y, camera.position.z);
            camera.update();
            tiledSetUp.updateCamera(camera);
        }
        //Updates Camera if the X positions has changed
        if((cole.getX() > WORLD_WIDTH/2f) && (cole.getX() < tiledSetUp.getLevelWidth() - WORLD_WIDTH/2f)) {
            camera.position.set(cole.getX(), camera.position.y, camera.position.z);
            camera.update();
            tiledSetUp.updateCamera(camera);
        }

        //Updates the Camera if the Y positions has changed
        if(cole.getY() >  WORLD_HEIGHT / 2){
            camera.position.set(camera.position.x, cole.getY(), camera.position.z);
            camera.update();
            tiledSetUp.updateCamera(camera);
        }

        //Updates the change of camera to keep the UI moving with the player
        xCameraDelta = camera.position.x - WORLD_WIDTH/2f;
        yCameraDelta = camera.position.y - WORLD_HEIGHT/2f;
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

        //======================== Draws Titled =============================


        //======================== Draws ==============================

        batch.begin();
        drawBackground();
        batch.end();

        tiledSetUp.drawTiledMap();

        batch.begin();
        if(developerMode){debugInfo();}        //If dev mode is on draw hit boxes and phone stats
        for (DrainableObject drainable : drainables){ drainable.draw(batch); }
        for(Collectible collectible : collectibles) {collectible.draw(batch);}
        for(EndShard endShard : endShards){endShard.draw(batch);}
        for(Civilian civilian : civilians){civilian.draw(batch);}
        for(CheckpointObject checkpoint : checkpoints){checkpoint.draw(batch);}
        cole.drawAnimations(batch);
        drawAction();
        for(Projectile projectile : projectiles){projectile.drawAnimation(batch);}
        for(Water water : waters){water.draw(batch);}
        for(Enemy enemy : enemies){enemy.drawAnimations(batch);}
        batch.end();


        //=================== Draws the Menu Background =====================
        drawUIBackground();
        drawHealthAndEnergy();

        batch.begin();
        drawUIText();
        drawCollectibleSum();
        drawPopUpMenu();

        //=================== Draws the Menu Buttons =========================
        if(pausedFlag && !skinFlag && !helpFlag && !endFlag){drawMainButtons();}

        //================= Draw Menu Button Text =============================
        if(skinFlag){ drawSkinsMenu(); }
        if(endFlag){drawEndLevelScreenMenu();}
        else if(pausedFlag && !helpFlag && !skinFlag){drawButtonText();}
        else if(pausedFlag){drawBackButtonText();}
        batch.end();
    }

    private void drawBackground(){
        batch.draw(mainScreenTextures.backgroundColor, xCameraDelta, yCameraDelta);
        for(int i = 0; i < tiledSetUp.getLevelWidth()/WORLD_WIDTH + 1; i++){
            batch.draw(mainScreenTextures.backgroundBack, xCameraDelta - xCameraDelta * 0.1f + WORLD_WIDTH *i, yCameraDelta);
            batch.draw(mainScreenTextures.backgroundMid, xCameraDelta - xCameraDelta * 0.2f + WORLD_WIDTH *i, yCameraDelta);
            batch.draw(mainScreenTextures.backgroundFront, xCameraDelta - xCameraDelta * 0.4f + WORLD_WIDTH *i, yCameraDelta);

        }
    }

    private void drawAction(){
        if((cole.getIsTouchingLedge() || cole.getIsTouchingPole()) && !cole.getIsClimbingPole() && !cole.getIsHangingLedge()){
            batch.draw(mainScreenTextures.eTexture, cole.getX() + mainScreenTextures.eTexture.getWidth()/2f,
                    cole.getY() + cole.getHeight() + 5 + mainScreenTextures.eTexture.getHeight()/2f  );
        }

        if(cole.getCanDrain() || isTouchingPerson){
            batch.draw(mainScreenTextures.eTexture, cole.getX() + 8 + mainScreenTextures.eTexture.getWidth()/2f,
                    cole.getY() + cole.getHeight() + 2 + mainScreenTextures.eTexture.getHeight()/2f  );
            batch.draw(mainScreenTextures.qTexture, cole.getX() - 8 + mainScreenTextures.qTexture.getWidth()/2f,
                    cole.getY() + cole.getHeight() + 2 + mainScreenTextures.qTexture.getHeight()/2f  );
        }

        bitmapFont.getData().scale(0.01f);
        if(isTouchingPerson){
            textAlignment.centerText(batch, bitmapFont, "Heal", cole.getX() + 28 + mainScreenTextures.eTexture.getWidth()/2f,
                    cole.getY() + cole.getHeight() + 10 + healText  + mainScreenTextures.eTexture.getHeight()/2f  );
            textAlignment.centerText(batch, bitmapFont, "Kill", cole.getX() - 15 + mainScreenTextures.qTexture.getWidth()/2f,
                    cole.getY() + cole.getHeight() + 10 + killText + mainScreenTextures.qTexture.getHeight()/2f  );
        }
    }

    /**
     * Purpose: Draws the menu background and instructions
     */
    private void drawPopUpMenu() {
        bitmapFont.getData().setScale(0.3f);
        if ((pausedFlag && !skinFlag) || endFlag || helpFlag) {
            batch.draw(mainScreenTextures.menuBackgroundTexture, xCameraDelta + WORLD_WIDTH / 2f - WORLD_WIDTH / 4, yCameraDelta + 10, WORLD_WIDTH / 2, WORLD_HEIGHT - 20);
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

        bitmapFont.getData().setScale(0.25f);
        textAlignment.centerText(batch, bitmapFont, cole.getCurrentAttack(), xCameraDelta + WORLD_WIDTH/2f + 50,yCameraDelta + WORLD_HEIGHT - 14);
    }

    /**
     * Purpose: Draws the Health, Energy bars and draws the Current Attack Box
     */
    private void drawHealthAndEnergy(){
        makeBar((int) cole.getCurrentHealth()/20, (int) cole.getMaxHealth()/20, Color.RED, 0);
        makeBar((int) cole.getCurrentEnergy()/20, (int) cole.getMaxEnergy()/20, Color.BLUE, 15);

        debugRendering.startBoarderRender();
        debugRendering.getShapeRendererBoarder().rect(xCameraDelta + WORLD_WIDTH/2f,  yCameraDelta + WORLD_HEIGHT - 27 , 20, 20);
        debugRendering.endBoarderRender();
    }

    private void drawCollectibleSum(){
        bitmapFont.getData().setScale(0.25f);

        //Bullet
        switch (cole.getCurrentAttack()) {
            case "Lighting Bolt":
                batch.draw(mainScreenTextures.bulletSpriteSheet[0][0], xCameraDelta + WORLD_WIDTH / 2f, yCameraDelta + WORLD_HEIGHT - 27, 20, 20);
                break;
            case "Bomb":
                batch.draw(mainScreenTextures.bombSpriteSheet[0][0], xCameraDelta + WORLD_WIDTH / 2f, yCameraDelta + WORLD_HEIGHT - 27, 20, 20);
                break;
            case "Torpedo":
                batch.draw(mainScreenTextures.torpedoSpriteSheet[0][0], xCameraDelta + WORLD_WIDTH / 2f, yCameraDelta + WORLD_HEIGHT - 27, 20, 20);
                break;
        }

        //============================= Shards
        batch.draw(mainScreenTextures.collectibleSpriteSheet[0][0], xCameraDelta + WORLD_WIDTH/2f + 90,  yCameraDelta + WORLD_HEIGHT - 15 , 8, 8);
        textAlignment.centerText(batch, bitmapFont, collectibleSum-collectibles.size + "/" + collectibleSum, xCameraDelta + WORLD_WIDTH/2f + 110,  yCameraDelta + WORLD_HEIGHT - 8 );

        //=============================== Heals ===============================
        batch.draw(mainScreenTextures.peopleUpSpriteSheet[0][0], xCameraDelta + WORLD_WIDTH/2f + 90,  yCameraDelta + WORLD_HEIGHT - 25 , 8, 8);
        textAlignment.centerText(batch, bitmapFont, healed + "/" + civilians.size, xCameraDelta + WORLD_WIDTH/2f + 110,  yCameraDelta + WORLD_HEIGHT - 18 );

        //========================= Kills ===============
        batch.draw(mainScreenTextures.peopleUpSpriteSheet[0][4], xCameraDelta + WORLD_WIDTH/2f + 90,  yCameraDelta + WORLD_HEIGHT - 35 , 8, 8);
        textAlignment.centerText(batch, bitmapFont, killed + "/" + civilians.size, xCameraDelta + WORLD_WIDTH/2f + 110,  yCameraDelta + WORLD_HEIGHT - 28 );

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
        batch.draw(mainScreenTextures.controlsTexture, xCameraDelta, yCameraDelta);
  }


    /**
     * Input: Void
     * Output: Void
     * Purpose: Draws the text for instructions
     */
    private void drawSkinsMenu() {
        bitmapFont.setColor(Color.WHITE);
        bitmapFont.getData().setScale(0.5f);

        batch.draw(mainScreenTextures.menuBackgroundTexture, xCameraDelta, yCameraDelta);

        textAlignment.centerText(batch, bitmapFont, "Lighting Skin Selection", xCameraDelta + WORLD_WIDTH/2f, yCameraDelta + WORLD_HEIGHT - 30);


        //============================= Draws the boxes Cole in them with different colors =========
        for(int i = 0; i < 4; i++){
            if(skinIndex == i){
                batch.draw(mainScreenTextures.buttonSpriteSheet[0][1], xCameraDelta + 12 + (50 + 10) * i, yCameraDelta + WORLD_HEIGHT/2f, 50, 50);
                batch.draw(mainScreenTextures.coleSpriteSheet[3][3], xCameraDelta + 38 -  25/2f  + (50 + 10) * i,
                        yCameraDelta  + WORLD_HEIGHT/2f + 25/2f, 25, 25);
                batch.draw(mainScreenTextures.meleeSpriteSheet[i][2],xCameraDelta + 47 -  25/2f  + (50 + 10) * i,
                        yCameraDelta + WORLD_HEIGHT/2f + 25/2f, 25, 25);
            }
            else{
                batch.draw(mainScreenTextures.buttonSpriteSheet[0][0], xCameraDelta + 12 + (50 + 10) * i, yCameraDelta + WORLD_HEIGHT/2f, 50, 50);
                batch.draw(mainScreenTextures.coleSpriteSheet[3][3], xCameraDelta + 38 -  25/2f  + (50 + 10) * i,
                        yCameraDelta + WORLD_HEIGHT/2f + 25/2f, 25, 25);
                batch.draw(mainScreenTextures.meleeSpriteSheet[i][2],xCameraDelta + 47 -  25/2f  + (50 + 10) * i,
                        yCameraDelta + WORLD_HEIGHT/2f + 25/2f, 25, 25);
            }

            //========================= Draws Locks on those that are no complete ==================
            bitmapFont.getData().setScale(0.3f);
            if(i == 1 && !infamous.getIfCollectibleComplete()){
                batch.draw(mainScreenTextures.lockTexture, xCameraDelta + 12 + (50 + 10) * i, yCameraDelta + WORLD_HEIGHT/2f, 50, 50);
                textAlignment.centerText(batch, bitmapFont, "Collect all", xCameraDelta + 12 + (50 + 31) * i, yCameraDelta + WORLD_HEIGHT/2f - 10);
                batch.draw(mainScreenTextures.collectibleSpriteSheet[0][0], xCameraDelta + 12 + (50 + 15) * i, yCameraDelta + WORLD_HEIGHT/2f - 50, 30, 30);
            }
            else if(i == 2 && !infamous.getIfHealsComplete()){
                batch.draw(mainScreenTextures.lockTexture, xCameraDelta + 12 + (50 + 10) * i, yCameraDelta + WORLD_HEIGHT/2f, 50, 50);
                textAlignment.centerText(batch, bitmapFont, "Heal all", xCameraDelta + 12 + (50 + 22) * i, yCameraDelta + WORLD_HEIGHT/2f - 10);
                batch.draw(mainScreenTextures.peopleUpSpriteSheet[0][0], xCameraDelta + 12 + (50 + 15) * i, yCameraDelta + WORLD_HEIGHT/2f - 50, 30, 30);
            }
            else if(i == 3 && !infamous.getIfKillsComplete()){
                batch.draw(mainScreenTextures.lockTexture, xCameraDelta + 12 + (50 + 10) * i, yCameraDelta + WORLD_HEIGHT/2f, 50, 50);
                textAlignment.centerText(batch, bitmapFont, "Kill all", xCameraDelta + 12 + (50 + 19) * i, yCameraDelta + WORLD_HEIGHT/2f - 10);
                batch.draw(mainScreenTextures.peopleUpSpriteSheet[0][4], xCameraDelta + 12 + (50 + 13) * i, yCameraDelta + WORLD_HEIGHT/2f - 50, 30, 30);
            }
        }

        //======================= Draws exit button ==========================================
        if(skinExit){
            batch.draw(mainScreenTextures.buttonSpriteSheet[0][1], xCameraDelta + WORLD_WIDTH / 2f - MENU_BUTTON_WIDTH / 2f, yCameraDelta + 10, MENU_BUTTON_WIDTH, MENU_BUTTON_HEIGHT);
        }
        else{
            batch.draw(mainScreenTextures.buttonSpriteSheet[0][0], xCameraDelta + WORLD_WIDTH / 2f - MENU_BUTTON_WIDTH / 2f, yCameraDelta + 10, MENU_BUTTON_WIDTH, MENU_BUTTON_HEIGHT);
        }
        bitmapFont.getData().setScale(0.3f);
        textAlignment.centerText(batch, bitmapFont, "Back", xCameraDelta + WORLD_WIDTH/2f, yCameraDelta + 27);
    }

    private void drawEndLevelScreenMenu(){
        bitmapFont.setColor(Color.WHITE);
        bitmapFont.getData().setScale(0.5f);

        batch.draw(mainScreenTextures.menuBackgroundTexture, xCameraDelta, yCameraDelta);

        textAlignment.centerText(batch, bitmapFont, "Level Complete!", xCameraDelta + WORLD_WIDTH/2f, yCameraDelta + WORLD_HEIGHT - 40);

        bitmapFont.getData().setScale(0.3f);
        textAlignment.centerText(batch, bitmapFont, "Blast Shards Collected: " + (collectibleSum-collectibles.size) + "/" + collectibleSum, xCameraDelta + WORLD_WIDTH/2f - 24, yCameraDelta + WORLD_HEIGHT - 80);
        textAlignment.centerText(batch, bitmapFont, "Civilians Healed: " + healed + "/" + civilians.size, xCameraDelta + WORLD_WIDTH/2f - 40, yCameraDelta + WORLD_HEIGHT - 110);
        textAlignment.centerText(batch, bitmapFont, "Civilians Killed: " + killed + "/" + civilians.size, xCameraDelta + WORLD_WIDTH/2f - 41, yCameraDelta + WORLD_HEIGHT - 140);

        if(infamous.getCollected(tiledSelection)){
            batch.draw(mainScreenTextures.collectibleSpriteSheet[0][0], xCameraDelta + WORLD_WIDTH/2f + 60, yCameraDelta + WORLD_HEIGHT - 90, 20, 20);
        }
        if(infamous.getHealed(tiledSelection)){
            batch.draw(mainScreenTextures.peopleUpSpriteSheet[0][0], xCameraDelta + WORLD_WIDTH/2f + 60, yCameraDelta + WORLD_HEIGHT - 120, 20, 20);
        }
        if(infamous.getKilled(tiledSelection)){
            batch.draw(mainScreenTextures.peopleUpSpriteSheet[0][4], xCameraDelta + WORLD_WIDTH/2f + 60, yCameraDelta + WORLD_HEIGHT - 150, 20, 20);
        }


        if(exitIndex == 0){
            batch.draw(mainScreenTextures.buttonSpriteSheet[0][1], xCameraDelta + WORLD_WIDTH / 2f - MENU_BUTTON_WIDTH / 2f - 50, yCameraDelta + 34, MENU_BUTTON_WIDTH, MENU_BUTTON_HEIGHT);
            batch.draw(mainScreenTextures.buttonSpriteSheet[0][0], xCameraDelta + WORLD_WIDTH / 2f - MENU_BUTTON_WIDTH / 2f + 50, yCameraDelta + 34, MENU_BUTTON_WIDTH, MENU_BUTTON_HEIGHT);
        }
        else if(exitIndex == 1){
            batch.draw(mainScreenTextures.buttonSpriteSheet[0][0], xCameraDelta + WORLD_WIDTH / 2f - MENU_BUTTON_WIDTH / 2f - 50, yCameraDelta + 34, MENU_BUTTON_WIDTH, MENU_BUTTON_HEIGHT);
            batch.draw(mainScreenTextures.buttonSpriteSheet[0][1], xCameraDelta + WORLD_WIDTH / 2f - MENU_BUTTON_WIDTH / 2f + 50, yCameraDelta + 34, MENU_BUTTON_WIDTH, MENU_BUTTON_HEIGHT);
        }

        textAlignment.centerText(batch, bitmapFont, "Main Menu", xCameraDelta + WORLD_WIDTH/2f - 50, yCameraDelta + 51);
        textAlignment.centerText(batch, bitmapFont, "Continue", xCameraDelta + WORLD_WIDTH/2f + 50, yCameraDelta + 51);


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
            if (i == 2 && musicControl.getSFXVolume() == 0) { string = menuButtonText[menuButtonText.length - 1]; }
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
        Gdx.gl.glClearColor(Color.CLEAR.r, Color.CLEAR.g, Color.CLEAR.b, Color.CLEAR.a); //Sets color to black
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);										 //Sends it to the buffer
    }

    /**
     * Purpose: Destroys everything once we move onto the new screen
    */
    @Override
    public void dispose() {
    }
}
