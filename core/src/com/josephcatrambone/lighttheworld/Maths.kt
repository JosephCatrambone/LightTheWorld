package com.josephcatrambone.lighttheworld

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3

/**
 * Created by Jo on 2017-07-09.
 * Copied: 2017/11/04
 */
class Vec(var x:Float=0f, var y:Float=0f, var z:Float=0f, var w:Float=0f) {

	// libGDX Interop section.
	constructor(v2: Vector2): this( v2.x, v2.y, 0f, 0f)
	constructor(v3: Vector3):this(v3.x, v3.y, v3.z, 0f)

	fun toGDXVector2(): Vector2 = Vector2(this.x, this.y)
	fun toGDXVector3(): Vector3 = Vector3(this.x, this.y, this.z)
	// End libGDX interop section

	val isZero:Boolean
		get():Boolean = x==0f && y==0f && z==0f && w==0f

	val squaredMagnitude:Float
		get():Float = this.dot(this)

	val magnitude:Float
		get():Float = Math.sqrt(this.squaredMagnitude.toDouble()).toFloat()

	var data:FloatArray
		get() = floatArrayOf(x, y, z, w)
		set(value:FloatArray) {
			this.x = value.getOrElse(0, {_ -> 0f})
			this.y = value.getOrElse(1, {_ -> 0f})
			this.z = value.getOrElse(2, {_ -> 0f})
			this.w = value.getOrElse(3, {_ -> 0f})
		}

	operator fun plus(value:Float):Vec = Vec(this.x+value, this.y+value, this.z+value, this.w+value)
	operator fun minus(value:Float):Vec = Vec(this.x-value, this.y-value, this.z-value, this.w-value)
	operator fun times(value:Float):Vec = Vec(this.x*value, this.y*value, this.z*value, this.w*value)
	operator fun div(value:Float):Vec = Vec(this.x/value, this.y/value, this.z/value, this.w/value)

	operator fun plus(other:Vec):Vec = Vec(this.x+other.x, this.y+other.y, this.z+other.z, this.w+other.w)
	operator fun minus(other:Vec):Vec = Vec(this.x-other.x, this.y-other.y, this.z-other.z, this.w-other.w)
	operator fun times(other:Vec):Vec = Vec(this.x*other.x, this.y*other.y, this.z*other.z, this.w*other.w)
	operator fun div(other:Vec):Vec = Vec(this.x/other.x, this.y/other.y, this.z/other.z, this.w/other.w) // TODO: This probably shouldn't exist because of the default zeros.

	fun sum():Float {
		return x+y+z+w
	}

	fun dot(other:Vec):Float {
		return (this * other).sum()
	}

	// Perform the cross product with this as a vector2
	fun cross2(other:Vec):Float = x*other.y - y*other.x

	fun cross3(other:Vec):Vec {
		TODO()
	}

	fun normalized():Vec {
		// elements / sqrt(sum(elem ^2))
		val mag = this.magnitude
		// If we have no magnitude, just return a zero vec.
		if(mag == 0f) {
			TODO("Unhandled case: Normalizing zero-length vector")
		}

		return Vec(x/mag, y/mag, z/mag, w/mag)
	}

	fun normalize() {
		val mag = this.magnitude
		if(mag == 0f) { TODO() }
		x /= mag
		y /= mag
		z /= mag
		w /= mag
	}

	fun distanceSquared(other:Vec): Float {
		val delta = this - other
		return delta.x*delta.x + delta.y*delta.y + delta.z*delta.z + delta.w*delta.w
	}

	fun project(other:Vec): Vec {
		// Project this vector onto the other.
		// (A dot norm(b)) * norm(b)
		// or
		// (a dot b) / (b dot b) * b
		val bNorm = other.normalized()
		return bNorm * this.dot(bNorm)
	}
}

class Line(val start:Vec, val end:Vec) {
	/***
	 *
	 */
	fun intersection2D(other:Line, epsilon:Float = 1e-8f): Vec? {
		// Begin with line-line intersection.
		val determinant = ((start.x-end.x)*(other.start.y-other.end.y))-((start.y-end.y)*(other.start.x-other.end.x))
		if(Math.abs(determinant.toDouble()).toFloat() < epsilon) {
			return null;
		}

		val candidatePoint = Vec(
			((start.x*end.y - start.y*end.x)*(other.start.x-other.end.x))-((start.x-end.x)*(other.start.x*other.end.y - other.start.y*other.end.x)),
			((start.x*end.y - start.y*end.x)*(other.start.y-other.end.y))-((start.y-end.y)*(other.start.x*other.end.y - other.start.y*other.end.x))
		)/determinant

		// If the lines are infinite, we're done.  No more work.
		return candidatePoint
	}

