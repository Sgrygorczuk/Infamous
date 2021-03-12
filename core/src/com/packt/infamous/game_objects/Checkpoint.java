package com.packt.infamous.game_objects;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Checkpoint{
    private int health, energy;
    private float location_x, location_y;

    /**
     * Purpose: Creates 1int data to be applied to the cole instance of a screen
     * @param health
     * @param energy
     * @param location_x
     * @param location_y
     */
    public Checkpoint(int health, int energy, float location_x, float location_y) {
        this.health = health;
        this.energy = energy;
        this.location_x = location_x;
        this.location_y = location_y;
    }

    public int getEnergy() { return energy; }
    public int getHealth() { return health; }
    public float getLocation_x() {return location_x;}
    public float getLocation_y() {return location_y;}
}
