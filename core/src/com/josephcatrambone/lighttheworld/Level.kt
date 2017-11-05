package com.josephcatrambone.lighttheworld

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.scenes.scene2d.utils.Drawable
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Align
import com.josephcatrambone.lighttheworld.tiles.LightTile

class Level(mapDescription: String, skin:Skin = GDXMain.skin) : Table(skin) {
	val TEXT_ON = "*"
	val TEXT_OFF = "."
	val TEXT_DISABLE = "x"

	val buttons = mutableListOf<LightTile>()
	val tableWidth:Int
	val tableHeight:Int
	val delayToggle = 0.01f;
	var delayToNextToggle: Float = 0.01f

	private val pendingToggles = mutableListOf<LightTile>()
	override fun isTouchable(): Boolean {
		return super.isTouchable() and pendingToggles.isEmpty() // We don't want to be messed with while we're changing.
	}

	init {
		//this.debug = true;
		this.clear()
		this.clearChildren()
		buttons.clear()

		// Don't do any of this in here because it's the parent's responsibility to handle location and such.
		this.setTransform(true)
		//this.setOrigin(0.5f, 0.5f)
		//this.setScale(0.5f)
		/*
		// We redefined the checkbox style in skinui.
		val lightOn: TextureAtlas.AtlasRegion = GDXMain.atlas.findRegion("yellowtile")!!
		val lightOff: TextureAtlas.AtlasRegion = GDXMain.atlas.findRegion("darktile")!!
		val checkboxOff = TextureRegionDrawable(lightOff)
		val checkboxOn = TextureRegionDrawable(lightOn)
		//val cbStyle:CheckBox.CheckBoxStyle = CheckBox.CheckBoxStyle()
		cb.style.checkboxOn = checkboxOn
		cb.style.checkboxOff = checkboxOff
		*/

		// Load the map
		val tokens = mapDescription.split(' ', '\t', '\n')
		tableWidth = tokens[0].toInt()
		tableHeight = tokens[1].toInt()

		val parent = this;
		for(tileId in 0 until tableWidth*tableHeight) {
			val readOffset = tileId+2; // Two because we have to read those integers above.

			val cb = LightTile(skin)
			when(tokens[readOffset]) {
				TEXT_ON -> cb.isChecked = true
				TEXT_OFF -> cb.isChecked = false
				TEXT_DISABLE -> cb.isDisabled = true
			}
			cb.setOrigin(Align.center)
			cb.setTransform(true) // To allow rotate + scale.
			add(cb)
			buttons.add(cb)

			cb.addListener(object : ChangeListener() {
				override fun changed(event: ChangeEvent?, actor: Actor?) {
					// TODO: We should only report the first screen click, otherwise people can multi-tap to break things.
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
			// Disable this while we're showing the animation.
			this.touchable = Touchable.disabled

			// Propagate this click to the neighbors.
			val (x, y) = indexToGridPosition(buttons.indexOf(from))

			// Interlace the items growing outwards in the cardinal directions from this point.
			// We don't just toggle all these because we want to add JUICE!
			val rightList = mutableListOf<LightTile>()
			for(x2 in x+1 until tableWidth) {
				val next = buttons[gridPositionToIndex(x2, y)]
				if(next.isDisabled) {
					break
				} else {
					rightList.add(next)
				}
			}
			val leftList = mutableListOf<LightTile>()
			for(x2 in x-1 downTo 0) {
				val next = buttons[gridPositionToIndex(x2, y)]
				if(next.isDisabled) {
					break
				} else {
					leftList.add(next)
				}
			}
			val downList = mutableListOf<LightTile>()
			for(y2 in y+1 until tableHeight) {
				val next = buttons[gridPositionToIndex(x, y2)]
				if(next.isDisabled) {
					break
				} else {
					downList.add(next)
				}
			}
			val upList = mutableListOf<LightTile>()
			for(y2 in y-1 downTo 0) {
				val next = buttons[gridPositionToIndex(x, y2)]
				if(next.isDisabled) {
					break
				} else {
					upList.add(next)
				}
			}

			// Now push all the toggle events, interpolating each direction.
			while(upList.isNotEmpty() || downList.isNotEmpty() || rightList.isNotEmpty() || leftList.isNotEmpty()) {
				if(rightList.isNotEmpty()) { pendingToggles.add(rightList.removeAt(0)) }
				if(upList.isNotEmpty()) { pendingToggles.add(upList.removeAt(0)) }
				if(leftList.isNotEmpty()) { pendingToggles.add(leftList.removeAt(0)) }
				if(downList.isNotEmpty()) { pendingToggles.add(downList.removeAt(0)) }
			}
		}
	}

	fun isComplete():Boolean = buttons.all { b -> (b.isDisabled || b.isChecked) && pendingToggles.isEmpty() }

	override fun act(delta: Float) {
		super.act(delta)

		if(pendingToggles.isNotEmpty() && delayToNextToggle <= 0f) {
			if(!pendingToggles[0].isDisabled) {
				pendingToggles[0].isChecked = !pendingToggles[0].isChecked // DO THIS FIRST!

				val t = pendingToggles[0]
				TweenManager.activeTweens.add(Tween(0.3f, floatArrayOf(1.0f, 1.2f, 1.0f), {f -> t.setScale(f)}))
			}
			// Pop only AFTER this is completed so we don't hit an endless loop on the last operation.
			val lightTile = pendingToggles.removeAt(0)
			delayToNextToggle = delayToggle

			// If that was the last one, reenable touchable.
			if(pendingToggles.isEmpty()) {
				this.touchable = Touchable.enabled
			}
		}
		delayToNextToggle -= delta
		TweenManager.update(delta)
	}

	override fun getWidth(): Float {
		var total = 0f
		for(i in 0 until this.columns) {
			total += this.getColumnWidth(i)
		}
		return total
	}

	override fun getHeight(): Float {
		var total = 0f
		for(i in 0 until this.rows) {
			total += this.getRowHeight(i)
		}
		return total
	}
}