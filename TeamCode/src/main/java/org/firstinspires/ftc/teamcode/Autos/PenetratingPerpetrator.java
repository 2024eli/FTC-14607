package org.firstinspires.ftc.teamcode.Autos;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.hardware.DcMotorEx;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.teamcode.CV.SleeveDetectPipeline;
import org.firstinspires.ftc.teamcode.Robots.BumbleBee;
import org.openftc.easyopencv.OpenCvCamera;
import org.openftc.easyopencv.OpenCvCameraFactory;
import org.openftc.easyopencv.OpenCvCameraRotation;
import org.openftc.easyopencv.OpenCvWebcam;

@Autonomous(name = "test auto1", group="Test")
public class PenetratingPerpetrator extends LinearOpMode {
    BumbleBee control;
    OpenCvWebcam webcam;

    @Override
    public void runOpMode() {
        control = new BumbleBee(hardwareMap, this, telemetry);

        control.clawClose();
        sleep(500);
        control.setSwivel(0.66);
        control.setLift(1);

        waitForStart();

        // initialize webcam and detect color using pipeline
        control.rotateP(90);
        telemetry.addData("Angle", control.imu.getAngularOrientation());
        sleep(1000);
    }

}