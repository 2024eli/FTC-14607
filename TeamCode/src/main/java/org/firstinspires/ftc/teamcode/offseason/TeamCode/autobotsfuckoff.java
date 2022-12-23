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
//    control = new HardwareController(hardwareMap, this, telemetry);
//    final float slowSpeed = 0.3f;
//    final float midSpeed = 0.5f;
//    final float normalSpeed = 0.8f;
//    private boolean wasYPressed = false;
//    private boolean isLifted = false;


//    @Override
//    public void runOpMode() {
//        waitForStart();
//
//        control.setSlidePos(10);
//
//
//        telemetry.addLine("Opmode Running");
//        telemetry.update();
//    }
//}

    @Override
    public void runOpMode() {
        waitForStart();
        control = new HardwareController(hardwareMap, this, telemetry);

//        Gamepad currentGamepad1 = new Gamepad();
//        Gamepad prevGamepad1 = new Gamepad();
//        Gamepad currentGamepad2 = new Gamepad();
//        Gamepad prevGamepad2 = new Gamepad();

//        boolean slowMode = false;
//        float speedFactor = slowSpeed;
        control.frontLeft.setDirection(DcMotorEx.Direction.REVERSE);
        control.backLeft.setDirection(DcMotorEx.Direction.REVERSE);

//        control.rightSlide.setDirection(DcMotorEx.Direction.REVERSE);
//
//        waitForStart();
        control.frontRight.setPower(0.5);
        control.frontLeft.setPower(0.5);
        control.backLeft.setPower(0.5);
        control.backRight.setPower(0.5);
        sleep(1000);
//
//        while (opModeIsActive()) {
//            prevGamepad1.copy(currentGamepad1);
//            currentGamepad1.copy(gamepad1);
//            prevGamepad2.copy(currentGamepad2);
//            currentGamepad2.copy(gamepad2);
//
//            // ----------------------------------- Gamepad 1----------------------------------------
//            // drivetrain
//            if (gamepad1.dpad_up) speedFactor = normalSpeed;
//            else if (gamepad1.dpad_down) speedFactor = slowSpeed;
//
//            float y = -gamepad1.left_stick_y * speedFactor;
//            float x = gamepad1.left_stick_x * midSpeed;
//            float rx = gamepad1.right_stick_x * speedFactor;
//
//            float frontLeftPower = (y + x + rx);
//            float backLeftPower = (y - x + rx) * 1.25f;
//            float frontRightPower = (y - x - rx);
//            float backRightPower = (y + x - rx) * 1.3f;
//
//            control.frontLeft.setPower(0.5);
//            control.backLeft.setPower(0.5);
//            control.frontRight.setPower(0.5);
//            control.backRight.setPower(0.5);
//            sleep(1000);

    }
}