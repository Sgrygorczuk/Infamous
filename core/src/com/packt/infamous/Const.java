package com.packt.infamous;


public final class Const{

    //====================== Dimensions =========================
    public static final float WORLD_WIDTH = 256;
    public static final float WORLD_HEIGHT = 240;

    //====================== Logo Dimensions ====================
    public static final float LOGO_WIDTH = 141 * WORLD_WIDTH/480;
    public static final float LOGO_HEIGHT = 128 * WORLD_HEIGHT/320;

    //==================== Loading Bar Dimension ==============
    public static final float LOADING_Y = 10;
    public static final float LOADING_WIDTH = 3 * WORLD_WIDTH/4f;
    public static final float LOADING_HEIGHT = 30;
    public static final float LOADING_OFFSET = 5;

    //====================== Dev Text =========================
    public static final float DEVELOPER_TEXT_X = 20;
    public static final float DEVELOPER_TEXT_Y = 300 * WORLD_HEIGHT/320;

    //====================== Instruction/Help =========================
    public static final float INSTRUCTIONS_Y_START = 230 * WORLD_HEIGHT/320;
    public static final float INSTRUCTION_BUTTON_Y = 70 * WORLD_HEIGHT/320;

    //====================== General Text =========================
    public static final float TEXT_OFFSET = 20;

    //====================== Menu Buttons =========================
    public static final int NUM_BUTTONS_MENU_SCREEN = 3;
    public static final int NUM_BUTTONS_MAIN_SCREEN = 4;
    public static final float MENU_BUTTON_Y_START = 240 * WORLD_HEIGHT/320;
    public static final float MENU_BUTTON_WIDTH = 150 * WORLD_WIDTH/480;
    public static final float MENU_BUTTON_HEIGHT = 40f * WORLD_HEIGHT/320;
    public static final float MENU_BUTTON_FONT = 0.5f * WORLD_HEIGHT/320;


    //========================= Tile Data ================================
    public static final float TILED_WIDTH = 16;
    public static final float TILED_HEIGHT = 16;
    public static final float COLE_WIDTH = 16;
    public static final float COLE_HEIGHT = 32;

    public static final float UI_HEIGHT = 32;

    //======================== Movement Data ===========================
    public static final float GRAVITY = 2f;
    public static final float JUMP_PEAK = 30f;
    public static final float ACCELERATION = 1f;
    public static final float FRICTION = 0.5f;
    public static final float MAX_VELOCITY = 3f;

    //======================== Weapon Related ===========================
    public static final float BOLT_SPEED = 2f;
    public static final int BOLT_DAMAGE = 1;

    public static final int EXPLOSIVE_DAMAGE = 5;
    public static final int EXPLOSIVE_RADIUS = 15;
    public static final float EXPLOSIVE_LINGER = 0.5f;

    public static final float PROJ_TIME = 0.5f;


}