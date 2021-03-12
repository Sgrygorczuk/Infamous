package com.packt.infamous.tools;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import static com.packt.infamous.Const.WORLD_HEIGHT;
import static com.packt.infamous.Const.WORLD_WIDTH;

public class TiledSetUp {

    //================================= Variables ==================================================
    SpriteBatch batch;
    Camera camera;
    Viewport viewport;
    private TiledMap tiledMap;
    private OrthogonalTiledMapRenderer orthogonalTiledMapRenderer;
    private float levelHeight;
    private float levelWidth;

    //====================================== Constructor ===========================================
    public TiledSetUp(AssetManager tiledManager, SpriteBatch batch, String mapName){
        this.batch = batch;

        camera = new OrthographicCamera();                                          //Sets a 2D view
        camera.position.set(WORLD_WIDTH / 2, WORLD_HEIGHT / 2, 0);           //Places the camera in the center of the view port
        camera.update();                                                            //Updates the camera
        viewport = new StretchViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);          //Stretches the image to fit the screen
        viewport.apply();

        showTiled(tiledManager, mapName);
    }
    
    //========================================= Methods ============================================
    /**
     * Purpose: Collects that data necessary for drawing and extracting stuff from Tiled
     * @param tiledManager the asset manager we will pull the map from
     * @param mapName name of the map which we will refer to
     */
    private void showTiled(AssetManager tiledManager, String mapName){
        //Gets the map
        tiledMap = tiledManager.get(mapName);
        //Makes it into a drawing that we can call
        orthogonalTiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap, batch);
        //Center the drawing based on the camera
        orthogonalTiledMapRenderer.setView((OrthographicCamera) camera);

        //Uses to tell
        TiledMapTileLayer tiledMapTileLayer = (TiledMapTileLayer) tiledMap.getLayers().get(0);
        levelHeight = tiledMapTileLayer.getHeight() * tiledMapTileLayer.getTileHeight();
        levelWidth = tiledMapTileLayer.getWidth() * tiledMapTileLayer.getTileWidth();

    }

    /**
     * Purpose: Scroll camera vertically
     * @param delta timing
     * @param speed at which camera moves
     */
    public void scrollCameraVertically(float delta, float speed) {
        if (camera.position.y + delta * speed + WORLD_HEIGHT < levelHeight) {
            camera.position.y += delta * speed;
            camera.position.set(camera.position.x, camera.position.y, camera.position.z);
            camera.update();
            orthogonalTiledMapRenderer.setView((OrthographicCamera) camera);
        }
    }

    /**
     * Purpose: Scroll camera horizontally
     */
    public void scrollCameraHorizontally(float delta, float speed) {
        if (camera.position.x + delta * speed + WORLD_WIDTH < levelHeight) {
            camera.position.x += delta * speed;
            camera.position.set(camera.position.x, camera.position.y, camera.position.z);
            camera.update();
            orthogonalTiledMapRenderer.setView((OrthographicCamera) camera);
        }
    }

    public void updateXCameraPosition(float x){
        if((x > WORLD_WIDTH/2f) && (x < levelWidth - WORLD_WIDTH/2f)) {
            camera.position.set(x, camera.position.y, camera.position.z);
            camera.update();
            orthogonalTiledMapRenderer.setView((OrthographicCamera) camera);
        }
    }

    public void updateCamera(Camera camera){
        this.camera = camera;
        orthogonalTiledMapRenderer.setView((OrthographicCamera) camera);
    }

    /**
     * Purpose: Moves around the camera given the position
     * @param x coordinate
     * @param y coordinate
     */
    public void updateCameraPosition(float x, float y) {
        if((x > WORLD_WIDTH/2f) && (x < levelWidth - WORLD_WIDTH/2f)) {
            camera.position.set(x, camera.position.y, camera.position.z);
            camera.update();
            orthogonalTiledMapRenderer.setView((OrthographicCamera) camera);
        }

        if((y > WORLD_HEIGHT/2f) && (x < levelHeight - WORLD_HEIGHT/2f)) {
            camera.position.set(camera.position.x, y, camera.position.z);
            camera.update();
            orthogonalTiledMapRenderer.setView((OrthographicCamera) camera);
        }
    }

    /**
     * Purpose: Allow user to get coordinates of all the objects in that layer
     * @param layerName tells us the name of the layer we want to pull from
     * @return a Vector2 of coordinates
     */
    public Array<Vector2> getLayerCoordinates(String layerName) {
        //Grab the layer from tiled map
        MapLayer mapLayer = tiledMap.getLayers().get(layerName);
        Array<Vector2> coordinates = new Array<>();

        //Grabs the coordinates for each instance of that object in the map
        for (MapObject mapObject : mapLayer.getObjects()) {
            Vector2 coordinate = new Vector2(mapObject.getProperties().get("x", Float.class),
                    mapObject.getProperties().get("y", Float.class));
            coordinates.add(coordinate);
        }
        return coordinates;
    }

    /**
     * Purpose: Allow user to get dimensions of the object
     * @param layerName tells us the name of the layer we want to pull from
     * @return a Vector2 of width and height
     */
    public Array<Vector2> getLayerDimensions(String layerName) {
        //Grab the layer from tiled map
        MapLayer mapLayer = tiledMap.getLayers().get(layerName);
        Array<Vector2> dimensions = new Array<>();

        //Grabs the coordinates for each instance of that object in the map
        for (MapObject mapObject : mapLayer.getObjects()) {
            Vector2 dimension = new Vector2(mapObject.getProperties().get("width", Float.class),
                    mapObject.getProperties().get("height", Float.class));
            dimensions.add(dimension);
        }
        return dimensions;
    }

    public float getLevelHeight(){return levelHeight;}

    public float getLevelWidth(){return levelWidth;}

    /**
     * Purpose: Draws the tiled map
     */
    public void drawTiledMap() {
        batch.setProjectionMatrix(camera.projection);
        batch.setTransformMatrix(camera.view);
        orthogonalTiledMapRenderer.render();
    }

}
