package com.josephcatrambone.lighttheworld.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.viewport.FitViewport
import com.josephcatrambone.lighttheworld.*

class LevelSelectScreen : Screen() {

	var stage: Stage
	val background = Background(Vec(15.1f, -30.2f))

	init {
		val camera = OrthographicCamera(480f, 800f)
		camera.setToOrtho(false)
		stage = Stage(FitViewport(480f, 800f, camera))
		stage.camera.position.set(0f, 0f, 1f)
		stage.camera.update(true)

		stage.addActor(background);
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