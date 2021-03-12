package com.packt.infamous.tools;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * Used to align text
 */
public class TextAlignment {

    //================================== Constructor ===============================================
    public TextAlignment(){}

    //================================== Methods ===================================================
    /**
     * Purpose: General purpose function that centers the text on the position
     * @param batch to where we are drawing this to
     * @param bitmapFont the font styling
     * @param string the text that's being written
     * @param x x position
     * @param y y position
     */
    public void centerText(SpriteBatch batch, BitmapFont bitmapFont, String string, float x, float y){
        GlyphLayout glyphLayout = new GlyphLayout();
        glyphLayout.setText(bitmapFont, string);
        bitmapFont.draw(batch, string,  x - glyphLayout.width/2, y + glyphLayout.height/2);
    }

    /**
     * Purpose: This function take a string and adds a new line whenever it reaches the length between it's starting position andlengtht,
     * if start + length happens to occur on a non space char it goes back to the nearest space char
     * @param str the string we will be breaking up
     * @param lineLength the number of character that we will allow before finding next space to make a new line
     * @return a reformatted string
     * (Ex. (Hello I am Dev, 5) -> Hello
     *                             I am
     *                             Dev
     */
    public String addNewLine(String str, int lineLength) {
        int spaceFound;
        int reminder = 0; //Used to push back the check to wherever the last " " was
        for (int j = 0; lineLength * (j + 1) + j - reminder < str.length(); j++) {
            //Finds the new position of where a " " occurs
            spaceFound = str.lastIndexOf(" ", lineLength * (j + 1) + j - reminder);
            //Adds in a new line if this is not the end of the string
            if (str.length() >= spaceFound + 1) {
                str = str.substring(0, spaceFound + 1) + "\n" + str.substring(spaceFound);
                reminder = lineLength * (j + 1) + j - spaceFound;
            }
        }
        return str;
    }
}
