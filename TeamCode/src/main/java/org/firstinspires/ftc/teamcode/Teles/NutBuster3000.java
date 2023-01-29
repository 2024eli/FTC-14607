package org.firstinspires.ftc.teamcode.Teles;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Robots.BumbleBee;

@TeleOp(name = "test tele1", group="Test")
public class NutBuster3000 extends LinearOpMode {
    BumbleBee control;

    @Override
    public void runOpMode() {
        control = new BumbleBee(hardwareMap, this, telemetry);

        waitForStart();
        while(opModeIsActive()) {
            telemetry.addData("First Angle", control.imu.getAngularOrientation().firstAngle);
            telemetry.addData("Second Angle", control.imu.getAngularOrientation().secondAngle);
            telemetry.addData("Third Angle", control.imu.getAngularOrientation().thirdAngle);
            telemetry.update();
        }
    }
}