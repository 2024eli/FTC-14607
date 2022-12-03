package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.exception.RobotCoreException;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.Servo;

@TeleOp(name = "Main Teleop")
public class TelepathicReSUScitation extends LinearOpMode {

    @Override
    public void runOpMode() {
        DcMotorEx frontLeft = hardwareMap.get(DcMotorEx.class, "frontLeft");
        DcMotorEx backLeft = hardwareMap.get(DcMotorEx.class, "backLeft");
        DcMotorEx backRight = hardwareMap.get(DcMotorEx.class, "backRight");
        DcMotorEx frontRight = hardwareMap.get(DcMotorEx.class, "frontRight");
        //Servo servo = hardwareMap.get(Servo.class, "servo1");

        Gamepad currentGamepad = new Gamepad();
        Gamepad prevGamepad = new Gamepad();

        waitForStart();

        while(opModeIsActive()) {

            prevGamepad.copy(currentGamepad);
            currentGamepad.copy(gamepad1);
            double y = -gamepad1.left_stick_y * 0.6;
            double x = gamepad1.left_stick_x * 0.6;
            double rx = gamepad1.right_stick_x * 0.6;


            double frontLeftPower = (y + x + rx) * 0.5;
            double backLeftPower = (y - x + rx) * -1.26 * 0.;
            double frontRightPower = (y - x - rx);
            double backRightPower = (y + x - rx) * 1.3;

            frontLeft.setPower(-frontLeftPower);
            backLeft.setPower(backLeftPower);
            frontRight.setPower(frontRightPower);
            backRight.setPower(backRightPower);

//            if (currentGamepad.a && !prevGamepad.a) {
//                servo.setPosition(servo.getPosition() + 0.1);
//            }
//            if (currentGamepad.b && !prevGamepad.b) {
//                servo.setPosition(servo.getPosition() - 0.1);
//            }

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