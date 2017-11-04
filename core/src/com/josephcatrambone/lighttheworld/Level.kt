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
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
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
	val delayToggle = 0.05f;
	var delayToNextToggle: Float = 0.05f

	private val pendingToggles = mutableListOf<LightTile>()
	override fun isTouchable(): Boolean {
		return super.isTouchable() and pendingToggles.isEmpty() // We don't want to be messed with while we're changing.
	}

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

		val parent = this;
		for(tileId in 0 until tableWidth*tableHeight) {
			val readOffset = tileId+2; // Two because we have to read those integers above.

			val cb = LightTile(skin)
			cb.style.checkboxOn = checkboxOn
			cb.style.checkboxOff = checkboxOff
			cb.isChecked = (tokens[readOffset] == TEXT_ON)
			add(cb)
			buttons.add(cb)

			cb.addListener(object : ChangeListener() {
				override fun changed(event: ChangeEvent?, actor: Actor?) {
					parent.reportClick(actor as LightTile)
				}
			})

			if((tileId+1)%tableWidth == 0) {
				row()
			}
		}

		this.layout()
	}

	private fun indexToGridPosition(index:Int): Pair<Int, Int> = Pair(index%tableWidth, index/tableWidth)
	private fun gridPositionToIndex(x:Int, y:Int): Int = x + y*tableWidth

	private fun reportClick(from:LightTile) {
		// If pending toggles is NOT empty, this click happened as a result of the program switching stuff.
		if(pendingToggles.isEmpty()) {
			// Propagate this click to the neighbors.
			val (x, y) = indexToGridPosition(buttons.indexOf(from))
			println("Got a report from... $from at $x $y")
			// Interlace the items growing outwards in the cardinal directions from this point.
			// We don't just toggle all these because we want to add JUICE!
			for(x2 in x+1 until tableWidth) {
				pendingToggles.add(buttons[gridPositionToIndex(x2, y)])
			}
			for(x2 in x-1 downTo 0) {
				pendingToggles.add(buttons[gridPositionToIndex(x2, y)])
			}
			for(y2 in y+1 until tableHeight) {
				pendingToggles.add(buttons[gridPositionToIndex(x, y2)])
			}
			for(y2 in y-1 downTo 0) {
				pendingToggles.add(buttons[gridPositionToIndex(x, y2)])
			}
		}
	}

	fun isComplete():Boolean = buttons.all { b -> b.isChecked }

	override fun act(delta: Float) {
		super.act(delta)

		if(pendingToggles.isNotEmpty() && delayToNextToggle <= 0f) {
			pendingToggles[0].isChecked = !pendingToggles[0].isChecked // DO THIS FIRST!
			// Pop only AFTER this is completed so we don't hit an endless loop on the last operation.
			val lightTile = pendingToggles.removeAt(0)
			// TODO: If lighttile is some special type
			delayToNextToggle = delayToggle
		}
		delayToNextToggle -= delta
	}

	// This gets called perpetually to check if stuff has a mousedown happening.
	//override fun hit(x: Float, y: Float, touchable: Boolean): Actor? {
}