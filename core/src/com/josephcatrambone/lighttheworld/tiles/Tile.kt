package com.josephcatrambone.lighttheworld.tiles

import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.josephcatrambone.lighttheworld.GDXMain
import com.josephcatrambone.lighttheworld.Image

/**
 * Created by jo on 2017-11-05.
 */
open class Tile : Image(TextureRegionDrawable(GDXMain.atlas.findRegion("notile"))) {
}

