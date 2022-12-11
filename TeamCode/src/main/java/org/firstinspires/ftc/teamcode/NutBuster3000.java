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
        Servo armSwivel = hardwareMap.get(Servo.class, "swivel");
        Servo armVertical = hardwareMap.get(Servo.class, "lift");
        Servo claw = hardwareMap.get(Servo.class, "claw");
        DcMotorEx leftSlide = hardwareMap.get(DcMotorEx.class, "LeftSlide");
        DcMotorEx rightSlide = hardwareMap.get(DcMotorEx.class, "RightSlide");

        Gamepad currentGamepad = new Gamepad();
        Gamepad prevGamepad = new Gamepad();

        HardwareController control = new HardwareController(hardwareMap, this, telemetry);
        waitForStart();
        while(opModeIsActive()) {
            int r = control.rightSlide.getCurrentPosition();
            int l = control.leftSlide.getCurrentPosition();
            telemetry.addData("right at", r);
            telemetry.addData("left at", l);
            int r1 = r - 5;
            int l1 = l + 5;
            telemetry.addData("setting right to", r1);
            telemetry.addData("setting left to", l1);
            control.rightSlide.setTargetPosition(r1);
            control.leftSlide.setTargetPosition(l1);
            control.rightSlide.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            control.leftSlide.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            control.rightSlide.setVelocity(100);
            control.leftSlide.setVelocity(100);
            telemetry.update();
            sleep(1000);
            r = control.rightSlide.getCurrentPosition();
            l = control.leftSlide.getCurrentPosition();
            telemetry.addData("right at", r);
            telemetry.addData("left at", l);
            sleep(2000);
            telemetry.addLine("Opmode Running");
            telemetry.update();
        }
    }
}