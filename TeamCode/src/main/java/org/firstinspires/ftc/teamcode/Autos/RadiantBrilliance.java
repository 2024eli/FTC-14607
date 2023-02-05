package org.firstinspires.ftc.teamcode.Autos;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.tfod.Recognition;
import org.firstinspires.ftc.robotcore.external.tfod.TFObjectDetector;
import org.firstinspires.ftc.teamcode.CV.SleeveDetectPipeline;
import org.firstinspires.ftc.teamcode.Robots.BumbleBee;
import org.openftc.easyopencv.OpenCvCamera;
import org.openftc.easyopencv.OpenCvCameraFactory;
import org.openftc.easyopencv.OpenCvCameraRotation;
import org.openftc.easyopencv.OpenCvWebcam;

import java.util.List;

@Config
@Autonomous(name = "TF Left 1s0m2t", group="Main")
public class RadiantBrilliance extends LinearOpMode {
    BumbleBee control;
    private static final String TFOD_MODEL_ASSET = "MobileNetV2-320-v1.tflite";

    private static final String[] LABELS = {"pos1", "pos2", "pos3"};

    private static final String VUFORIA_KEY =
            "AY6BsCf/////AAABmZ6ima003kgksPYl8C+B8VZ1LH2yueEfFxy4p14SPPRdHkGuEtSIewtOX5QIPU6XkSPde" +
                    "k3tAIssoQkq1jF8hmsZINtLhHYNPojqBvsizbouwQTwhRm+Xej0KFhPo5yvOTJZRuJ5faItG0UGRTJ5u3wpfaWohaEyDwFgl" +
                    "DsYeTIhp0zXk0cVhjctUpYPnd5advw0jRBfEoa5GV+rHi/kxPzvzyvPrVevqzLyRPRDBMneVn6MnD9/Nyvb5QUh9ZGnRBZxT" +
                    "5ilYq1yWiP9R98pZdYnGwYStzkz+hZDHWluCwwduRF4blVS2W6jgC0RZfqMWT+7rG58RdpjjhzH7CYkcIW2R256kPTP9b85O" +
                    "prB1eap";

    private VuforiaLocalizer vuforia; // vuforia used only for feeding camera stream to tfod, no localization

    private TFObjectDetector tfod; // object detection on the sleeve only

    public static int n_cycles = 2;
    public static int angle = -85;
    public static int angle2 = 0;
    public static int zone = 1;
    public static int dist0 = 8;
    public static int dist1 = 0;
    public static int dist2 = 58;
    public static int dist3 = 20;
    public static int dist4 = 42;
    public static int dist5 = 57;
    public static int height1 = 175;
    public static boolean pushCone = true;
    public static boolean cycle = true;


