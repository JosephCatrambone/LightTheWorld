package com.josephcatrambone.lighttheworld;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.josephcatrambone.lighttheworld.screens.IntroScreen;
import com.josephcatrambone.lighttheworld.screens.LevelSelectScreen;
import com.josephcatrambone.lighttheworld.screens.MainGameScreen;
import com.josephcatrambone.lighttheworld.screens.Screen;

import java.util.Stack;

/**
 * TODO:
 * 1. Load level + handle tile propagation.
 * 2. Level transitions.
 * 3. Tile variety and high score display.
 * 4. UI: Restart level button, 'go back' button for level select.
 */
public class GDXMain extends ApplicationAdapter {
	public static final String ATLAS_NAME = "images.atlas";

	public static Stack<Screen> screenStack = new Stack<Screen>();
	//public static AssetManager assetManager; // Maybe we don't want to expose this.
	public static TextureAtlas atlas;
	public static Skin skin;

	@Override
	public void create () {
		// Load our assets first.
		AssetManager assetManager = new AssetManager();
		assetManager.load(ATLAS_NAME, TextureAtlas.class);
		assetManager.update();
		assetManager.finishLoading();

		// Load the atlas from the manager.
		GDXMain.atlas = assetManager.get(ATLAS_NAME, TextureAtlas.class);

		// Load the skin.
		FileHandle skinHandle = Gdx.files.internal("uiskin.json");
		GDXMain.skin = new Skin(skinHandle);

		// Catch the back key on Android.
		Gdx.input.setCatchBackKey(true);

		screenStack.push(new IntroScreen()); // This will push the other screens.
		screenStack.peek().restore();
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		// Render
		screenStack.peek().render();

		// Update everything
		float dt = Gdx.graphics.getDeltaTime();
		screenStack.peek().update(dt);
		TweenManager.update(dt);

		// Check to see if 'back' was pressed.
		if(Gdx.input.isKeyJustPressed(Input.Keys.BACK) || Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
			Screen s = screenStack.pop();
			s.dispose();
			if(screenStack.isEmpty()) {
				//System.exit(0);
				Gdx.app.exit();
			} else {
				screenStack.peek().restore(); // Now you're on top.
			}
		}
	}
	
	@Override
	public void dispose () {
		while(!screenStack.isEmpty()) {
			Screen s = screenStack.pop();
			s.dispose();
		}
		atlas.dispose();
		skin.dispose();
	}
}
