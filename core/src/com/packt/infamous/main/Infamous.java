package com.packt.infamous.main;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.packt.infamous.screens.LoadingScreen;

import java.lang.reflect.Array;

public class Infamous extends Game {

	//Holds the UI images and sound files
	private final AssetManager assetManager = new AssetManager();
	private int textureChoice = 0;

	private boolean[] collectibleComplete = new boolean[]{false, false, false, false, false, false};
	private boolean[] healsComplete = new boolean[]{false, false, false, false, false, false};
	private boolean[] killsComplete = new boolean[]{false, false, false, false, false, false};

	public int getTextureChoice() {
		return textureChoice;
	}

	public void setTextureChoice(int textureChoice) {
		this.textureChoice = textureChoice;
	}

	public boolean getIfCollectibleComplete(){
		for(boolean collectible : collectibleComplete){
			if(!collectible){return false;}
		}
		return true;
	}

	public boolean getIfHealsComplete(){
		for(boolean heal : healsComplete){
			if(!heal){return false;}
		}
		return true;
	}

	public boolean getIfKillsComplete(){
		for(boolean kill : killsComplete){
			if(!kill){return false;}
		}
		return true;
	}

	public void setHealed(int index){ healsComplete[index] = true; }
	public void setCollected(int index){ collectibleComplete[index] = true; }
	public void setKilled(int index){ killsComplete[index] = true; }

	public boolean getHealed(int index){return healsComplete[index];}
	public boolean getCollected(int index){return collectibleComplete[index];}
	public boolean getKilled(int index){return killsComplete[index];}


	/**
	 * Purpose: Tells the game what controls/information it should provide
	*/
	public Infamous(){}

	/**
	 * 	Purpose: Returns asset manager with all its data
	 * 	Output: Asset Manager
	*/
	public AssetManager getAssetManager() { return assetManager; }

	/**
	Purpose: Starts the app
	*/
	@Override
	public void create () {
		assetManager.setLoader(TiledMap.class, new TmxMapLoader(new InternalFileHandleResolver()));
		setScreen(new LoadingScreen(this));
	}

}
