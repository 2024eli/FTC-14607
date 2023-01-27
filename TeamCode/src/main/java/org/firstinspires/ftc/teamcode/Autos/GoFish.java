package org.firstinspires.ftc.teamcode.Autos;


import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.teamcode.Robots.BumbleBee;

@Autonomous(name = "Forward - setTargetPosition", group="Backup")
public class GoFish extends LinearOpMode {
    BumbleBee control;

    @Override
    public void runOpMode() {
        control = new BumbleBee(hardwareMap, this, telemetry);
        control.clawClose();
        control.setLift(1);
        waitForStart();

        control.backward(110, 250);
        while(control.frontRight.isBusy()) {}
    }
}