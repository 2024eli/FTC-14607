package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.exception.RobotCoreException;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Servo;

@TeleOp(name = "test tele")
public class NutBuster3000 extends LinearOpMode {

    @Override
    public void runOpMode() {
        DcMotorEx testm = hardwareMap.get(DcMotorEx.class, "testm");
        HardwareController control = new HardwareController(hardwareMap, this, telemetry);
        control.rightSlide.setVelocity(300);
        control.leftSlide.setVelocity(300);
        control.rightSlide.setMode(DcMotorEx.RunMode.RUN_TO_POSITION);
        control.leftSlide.setMode(DcMotorEx.RunMode.RUN_TO_POSITION);
        Gamepad currentGamepad = new Gamepad();
        Gamepad prevGamepad = new Gamepad();

        waitForStart();
        while(opModeIsActive()) {
            testm.setTargetPosition(100);
            testm.setVelocity(200);
            testm.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            telemetry.addLine("Opmode Running");
            telemetry.update();
        }
    }
}