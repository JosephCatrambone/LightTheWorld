package com.josephcatrambone.lighttheworld.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.MoveToAction
import com.badlogic.gdx.utils.viewport.FitViewport
import com.josephcatrambone.lighttheworld.GDXMain
import com.josephcatrambone.lighttheworld.Level

class MainGameScreen : Screen() {

	var stage: Stage
	var level: Level

	init {
		val camera = OrthographicCamera(480f, 800f)
		camera.setToOrtho(false)
		stage = Stage(FitViewport(480f, 800f, camera))
		level = Level("""
			5 4
			. . . . .
			. . * . .
			. * . . .
			. . . . .
		""".trimIndent())
		stage.addActor(level)

		stage.camera.position.set(0f, 0f, 1f)
		stage.camera.update(true)
		level.setPosition(0f, 0f);
	}

	override fun update(deltaTime: Float) {
		stage.act(deltaTime)
		if(level.isComplete()) {
			println("Complete!")
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