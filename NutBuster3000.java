package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.exception.RobotCoreException;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.Servo;

@TeleOp(name = "test tele")
public class NutBuster3000 extends LinearOpMode {

    @Override
    public void runOpMode() {
        Servo servo1 = hardwareMap.get(Servo.class, "servo1");
        Servo servo2 = hardwareMap.get(Servo.class, "servo2");
        Servo servo3 = hardwareMap.get(Servo.class, "servo3");

        Gamepad currentGamepad = new Gamepad();
        Gamepad prevGamepad = new Gamepad();

        waitForStart();

        while(opModeIsActive()) {

            try {
                prevGamepad.copy(currentGamepad);
                currentGamepad.copy(gamepad1);
            } catch (RobotCoreException e) {
                telemetry.addLine("exception");
            }
            if (currentGamepad.a) { //open claw
                servo1.setPosition(1);
            } else if (currentGamepad.b) { //close claw
                servo1.setPosition(0);
            }
            if (currentGamepad.x) {
                servo2.setPosition(servo2.getPosition() + 0.05);
            } else if (currentGamepad.y) {
                servo2.setPosition(servo2.getPosition() - 0.05);
            }

//            if (currentGamepad.a && !prevGamepad.a) {
//                servo1.setPosition(servo1.getPosition() + 0.05);
//            }
//            else if (currentGamepad.b && !prevGamepad.b) {
//                servo1.setPosition(servo1.getPosition() - 0.05);
//            }
//            if (currentGamepad.x && !prevGamepad.x) {
//                servo2.setPosition(servo2.getPosition() + 0.05);
//            }
//            else if(currentGamepad.y && !prevGamepad.y) {
//                servo2.setPosition(servo2.getPosition() - 0.05);
//            }
//            if (currentGamepad.dpad_down && !prevGamepad.dpad_down) {
//                servo3.setPosition(servo3.getPosition() + 0.05);
//            } else if (currentGamepad.dpad_up && !prevGamepad.dpad_up) {
//                servo3.setPosition(servo3.getPosition() - 0.05);
//            }
            telemetry.addLine("Opmode Running");
            telemetry.update();
        }
    }
}