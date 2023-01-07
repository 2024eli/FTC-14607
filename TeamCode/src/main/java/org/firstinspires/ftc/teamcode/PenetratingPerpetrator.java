package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;

@Autonomous(name = "test auto", group="Test")
public class PenetratingPerpetrator extends LinearOpMode {
    HardwareController control;

    @Override
    public void runOpMode() {
        control = new HardwareController(hardwareMap, this, telemetry);

        waitForStart();
        telemetry.addLine("waiting");
        telemetry.update();
        sleep(1000);

        control.forward(30, 250);
        while (control.frontRight.isBusy()) {

        }



        telemetry.addLine("Opmode Running");
        telemetry.update();

    }
}