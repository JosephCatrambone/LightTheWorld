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

class Tween(val time:Float, val stops:FloatArray, val updateFunction: (Float) -> Unit) {
	private var accumulator = 0f
	private val stopSize = time/stops.size.toFloat()
	val dead:Boolean = accumulator > time

	fun update(dt:Float) {
		if(!dead) {
			this.accumulator += dt
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

			TweenManager.activeTweens.removeIf({ it.dead })
		}
	}
}
