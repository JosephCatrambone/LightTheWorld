package com.josephcatrambone.lighttheworld.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.viewport.FitViewport
import com.josephcatrambone.lighttheworld.Background
import com.josephcatrambone.lighttheworld.GDXMain
import com.josephcatrambone.lighttheworld.Vec
import com.josephcatrambone.lighttheworld.tiles.LightTile
import com.josephcatrambone.lighttheworld.tiles.Tile

class IntroScreen : Screen() {

	var stage: Stage
	val background:Background
	val table:Table
	val buttons:Array<Tile>

	init {
		val camera = OrthographicCamera(480f, 800f)
		camera.setToOrtho(false)
		stage = Stage(FitViewport(480f, 800f, camera))
		stage.camera.position.set(0f, 0f, 1f)
		stage.camera.update(true)

		background = Background(Vec(15.1f, -30.2f))
		stage.addActor(background);
		table = Table(GDXMain.skin)
		buttons = Array(16*16, { i ->
			val t = LightTile(false)
			table.add(t)
			if(i+1 % 16 == 0) {
				table.row()
			}
			t
		})
		table.layout()
	}

	fun advanceScreen() {
		// Pop = Destroy this screen and add the title screen.
		val s = GDXMain.screenStack.pop()
		GDXMain.screenStack.push(TitleScreen())
		GDXMain.screenStack.peek().restore()
		s.dispose() // Destroy ourselves.
	}

	override fun update(deltaTime: Float) {
		stage.act(deltaTime)
		if(Gdx.input.isTouched() || Gdx.input.isKeyPressed(Input.Keys.ANY_KEY)) {
			advanceScreen()
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