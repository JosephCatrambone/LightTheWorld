package com.josephcatrambone.lighttheworld.tiles

import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.josephcatrambone.lighttheworld.GDXMain

class BlockTile : Tile() {
	init {
		this.drawable = TextureRegionDrawable(GDXMain.atlas.findRegion("blocktile"))
	}
}