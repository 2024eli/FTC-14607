package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.exception.RobotCoreException;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;
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
        //lS.setPositionPIDFCoefficients(10.0);
        //lS.setVelocityPIDFCoefficients(10.0, 0.05, 0.0, 0.0);
        //lS.setPIDFCoefficients(DcMotorEx.RunMode.RUN_USING_ENCODER, new PIDFCoefficients(10, 0.05, 0.0, 0.0));
        waitForStart();
        //rS.setVelocity(100);
        lS.setVelocity(100);
        while(opModeIsActive()) {
            int l = lS.getCurrentPosition();
            //int r = rS.getCurrentPosition();
            //telemetry.addData("at", r);
            telemetry.addData("at l", l);
            telemetry.addData("PIDF Coefficients", lS.getPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER));
            //int r1 = -500;
            //telemetry.addData("setting to", r1);
            telemetry.addData("setting left to", 0);
            //rS.setTargetPosition(r1);
            lS.setVelocity(200);
            //rS.setVelocity(200);

            //rS.setMode(DcMotorEx.RunMode.RUN_TO_POSITION);
            lS.setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);
//            telemetry.addLine("waiting");
//            telemetry.update();
//            sleep(5000);
//            lS.setVelocity(200);
//            lS.setTargetPosition(0);
//            lS.setVelocity(200);
//            lS.setMode(DcMotorEx.RunMode.RUN_TO_POSITION);
            telemetry.addLine("Opmode Running");
            telemetry.update();
        }
    }
}