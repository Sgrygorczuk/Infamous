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
import static com.packt.infamous.Const.NUM_BUTTONS_MENU_SCREEN;
import static com.packt.infamous.Const.TEXT_OFFSET;
import static com.packt.infamous.Const.WORLD_HEIGHT;
import static com.packt.infamous.Const.WORLD_WIDTH;

public class MenuScreen extends ScreenAdapter{

    //=========================================== Variable =========================================

    //================================== Image processing ===========================================
    private SpriteBatch batch = new SpriteBatch();
    private Viewport viewport;
    private Camera camera;

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
    private boolean levelSelectFlag;   //Tells if credits menu is up or not

    //=================================== Miscellaneous Vars =======================================
    //String used on the buttons
    private final String[] buttonText = new String[]{"Play", "Level Select", "Controls"};
    private float backButtonY = 10;
    private int buttonIndex = 0;    //Tells us which button we're currently looking at

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
        update();       //Update the variables
        draw();
    }

    /**
     Purpose: Updates all the moving components and game variables
     */
    private void update() { inputHandling(); }

    /**
     * Purpose: Allow user to navigate the menus
     */
    private void inputHandling(){
        if(!helpFlag) {
            //Movement Vertically
            if (Gdx.input.isKeyJustPressed(Input.Keys.W) || Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
                buttonIndex--;
                if (buttonIndex <= -1) {
                    buttonIndex = NUM_BUTTONS_MENU_SCREEN - 1;
                }
            }

            if (Gdx.input.isKeyJustPressed(Input.Keys.S) || Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
                buttonIndex++;
                if (buttonIndex >= NUM_BUTTONS_MENU_SCREEN) {
                    buttonIndex = 0;
                }
            }

            if (Gdx.input.isKeyJustPressed(Input.Keys.E)) {
                //Launches the game
                if (buttonIndex == 0) {
                    musicControl.playSFX(0);
                    infamous.setScreen(new LoadingScreen(infamous, 1, 0));
                }
                else if(buttonIndex == 1){
                    levelSelectFlag = true;
                }
                //Turns on the help menu
                else if (buttonIndex == 2) {
                    helpFlag = true;
                }
            }
        }
        else if(helpFlag){
            if (Gdx.input.isKeyJustPressed(Input.Keys.E) || Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
                helpFlag = false;
                backButtonY = 10;
            }
        }
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
        //Draws the Play|Help|Credits text on buttons
        if(!helpFlag){
            drawMainButtons();
            drawButtonText();
        }
        else if(helpFlag){
            drawBackButton();
            drawInstructions();
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
     * Purpose: Draws the main buttons on the screen
     */
    private void drawMainButtons(){
        for(int i = 0; i < NUM_BUTTONS_MENU_SCREEN; i++){
            if(i == buttonIndex){
                batch.draw(menuScreenTextures.buttonSpriteSheet[0][1], 10, 2*WORLD_HEIGHT/3 - 15 - (MENU_BUTTON_HEIGHT + 15) * i, MENU_BUTTON_WIDTH, MENU_BUTTON_HEIGHT);
            }
            else{
                batch.draw(menuScreenTextures.buttonSpriteSheet[0][0], 10, 2*WORLD_HEIGHT/3 - 15 - (MENU_BUTTON_HEIGHT + 15) * i, MENU_BUTTON_WIDTH, MENU_BUTTON_HEIGHT);
            }
        }
    }

    private void drawBackButton(){
        batch.draw(menuScreenTextures.buttonSpriteSheet[0][1], WORLD_WIDTH/2f - menuScreenTextures.buttonSpriteSheet[0][1].getRegionWidth()/2f - 8, backButtonY, MENU_BUTTON_WIDTH, MENU_BUTTON_HEIGHT);

    }

    /**
     * Purpose: Draws the text on the Play|Help|Credits buttons
    */
    private void drawButtonText(){
        bitmapFont.getData().setScale(MENU_BUTTON_FONT);
        for(int i = 0; i < NUM_BUTTONS_MENU_SCREEN; i ++) {
            textAlignment.centerText(batch, bitmapFont, buttonText[i], 10 + MENU_BUTTON_WIDTH/2f,
                    2*WORLD_HEIGHT/3 + 0.65f * MENU_BUTTON_HEIGHT - 15 - (MENU_BUTTON_HEIGHT + 15) * i);
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
        batch.draw(menuScreenTextures.controlsTexture, 0, 0);
    }


    /**
     * Purpose: Gets rid of all visuals
    */
    @Override
    public void dispose() {
    }
}