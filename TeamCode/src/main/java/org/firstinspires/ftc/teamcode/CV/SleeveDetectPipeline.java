package org.firstinspires.ftc.teamcode.CV;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.openftc.easyopencv.OpenCvPipeline;

public class SleeveDetectPipeline extends OpenCvPipeline {

    public enum DetectedColor {
        NONE,
        GREEN,
        YELLOW,
        PURPLE,
    }

    // TOPLEFT anchor point for the bounding box
    private static Point SLEEVE_TOPLEFT_ANCHOR_POINT = new Point(145, 80);

    // Width and height for the bounding box
//    public static int REGION_WIDTH = 30;
//    public static int REGION_HEIGHT = 50;
    public static int REGION_WIDTH = 60;
    public static int REGION_HEIGHT = 100;

    // Lower and upper boundaries for colors
    private static final Scalar


// blue green and yellow

            lower_green_bounds = new Scalar(35, 123, 123),
            upper_green_bounds = new Scalar(81, 255, 255),
            lower_yellow_bounds = new Scalar(20, 123, 123),
            upper_yellow_bounds = new Scalar(30, 255, 255),
            lower_purple_bounds = new Scalar(130, 50, 50),
            upper_purple_bounds = new Scalar(180, 255, 255);


    // Color definitions
    private final Scalar
            GREEN = new Scalar(0, 255, 0),
            YELLOW = new Scalar(255, 255, 0),
            PURPLE = new Scalar(255, 0, 0);

    // Percent and mat definitions
    private double grePercent, oraPercent, purPercent;
    private Mat greMat = new Mat(), oraMat = new Mat(), purMat = new Mat(), blurredMat = new Mat(), kernel = new Mat();

    // Anchor point definitions
    Point sleeve_pointA = new Point(
            SLEEVE_TOPLEFT_ANCHOR_POINT.x,
            SLEEVE_TOPLEFT_ANCHOR_POINT.y);
    Point sleeve_pointB = new Point(
            SLEEVE_TOPLEFT_ANCHOR_POINT.x + REGION_WIDTH,
            SLEEVE_TOPLEFT_ANCHOR_POINT.y + REGION_HEIGHT);

    // Running variable storing the parking position
    private volatile DetectedColor detectedColor = DetectedColor.NONE;

    @Override
    public Mat processFrame(Mat input) {
        // Noise reduction
        Imgproc.cvtColor(input, input, Imgproc.COLOR_RGB2HSV);
        Imgproc.blur(input, blurredMat, new Size(5, 5));
        blurredMat = blurredMat.submat(new Rect(sleeve_pointA, sleeve_pointB));

        // Apply Morphology
        kernel = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(3, 3));
        Imgproc.morphologyEx(blurredMat, blurredMat, Imgproc.MORPH_CLOSE, kernel);

        // Gets channels from given source mat
        Core.inRange(blurredMat, lower_green_bounds, upper_green_bounds, greMat);
        Core.inRange(blurredMat, lower_yellow_bounds, upper_yellow_bounds, oraMat);
        Core.inRange(blurredMat, lower_purple_bounds, upper_purple_bounds, purMat);

        // Gets color specific values
        grePercent = Core.countNonZero(greMat);
        oraPercent = Core.countNonZero(oraMat);
        purPercent = Core.countNonZero(purMat);

        // Calculates the highest amount of pixels being covered on each side
        //double maxPercent = Math.max(grePercent, Math.max(oraPercent, purPercent));
        double maxPercent = Math.max(purPercent, Math.max(oraPercent, grePercent));

        // Checks all percentages, will highlight bounding box in camera preview
        // based on what color is being detected
        if (maxPercent == grePercent) {
            detectedColor = DetectedColor.GREEN;
            Imgproc.rectangle(
                    input,
                    sleeve_pointA,
                    sleeve_pointB,
                    GREEN,
                    2
            );
        } else if (maxPercent == oraPercent) {
            detectedColor = DetectedColor.YELLOW;
            Imgproc.rectangle(
                    input,
                    sleeve_pointA,
                    sleeve_pointB,
                    YELLOW,
                    2
            );
        } else if (maxPercent == purPercent) {
            detectedColor = DetectedColor.PURPLE;
            Imgproc.rectangle(
                    input,
                    sleeve_pointA,
                    sleeve_pointB,
                    PURPLE,
                    2
            );
        }

        // Memory cleanup
        blurredMat.release();
        greMat.release();
        oraMat.release();
        purMat.release();
        kernel.release();

        return input;
    }

    // Returns an enum being the current position where the robot will park
    public DetectedColor getDetectedColor() {
        return detectedColor;
    }
}
