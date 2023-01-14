package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Robots.BumbleBee;

@TeleOp(name = "test tele", group="Test")
public class NutBuster3000 extends LinearOpMode {
    BumbleBee control;

    @Override
    public void runOpMode() {
        control = new BumbleBee(hardwareMap, this, telemetry);

        waitForStart();
        while(opModeIsActive()) {


            telemetry.addLine("Opmode Running");
            telemetry.update();
        }
    }
}