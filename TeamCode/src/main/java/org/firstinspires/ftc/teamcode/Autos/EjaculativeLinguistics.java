package org.firstinspires.ftc.teamcode.Autos;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.teamcode.CV.SleeveDetectPipeline;
import org.firstinspires.ftc.teamcode.Robots.BumbleBee;
import org.openftc.easyopencv.OpenCvCamera;
import org.openftc.easyopencv.OpenCvCameraFactory;
import org.openftc.easyopencv.OpenCvCameraRotation;
import org.openftc.easyopencv.OpenCvWebcam;

@Config
@Autonomous(name = "test auto2", group="Test")
public class EjaculativeLinguistics extends LinearOpMode {
    BumbleBee control;
    OpenCvWebcam webcam;
    public static int SPEED = 400;
    public static int DISTANCE = 60;

    @Override
    public void runOpMode() {
        control = new BumbleBee(hardwareMap, this, telemetry);

        control.clawClose();
        sleep(500);
        control.setSwivel(0.66);
        control.setLift(1);

        waitForStart();
        telemetry.addData("Status", "running");
        telemetry.update();
        control.strafeExp(DISTANCE);
        telemetry.addData("Status", "completed");
        telemetry.update();
        sleep(2000);
    }

}