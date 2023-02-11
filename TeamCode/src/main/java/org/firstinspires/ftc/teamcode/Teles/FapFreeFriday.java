package org.firstinspires.ftc.teamcode.Teles;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;

import org.firstinspires.ftc.teamcode.Robots.BumbleBee;
import org.firstinspires.ftc.teamcode.Robots.BumbleBee2;

@Config
@TeleOp(name = "test tele2", group="Test")
public class FapFreeFriday extends LinearOpMode {

    public static int angle = 180;
    @Override
    public void runOpMode() {
        DcMotorEx leftS = hardwareMap.get(DcMotorEx.class, "LeftSlide");
        DcMotorEx rightS = hardwareMap.get(DcMotorEx.class, "RightSlide");

        BumbleBee2 control = new BumbleBee2(hardwareMap, this, telemetry);
        //lS.setPositionPIDFCoefficients(10.0);
        //lS.setVelocityPIDFCoefficients(10.0, 0.05, 0.0, 0.0);
        //lS.setPIDFCoefficients(DcMotorEx.RunMode.RUN_USING_ENCODER, new PIDFCoefficients(10, 0.05, 0.0, 0.0));
        telemetry.addData("Pos coefficients", control.frontRight.getPIDFCoefficients(DcMotor.RunMode.RUN_TO_POSITION));
        telemetry.addData("velo coefficients", control.frontRight.getPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER));
        telemetry.update();
        waitForStart();
        telemetry.addData("Current angle", -control.imu.getAngularOrientation().firstAngle);
        telemetry.addData("Rotating to", angle);
        telemetry.update();

        control.forward(60,200);

        telemetry.addLine("completed");
        telemetry.update();
        sleep(5000);
    }
}