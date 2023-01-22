package org.firstinspires.ftc.teamcode.Autos;

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

@Autonomous(name = "Webcam Deposit left", group="Main")
public class TheWidening2 extends LinearOpMode {
    BumbleBee control;
    OpenCvWebcam webcam;

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
//        webcam.stopStreaming();
//        webcam.closeCameraDevice();


        waitForStart();
//auto thoomin
        SleeveDetectPipeline.DetectedColor color = pipeline.getDetectedColor();
        telemetry.addData("Detected color", color);
        telemetry.update();
        sleep(3000);

        int zone = 0;
        SleeveDetectPipeline.DetectedColor DetectedColor;
        if(color == SleeveDetectPipeline.DetectedColor.YELLOW) {
            zone = 1;
        } else if(color == SleeveDetectPipeline.DetectedColor.GREEN) {
            zone = 2;
        } else{
            zone = 3;
        }
        if (zone != 0){
            //turn off camera
            webcam.stopStreaming();
            webcam.stopRecordingPipeline();
        }


        control.forward(12, 250);
        for( DcMotorEx m : control.drivetrain) { m.setVelocity(0); }
        control.setSlidePos(BumbleBee.SHORTPOLE);
        sleep(500);
        control.setSwivel(0.92);
        control.setLift(0.1);
        sleep(1000);
        control.clawOpen();
        sleep(500);
        control.setSwivel(0.66);
        control.setLift(1);
        control.clawClose();
        sleep(500);
        control.setSlidePos(BumbleBee.GROUND);
        //************** done depositing short **********
        control.backward(11, 250);
        sleep(500);
        control.right(59,200);
        sleep(500);
        for( DcMotorEx m : control.drivetrain) { m.setVelocity(0); }
        control.forward(53, 250);
        sleep(500);
        if (zone==3){
            //control.rotate(180);
        }
        else if (zone==2){
            control.right(56,200);
        }
        else{
            control.right(112,200);
        }

    }
}