package org.firstinspires.ftc.teamcode;


import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;

@Autonomous(name = "forwardauto")
public class goforward extends LinearOpMode {
    HardwareController control;

    @Override
    public void runOpMode() {
        control = new HardwareController(hardwareMap, this, telemetry);


        control.clawClose();
        control.setLift(1);
        waitForStart();


        control.forward(120, 250);

        while(control.frontRight.isBusy()) {}

    }
}