package com.josephcatrambone.lighttheworld

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.scenes.scene2d.utils.Drawable
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.josephcatrambone.lighttheworld.tiles.LightTile

class Level(mapDescription: String) : Table() {
	val TEXT_ON = "*"
	val TEXT_OFF = "."

	val buttons = mutableListOf<LightTile>()
	val tableWidth:Int
	val tableHeight:Int

	init {
		this.debug = true;
		this.skin = Skin(Gdx.files.internal("uiskin.json"))
		this.clear()
		this.clearChildren()
		buttons.clear()

		// Set up the checkbox style.
		val lightOn: TextureAtlas.AtlasRegion = GDXMain.atlas.findRegion("yellowtile")!!
		val lightOff: TextureAtlas.AtlasRegion = GDXMain.atlas.findRegion("darktile")!!
		val checkboxOff = TextureRegionDrawable(lightOff)
		val checkboxOn = TextureRegionDrawable(lightOn)
		//val cbStyle:CheckBox.CheckBoxStyle = CheckBox.CheckBoxStyle()

		// Load the map
		val tokens = mapDescription.split(' ', '\t', '\n')
		tableWidth = tokens[0].toInt()
		tableHeight = tokens[1].toInt()

		for(tileId in 0 until tableWidth*tableHeight) {
			val readOffset = tileId+2; // Two because we have to read those integers above.

			val cb = LightTile(skin)
			cb.style.checkboxOn = checkboxOn
			cb.style.checkboxOff = checkboxOff
			cb.isChecked = (tokens[readOffset] == TEXT_ON)
			add(cb)
			buttons.add(cb)

			if((tileId+1)%tableWidth == 0) {
				row()
			}
		}

		this.layout()

		// Attach the input handlers for this object.
		this.addListener(object : ClickListener() {
			override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int): Boolean {
				val touchStatus = super.touchDown(event, x, y, pointer, button)
				return touchStatus
			}

			override fun touchUp(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int) {
				super.touchUp(event, x, y, pointer, button)
				val parent:Table = event!!.listenerActor as Table
				// Find the grid X and grid Y.
				// Since in theory the tiles could be different sizes, iterate over the width to get the exact row.
				val screenCoords = Vector2(Gdx.input.getX().toFloat(), Gdx.input.getY().toFloat())
				val coords = parent.screenToLocalCoordinates(screenCoords.cpy())
				var xPos = coords.x // Starting at the right.
				var yPos = coords.y // And the bottom (?)
				var gridX = -1
				var gridY = -1
				for(gridYTemp in 0 until parent.rows) {
					val rowHeight = parent.getRowHeight(gridYTemp)
					if(rowHeight < yPos) {
						gridY = gridYTemp
						break
					} else {
						yPos -= rowHeight
					}
				}
				for(gridXTemp in 0 until parent.columns) {
					val colWidth = parent.getColumnWidth(gridXTemp)
					if(colWidth < xPos) {
						gridX = gridXTemp
						break
					} else {
						xPos -= colWidth
					}
				}
				val button:LightTile? = buttons.find { it -> it.hit(x, y, false) != null }
				println("$xPos $yPos $button $gridX $gridY")
			}
		})
	}

	fun isComplete():Boolean = buttons.all { b -> b.isChecked }

	override fun act(delta: Float) {
		super.act(delta)
	}

	// This gets called perpetually to check if stuff has a mousedown happening.
	//override fun hit(x: Float, y: Float, touchable: Boolean): Actor? {
}