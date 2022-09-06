package org.firstinspires.ftc.teamcode.util.math

import com.qualcomm.robotcore.util.Range
import kotlin.math.* // ktlint-disable no-wildcard-imports

object MathUtil {
    const val EPSILON = 1e-6
    const val TAU = 2 * PI

    val Double.toRadians get() = Math.toRadians(this)
    val Double.toDegrees get() = Math.toDegrees(this)

    fun Double.clip(a: Double) = Range.clip(this, -a, a)

    fun rotatePoint(p: Point, h: Angle) = Point(
        h.cos * p.y + h.sin * p.x,
        h.sin * p.y - h.cos * p.x
    )

    fun angleThresh(a: Angle, b: Angle, c: Angle): Boolean {
        return (a - b).wrap().angle.absoluteValue < c.angle
    }

    fun maxify(input: Double, min: Double): Double {
        return when (input) {
            in 0.0..min -> min
            in -min..0.0 -> -min
            else -> input
        }
    }

    fun clipIntersection(start: Point, end: Point, robot: Point): Point {
        if (start.y == end.y)
            start.y += 0.01
        if (start.x == end.x)
            start.x += 0.01

        val m1 = (end.y - start.y) / (end.x - start.x)
        val m2 = -1.0 / m1
        val xClip = (-m2 * robot.x + robot.y + m1 * start.x - start.y) / (m1 - m2)
        val yClip = m1 * (xClip - start.x) + start.y
        return Point(xClip, yClip)
    }

    fun extendLine(firstPoint: Point, secondPoint: Point, distance: Double): Point {
        val lineAngle = (secondPoint - firstPoint).atan2
        val length = secondPoint.distance(firstPoint)
        val extendedLineLength = length + distance
        val extended = secondPoint.copy
        extended.x = lineAngle.cos * extendedLineLength + firstPoint.x
        extended.y = lineAngle.sin * extendedLineLength + firstPoint.y
        return extended
    }

    /**
     * Returns the closest intersection point to the end of a line segment created through the intersection of a line and circle.
     * The main purpose of this is for pure pursuit but I'll prob implement it into our goToPosition algorithm.
     * For pure pursuit use, c would be the clipped robot point, startPoint would be the current segment start point,
     * endPoint would be the current segment end point, and radius would be our follow distance
     *
     * @param center          center point of circle
     * @param startPoint start point of the line segment
     * @param endPoint   end point of the line segment
     * @param radius     radius of the circle
     * @return intersection point closest to endPoint
     * @see [https://mathworld.wolfram.com/Circle-LineIntersection.html](https://mathworld.wolfram.com/Circle-LineIntersection.html)
     */
    fun circleLineIntersection(
        center: Point,
        startPoint: Point,
        endPoint: Point,
        radius: Double
    ): Point {
        val start = startPoint - center
        val end = endPoint - center
        val deltas = end - start
        val d = start.x * end.y - end.x * start.y
        val discriminant = radius.pow(2) * deltas.hypot.pow(2) - d.pow(2)

        // discriminant = 0 for 1 intersection, >0 for 2
        val intersections = ArrayList<Point>()
        val xLeft = d * deltas.y
        val yLeft = -d * deltas.x
        val xRight: Double = stupidSign(deltas.y) * deltas.x * sqrt(discriminant)
        val yRight = deltas.y.absoluteValue * sqrt(discriminant)
        val div = deltas.hypot.pow(2)
        if (discriminant == 0.0) {
            intersections.add(Point(xLeft / div, yLeft / div))
        } else {
            // add 2 points, one with positive right side and one with negative right side
            intersections.add(Point((xLeft + xRight) / div, (yLeft + yRight) / div))
            intersections.add(Point((xLeft - xRight) / div, (yLeft - yRight) / div))
        }
        var closest = Point(69420.0, -10000.0)
        for (p in intersections) { // add circle center offsets
            p.x += center.x
            p.y += center.y
            if (p.distance(endPoint) < closest.distance(endPoint)) closest = p
        }
        return closest
    }

    private fun stupidSign(a: Double): Int = if (a > 0) 1 else -1
}
