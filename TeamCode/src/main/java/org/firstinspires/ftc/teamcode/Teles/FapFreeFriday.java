package org.firstinspires.ftc.teamcode.Teles;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorEx;

import org.firstinspires.ftc.teamcode.Robots.BumbleBee;

@TeleOp(name = "test tele2", group="Test")
public class FapFreeFriday extends LinearOpMode {

    @Override
    public void runOpMode() {
        DcMotorEx leftS = hardwareMap.get(DcMotorEx.class, "LeftSlide");
        DcMotorEx rightS = hardwareMap.get(DcMotorEx.class, "RightSlide");

        BumbleBee control = new BumbleBee(hardwareMap, this, telemetry);
        //lS.setPositionPIDFCoefficients(10.0);
        //lS.setVelocityPIDFCoefficients(10.0, 0.05, 0.0, 0.0);
        //lS.setPIDFCoefficients(DcMotorEx.RunMode.RUN_USING_ENCODER, new PIDFCoefficients(10, 0.05, 0.0, 0.0));
        waitForStart();
        while(opModeIsActive()) {
            control.setSlidePos(BumbleBee.GROUND);
            telemetry.addData("slides at", BumbleBee.GROUND);
            telemetry.update();
            sleep(2000);

            control.setSlidePos(BumbleBee.SHORTPOLE);
            telemetry.addData("slides at", BumbleBee.SHORTPOLE);
            telemetry.update();
            sleep(2000);

            control.setSlidePos(BumbleBee.MEDIUMPOLE);
            telemetry.addData("slides at", BumbleBee.MEDIUMPOLE);
            telemetry.update();
            sleep(2000);

            control.setSlidePos(BumbleBee.TALLPOLE);
            telemetry.addData("slides at", BumbleBee.TALLPOLE);
            telemetry.update();
            sleep(4000);
        }
    }
}