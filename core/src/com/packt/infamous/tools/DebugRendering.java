package com.packt.infamous.tools;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class DebugRendering {

    //=================================== Variable =================================================
    private ShapeRenderer shapeRendererEnemy;       //Creates the wire frames for enemies
    private ShapeRenderer shapeRendererUser;        //Creates the wire frame for user
    private ShapeRenderer shapeRendererBackground;  //Creates the wire frame for background objects
    private ShapeRenderer shapeRendererCollectible; //Creates wireframe for collectibles
    private ShapeRenderer shapeRenderUI;
    private ShapeRenderer shapeRenderBoarder;
    private ShapeRenderer shapeRendererBarFill;

    private final Camera camera;    //To set the view for the renders to follow 

    //Initial drawing style for the shapes
    private ShapeRenderer.ShapeType[] shapeTypes = new ShapeRenderer.ShapeType[]{
            ShapeRenderer.ShapeType.Line, ShapeRenderer.ShapeType.Line, ShapeRenderer.ShapeType.Line,
            ShapeRenderer.ShapeType.Line, ShapeRenderer.ShapeType.Filled, ShapeRenderer.ShapeType.Line,
            ShapeRenderer.ShapeType.Filled};
    

    //================================= Constructor ================================================
    public DebugRendering(Camera camera){
        this.camera = camera;
        showRender();
    }


    //==================================== Methods =================================================
    /**
    Input: Void
    Output: Void
    Purpose: Sets up the different renders to draw objects in wireframe
    */
    public void showRender(){
        //Enemy
        shapeRendererEnemy = new ShapeRenderer();
        shapeRendererEnemy.setColor(Color.RED);

        //User
        shapeRendererUser = new ShapeRenderer();
        shapeRendererUser.setColor(Color.GREEN);

        //Background
        shapeRendererBackground = new ShapeRenderer();
        shapeRendererBackground.setColor(Color.WHITE);

        //Intractable
        shapeRendererCollectible = new ShapeRenderer();
        shapeRendererCollectible.setColor(Color.BLUE);

        //UI
        shapeRenderUI = new ShapeRenderer();
        shapeRenderUI.setColor(Color.BLACK);

        //Bar Boarder
        shapeRenderBoarder = new ShapeRenderer();
        shapeRenderBoarder.setColor(Color.WHITE);

        //Bar Fill
        shapeRendererBarFill = new ShapeRenderer();
        shapeRendererBarFill.setColor(Color.RED);
    }

    /**
    Purpose: Sets up rendering method to begin drawing the enemy hitboxes 
    Input: Void
    Output: Void
    */
    public void startEnemyRender(){
        shapeRendererEnemy.setProjectionMatrix(camera.projection);   
        shapeRendererEnemy.setTransformMatrix(camera.view);         
        shapeRendererEnemy.begin(shapeTypes[0]);
    }
    
    /**
     Purpose: Ends rendering method to begin drawing the enemy hitboxes 
     Input: Void
     Output: Void
     */
    public void endEnemyRender(){ shapeRendererEnemy.end(); }

    /**
     Purpose: Sets up rendering method to begin drawing the user hitboxes 
     Input: Void
     Output: Void
     */
    public void startUserRender(){
        shapeRendererUser.setProjectionMatrix(camera.projection); 
        shapeRendererUser.setTransformMatrix(camera.view);       
        shapeRendererUser.begin(shapeTypes[1]);
    }

    /**
     Purpose: Ends rendering method to begin drawing the user hitboxes 
     Input: Void
     Output: Void
     */
    public void endUserRender(){ shapeRendererUser.end(); }


    /**
     Purpose: Ends rendering method to begin drawing the background hitboxes 
     Input: Void
     Output: Void
     */
    public void startBackgroundRender(){
        shapeRendererBackground.setProjectionMatrix(camera.projection);       
        shapeRendererBackground.setTransformMatrix(camera.view);                       
        shapeRendererBackground.begin(shapeTypes[2]);
    }

    /**
     Purpose: Ends rendering method to begin drawing the background hitboxes 
     Input: Void
     Output: Void
     */
    public void endBackgroundRender(){ shapeRendererBackground.end(); }

    /**
     Purpose: Ends rendering method to begin drawing the collectible hitboxes 
     Input: Void
     Output: Void
     */
    public void startCollectibleRender(){
        shapeRendererCollectible.setProjectionMatrix(camera.projection);
        shapeRendererCollectible.setTransformMatrix(camera.view);
        shapeRendererCollectible.begin(shapeTypes[3]);
    }

    /**
     Purpose: Ends rendering method to begin drawing the collectibles hitboxes 
     Input: Void
     Output: Void
     */
    public void endCollectibleRender(){ shapeRendererCollectible.end(); }

    /**
     Purpose: Ends rendering method to begin drawing the collectible hitboxes
     Input: Void
     Output: Void
     */
    public void startUIRender(){
        shapeRenderUI.setProjectionMatrix(camera.projection);
        shapeRenderUI.setTransformMatrix(camera.view);
        shapeRenderUI.begin(shapeTypes[4]);
    }

    /**
     Purpose: Ends rendering method to begin drawing the collectibles hitboxes
     Input: Void
     Output: Void
     */
    public void endUIRender(){ shapeRenderUI.end(); }

    /**
     Purpose: Ends rendering method to begin drawing the collectible hitboxes
     Input: Void
     Output: Void
     */
    public void startBoarderRender(){
        shapeRenderBoarder.setProjectionMatrix(camera.projection);
        shapeRenderBoarder.setTransformMatrix(camera.view);
        shapeRenderBoarder.begin(shapeTypes[5]);
    }

    /**
     Purpose: Ends rendering method to begin drawing the collectibles hitboxes
     Input: Void
     Output: Void
     */
    public void endBoarderRender(){ shapeRenderBoarder.end(); }

    /**
     Purpose: Ends rendering method to begin drawing the collectible hitboxes
     Input: Void
     Output: Void
     */
    public void startBarFillRender(){
        shapeRendererBarFill.setProjectionMatrix(camera.projection);
        shapeRendererBarFill.setTransformMatrix(camera.view);
        shapeRendererBarFill.begin(shapeTypes[6]);
    }

    /**
     Purpose: Ends rendering method to begin drawing the collectibles hitboxes
     Input: Void
     Output: Void
     */
    public void endBarFillRender(){ shapeRenderUI.end(); }

    public ShapeRenderer getShapeRenderEnemy(){return shapeRendererEnemy;}
    public ShapeRenderer getShapeRendererUser(){return shapeRendererUser;}
    public ShapeRenderer getShapeRendererBackground(){return shapeRendererBackground;}
    public ShapeRenderer getShapeRendererCollectible(){return shapeRendererCollectible;}
    public ShapeRenderer getShapeRendererUI(){return shapeRenderUI;}
    public ShapeRenderer getShapeRendererBoarder(){return shapeRenderBoarder;}
    public ShapeRenderer getShapeRendererBarFill(){return shapeRendererBarFill;}

    /**
     * Purpose: Allow the programmer to change the color of the render 
     * @param color the new color that the render will be drawn in
     */
    public void setShapeRendererEnemyColor(Color color){ shapeRendererEnemy.setColor(color); }
    public void setShapeRendererUserColor(Color color){ shapeRendererUser.setColor(color); }
    public void setShapeRendererBackgroundColor(Color color){ shapeRendererBackground.setColor(color); }
    public void setShapeRendererCollectibleColor(Color color){ shapeRendererCollectible.setColor(color); }

    /**
     * Purpose: Allow the programmer to change the shape type of the line 
     * @param shapeType the new shapeType of the render 
     */
    public void setShapeRendererEnemyShapeType(ShapeRenderer.ShapeType shapeType){shapeTypes[0] = shapeType;}
    public void setShapeRendererUserShapeType(ShapeRenderer.ShapeType shapeType){shapeTypes[1] = shapeType;}
    public void setShapeRendererBackgroundShapeType(ShapeRenderer.ShapeType shapeType){shapeTypes[2] = shapeType;}
    public void setShapeRendererCollectibleShapeType(ShapeRenderer.ShapeType shapeType){shapeTypes[3] = shapeType;}
    
}
