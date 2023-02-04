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

@Autonomous(name = "FINALRIGHTAUTO1+2", group="Test")
public class EjaculativeLinguistics extends LinearOpMode {
    BumbleBee control;
    OpenCvWebcam webcam;
    public static int n_cycles = 2;
    public static int angle = 90;
    public static int angle2 = 0;
    public static int zone = 1;
    public static int dist1 = 9;
    public static int dist2 = 60;
    public static int dist3 = 22;
    public static int dist4 = 49;
    public static int dist5 = 51;
    public static int height1 = 110;
    public static boolean pushCone = true;
    public static boolean cycle = true;


    @Override
    public void runOpMode() {
        control = new BumbleBee(hardwareMap, this, telemetry);

        control.clawClose();
        sleep(500);
        control.setSwivel(0.666);
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
        //camera sees the mf colors
        SleeveDetectPipeline.DetectedColor color = pipeline.getDetectedColor();
        telemetry.addData("Detected color", color);
        telemetry.update();
        sleep(800);

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
//camera done seeing colors :(

        control.forward(11, 250);
        control.setSlidePos(BumbleBee.SHORTPOLE);
        sleep(500);
        control.setSwivel(0.4);
        control.setLift(0);
        sleep(1000);
        control.clawOpen();
        sleep(500);
        control.setSwivel(0.66);
        control.setSlidePos(BumbleBee.GROUND);
        sleep(500);

        if (! pushCone) return;
        // grab signal cone and move it forward and move to stack
        control.forward(dist1, 500);
        sleep(300);
        control.forward(dist2, 500);
        sleep(300);
        control.setLift(1);
        control.rotate(angle);
        sleep(500);
        control.forward(dist3, 400);
        sleep(500);

        if (! cycle) return;
        // cycle
        control.setLift(0);
        sleep(500);
        for(int i=0; i<n_cycles; i++) {
            control.setSlidePos(height1-(i*30));
            sleep(800);
            control.clawClose();
            sleep(500);
            control.setSlidePos(height1+300);
            sleep(100);
            control.setLift(1);
            sleep(800);
            control.backward(dist4, 400);
            sleep(800);
            control.setSlidePos(BumbleBee.TALLPOLE);
            sleep(800);
            control.setSwivel(0.42);
            sleep(600);
            control.setLift(0);
            sleep(1200);
            control.clawOpen();
            sleep(500);
            control.setSwivel(0.66);
            if (i==n_cycles-1) {
            }
            else {
                control.forward(dist5, 400);
                sleep(500);
            }
        }
        control.setSlidePos(BumbleBee.GROUND);

        // park
        //control.right(56*(zone-1), 200);

        if (zone==1){
            control.backward(4,400);
        }
        else if (zone==2){
           control.forward(26,400);
        }
        else{
            control.forward(58,400);
        }
        sleep(100);
        control.setLift(1);
        sleep(100);

        telemetry.addData("Status", "completed");
        telemetry.update();
        sleep(2000);
    }

}