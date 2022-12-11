package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.exception.RobotCoreException;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.Servo;

@TeleOp(name = "Main Teleop")
public class TelepathicReSUScitation extends LinearOpMode {
    HardwareController control;
    final float slowSpeed = 0.3f;
    final float normalSpeed = 0.8f;

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

        waitForStart();

        double lastSwivelPos = 0.666;
        while(opModeIsActive()) {
            prevGamepad1.copy(currentGamepad1);
            currentGamepad1.copy(gamepad1);
            prevGamepad2.copy(currentGamepad2);
            currentGamepad2.copy(gamepad2);

            // ----------------------------------- Gamepad 1----------------------------------------
            // drivetrain
            if (gamepad1.dpad_up) speedFactor = normalSpeed;
            else if (gamepad1.dpad_down) speedFactor = slowSpeed;

            float y = -gamepad1.left_stick_y * speedFactor;
            float x = gamepad1.left_stick_x * speedFactor;
            float rx = gamepad1.right_stick_x * speedFactor;

            float frontLeftPower = (y + x + rx) ;
            float backLeftPower = (y - x + rx) * 1.25f;
            float frontRightPower = (y - x - rx);
            float backRightPower = (y + x - rx) * 1.3f;

            control.frontLeft.setPower(frontLeftPower);
            control.backLeft.setPower(backLeftPower);
            control.frontRight.setPower(frontRightPower);
            control.backRight.setPower(backRightPower);

            //slides
            int slidePos = control.rightSlide.getCurrentPosition();
            if (gamepad1.right_trigger > 0) slidePos += 10;
            else if (gamepad1.left_trigger > 0) slidePos -= 10;
            else if (gamepad1.x) slidePos = control.GROUND;
            else if (gamepad1.a) slidePos = control.SHORTPOLE;
            else if (gamepad1.b) slidePos = control.MEDIUMPOLE;
            else if (gamepad1.y) slidePos = control.TALLPOLE;
            control.setSlidePos(slidePos);
            telemetry.addData("RSlide position", control.rightSlide.getCurrentPosition());
            telemetry.addData("LSlide position", control.leftSlide.getCurrentPosition());

            // ----------------------------------- Gamepad 2----------------------------------------
            // claw
            if (gamepad2.a) control.clawClose();
            else if (gamepad2.b) control.clawOpen();

            // swivel
            if (gamepad2.right_bumper) lastSwivelPos += 0.02;
            else if (gamepad2.left_bumper) lastSwivelPos -= 0.02;
            control.setSwivel(lastSwivelPos);

            // lift
            if (gamepad2.right_trigger > 0) control.setLift(control.lift.getPosition() + 0.02);
            else if (gamepad2.left_trigger > 0) control.setLift(control.lift.getPosition() - 0.02);


            telemetry.addLine("Opmode Running");
            telemetry.update();
        }
    }
}