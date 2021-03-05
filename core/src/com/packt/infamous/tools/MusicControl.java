package com.packt.infamous.tools;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;

/**
 * This is where all the music will be played from
 * We can add songs, and SFX in the songSelect and SoundSelect for to later recall
 */
public class MusicControl {

    //================================= Variables ==================================================
    private Music music;           //What reads the music and plays it on loop
    private float musicVolume = 1f;  //Current sfx volume
    private float sfxVolume = 1f;  //Current sfx volume
    AssetManager musicManager;     //All the data from the AssetManager

    //================================= Data =======================================================
    //Holds all the names for the songs
    String[] songSelect = new String[]{
            "Music/TestMusic.wav",
    };

    //Holds all the names for the SFX
    String[] soundSelect = new String[]{
            "SFX/TestButton.wav"
    };

    //====================================== Constructor ===========================================
    public MusicControl(AssetManager assetManager){musicManager = assetManager;}

    //========================================= Methods ============================================
    /**
     * Purpose: Starts up the music objects
     * @param songSelection selects which song is going to play for the screen
     */
    public void showMusic(int songSelection){
        music = musicManager.get(songSelect[songSelection], Music.class);
        music.setVolume(musicVolume);
        music.setLooping(true);
        music.play();
    }

    /**
     * Purpose: Stop/Start the music from playing
     */
    public void stopMusic(){ music.stop(); }
    public void startMusic(){ music.play(); }

    /**
     * Purpose: Plays a SFX from the list
     * @param soundSelection which SFX we're going to play
     */
    public void playSFX(int soundSelection) {musicManager.get(soundSelect[soundSelection], Sound.class).play(1/2f * sfxVolume); }

    /**
     * Generic turn the sound on and off
     */
    public void soundOnOff() {
        //Turns the volume down
        if (sfxVolume == 1f) {
            stopMusic();
            setSFXVolume(0);
        }
        //Turns the sound on
        else {
            startMusic();
            setSFXVolume(1);
        }
    }

    /**
     * Purpose: Update SFX volume
     */
    public float getSFXVolume(){ return sfxVolume; }
    public void setSFXVolume(float sfxVolume){this.sfxVolume = sfxVolume;}

    /**
     * Purpose: Update Music volume
     */
    public float getMusicVolumeVolume(){ return musicVolume; }
    public void setMusicVolume(float musicVolume){this.musicVolume = musicVolume;}
}
