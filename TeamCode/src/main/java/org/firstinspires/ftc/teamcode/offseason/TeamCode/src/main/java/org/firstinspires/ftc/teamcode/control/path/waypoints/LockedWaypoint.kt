package org.firstinspires.ftc.teamcode.control.path.waypoints

import org.firstinspires.ftc.teamcode.control.path.funcs.Func
import org.firstinspires.ftc.teamcode.util.math.Angle

open class LockedWaypoint(
    x: Double,
    y: Double,
    followDistance: Double,
    var h: Angle,
    func: Func? = null
) : Waypoint(x, y, followDistance, func) {

    override val copy: Waypoint get() = LockedWaypoint(x, y, followDistance, h, func)
    override fun toString(): String {
        return String.format(
            "%.1f, %.1f, %.1f, %s, %s",
            x,
            y,
            followDistance,
            h,
            func
        )
    }
}
