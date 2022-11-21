package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.tfod.Recognition;
import org.firstinspires.ftc.robotcore.external.tfod.TFObjectDetector;

import java.util.List;
@TeleOp(name = "vision auto")
public class OpticalAnalogy extends LinearOpMode {

    HardwareController control;
    /*
     * Specify the source for the Tensor Flow Model.
     * If the TensorFlowLite object model is included in the Robot Controller App as an "asset",
     * the OpMode must to load it using loadModelFromAsset().  However, if a team generated model
     * has been downloaded to the Robot Controller's SD FLASH memory, it must to be loaded using loadModelFromFile()
     * Here we assume it's an Asset.    Also see method initTfod() below .
     */
    private static final String TFOD_MODEL_ASSET = "UltimateGoal.tflite";

    //private static final String TFOD_MODEL_FILE  = "/sdcard/FIRST/tflitemodels/Powerplayn-v1.tflite";

    private static final String[] LABELS = {
            "Sleeve-pos1",
            "Sleeve-pos2",
            "Sleeve-pos3",
            "RedSleeve",
            "BlueSleeve",
            "WallImage-Wires",
            "WallImage-Powerlines",
            "WallImage-Circuitboard",
            "WallImage-Engine",
            "RedCone",
            "BlueCone"
    };

    private static final String VUFORIA_KEY =
            "AY6BsCf/////AAABmZ6ima003kgksPYl8C+B8VZ1LH2yueEfFxy4p14SPPRdHkGuEtSIewtOX5QIPU6XkSPde" +
    "k3tAIssoQkq1jF8hmsZINtLhHYNPojqBvsizbouwQTwhRm+Xej0KFhPo5yvOTJZRuJ5faItG0UGRTJ5u3wpfaWohaEyDwFgl" +
    "DsYeTIhp0zXk0cVhjctUpYPnd5advw0jRBfEoa5GV+rHi/kxPzvzyvPrVevqzLyRPRDBMneVn6MnD9/Nyvb5QUh9ZGnRBZxT" +
    "5ilYq1yWiP9R98pZdYnGwYStzkz+hZDHWluCwwduRF4blVS2W6jgC0RZfqMWT+7rG58RdpjjhzH7CYkcIW2R256kPTP9b85O" +
    "prB1eap";

    private VuforiaLocalizer vuforia; // store instance of vuforia localization engine

    private TFObjectDetector tfod; // store instance of tensorflow object detection engine

    @Override
    public void runOpMode() {
        //control = new HardwareController(hardwareMap, this, telemetry);
        // The TFObjectDetector uses the camera frames from the VuforiaLocalizer, so we create that
        // first.
        initVuforia();
        initTfod();

        /**
         * Activate TensorFlow Object Detection before we wait for the start command.
         * Do it here so that the Camera Stream window will have the TensorFlow annotations visible.
         **/
        if (tfod != null) {
            tfod.activate();

            // The TensorFlow software will scale the input images from the camera to a lower resolution.
            // This can result in lower detection accuracy at longer distances (> 55cm or 22").
            // If your target is at distance greater than 50 cm (20") you can increase the magnification value
            // to artificially zoom in to the center of image.  For best results, the "aspectRatio" argument
            // should be set to the value of the images used to create the TensorFlow Object Detection model
            // (typically 16/9).
            tfod.setZoom(1.5, 16.0/9.0);
        }

        /* Wait for the game to begin */
        telemetry.addLine(tfod == null ? "tfod null":tfod.toString());
        telemetry.addLine(vuforia == null ? "vuforia null":vuforia.toString());

        telemetry.addData(">", "Press Play to start op mode");
        telemetry.update();
        waitForStart();

        if (opModeIsActive()) {
            while (opModeIsActive()) {
                if (tfod != null) {
                    // getUpdatedRecognitions() will return null if no new information is available since
                    // the last time that call was made.
                    List<Recognition> updatedRecognitions = tfod.getUpdatedRecognitions();
                    if (updatedRecognitions != null) {
                        telemetry.addData("# Objects Detected", updatedRecognitions.size());

                        // step through the list of recognitions and display image position/size information for each one
                        // Note: "Image number" refers to the randomized image orientation/number
                        for (Recognition recognition : updatedRecognitions) {
                            double col = (recognition.getLeft() + recognition.getRight()) / 2 ;
                            double row = (recognition.getTop()  + recognition.getBottom()) / 2 ;
                            double width  = Math.abs(recognition.getRight() - recognition.getLeft()) ;
                            double height = Math.abs(recognition.getTop()  - recognition.getBottom()) ;

                            telemetry.addData(""," ");
                            telemetry.addData("Image", "%s (%.0f %% Conf.)", recognition.getLabel(), recognition.getConfidence() * 100 );
                            telemetry.addData("- Position (Row/Col)","%.0f / %.0f", row, col);
                            telemetry.addData("- Size (Width/Height)","%.0f / %.0f", width, height);
                        }
                        telemetry.update();
                    }
                } else {
                    telemetry.addLine("Tfod is null");
                    telemetry.update();
                }
            }
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
        tfodParameters.minResultConfidence = 0.75f;
        tfodParameters.isModelTensorFlow2 = true;
        tfodParameters.inputSize = 640;
        tfod = ClassFactory.getInstance().createTFObjectDetector(tfodParameters, vuforia);

        // Use loadModelFromAsset() if the TF Model is built in as an asset by Android Studio
        // Use loadModelFromFile() if you have downloaded a custom team model to the Robot Controller's FLASH.
        tfod.loadModelFromAsset(TFOD_MODEL_ASSET, LABELS);
        //tfod.loadModelFromFile(TFOD_MODEL_FILE, LABELS);
    }
}
