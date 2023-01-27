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
@Autonomous(name = "TF Vision Auto",group="Main")
public class OpticalAnalogy extends LinearOpMode {
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

    @Override
    public void runOpMode() {
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
        telemetry.addData(">", "Press Play to start op mode");
        telemetry.update();
        waitForStart();

        telemetry.addLine("Reading sleeve...");
        telemetry.update();
        int zone = 2;
        int[] labelCounts = new int[3];
        if (tfod != null) {
            for (int i = 0; i < 10; i++) {
                List<Recognition> recognitions = tfod.getRecognitions();
                if (recognitions != null && recognitions.size() > 0) {
                    Recognition recognition = recognitions.get(0);
                    String label = recognition.getLabel();
                    switch (label) {
                        case "pos1":
                            labelCounts[0]++; break;
                        case "pos2":
                            labelCounts[1]++; break;
                        case "pos3":
                            labelCounts[2]++; break;
                    }
                }
                sleep(200);
            }
        }
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

        control.forward(12, 250);
        for( DcMotorEx m : control.drivetrain) { m.setVelocity(0); }
        control.setSlidePos(BumbleBee.SHORTPOLE);
        sleep(500);
        control.setSwivel(0.4);
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
        control.left(59,200);
        sleep(500);
        for( DcMotorEx m : control.drivetrain) { m.setVelocity(0); }
        control.forward(53, 250);
        sleep(500);
        if (zone==1){
//            control.rotate(180);
        }
        else if (zone==2){
            control.right(56,200);
        }
        else{
            control.right(112,200);
        }

    }

    private void initVuforia() {
        VuforiaLocalizer.Parameters parameters = new VuforiaLocalizer.Parameters();

        parameters.vuforiaLicenseKey = VUFORIA_KEY;
        parameters.cameraName = hardwareMap.get(WebcamName.class, "Webcam 1");
        // parameters.cameraDirection = VuforiaLocalizer.CameraDirection.BACK;

        vuforia = ClassFactory.getInstance().createVuforia(parameters);
    }

    private void initTfod() {
        int tfodMonitorViewId = hardwareMap.appContext.getResources().getIdentifier(
                "tfodMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        TFObjectDetector.Parameters tfodParameters = new TFObjectDetector.Parameters(tfodMonitorViewId);
        tfodParameters.minResultConfidence = 0.70f;
        tfodParameters.isModelTensorFlow2 = true;
        tfodParameters.inputSize = 640;
        tfod = ClassFactory.getInstance().createTFObjectDetector(tfodParameters, vuforia);

        // Use loadModelFromAsset() if the TF Model is built in as an asset by Android Studio
        // Use loadModelFromFile() if you have downloaded a custom team model to the Robot Controller's FLASH.
        tfod.loadModelFromAsset(TFOD_MODEL_ASSET, LABELS);
        //tfod.loadModelFromFile(TFOD_MODEL_FILE, LABELS);
    }
}
