package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.exception.RobotCoreException;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Servo;

@TeleOp(name = "test tele2")
public class FapFreeFriday extends LinearOpMode {

    @Override
    public void runOpMode() {
        Servo armSwivel = hardwareMap.get(Servo.class, "swivel");
        Servo armVertical = hardwareMap.get(Servo.class, "lift");
        Servo claw = hardwareMap.get(Servo.class, "claw");
        DcMotorEx leftSlide = hardwareMap.get(DcMotorEx.class, "LeftSlide");
        DcMotorEx rightSlide = hardwareMap.get(DcMotorEx.class, "RightSlide");
        DcMotorEx tm = hardwareMap.get(DcMotorEx.class, "testmotor");
        tm.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        tm.setVelocity(10);


        Gamepad currentGamepad = new Gamepad();
        Gamepad prevGamepad = new Gamepad();

        HardwareController control = new HardwareController(hardwareMap, this, telemetry);
        waitForStart();
        while(opModeIsActive()) {
            int r = tm.getCurrentPosition();
            telemetry.addData("at", r);
            int r1 = r - 5;
            telemetry.addData("setting to", r1);
            control.rightSlide.setTargetPosition(r1);
            telemetry.update();
            sleep(1000);
            r = control.rightSlide.getCurrentPosition();
            telemetry.addData("right at", r);
            sleep(2000);
            telemetry.addLine("Opmode Running");
            telemetry.update();
        }
    }
}