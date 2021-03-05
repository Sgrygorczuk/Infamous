package com.packt.infamous.tools;

//import com.badlogic.gdx.tools.texturepacker.TexturePacker;

/**
 *Make sure to add this in build.gradle (Project: ProjectName) for this tool to work
 * project(":core") {
 *     apply plugin: "java"
 *     dependencies {
 *         implementation project(":core")
 *         implementation "com.badlogicgames.gdx:gdx-tools:$gdxVersion"
 *         implementation "com.badlogicgames.gdx:gdx-backend-lwjgl:$gdxVersion"
 *         implementation "com.badlogicgames.gdx:gdx-platform:$gdxVersion:natives-desktop"
 *     }
 * }
 */

/**
 * This tool grabs images or fonts from a folder and creates a single image to can be recalled using
 * TexturePacker
 */
public class TexturePackerTool {

    public TexturePackerTool(){
        //Set up for a font
        //TexturePacker.process("/home/sebastian/Projects/LibGDX_Personal/Templet/android/assets/Fonts", "/home/sebastian/Projects/LibGDX_Personal/Templet/android/assets", "font_assets");

    }
}