	fun segmentIntersection2D(other:Line): Vec? {
		val a = this.start
		val b = this.end
		val c = other.start
		val d = other.end

		val r = b-a
		val s = d-c

		val rxs = r.cross2(s)
		val t:Float = (c-a).cross2(s)/rxs
		val u:Float = (c-a).cross2(r)/rxs

		if(t < 0 || t > 1 || u < 0 || u > 1) {
			return null;
		}
		return a + r*t
	}

	fun pointOnLine(pt:Vec, epsilon:Float = 1e-6f):Boolean {
		// Is this point a solution to this line?
		if(epsilon == 0f) {
			// THIS IS A BAD IDEA!  NUMERICAL PRECISION IS A FACTOR!
			// p1 + t*(p2-p1) = pt?
			// Just solve for t, and if the value is between 0 and 1, it's on the line.
			// t*(p2-p1) = pt - p1
			// t = (pt - p1)/(p2-p1)
			// Unfortunately, we've gotta' do this component-wise.
			val tX = (pt.x - start.x) / (end.x - start.x)
			if (tX < 0 || tX > 1) {
				return false
			}
			val tY = (pt.y - start.y) / (end.y - start.y)
			if (tY < 0 || tY > 1) {
				return false
			}
			return true
		} else {
			TODO("Bugfix")
			return ((Math.abs(((end.y-start.y)*pt.x - (end.x-start.x)*pt.y + end.x*start.y - end.y*start.x).toDouble()))/Math.sqrt(((end.x-start.x)*(end.x-start.x) + (end.y-start.y)*(end.y-start.y)).toDouble()).toFloat()) < epsilon
		}
	}
}

class AABB(val x:Float, val y:Float, val w:Float, val h:Float) {

	val center = Vec(x+(w/2), y+(h/2))

	fun overlaps(other:AABB):Boolean {
		// This Right < Other Left
		if(x+w < other.x) {
			return false
		}
		// thisTop < otherBottom
		if(y+h < other.y) {
			return false
		}
		// thisLeft > otherRight
		if(x > other.x+other.w) {
			return false
		}
		// thisBottom > otherTop
		if(y > other.y+other.h) {
			return false
		}
		return true
	}

	fun pointInside(otherX:Float, otherY:Float):Boolean {
		return otherX > this.x && otherX < (this.x+this.w) && otherY > this.y && otherY < (this.y+this.h)
	}

	// Returns the smallest vector required to push the other AABB out of this one.
	// NOTE: Assumes that these AABBs overlap.
	fun getPushForce(other:AABB): Vec {
		// For each axis, calculate the overlap and keep the smallest one.
		val otherCenter = other.center
		val thisCenter = this.center
		val zeroOverlapX = this.w/2 + other.w/2 // Sum of half-widths.
		val dx = otherCenter.x - thisCenter.x
		val forceX = zeroOverlapX - Math.abs(dx) // If we apply forceX to the other object, it will bring it to this one's surface.
		val zeroOverlapY = this.h/2 + other.h/2
		val dy = otherCenter.y - thisCenter.y
		val forceY = zeroOverlapY - Math.abs(dy)
		if(Math.abs(forceX) < Math.abs(forceY)) {
			return Vec(Math.copySign(forceX, dx))
		} else {
			return Vec(0f, Math.copySign(forceY, dy))
		}
	}
}

class Triangle(val a:Vec, val b:Vec, val c:Vec) {
	fun pointInTriangle2D(p:Vec):Boolean {
		/*
		// Any point p where [B-A] cross [p-A] does not point in the same direction as [B-A] cross [C-A] isn't inside the triangle.
		val pPrime = p-a
		val q = b-a
		val r = c-a
		if((q.cross2(pPrime) >= 0) != (q.cross2(r) >= 0)) {
			return false // Can't be
		}
		// One more check now.  Need to verify it's inside the other sides, too.
		val qPrime = p-b
		val s = a-b
		val t = c-b
		val qpos = s.cross2(qPrime)
		val dpos = s.cross2(t)
		return (qpos >= 0) == (dpos >= 0)
		*/
		// Barycentric coordinate version from Realtime Collision Detection.
		// Compute vectors
		val v0 = c-a
		val v1 = b-a
		val v2 = p-a

		// Compute dot products
		val dot00 = v0.dot(v0)
		val dot01 = v0.dot(v1)
		val dot02 = v0.dot(v2)
		val dot11 = v1.dot(v1)
		val dot12 = v1.dot(v2)

		// Compute barycentric coordinates
		val denom = (dot00 * dot11 - dot01 * dot01)
		if(denom == 0f) { // degenerate.
			return false
		}
		val invDenom = 1 / denom
		val u = (dot11 * dot02 - dot01 * dot12) * invDenom
		val v = (dot00 * dot12 - dot01 * dot02) * invDenom

		// Check if point is in triangle
		return (u >= 0) && (v >= 0) && (u + v < 1)
	}
}

