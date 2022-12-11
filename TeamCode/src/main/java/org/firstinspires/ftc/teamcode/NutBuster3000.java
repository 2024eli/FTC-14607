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
        HardwareController control = new HardwareController(hardwareMap, this, telemetry);
        control.rightSlide.setVelocity(300);
        control.leftSlide.setVelocity(300);
        control.rightSlide.setMode(DcMotorEx.RunMode.RUN_TO_POSITION);
        control.leftSlide.setMode(DcMotorEx.RunMode.RUN_TO_POSITION);
        Gamepad currentGamepad = new Gamepad();
        Gamepad prevGamepad = new Gamepad();

        waitForStart();

        while(opModeIsActive()) {
            prevGamepad.copy(currentGamepad);
            currentGamepad.copy(gamepad1);

            int r = control.rightSlide.getCurrentPosition();
            int l = control.leftSlide.getCurrentPosition();
            telemetry.addData("RSlide position", control.rightSlide.getCurrentPosition());
            telemetry.addData("LSlide position", control.leftSlide.getCurrentPosition());
            int r1 = r + 50;
            int l1 = l - 50;
            telemetry.addData("setting r to", r1);
            telemetry.addData("setting l to", l1);
            control.rightSlide.setTargetPosition(r1);
            control.leftSlide.setTargetPosition(l1);


            telemetry.addLine("Opmode Running");
            telemetry.update();
        }
    }
}