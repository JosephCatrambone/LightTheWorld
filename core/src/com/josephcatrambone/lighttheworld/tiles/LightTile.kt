package com.josephcatrambone.lighttheworld.tiles

import com.badlogic.gdx.graphics.g2d.Batch
//import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.josephcatrambone.lighttheworld.GDXMain

class LightTile(var lit:Boolean = false) : Tile() {
	val activeImage: TextureRegionDrawable = TextureRegionDrawable(GDXMain.atlas.findRegion("yellowtile"))
	val inactiveImage: TextureRegionDrawable = TextureRegionDrawable(GDXMain.atlas.findRegion("darktile"))

	init {
		updateDrawable()
	}

	override fun draw(batch: Batch?, parentAlpha: Float) {
		updateDrawable()
		super.draw(batch, parentAlpha)
	}

	private fun updateDrawable() {
		this.drawable = if(lit) {
			activeImage
		} else {
			inactiveImage
		}
	}
}