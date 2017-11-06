package com.josephcatrambone.lighttheworld

import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.utils.Align
import com.josephcatrambone.lighttheworld.tiles.BlockTile
import com.josephcatrambone.lighttheworld.tiles.LightTile
import com.josephcatrambone.lighttheworld.tiles.OneWayTile
import com.josephcatrambone.lighttheworld.tiles.Tile

class Level(mapDescription: String) : Table() {
	val TEXT_ON = "*"
	val TEXT_OFF = "."
	val TEXT_DISABLE = "x"
	val TEXT_ONEWAY_RIGHT = ">"
	val TEXT_ONEWAY_UP = "^"
	val TEXT_ONEWAY_LEFT = "<"
	val TEXT_ONEWAY_DOWN = "v"

	var taps:Int = 0

	val buttons = mutableListOf<Tile>()
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
		this.setOrigin(0.5f, 0.5f)
		//this.setScale(0.5f)

		// Load the map
		val tokens = mapDescription.split(' ', '\t', '\n')
		tableWidth = tokens[0].toInt()
		tableHeight = tokens[1].toInt()

		for(tileId in 0 until tableWidth*tableHeight) {
			val readOffset = tileId+2; // Two because we have to read those integers above.

			val cb = when(tokens[readOffset]) {
				TEXT_ON -> LightTile(true)
				TEXT_OFF -> LightTile(false)
				TEXT_DISABLE -> BlockTile()
				TEXT_ONEWAY_RIGHT -> OneWayTile(OneWayTile.Direction.RIGHT)
				TEXT_ONEWAY_UP -> OneWayTile(OneWayTile.Direction.UP)
				TEXT_ONEWAY_LEFT -> OneWayTile(OneWayTile.Direction.LEFT)
				TEXT_ONEWAY_DOWN -> OneWayTile(OneWayTile.Direction.DOWN)
				else -> Tile()
			}
			cb.setOrigin(Align.center)
			add(cb)
			buttons.add(cb)

			if((tileId+1)%tableWidth == 0) {
				row()
			}
		}

		this.layout()

		// Input handling:
		// Rather than assign a ClickListener to each of the children, we'll just handle the click callback ourselves.
		val parent = this;
		this.addListener(object : ClickListener() {
			override fun touchUp(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int) {
				super.touchUp(event, x, y, pointer, button)
				// X and Y are relative to this object's origin, which I think is lower-left no matter where it is.
				// Transform X into the percent of the total height, then into the X Grid coord.  Same with Y.
				val gridX = ((x/parent.width)*tableWidth).toInt()
				val gridY = (tableHeight-1) - (((y/parent.height)*tableHeight).toInt())
				if(gridX >= 0 && gridX < tableWidth && gridY >= 0 && gridY < tableHeight) {
					handleClick(buttons[gridPositionToIndex(gridX, gridY)])
				}
			}
		})
	}

	private fun indexToGridPosition(index:Int): Pair<Int, Int> = Pair(index%tableWidth, index/tableWidth)
	private fun gridPositionToIndex(x:Int, y:Int): Int = x + y*tableWidth

	private fun handleClick(from:Tile) {
		// If pending toggles is NOT empty, this click happened as a result of the program switching stuff.
		if(pendingToggles.isEmpty()) {
			if(from !is LightTile) {
				return;
			}

			this.taps += 1 // Keep track of this for our top score.  Lower is better.

			// Disable this while we're showing the animation.
			this.touchable = Touchable.disabled

			// Propagate this click to the neighbors.
			val (x, y) = indexToGridPosition(buttons.indexOf(from))

			// Interlace the items growing outwards in the cardinal directions from this point.
			// We don't just toggle all these because we want to add JUICE!
			val rightList = mutableListOf<LightTile>()
			for(x2 in x+1 until tableWidth) {
				val next = buttons[gridPositionToIndex(x2, y)]
				if(next is LightTile) {
					rightList.add(next)
				} else if(next is OneWayTile && next.direction == OneWayTile.Direction.RIGHT) {
					// Pass, but don't break.
				} else {
					break
				}
			}
			val leftList = mutableListOf<LightTile>()
			for(x2 in x-1 downTo 0) {
				val next = buttons[gridPositionToIndex(x2, y)]
				if(next is LightTile) {
					leftList.add(next)
				} else if(next is OneWayTile && next.direction == OneWayTile.Direction.LEFT) {
					// Pass, but don't break.
				} else {
					break
				}
			}
			val downList = mutableListOf<LightTile>()
			for(y2 in y+1 until tableHeight) {
				val next = buttons[gridPositionToIndex(x, y2)]
				if(next is LightTile) {
					downList.add(next)
				} else if(next is OneWayTile && next.direction == OneWayTile.Direction.DOWN) {
					// Pass, but don't break.
				} else {
					break
				}
			}
			val upList = mutableListOf<LightTile>()
			for(y2 in y-1 downTo 0) {
				val next = buttons[gridPositionToIndex(x, y2)]
				if(next is LightTile) {
					upList.add(next)
				} else if(next is OneWayTile && next.direction == OneWayTile.Direction.UP) {
					// Pass, but don't break.
				} else {
					break
				}
			}

			// Now push all the toggle events, interpolating each direction.
			pendingToggles.add(from)
			while(upList.isNotEmpty() || downList.isNotEmpty() || rightList.isNotEmpty() || leftList.isNotEmpty()) {
				if(rightList.isNotEmpty()) { pendingToggles.add(rightList.removeAt(0)) }
				if(upList.isNotEmpty()) { pendingToggles.add(upList.removeAt(0)) }
				if(leftList.isNotEmpty()) { pendingToggles.add(leftList.removeAt(0)) }
				if(downList.isNotEmpty()) { pendingToggles.add(downList.removeAt(0)) }
			}
		}
	}

	fun isComplete():Boolean = buttons.all { b -> (b is LightTile && b.lit) || b !is LightTile } && TweenManager.activeTweens.isEmpty()

	override fun act(delta: Float) {
		super.act(delta)

		if(pendingToggles.isNotEmpty() && delayToNextToggle <= 0f) {
			pendingToggles[0].lit = !pendingToggles[0].lit // DO THIS FIRST!
			val t = pendingToggles[0]
			TweenManager.activeTweens.add(MultiStopTween(0.3f, floatArrayOf(1.0f, 1.2f, 1.0f), { f -> t.setScale(f)}))

			// Pop only AFTER this is completed so we don't hit an endless loop on the last operation.
			val lightTile = pendingToggles.removeAt(0)
			delayToNextToggle = delayToggle

			// If that was the last one, reenable touchable.
			if(pendingToggles.isEmpty()) {
				this.touchable = Touchable.enabled
			}
		}
		delayToNextToggle -= delta
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