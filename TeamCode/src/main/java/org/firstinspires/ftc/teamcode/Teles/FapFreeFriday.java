package org.firstinspires.ftc.teamcode.Teles;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorEx;

import org.firstinspires.ftc.teamcode.Robots.BumbleBee;

@Config
@TeleOp(name = "test tele2", group="Test")
public class FapFreeFriday extends LinearOpMode {

    public static int angle = 180;
    @Override
    public void runOpMode() {
        DcMotorEx leftS = hardwareMap.get(DcMotorEx.class, "LeftSlide");
        DcMotorEx rightS = hardwareMap.get(DcMotorEx.class, "RightSlide");

        BumbleBee control = new BumbleBee(hardwareMap, this, telemetry);
        //lS.setPositionPIDFCoefficients(10.0);
        //lS.setVelocityPIDFCoefficients(10.0, 0.05, 0.0, 0.0);
        //lS.setPIDFCoefficients(DcMotorEx.RunMode.RUN_USING_ENCODER, new PIDFCoefficients(10, 0.05, 0.0, 0.0));
        waitForStart();
        telemetry.addData("Rotating to", angle);
        telemetry.update();

        control.rotate(angle);

        telemetry.addLine("completed");
        telemetry.update();
        sleep(5000);
    }
}