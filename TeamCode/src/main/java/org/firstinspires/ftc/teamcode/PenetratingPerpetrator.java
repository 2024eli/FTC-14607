package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.teamcode.Robots.BumbleBee;

@Autonomous(name = "test auto", group="Test")
public class PenetratingPerpetrator extends LinearOpMode {
    BumbleBee control;

    @Override
    public void runOpMode() {
        control = new BumbleBee(hardwareMap, this, telemetry);

        waitForStart();
        telemetry.addLine("waiting");
        telemetry.update();
        sleep(1000);

        control.rotate(90);

        sleep(2000);



        telemetry.addLine("Opmode Running");
        telemetry.update();

    }
}