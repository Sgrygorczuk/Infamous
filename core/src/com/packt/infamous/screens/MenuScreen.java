package com.packt.infamous.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.packt.infamous.main.Infamous;
import com.packt.infamous.screens.textures.MenuScreenTextures;
import com.packt.infamous.tools.MusicControl;
import com.packt.infamous.tools.TextAlignment;

import static com.packt.infamous.Const.INSTRUCTIONS_Y_START;
import static com.packt.infamous.Const.MENU_BUTTON_FONT;
import static com.packt.infamous.Const.MENU_BUTTON_HEIGHT;
import static com.packt.infamous.Const.MENU_BUTTON_WIDTH;
import static com.packt.infamous.Const.TEXT_OFFSET;
import static com.packt.infamous.Const.WORLD_HEIGHT;
import static com.packt.infamous.Const.WORLD_WIDTH;

//TODO : Since we're following NES no mouse controls for the buttons so we need to rework those
//TODO cont: to use WASD/Arrow Keys

public class MenuScreen extends ScreenAdapter{

    //=========================================== Variable =========================================

    //================================== Image processing ===========================================
    private SpriteBatch batch = new SpriteBatch();
    private Viewport viewport;
    private Camera camera;

    //=================================== Buttons ==================================================
    private Stage menuStage;
    private ImageButton[] menuButtons;
    private ImageButton backButton;

    //===================================== Tools ==================================================
    private MusicControl musicControl;
    private MenuScreenTextures menuScreenTextures;
    private final Infamous infamous;
    private final TextAlignment textAlignment = new TextAlignment();

    //=========================================== Text =============================================
    //Font used for the user interaction
    private BitmapFont bitmapFont = new BitmapFont();

    //============================================= Flags ==========================================
    private boolean helpFlag;      //Tells if help menu is up or not
    private boolean creditsFlag;   //Tells if credits menu is up or not

    //=================================== Miscellaneous Vars =======================================
    //String used on the buttons
    private final String[] buttonText = new String[]{"Play", "Help", "Credits"};
    private float backButtonY = 10;

    //================================ Set Up ======================================================

