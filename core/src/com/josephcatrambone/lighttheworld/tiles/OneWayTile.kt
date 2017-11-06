package com.josephcatrambone.lighttheworld.tiles

/**
 * Created by jo on 2017-11-05.
 */
import com.badlogic.gdx.graphics.g2d.Batch
//import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.josephcatrambone.lighttheworld.GDXMain

class OneWayTile(val direction:Direction) : Tile(
) {
	enum class Direction { RIGHT, UP, LEFT, DOWN }

	init {
		this.drawable = TextureRegionDrawable(
			GDXMain.atlas.findRegion("oneway_${direction.name.toLowerCase()}")
		)
		this.setSize(drawable.minWidth, drawable.minHeight)
		this.width = drawable.minWidth
		this.height = drawable.minHeight
	}
}