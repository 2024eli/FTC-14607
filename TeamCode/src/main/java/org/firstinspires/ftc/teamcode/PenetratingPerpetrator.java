package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
//import org.firstinspires.ftc.teamcode.hardware.Hardware;

@Autonomous(name = "test auto")
public class PenetratingPerpetrator extends LinearOpMode {
    HardwareController control = new HardwareController(hardwareMap, this, telemetry);


    @Override
    public void runOpMode() {
        waitForStart();

        control.setSlidePos(10);


        telemetry.addLine("Opmode Running");
        telemetry.update();
    }
}