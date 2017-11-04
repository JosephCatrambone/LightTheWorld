package com.josephcatrambone.lighttheworld

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.utils.Drawable
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable

class Level(mapDescription: String) : Table() {
	val TEXT_ON = "*"
	val TEXT_OFF = "."

	val buttons = mutableListOf<CheckBox>()
	val tableWidth:Int
	val tableHeight:Int

	init {
		this.skin = Skin(Gdx.files.internal("uiskin.json"))
		this.clear()
		this.clearChildren()
		buttons.clear()

		// Set up the checkbox style.
		val lightOn: TextureAtlas.AtlasRegion = GDXMain.atlas.findRegion("yellowtile")!!
		val lightOff: TextureAtlas.AtlasRegion = GDXMain.atlas.findRegion("darktile")!!
		val cbStyle:CheckBox.CheckBoxStyle = CheckBox.CheckBoxStyle()
		val checkboxOff = TextureRegionDrawable(lightOff)
		val checkboxOn = TextureRegionDrawable(lightOn)

		// Load the map
		val tokens = mapDescription.split(' ', '\t', '\n')
		tableWidth = tokens[0].toInt()
		tableHeight = tokens[1].toInt()

		for(tileId in 0 until tableWidth*tableHeight) {
			val readOffset = tileId+2; // Two because we have to read those integers above.

			val cb = CheckBox("", skin)
			cb.style.checkboxOn = checkboxOn
			cb.style.checkboxOff = checkboxOff
			cb.isChecked = (tokens[readOffset] == TEXT_ON)
			add(cb)

			if((tileId+1)%tableWidth == 0) {
				row()
			}
		}

		this.layout()
	}

	fun isComplete() {

	}
}