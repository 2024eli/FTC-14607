package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.exception.RobotCoreException;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.Servo;

@TeleOp(name = "Main Teleop")
public class TelepathicReSUScitation extends LinearOpMode {
    HardwareController control;
    final float slowSpeed = 0.3f;
    final float midSpeed = 0.5f;
    final float normalSpeed = 0.8f;
    private boolean wasYPressed = false;
    private boolean isLifted = false;


    @Override
    public void runOpMode() {
        control = new HardwareController(hardwareMap, this, telemetry);

        Gamepad currentGamepad1 = new Gamepad();
        Gamepad prevGamepad1 = new Gamepad();
        Gamepad currentGamepad2 = new Gamepad();
        Gamepad prevGamepad2 = new Gamepad();

        boolean slowMode = false;
        float speedFactor = slowSpeed;
        control.frontLeft.setDirection(DcMotorEx.Direction.REVERSE);
        control.backLeft.setDirection(DcMotorEx.Direction.REVERSE);

        control.rightSlide.setDirection(DcMotorEx.Direction.REVERSE);

        waitForStart();

        while (opModeIsActive()) {
            prevGamepad1.copy(currentGamepad1);
            currentGamepad1.copy(gamepad1);
            prevGamepad2.copy(currentGamepad2);
            currentGamepad2.copy(gamepad2);

            // ----------------------------------- Gamepad 1----------------------------------------
            // drivetrain
            if (gamepad1.dpad_up) speedFactor = normalSpeed;
            else if (gamepad1.dpad_down) speedFactor = slowSpeed;

            float y = -gamepad1.left_stick_y * speedFactor;
            float x = gamepad1.left_stick_x * midSpeed;
            float rx = gamepad1.right_stick_x * speedFactor;

            float frontLeftPower = (y + x + rx);
            float backLeftPower = (y - x + rx) * 1.25f;
            float frontRightPower = (y - x - rx);
            float backRightPower = (y + x - rx) * 1.3f;

            control.frontLeft.setPower(frontLeftPower);
            control.backLeft.setPower(backLeftPower);
            control.frontRight.setPower(frontRightPower);
            control.backRight.setPower(backRightPower);

            if (gamepad1.a) control.clawClose();
            else if (gamepad1.b) control.clawOpen();

            // ----------------------------------- Gamepad 2----------------------------------------
            // claw
            // swivel
            if (gamepad2.left_bumper && gamepad2.right_bumper) control.setSwivel(0.666);
            else if (gamepad2.right_bumper) control.setSwivel(control.swivel.getPosition() + 0.02);
            else if (gamepad2.left_bumper) control.setSwivel(control.swivel.getPosition() - 0.02);

            //lift
            if (gamepad2.right_trigger > 0) control.setLift(control.lift.getPosition() + 0.05);
            else if (gamepad2.left_trigger > 0) control.setLift(control.lift.getPosition() - 0.05);

            //slides alex's
            control.rightSlide.setPower((gamepad2.left_stick_y)*0.95);
            control.leftSlide.setPower((gamepad2.left_stick_y)*0.95);
            control.rightSlide.setPower((gamepad2.right_stick_y)*0.4);
            control.leftSlide.setPower((gamepad2.right_stick_y)*0.4);


//            if (gamepad2.left_stick_y > 0) {
//                control.leftSlide.setTargetPosition(control.leftSlide.getCurrentPosition() - 50);
//                control.leftSlide.setMode(DcMotor.RunMode.RUN_zWITHOUT_ENCODER);
//                control.leftSlide.setPower(-.1);
//                control.rightSlide.setTargetPosition(control.rightSlide.getCurrentPosition() + 50);
//                control.rightSlide.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
//                control.rightSlide.setPower(1);
//            } else if (gamepad2.left_stick_y < 0) {
//                control.leftSlide.setTargetPosition(control.leftSlide.getCurrentPosition() + 50);
//                control.leftSlide.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
//                control.leftSlide.setPower(-.1);
//                control.rightSlide.setTargetPosition(control.rightSlide.getCurrentPosition() - 50);
//                control.rightSlide.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
//                control.rightSlide.setPower(1);
//
//                telemetry.addLine(String.valueOf(control.leftSlide.getPowerFloat()));
//                telemetry.update();
//            }
        }
    }
}