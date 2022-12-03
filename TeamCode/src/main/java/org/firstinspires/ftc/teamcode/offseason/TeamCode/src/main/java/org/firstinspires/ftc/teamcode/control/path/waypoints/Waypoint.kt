package org.firstinspires.ftc.teamcode.control.path.waypoints

import org.firstinspires.ftc.teamcode.control.path.funcs.Func
import org.firstinspires.ftc.teamcode.util.math.Point
import org.firstinspires.ftc.teamcode.util.math.Pose

// id love for this to be a dataclass but yeah sucks to suck
open class Waypoint(
    var x: Double = 0.0,
    var y: Double = 0.0,
    val followDistance: Double = 0.0,
    val func: Func? = null
) {
    val p get() = Point(x, y)

    open val copy: Waypoint get() = Waypoint(x, y, followDistance, func)
    fun distance(p2: Waypoint) = p.distance(p2.p)
    fun distance(p2: Pose) = p.distance(p2.p)

    override fun toString(): String {
        return String.format(
            "%.1f, %.1f, %.1f, %s",
            x,
            y,
            followDistance,
            func
        )
    }
}
