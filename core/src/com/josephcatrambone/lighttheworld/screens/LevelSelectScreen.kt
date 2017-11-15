package com.josephcatrambone.lighttheworld.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.scenes.scene2d.*
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.viewport.FitViewport
import com.josephcatrambone.lighttheworld.*
import javafx.scene.input.TouchEvent

class LevelSelectScreen : Screen() {

	val numLevels = 5 //
	var stage: Stage
	val background = Background(Vec(15.1f, -30.2f))
	val table = Table()

	init {
		val camera = OrthographicCamera(480f, 800f)
		camera.setToOrtho(false)
		stage = Stage(FitViewport(480f, 800f, camera))
		stage.camera.position.set(0f, 0f, 1f)
		stage.camera.update(true)

		stage.addActor(background);
		stage.addActor(table)

		// TODO: Num levels.
		for(i in 0 until numLevels) {
			// Create a button to load the level.
			val levelSelectButton = TextButton("Level $i", GDXMain.skin)
			// Attach the level load operation.
			levelSelectButton.addListener(object : ClickListener() {
				override fun clicked(event: InputEvent?, x: Float, y: Float) {
					super.clicked(event, x, y)
					val mgs = MainGameScreen()
					GDXMain.screenStack.push(mgs)
					mgs.loadLevel(i)
					mgs.currentLevelIndex = i
					GDXMain.screenStack.peek().restore()
				}
			})
			// Add to the table.
			table.add(levelSelectButton)
			table.row()
		}

		table.layout()
	}

	override fun update(deltaTime: Float) {
		stage.act(deltaTime)
	}

	override fun render() {
		stage.draw()
	}

	override fun restore() {
		super.restore()
		Gdx.input.inputProcessor = this.stage
	}

	override fun dispose() {
		super.dispose()
	}
}