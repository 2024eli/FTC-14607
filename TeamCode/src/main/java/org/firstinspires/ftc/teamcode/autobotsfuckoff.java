package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.exception.RobotCoreException;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import org.firstinspires.ftc.teamcode.HardwareController;

@Autonomous(name = "autobots")
public class autobotsfuckoff extends LinearOpMode {
    HardwareController control;

    @Override
    public void runOpMode() {
        waitForStart();
        control = new HardwareController(hardwareMap, this, telemetry);

        control.frontLeft.setDirection(DcMotorEx.Direction.REVERSE);
        control.backLeft.setDirection(DcMotorEx.Direction.REVERSE);

        waitForStart();
        control.frontRight.setPower(0.5);
        control.frontLeft.setPower(0.5);
        control.backLeft.setPower(0.5);
        control.backRight.setPower(0.5);
        sleep(1000);


    }
}