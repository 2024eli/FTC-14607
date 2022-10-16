package org.firstinspires.ftc.teamcode.hardware

import org.firstinspires.ftc.teamcode.util.opmode.OpModePacket
import java.util.*

abstract class BaseRobot(val dataPacket: OpModePacket) {
    abstract fun init()
    abstract fun update()
    val allHardware: ArrayList<Hardware> = ArrayList()
}
