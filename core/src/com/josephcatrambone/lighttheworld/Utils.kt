package com.josephcatrambone.lighttheworld

import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

// Parallel execution of collection operations.
fun <T, R> Iterable<T>.pmap(
		numThreads: Int = Runtime.getRuntime().availableProcessors() - 2,
		exec: ExecutorService = Executors.newFixedThreadPool(numThreads),
		transform: (T) -> R
	): List<R> {

	// default size is just an inlined version of kotlin.collections.collectionSizeOrDefault
	val defaultSize = if (this is Collection<*>) this.size else 10
	val destination = Collections.synchronizedList(ArrayList<R>(defaultSize))

	for (item in this) {
		exec.submit { destination.add(transform(item)) }
	}

	exec.shutdown()
	exec.awaitTermination(1, TimeUnit.DAYS)

	return ArrayList<R>(destination)
}

open class Tween(val time:Float, val updateFunction: (Float) -> Unit) {
	protected var accumulator = 0f

	val dead:Boolean
		get() = accumulator >= time

	open fun update(dt:Float) {
		this.accumulator += dt
	}
}

class BasicTween(time:Float, val startValue:Float, val stopValue:Float, updateFunction: (Float) -> Unit) : Tween(time, updateFunction) {
	override fun update(dt:Float) {
		super.update(dt)
		if(!dead) {
			// Interpolate from 'from' to 'to'.
			val totalTimeRatio = this.accumulator / time
			// Calculate the stop in which we find ourselves.
			updateFunction(totalTimeRatio*stopValue + (1.0f-totalTimeRatio)*startValue)
		}
	}
}

class MultiStopTween(time:Float, val stops:FloatArray, updateFunction: (Float) -> Unit) : Tween(time, updateFunction) {
	private val stopSize = time/stops.size.toFloat()

	override fun update(dt:Float) {
		super.update(dt)
		if(!dead) {
			// Interpolate from 'from' to 'to'.
			val totalTimeRatio = this.accumulator / time
			// Calculate the stop in which we find ourselves.
			val stopIndex:Int = (totalTimeRatio*stops.size).toInt()
			if(stopIndex < stops.size-1) {
				// Find the interval of THIS ratio.
				val intervalTimeRatio = (this.accumulator - (stopIndex * stopSize)) / stopSize
				val amount = intervalTimeRatio * stops[stopIndex + 1] + (1.0f - intervalTimeRatio) * stops[stopIndex]
				updateFunction(amount)
			} else {
				updateFunction(stops.last())
			}
		}
	}
}

class TweenManager {
	companion object {
		@JvmStatic
		val activeTweens = mutableListOf<Tween>()

		@JvmStatic
		fun add(t:Tween) {
			TweenManager.activeTweens.add(t)
		}

		@JvmStatic
		fun update(dt: Float) {
			TweenManager.activeTweens.forEach { t:Tween ->
				t.update(dt)
			}

			TweenManager.activeTweens.removeIf({ t:Tween -> t.dead })

			if(TweenManager.activeTweens.isNotEmpty()) {
				println("${activeTweens.size} active tweens.")
			}
		}
	}
}
