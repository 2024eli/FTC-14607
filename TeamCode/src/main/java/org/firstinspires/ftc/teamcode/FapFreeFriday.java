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
        DcMotorEx lS = hardwareMap.get(DcMotorEx.class, "LeftSlide");
        DcMotorEx rS = hardwareMap.get(DcMotorEx.class, "RightSlide");

        Gamepad currentGamepad = new Gamepad();
        Gamepad prevGamepad = new Gamepad();

        HardwareController control = new HardwareController(hardwareMap, this, telemetry);
        waitForStart();
        rS.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        rS.setVelocity(200);
        lS.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        lS.setVelocity(10);
        while(opModeIsActive()) {
            int l = lS.getCurrentPosition();
            int r = rS.getCurrentPosition();
            telemetry.addData("at", r);
            telemetry.addData("at l", l);
            int l1 = -188;
            int r1 = -500;
            telemetry.addData("setting to", r1);
            control.rightSlide.setTargetPosition(r1);
            control.leftSlide.setTargetPosition(l1);
            telemetry.addLine("Opmode Running");
            telemetry.update();
        }
    }
}