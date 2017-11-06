package com.josephcatrambone.lighttheworld.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.MoveToAction
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.viewport.FitViewport
import com.josephcatrambone.lighttheworld.*

class MainGameScreen : Screen() {

	val LEVEL_TRANSITION_TIME:Float = 1.15f
	val LEVEL_TRANSITION_DISTANCE:Float = 2f*Gdx.graphics.height
	val LEVEL_TRANSITION_EASE_IN = 0.6f
	val LEVEL_TRANSITION_EASE_OUT = 1.5f

	val levels = listOf<String>(
		"""
			5 5
			x x . x x
			x x . x x
			. . . . .
			x x . x x
			x x . x x
		""".trimIndent(),
		"""
			5 4
			* * . . .
			. * . . *
			x . x x x
			. . . . .
		""".trimIndent(),
		"""
			5 3
			x x x x x
			. . > . .
			x x x x x
		""".trimIndent(),
		"""
			5 3
			x . . . x
			. . > . .
			x . x x x
		""".trimIndent(),
			"""
			5 4
			x . . . x
			. . < . .
			x ^ . . x
			x . x x x
		""".trimIndent()
		)

	val topScores:MutableList<Int> = MutableList<Int>(levels.size, {_ -> Int.MAX_VALUE})
	var currentLevelIndex = 0
	var justCompleted = false
	var stage: Stage
	lateinit var level: Level

	init {
		val camera = OrthographicCamera(480f, 800f)
		camera.setToOrtho(false)
		stage = Stage(FitViewport(480f, 800f, camera))
		stage.camera.position.set(0f, 0f, 1f)
		stage.camera.update(true)

		loadLevel(currentLevelIndex)
	}

	fun loadLevel(index:Int) {
		// Delete old level if it's present.
		stage.actors.forEach { a -> a.clear() }
		stage.actors.clear()

		// Load the new level.
		level = Level(levels[index])
		stage.addActor(level)

		// TODO: VERY IMPORTANT.  level.width != level.getWidth().  Can't seem to override the width property.
		// level.width returns 0 but level.getWidth() returns the right value.
		// TODO: Talk with the Kotlin group about this.
		// Calculate the difference between the level width and the screen width.
		val levelScale = min(stage.width/(level.getWidth()+20f), stage.height/(level.getHeight()+20f))

		level.setTransform(true)
		level.setOrigin(Align.center)
		val targetY = -(level.getHeight()/2.0f)*levelScale
		level.setPosition(-(level.getWidth()/2.0f)*levelScale, -LEVEL_TRANSITION_DISTANCE)
		level.setScale(levelScale)

		TweenManager.add(EaseTween(LEVEL_TRANSITION_TIME, -LEVEL_TRANSITION_DISTANCE, targetY, LEVEL_TRANSITION_EASE_IN, {f -> level.setPosition(level.getX(), f)}))
	}

	override fun update(deltaTime: Float) {
		stage.act(deltaTime)
		if(level.isComplete()) {
			if(!justCompleted) {
				justCompleted = true

				// If the high score for this one is better, use it!
				if (level.taps < topScores[currentLevelIndex]) {
					topScores[currentLevelIndex] = level.taps
				}
				// Transition the level by moving it away.
				TweenManager.activeTweens.add(EaseTween(LEVEL_TRANSITION_TIME, level.getY(), LEVEL_TRANSITION_DISTANCE, LEVEL_TRANSITION_EASE_OUT, { f -> level.setPosition(level.getX(), f) }))
			} else if(TweenManager.activeTweens.isEmpty()) {
				// If the tween has finished, load the next level and reset justCompleted.
				println("Tween done.  Loading next level.")
				currentLevelIndex++
				justCompleted = false
				loadLevel(currentLevelIndex)
			}
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