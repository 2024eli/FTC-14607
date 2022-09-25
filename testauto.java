package org.firstinspires.ftc.teamcode;

import java.util.ArrayList;
import java.util.HashMap;
import java.lang.Math;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import org.firstinspires.ftc.robotcore.external.Telemetry;

@Autonomous(name = "testauto")
public class testauto extends LinearOpMode {

    @Override
    public void runOpMode() {
        Servo servo = hardwareMap.get(Servo.class, "aServo");
        waitForStart();

        servo.setPosition(0.25);
        sleep(1500);
        servo.setPosition(0);
        sleep(1000);

        telemetry.addLine("Opmode Running");
        telemetry.update();
    }
}