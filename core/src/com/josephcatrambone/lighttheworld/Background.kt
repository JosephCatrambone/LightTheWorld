package com.josephcatrambone.lighttheworld

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.scenes.scene2d.Actor

class Background(val scrollSpeed:Vec, regionName:String = "cloud_bg") : Actor() {
	var scrollAmount: Vec = Vec()
	val img = GDXMain.atlas.findRegion(regionName)
	val copiesX:Int
	val copiesY:Int

	init {
		copiesX = 2*(Gdx.graphics.width/img.regionWidth)
		copiesY = 2*(Gdx.graphics.height/img.regionHeight)
	}

	override fun act(delta: Float) {
		super.act(delta)
		this.scrollAmount += scrollSpeed*delta
		this.scrollAmount.x %= img.regionWidth
		this.scrollAmount.y %= img.regionHeight
	}

	override fun draw(batch: Batch, parentAlpha: Float) {
		super.draw(batch, parentAlpha)
		for(y in -copiesY/2 .. copiesY) {
			for(x in -copiesX/2 .. copiesX) {
				// We want this to tile, so we draw n copies of it.
				// Offset is modulo the size of the image, so if it doesn't cover the BG, increase N.
				batch.draw(img, scrollAmount.x + (x*img.regionWidth), scrollAmount.y + (y*img.regionHeight))
			}
		}
	}
}