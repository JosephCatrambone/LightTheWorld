package com.josephcatrambone.lighttheworld

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.scenes.scene2d.Actor

class Background(val scrollSpeed:Vec) : Actor() {
	var scrollAmount: Vec = Vec()
	val img = GDXMain.atlas.findRegion("background")

	override fun act(delta: Float) {
		super.act(delta)
	}

	override fun draw(batch: Batch?, parentAlpha: Float) {
		super.draw(batch, parentAlpha)
	}
}