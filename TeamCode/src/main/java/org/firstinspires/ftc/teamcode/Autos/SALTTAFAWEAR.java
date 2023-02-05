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
@Autonomous(name = "TF Right 1s0m2t", group="Main")
public class SALTTAFAWEAR extends LinearOpMode {
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
        // --------------------------- INITIALIZATION ------------------------
        control = new BumbleBee(hardwareMap, this, telemetry);
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