    /**
     * Purpose: Grabs the info from main screen that holds asset manager
     * Input: Infamous
     */
    MenuScreen(Infamous infamous) { this.infamous = infamous;}

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
        showCamera();           //Sets up camera through which objects are draw through
        menuScreenTextures = new MenuScreenTextures();
        showButtons();          //Sets up the buttons
        showObjects();          //Sets up the font
        musicControl.showMusic(0);
    }

    /**
     * Purpose: Sets up the camera through which all the objects are view through
     */
    private void showCamera(){
        camera = new OrthographicCamera();
        camera.position.set(WORLD_WIDTH / 2, WORLD_HEIGHT / 2, 0);
        camera.update();
        viewport = new StretchViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);
    }

    /**
     * Purpose: Sets up the button
     */
    private void showButtons(){
        menuStage = new Stage(new StretchViewport(WORLD_WIDTH, WORLD_HEIGHT));
        Gdx.input.setInputProcessor(menuStage); //Give power to the menuStage

        setUpMainButtons(); //Places the three main Play|Help|Credits buttons on the screen
        setUpExitButton();  //Palaces the exit button that leaves the Help and Credits menus
    }

    /**
     * Purpose: Sets main three main Play|Help|Credits buttons on the screen
    */
    private void setUpMainButtons(){
        //Set up all the buttons used by the stage
        menuButtons = new ImageButton[3];

        //Get the textures of the buttons
        Texture menuButtonTexturePath = new Texture(Gdx.files.internal("UI/Button.png"));
        TextureRegion[][] buttonSpriteSheet = new TextureRegion(menuButtonTexturePath).split(
                menuButtonTexturePath.getWidth()/2, menuButtonTexturePath.getHeight());

        //Places the three main Play|Help|Credits buttons on the screen
        for(int i = 0; i < 3; i ++){
            menuButtons[i] =  new ImageButton(new TextureRegionDrawable(buttonSpriteSheet[0][0]), new TextureRegionDrawable(buttonSpriteSheet[0][1]));
            menuButtons[i].setPosition(20, 2*WORLD_HEIGHT/3 - (MENU_BUTTON_HEIGHT + 10) * i);
            menuButtons[i].setWidth(MENU_BUTTON_WIDTH);
            menuButtons[i].setHeight(MENU_BUTTON_HEIGHT);
            menuStage.addActor(menuButtons[i]);

            final int finalI = i;
            menuButtons[i].addListener(new ActorGestureListener() {
                @Override
                public void tap(InputEvent event, float x, float y, int count, int button) {
                    super.tap(event, x, y, count, button);
                    musicControl.playSFX(0);
                    //Launches the game
                    if(finalI == 0){
                        musicControl.playSFX(0);
                        infamous.setScreen(new LoadingScreen(infamous, 1));
                    }
                    //Turns on the help menu
                    else if(finalI == 1){
                        for (ImageButton imageButton : menuButtons) { imageButton.setVisible(false); }
                        helpFlag = true;
                        backButton.setVisible(true);
                        //The button moves to different place for help and credits menu
                        backButtonY = 40;
                        backButton.setPosition(WORLD_WIDTH/2f - MENU_BUTTON_WIDTH/2f, backButtonY);
                    }
                    //Turns on the credits menu
                    else{
                        for (ImageButton imageButton : menuButtons) { imageButton.setVisible(false); }
                        creditsFlag = true;
                        backButton.setVisible(true);
                        //The button moves to different place for help and credits menu
                        backButtonY = 10;
                        backButton.setPosition(WORLD_WIDTH/2f - MENU_BUTTON_WIDTH/2f, backButtonY);
                    }
                }
            });
        }
    }

    /**
     * Purpose: Turn off the credits or heap menu
     */
    private void setUpExitButton(){
        //Sets up the texture
        Texture menuButtonTexturePath = new Texture(Gdx.files.internal("UI/Button.png"));
        TextureRegion[][] buttonSpriteSheet = new TextureRegion(menuButtonTexturePath).split(
                menuButtonTexturePath.getWidth()/2, menuButtonTexturePath.getHeight());

        //Sets up the position
        backButton = new ImageButton(new TextureRegionDrawable(buttonSpriteSheet[0][0]), new TextureRegionDrawable(buttonSpriteSheet[0][1]));
        backButton.setPosition(WORLD_WIDTH/2f - MENU_BUTTON_WIDTH/2f, backButtonY);
        backButton.setWidth(MENU_BUTTON_WIDTH);
        backButton.setHeight(MENU_BUTTON_HEIGHT);
        menuStage.addActor(backButton);
        backButton.setVisible(false);
        //Sets up to turn of the help menu if clicked
        backButton.addListener(new ActorGestureListener() {
            @Override
            public void tap(InputEvent event, float x, float y, int count, int button) {
                super.tap(event, x, y, count, button);
                musicControl.playSFX(0);
                helpFlag = false;
                creditsFlag = false;
                //Turn on all buttons but turn off this one
                for (ImageButton imageButton : menuButtons) {
                    imageButton.setVisible(true);
                }
                backButton.setVisible(false);
            }
        });
    }

    /**
     * Purpose: Sets up objects such as debugger, musicControl, fonts and others
     */
    private void showObjects(){
        musicControl = new MusicControl(infamous.getAssetManager());

        if(infamous.getAssetManager().isLoaded("Fonts/Font.fnt")){bitmapFont = infamous.getAssetManager().get("Fonts/Font.fnt");}
        bitmapFont.getData().setScale(0.6f);
        bitmapFont.setColor(Color.BLACK);
    }

    //=================================== Execute Time Methods =====================================

    /**
     Purpose: Central function for the game, everything that updates run through this function
     */
    @Override
    public void render(float delta) {
        update(delta);       //Update the variables
        draw();
    }

    /**
     Purpose: Updates all the moving components and game variables
     Input: @delta - timing variable
     */
    private void update(float delta){
        updateCamera();
    }


    /**
     * Purpose: Resize the menuStage viewport in case the screen gets resized (Desktop)
     *          Moving the camera if that's part of the game
     */
    public void updateCamera() {
        //Resize the menu Stage if the screen changes size
        menuStage.getViewport().update(viewport.getScreenWidth(), viewport.getScreenHeight(), true);
    }

    //========================================== Drawing ===========================================

    /**
     * Purpose: Central drawing function, from here everything gets drawn
     */
    private void draw() {
        clearScreen();
        //Viewport/Camera projection
        batch.setProjectionMatrix(camera.projection);
        batch.setTransformMatrix(camera.view);
        //Batch setting up texture before drawing buttons
        batch.begin();
        batch.draw(menuScreenTextures.backgroundTexture, 0, 0, WORLD_WIDTH, WORLD_HEIGHT);
        //Draw the pop up menu
        if(helpFlag  || creditsFlag){batch.draw(menuScreenTextures.menuBackgroundTexture, 10, 10, WORLD_WIDTH - 20, WORLD_HEIGHT-20);}
        batch.end();

        menuStage.draw(); // Draws the buttons

        batch.begin();
        //Draws the Play|Help|Credits text on buttons
        if(!helpFlag && !creditsFlag){drawButtonText();}
        //Draws the Help Text
        else if(helpFlag){
            drawInstructions();
            drawBackButtonText();
        }
        //Draws the credits text
        else{
            drawCredits();
            drawBackButtonText();
        }
        batch.end();
    }

    /**
     *  Purpose: Clear the screen
    */
    private void clearScreen() {
        Gdx.gl.glClearColor(Color.BLACK.r, Color.BLACK.g, Color.BLACK.b, Color.BLACK.a);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    }

    /**
     * Purpose: Draws the text on the Play|Help|Credits buttons
    */
    private void drawButtonText(){
        bitmapFont.getData().setScale(MENU_BUTTON_FONT);
        for(int i = 0; i < 3; i ++) {
            textAlignment.centerText(batch, bitmapFont, buttonText[i], 20 + MENU_BUTTON_WIDTH/2f,
                    2*WORLD_HEIGHT/3 + 0.65f * MENU_BUTTON_HEIGHT - (MENU_BUTTON_HEIGHT + 10) * i);
        }
    }

    /**
     * Purpose: Draws the backButton text
     */
    protected void drawBackButtonText(){
        bitmapFont.getData().setScale(MENU_BUTTON_FONT);
        textAlignment.centerText(batch, bitmapFont, "Back", WORLD_WIDTH/2f,
                backButtonY + 0.65f * MENU_BUTTON_HEIGHT);
    }

    /**
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
     *  Purpose: Draws the credits screen
    */
    private void drawCredits(){
        //Title
        bitmapFont.getData().setScale(0.5f);
        textAlignment.centerText(batch, bitmapFont, "Credits", WORLD_WIDTH/2f, WORLD_HEIGHT-45);
        bitmapFont.getData().setScale(0.32f);

        textAlignment.centerText(batch, bitmapFont, "Programming & Art", WORLD_WIDTH/2f, WORLD_HEIGHT - 80);
        textAlignment.centerText(batch, bitmapFont, "########", WORLD_WIDTH/2f, WORLD_HEIGHT - 95);

        textAlignment.centerText(batch, bitmapFont, "Music", WORLD_WIDTH/2f, WORLD_HEIGHT - 125);
        textAlignment.centerText(batch, bitmapFont, "########", WORLD_WIDTH/2f, WORLD_HEIGHT - 140);

        textAlignment.centerText(batch, bitmapFont, "SFX - ########", WORLD_WIDTH/2f, WORLD_HEIGHT - 170);
        textAlignment.centerText(batch, bitmapFont, "########", WORLD_WIDTH/2f - 120, WORLD_HEIGHT - 190);
        textAlignment.centerText(batch, bitmapFont, "########", WORLD_WIDTH/2f, WORLD_HEIGHT - 190);
        textAlignment.centerText(batch, bitmapFont, "########", WORLD_WIDTH/2f + 120, WORLD_HEIGHT - 190);

        textAlignment.centerText(batch, bitmapFont, "Font - ########", WORLD_WIDTH/2f, WORLD_HEIGHT - 255);
        textAlignment.centerText(batch, bitmapFont, "########", WORLD_WIDTH/2f, WORLD_HEIGHT - 275);
    }

    /**
     * Purpose: Gets rid of all visuals
    */
    @Override
    public void dispose() {
        menuStage.dispose();
    }
}