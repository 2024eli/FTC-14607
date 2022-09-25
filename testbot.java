package org.firstinspires.ftc.teamcode;

import java.util.ArrayList;
import java.util.HashMap;
import java.lang.Math;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import org.firstinspires.ftc.robotcore.external.Telemetry;

@TeleOp(name = "TeleOp")
public class testbot extends LinearOpMode {

    @Override
    public void runOpMode() {
        DcMotorEx frontLeft = hardwareMap.get(DcMotorEx.class, "frontLeft");
        DcMotorEx backLeft = hardwareMap.get(DcMotorEx.class, "backLeft");
        DcMotorEx backRight = hardwareMap.get(DcMotorEx.class, "backRight");
        DcMotorEx frontRight = hardwareMap.get(DcMotorEx.class, "frontRight");
        Servo servo = hardwareMap.get(Servo.class, "servo1");
        waitForStart();

        while(opModeIsActive()) {
            double y = -gamepad1.left_stick_y;
            double x = gamepad1.left_stick_x * 1.1;
            double rx = gamepad1.right_stick_x;


            double frontLeftPower = (y + x + rx);
            double backLeftPower = (y - x + rx);
            double frontRightPower = (y - x - rx);
            double backRightPower = (y + x - rx);

            frontLeft.setPower(-frontLeftPower);
            backLeft.setPower(backLeftPower);
            frontRight.setPower(frontRightPower);
            backRight.setPower(backRightPower);

//            float adjusted_left_stick_y = gamepad2.left_stick_y;
//            float adjusted_left_stick_x = gamepad2.left_stick_x;
//            float adjusted_right_stick_x = gamepad2.right_stick_x;
//            bottomLeft.setPower(-adjusted_left_stick_y - adjusted_left_stick_x - adjusted_right_stick_x);
//            bottomRight.setPower(-adjusted_left_stick_y + adjusted_left_stick_x + adjusted_right_stick_x);
//            frontLeft.setPower(-adjusted_left_stick_y + adjusted_left_stick_x - adjusted_right_stick_x);
//            frontRight.setPower(-adjusted_left_stick_y - adjusted_left_stick_x + adjusted_right_stick_x);

            telemetry.addLine("Opmode Running");
            telemetry.update();
        }
    }
}