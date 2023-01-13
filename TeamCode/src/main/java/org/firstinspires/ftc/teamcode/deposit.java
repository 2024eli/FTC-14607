package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;

@Autonomous(name = "deposit auto")
public class deposit extends LinearOpMode {
    HardwareController control;

    @Override
    public void runOpMode() {
        control = new HardwareController(hardwareMap, this, telemetry);

        control.clawClose();
        sleep(500);
        control.setSwivel(0.66);
        control.setLift(1);
        waitForStart();

        control.forward(11.5, 250);
        for( DcMotorEx m : control.drivetrain) { m.setVelocity(0); }
        control.setSlidePos(HardwareController.SHORTPOLE);
        sleep(1000);
        control.setSwivel(0.4);
        control.setLift(0.1);
        sleep(1000);
        control.clawOpen();
        sleep(500);
        control.setSwivel(0.66);
        control.setLift(1);
        sleep(1000);
        control.setSlidePos(HardwareController.GROUND);
        control.right(120, 250);

        while(control.frontRight.isBusy()) {}
        sleep(1500);

    }
}