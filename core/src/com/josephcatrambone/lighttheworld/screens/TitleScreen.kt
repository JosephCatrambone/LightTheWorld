package com.josephcatrambone.lighttheworld.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.utils.viewport.FitViewport
import com.josephcatrambone.lighttheworld.Background
import com.josephcatrambone.lighttheworld.GDXMain
import com.josephcatrambone.lighttheworld.Vec

class TitleScreen : Screen() {

	var stage: Stage
	val background = Background(Vec(15.1f, -30.2f))

	init {
		val camera = OrthographicCamera(480f, 800f)
		camera.setToOrtho(false)
		stage = Stage(FitViewport(480f, 800f, camera))
		stage.camera.position.set(0f, 0f, 1f)
		stage.camera.update(true)

		stage.addActor(background);

		// We just got pushed onto the stack.  We should
	}

	override fun update(deltaTime: Float) {
		stage.act(deltaTime)
		if(Gdx.input.isKeyPressed(Input.Keys.ANY_KEY)) {
			GDXMain.screenStack.push(LevelSelectScreen())
			GDXMain.screenStack.peek().restore()
		}
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