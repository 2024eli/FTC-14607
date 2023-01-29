package org.firstinspires.ftc.teamcode.Autos;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorEx;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.tfod.Recognition;
import org.firstinspires.ftc.robotcore.external.tfod.TFObjectDetector;
import org.firstinspires.ftc.teamcode.Robots.BumbleBee;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

@Autonomous(name = "TF Vision Auto left",group="Main")
public class OpticalAnalogy2 extends LinearOpMode {
    BumbleBee control;

    public static int dist = 0;

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

    @Override
    public void runOpMode() {
        control = new BumbleBee(hardwareMap, this, telemetry);
        // initialize
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
        waitForStart();

        // read sleeve at the beginning of match ~2s for 10 reads
        telemetry.addLine("Reading sleeve...");
        telemetry.update();
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
                sleep(200);
            }
        }
        // find which pos was read the most
        int max = 0;
        for(int x : labelCounts) if (x > max) max = x;
        if (max < 3) zone = 2; // seems like the yellow square gives it the most trouble
        else {
            for (int i = 0; i < 3; i++) {
                if (labelCounts[i] == max) {
                    zone = i + 1;
                    break;
                }
            }
        }
        telemetry.addData("Found zone", zone);
        telemetry.update();

        // deposit on short pole
        control.forward(12, 250);
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

        // move around signal cone
        control.backward(11, 250);
        sleep(500);
        control.right(59,200);
        sleep(500);
        control.forward(49, 250);
        sleep(500);

        // **** park in detected zone ****
        //control.left(56*(3-zone), 200);
        switch (zone) {
            case 1:
                control.left(130, 200);
                break;
            case 2:
                control.left(58, 200);
                break;
            case 3:
                break;
        }

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
        tfodParameters.minResultConfidence = 0.70f;
        tfodParameters.isModelTensorFlow2 = true;
        tfodParameters.inputSize = 320;
        // initialize tfod
        tfod = ClassFactory.getInstance().createTFObjectDetector(tfodParameters, vuforia);
        tfod.loadModelFromAsset(TFOD_MODEL_ASSET, LABELS);
    }
}
