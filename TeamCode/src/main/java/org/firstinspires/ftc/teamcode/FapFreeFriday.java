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
        DcMotorEx leftS = hardwareMap.get(DcMotorEx.class, "LeftSlide");
        DcMotorEx rightS = hardwareMap.get(DcMotorEx.class, "RightSlide");

        HardwareController control = new HardwareController(hardwareMap, this, telemetry);
        //lS.setPositionPIDFCoefficients(10.0);
        //lS.setVelocityPIDFCoefficients(10.0, 0.05, 0.0, 0.0);
        //lS.setPIDFCoefficients(DcMotorEx.RunMode.RUN_USING_ENCODER, new PIDFCoefficients(10, 0.05, 0.0, 0.0));
        waitForStart();
        while(opModeIsActive()) {
            control.setSlidePos(HardwareController.GROUND);
            telemetry.addData("slides at", HardwareController.GROUND);
            telemetry.update();
            sleep(2000);

            control.setSlidePos(HardwareController.SHORTPOLE);
            telemetry.addData("slides at", HardwareController.SHORTPOLE);
            telemetry.update();
            sleep(2000);

            control.setSlidePos(HardwareController.MEDIUMPOLE);
            telemetry.addData("slides at", HardwareController.MEDIUMPOLE);
            telemetry.update();
            sleep(2000);

            control.setSlidePos(HardwareController.TALLPOLE);
            telemetry.addData("slides at", HardwareController.TALLPOLE);
            telemetry.update();
            sleep(4000);
        }
    }
}