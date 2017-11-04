package com.josephcatrambone.lighttheworld.screens

abstract class Screen {
	abstract fun update(deltaTime: Float)
	abstract fun render()
	open fun restore() {}
	open fun dispose() {}
}