    @Override
    public void runOpMode() {
        control = new BumbleBee(hardwareMap, this, telemetry);
        // --------------------------- INITIALIZATION ------------------------
        initVuforia();
        initTfod();
        if (tfod != null) {
            tfod.activate();
            tfod.setZoom(1.2, 16.0/9.0);
        }

        control.clawClose();
        sleep(500);
        control.setSwivel(0.66);
        control.setLift(1);
        telemetry.addData(">", "Press Play to start op mode");
        telemetry.update();
        int latestZone = 1;
        while (!isStarted()) {
            telemetry.addData("Found zone (1 also means none)", latestZone);
            telemetry.update();
            latestZone = readSleeve();
        }
        waitForStart();
        // ----------------------------- READ SLEEVE (2s) ------------------------------
        int zone = latestZone;

        telemetry.addData("Found zone", zone);
        telemetry.update();
        // --------------------------- DEPOSIT ON SHORT --------------------------
        control.forward(dist0, 250);
        control.setSlidePos(BumbleBee.SHORTPOLE);
        sleep(500);
        telemetry.addData("Status", "Depositing on short");
        telemetry.update();
        control.setSwivel(0.92);
        control.setLift(0);
        sleep(800);
        control.clawOpen();
        sleep(500);
        control.setSwivel(0.666);
        control.setSlidePos(BumbleBee.GROUND);
        sleep(500);
        // ------------------------- PUSH SIGNAL CONE AWAY -----------------------
        if (! pushCone) return;
        telemetry.addData("Status", "Pushing signal cone");
        telemetry.update();
        control.forward(dist1, 500);
        sleep(300);
        control.forward(dist2, 500);
        sleep(600);
        control.setLift(1);
        control.rotate(angle);
        sleep(500);
        control.forward(dist3, 400);
        sleep(500);
        // ----------------------------- CYCLES ---------------------------
        if (! cycle) return;
        telemetry.addData("Status", "Cycling");
        telemetry.update();
        control.setLift(0);
        sleep(500);
        for(int i=0; i<n_cycles; i++) {
            telemetry.addData("Iteration", i);
            telemetry.update();
            // grab cone
            control.setSlidePos(height1-(i*30));
            sleep(800);
            control.clawClose();
            sleep(500);
            control.setSlidePos(height1+300);
            sleep(100);
            control.setLift(1);
            sleep(800);
            // go to pole
            control.backward(dist4, 400);
            sleep(800);
            control.setSlidePos(BumbleBee.TALLPOLE);
            sleep(800);
            // drop cone
            control.setSwivel(0.92);
            sleep(600);
            control.setLift(0);
            sleep(1200);
            control.clawOpen();
            sleep(500);
            control.setSwivel(0.66);
            if (i < n_cycles-1) {
                control.forward(dist5, 400);
                sleep(500);
            }
        }
        control.setSlidePos(BumbleBee.GROUND);
        // ----------------------------- PARK --------------------------------
        telemetry.addData("Status", "Parking in zone "+zone);
        telemetry.update();

        if (zone==1) control.forward(57,400);
        else if (zone==2) control.forward(25,400);
        else control.backward(4,400);

        sleep(100);
        control.setLift(1);
        sleep(200);

        // ------------------------------- END ------------------------------
        telemetry.addData("Status", "Completed");
        telemetry.update();
        sleep(2000);
    }

    // returns zone
    public int readSleeve() {
        int zone = 1;
        int[] labelCounts = new int[3];
        if (tfod != null) {
            for (int i = 0; i < 10; i++) { // read 10 times to prevent random errors
                List<Recognition> recognitions = tfod.getRecognitions();
                if (recognitions != null && recognitions.size() > 0) {
                    // should only be seeing one recognition, if there are more the other is a misread
                    Recognition recognition = recognitions.get(0);
                    String label = recognition.getLabel();
                    switch (label) {
                        case "pos1":
                            labelCounts[1]++; break;
                        case "pos2":
                            labelCounts[0]++; break;
                        case "pos3":
                            labelCounts[2]++; break;
                    }
                }
                sleep(150);
            }
        }
        // find which pos was read the most
        int max = 0;
        for(int x : labelCounts) if (x > max) max = x;
        if (max < 3) zone = 1; // seems like the yellow square gives it the most trouble
        else {
            for (int i = 0; i < 3; i++) {
                if (labelCounts[i] == max) {
                    zone = i + 1;
                    break;
                }
            }
        }
        return zone;
    }

    private void initVuforia() {
        // create vuforia parameters
        VuforiaLocalizer.Parameters parameters = new VuforiaLocalizer.Parameters();
        parameters.vuforiaLicenseKey = VUFORIA_KEY;
        parameters.cameraName = hardwareMap.get(WebcamName.class, "Webcam 1");
        // initialize vuforia
        vuforia = ClassFactory.getInstance().createVuforia(parameters);
    }

    private void initTfod() {
        // create tfod parameters
        int tfodMonitorViewId = hardwareMap.appContext.getResources().getIdentifier(
                "tfodMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        TFObjectDetector.Parameters tfodParameters = new TFObjectDetector.Parameters(tfodMonitorViewId);
        tfodParameters.minResultConfidence = 0.667f;
        tfodParameters.isModelTensorFlow2 = true;
        tfodParameters.inputSize = 320;
        // initialize tfod
        tfod = ClassFactory.getInstance().createTFObjectDetector(tfodParameters, vuforia);
        tfod.loadModelFromAsset(TFOD_MODEL_ASSET, LABELS);
    }

}