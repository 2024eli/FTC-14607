package org.firstinspires.ftc.teamcode.Autos;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotorEx;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.teamcode.Robots.BumbleBee;
import org.firstinspires.ftc.teamcode.CV.SleeveDetectPipeline;
import org.openftc.easyopencv.OpenCvCamera;
import org.openftc.easyopencv.OpenCvCameraFactory;
import org.openftc.easyopencv.OpenCvCameraRotation;
import org.openftc.easyopencv.OpenCvWebcam;
@Config
@Autonomous(name = "EOCV Right 1s0m0t", group="Main")
public class TheWidening extends LinearOpMode {
    BumbleBee control;
    OpenCvWebcam webcam;
    public static int dist1 = 9,
            dist2 = 37,
            dist3 = 40, dist4 = -4, dist5 = -40;

    @Override
    public void runOpMode() {
        control = new BumbleBee(hardwareMap, this, telemetry);

        control.clawClose();
        sleep(500);
        control.setSwivel(0.66);
        control.setLift(1);

        // initialize webcam and detect color using pipeline
        int cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        webcam = OpenCvCameraFactory.getInstance().createWebcam(hardwareMap.get(WebcamName.class, "Webcam 1"), cameraMonitorViewId);
        SleeveDetectPipeline pipeline = new SleeveDetectPipeline();
        webcam.setPipeline(pipeline);
        webcam.openCameraDeviceAsync(new OpenCvCamera.AsyncCameraOpenListener()
        {
            @Override
            public void onOpened()
            {
                webcam.startStreaming(320, 240, OpenCvCameraRotation.UPRIGHT);
            }

            @Override
            public void onError(int errorCode)
            {
            }

        });

        waitForStart();
//auto thoomin
        SleeveDetectPipeline.DetectedColor color = pipeline.getDetectedColor();
        telemetry.addData("Detected color", color);
        telemetry.update();
        sleep(1000);

        int zone = 0;
        SleeveDetectPipeline.DetectedColor DetectedColor;
        if(color == SleeveDetectPipeline.DetectedColor.YELLOW) {
            zone = 1;
        } else if(color == SleeveDetectPipeline.DetectedColor.GREEN) {
            zone = 2;
        } else {
            zone = 3;
        }
        webcam.stopStreaming();
        webcam.stopRecordingPipeline();

        control.forward(dist1, 200);
        control.setSlidePos(BumbleBee.SHORTPOLE);
        sleep(500);
        control.setSwivel(0.4);
        control.setLift(0.1);
        sleep(1000);
        control.clawOpen();
        sleep(500);
        control.setSwivel(0.66);
        control.setLift(1);
        sleep(500);
        control.setSlidePos(BumbleBee.GROUND);
        // move around signal cone
        control.forward(dist2, 200);
        control.rotate(90);
        // park
        switch (zone) {
            case 3:
                control.forward(dist3, 200);
                break;
            case 2:
                control.forward(dist4, 200);
                break;
            case 1:
                control.forward(dist5, 200);
                break;
        }

    }
}