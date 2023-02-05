package org.firstinspires.ftc.teamcode.Autos;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.hardware.DcMotorEx;

import org.firstinspires.ftc.teamcode.Robots.BumbleBee;

// currently a hail mary auto, 0/10 would not recommend under normal circumstances in a real comp
@Autonomous(name = "Blind Right 1s0m0t", group="Test")
public class HomeDepo extends LinearOpMode {
    BumbleBee control;

    @Override
    public void runOpMode() {
        control = new BumbleBee(hardwareMap, this, telemetry);

        control.clawClose();
        sleep(500);
        control.setSwivel(0.66);
        control.setLift(1);

        waitForStart();
//auto thoomin

        control.forward(12, 250);
        for( DcMotorEx m : control.drivetrain) { m.setVelocity(0); }
        control.setSlidePos(BumbleBee.SHORTPOLE);
        sleep(500);
        control.setSwivel(0.4);
        control.setLift(0.1);
        sleep(1000);
        control.clawOpen();
        sleep(500);
        control.setSwivel(0.66);
        control.setLift(1);
        control.clawClose();
        sleep(500);
        control.setSlidePos(BumbleBee.GROUND);
        //************** done depositing short **********
        control.backward(11, 250);
        sleep(500);
        control.left(60,200);
        sleep(500);
        for( DcMotorEx m : control.drivetrain) { m.setVelocity(0); }
        control.forward(112, 250);
//        //go forward to cone stack
//        for( DcMotorEx m : control.drivetrain) { m.setVelocity(0); }
        sleep(1000);
        control.rotate(-90);
//        sleep(1000);
        control.clawOpen();
        control.setLift(0.12);
        //height of 5 cone stack
        control.setSlidePos(150);
        //go to cone stack
        control.forward(97, 200);
        for( DcMotorEx m : control.drivetrain) { m.setVelocity(0); }
        sleep(1000);
        control.clawClose();
        sleep(1000);
        control.setSlidePos(500);
        sleep(1000);
        control.backward(92,200);
        sleep(1000);
        control.setSlidePos(BumbleBee.TALLPOLE);
        control.setLift(1);
        sleep(3000);
        control.setSwivel(0.4);
        sleep(500);
        control.setLift(0.1);
        sleep(500);
        control.clawOpen();
        sleep(500);
        control.setSwivel(0.66);
        sleep(500);
        control.setSlidePos(BumbleBee.GROUND);

    }
}