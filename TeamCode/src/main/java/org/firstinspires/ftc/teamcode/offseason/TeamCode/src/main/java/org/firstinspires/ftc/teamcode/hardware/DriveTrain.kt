package org.firstinspires.ftc.teamcode.hardware

import com.qualcomm.robotcore.hardware.DcMotor
import org.firstinspires.ftc.teamcode.util.math.Angle
import org.firstinspires.ftc.teamcode.util.math.AngleUnit
import org.firstinspires.ftc.teamcode.util.math.Point
import org.firstinspires.ftc.teamcode.util.math.Pose
import org.firstinspires.ftc.teamcode.util.opmode.AzusaTelemetry
import org.openftc.revextensions2.ExpansionHubMotor
import kotlin.math.absoluteValue

class DriveTrain(
    frontLeft: ExpansionHubMotor,
    frontRight: ExpansionHubMotor,
    backLeft: ExpansionHubMotor,
    backRight: ExpansionHubMotor
) : Hardware() {

    var powers: Pose
    private val motors = arrayOf(frontLeft, frontRight, backLeft, backRight)

    override fun update(azuTelemetry: AzusaTelemetry) {
        val rawFrontLeft: Double = -powers.y - powers.x + powers.h.angle
        val rawFrontRight: Double = powers.y - powers.x + powers.h.angle
        val rawBackLeft: Double = -powers.y + powers.x + powers.h.angle
        val rawBackRight: Double = powers.y + powers.x + powers.h.angle

        var rawPowers = listOf(rawFrontLeft, rawFrontRight, rawBackLeft, rawBackRight)
        val max: Double = rawPowers.map { it.absoluteValue }.maxOrNull()!!
        if (max > 1.0) rawPowers = rawPowers.map { it / max }

        motors.forEachIndexed { i, it -> it.power = rawPowers[i] }
    }

    fun setZeroPowers() {
        powers = Pose(Point.ORIGIN, Angle(0.0, AngleUnit.RAW))
    }

    init {
        for (m in motors) {
            m.mode = DcMotor.RunMode.RUN_WITHOUT_ENCODER
            m.zeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE
        }
        powers = Pose(Point.ORIGIN, Angle(0.0, AngleUnit.RAW))
    }
}