class Polygon(val points:List<Vec>) {
	fun pointInside(pt:Vec): Boolean {
		TODO()
	}

	fun splitAtPoints(p1:Vec, p2:Vec, vararg innerPoints:Vec): Pair<Polygon, Polygon> {
		// Split the polygon with a fracture starting from p1 and running to p2.  Two polygons will be returned.
		// If innerPoints are returned, they will be included in both polygons between the fracture points.
		// Both p1 and p2 will be present in both sections.

		// Go around the points and find the indices of the cut.
		var p1Index=-1
		var p1Distance = Float.MAX_VALUE
		var p2Index=-1
		var p2Distance = Float.MAX_VALUE
		for(i in 0 until points.size) {
			// Get the distance from this point to p1.
			val distToP1 = p1.distanceSquared(points[i])
			val distToP2 = p2.distanceSquared(points[i])
			if(distToP1 < p1Distance) {
				p1Index = i
				p1Distance = distToP1
			}
			if(distToP2 < p2Distance) {
				p2Index = i
				p2Distance = distToP2
			}
		}

		// Make sure that p1 comes first.
		if(p2Index < p1Index) {
			val temp = p1Index
			p1Index = p2Index
			p2Index = temp
		}

		// If we have no internal points, we can just return the two new poligons.
		if(innerPoints.isEmpty()) {
			val leftPoints = points.filterIndexed{ i,v -> i <= p1Index || i >= p2Index }
			val rightPoints = points.filterIndexed{ i,v -> i >= p1Index && i <= p2Index }
			return Pair(Polygon(leftPoints), Polygon(rightPoints))
		} else {
			// There are two ways to assemble the halfs.
			// If the first point in the innerPoint list is closer to the start p1 and the end point is closer to p2, keep straight.  Otherwise flip the inner list.
			// p1 ------------------------- p2
			//     innerpt1 -------- innerp2
			val p1ToSplitHeadDistance = points[p1Index].distanceSquared(innerPoints.first())
			val p1ToSplitTailDistance = points[p1Index].distanceSquared(innerPoints.last())
			val p2ToSplitHeadDistance = points[p2Index].distanceSquared(innerPoints.first())
			val p2ToSplitTailDistance = points[p2Index].distanceSquared(innerPoints.last())
			if(p1ToSplitHeadDistance < p2ToSplitHeadDistance && p2ToSplitTailDistance < p1ToSplitTailDistance) {
				// We should take the points in order.
				val leftPoints = points.filterIndexed{ i,v -> i <= p1Index }.plus(innerPoints).plus(points.filterIndexed{ i,v -> i >= p2Index })
				val rightPoints = innerPoints.reversed().plus(points.filterIndexed{i,v -> i >= p1Index && i <= p2Index})
				return Pair(Polygon(leftPoints), Polygon(rightPoints))
			} else if(p1ToSplitHeadDistance > p2ToSplitHeadDistance && p2ToSplitTailDistance > p1ToSplitTailDistance) {
				// We should take the points reversed.
				val leftPoints = points.filterIndexed{ i,v -> i <= p1Index }.plus(innerPoints.reversed()).plus(points.filterIndexed{ i,v -> i >= p2Index })
				val rightPoints = innerPoints.toList().plus(points.filterIndexed{i,v -> i >= p1Index && i <= p2Index})
				return Pair(Polygon(leftPoints), Polygon(rightPoints))
			} else {
				// It's not clear which way they should rotate.
				throw Exception("Ambiguous split direction.  Polygon can't figure out how to match up the divide.")
			}
		}
	}
}

fun <T> min(vararg values: T): T where T:Comparable<T> {
	var least = values[0]
	values.forEach { v ->
		if(v < least) {
			least = v
		}
	}
	return least
}

fun <T> max(vararg values: T): T where T:Comparable<T> {
	var greatest = values[0]
	values.forEach { v ->
		if(v > greatest) {
			greatest = v
		}
	}
	return greatest
}
