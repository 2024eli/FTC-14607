package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.exception.RobotCoreException;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Servo;

@TeleOp(name = "test tele")
public class NutBuster3000 extends LinearOpMode {

    @Override
    public void runOpMode() {
        Servo armSwivel = hardwareMap.get(Servo.class, "swivel");
        Servo armVertical = hardwareMap.get(Servo.class, "vertical");
        DcMotorEx leftSlide = hardwareMap.get(DcMotorEx.class, "LeftSlide");
        DcMotorEx rightSlide = hardwareMap.get(DcMotorEx.class, "RightSlide");

        Gamepad currentGamepad = new Gamepad();
        Gamepad prevGamepad = new Gamepad();

        waitForStart();

        while(opModeIsActive()) {


            prevGamepad.copy(currentGamepad);
            currentGamepad.copy(gamepad1);

            try {
                for(double i=0; i<1; i+=0.01) {
                    armSwivel.setPosition(i);
                    wait(500);
                    telemetry.addData("servo pos", i);
                    telemetry.update();
                }





            } catch(Exception e) {}


            telemetry.addLine("Opmode Running");
            telemetry.update();
        }
    }
}