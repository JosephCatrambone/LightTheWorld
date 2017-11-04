package com.josephcatrambone.lighttheworld.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.viewport.FitViewport
import com.josephcatrambone.lighttheworld.GDXMain
import com.josephcatrambone.lighttheworld.Level

class MainGameScreen : Screen() {

	var stage: Stage = Stage(FitViewport(480f, 800f))
	var level: Level

	init {
		level = Level("""
			5 4
			. . . . .
			. . . . .
			. . . . .
			. . . . .
		""".trimIndent())
		stage.addActor(level)
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