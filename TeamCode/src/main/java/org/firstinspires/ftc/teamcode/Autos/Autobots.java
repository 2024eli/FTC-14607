package org.firstinspires.ftc.teamcode.Autos;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import org.firstinspires.ftc.teamcode.Robots.BumbleBee;

@Autonomous(name = "Backward - setPower", group="MzBackup")
public class Autobots extends LinearOpMode {
    BumbleBee control;

    @Override
    public void runOpMode() {
        waitForStart();
        control = new BumbleBee(hardwareMap, this, telemetry);
        control.frontLeft.setDirection(DcMotorEx.Direction.REVERSE);
        control.backLeft.setDirection(DcMotorEx.Direction.REVERSE);

        waitForStart();
        control.frontRight.setPower(-0.5);
        control.frontLeft.setPower(-0.5);
        control.backLeft.setPower(-0.5);
        control.backRight.setPower(-0.5);
        sleep(1000);
    }
}