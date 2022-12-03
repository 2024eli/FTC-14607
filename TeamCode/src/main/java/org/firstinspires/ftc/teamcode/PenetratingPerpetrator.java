package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
//import org.firstinspires.ftc.teamcode.hardware.Hardware;

@Autonomous(name = "test auto")
public class PenetratingPerpetrator extends LinearOpMode {
    HardwareController control;

    @Override
    public void runOpMode() {
        control = new HardwareController(hardwareMap, this, telemetry);
        waitForStart();

        control.forward(10, 1000);


        telemetry.addLine("Opmode Running");
        telemetry.update();
    }
}