package org.firstinspires.ftc.teamcode;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.lang.Math;

import com.arcrobotics.ftclib.controller.*;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.hardware.bosch.JustLoggingAccelerationIntegrator;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

/**
 * Controller class to congregate code for manipulating physical hardware on the robot
 * (e.g. movement methods, motor instances, sensor inputs)
 */
public class HardwareController {
    // op mode
    public LinearOpMode opMode;
    public Telemetry telemetry;
    // drivetrain
    public DcMotorEx frontLeft;
    public DcMotorEx frontRight;
    public DcMotorEx backLeft;
    public DcMotorEx backRight;
    public DcMotorEx[] drivetrain;
    public DcMotorEx odoRight;
    public DcMotorEx odoLeft;
    public DcMotorEx odoBack;
    // interactors
    public Servo claw;
    public Servo lift;
    public Servo swivel;
    public DcMotorEx rightSlide;
    public DcMotorEx leftSlide;
    public DcMotorEx[] slides;
    // sensor/controllers
    public BNO055IMU imu;
    public PIDFController drivepidfcontroller;
    public PIDFController slidepidfcontroller;
    // telemetry
    public Telemetry.Item frontLeftTelemetry;
    public Telemetry.Item frontRightTelemetry;
    public Telemetry.Item backRightTelemetry;
    public Telemetry.Item backLeftTelemetry;
    public Telemetry.Item[] drivetrainTelemetry;
    // hardware properties TODO: record these values when robot is finished
    public final int motorTicks = 600;
    public final double wheelDiameter = 3.0; // cm
    public final double wheelCircumference = Math.PI * wheelDiameter; // cm
    // other constants
    public final static int TALLPOLE = 600; //ticks
    public final static int MEDIUMPOLE = 400;
    public final static int SHORTPOLE = 200;
    public final static int GROUND = 0;

    /**
     * Instantiate all variables related to the robot and the opmode, initialize imu and pid w/ parameters
     * @param hardwareMap
     * @param opModeInstance
     * @param telemetryInstance
     */
    public HardwareController(@NonNull HardwareMap hardwareMap, LinearOpMode opModeInstance, Telemetry telemetryInstance) {
        opMode = opModeInstance;
        telemetry = telemetryInstance;

        frontRight = hardwareMap.get(DcMotorEx.class, "FrontRight");
        frontLeft = hardwareMap.get(DcMotorEx.class, "FrontLeft");
        backRight = hardwareMap.get(DcMotorEx.class, "BackRight");
        backLeft = hardwareMap.get(DcMotorEx.class, "BackLeft");

        frontLeft.setDirection(DcMotorEx.Direction.REVERSE);
        backLeft.setDirection(DcMotorEx.Direction.REVERSE);
        drivetrain = new DcMotorEx[]{frontRight, frontLeft, backRight, backLeft};
        for (DcMotorEx motor : drivetrain) {
            motor.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.BRAKE);
        }
//        odoRight = hardwareMap.get(DcMotorEx.class, "odoRight");
//        odoLeft = hardwareMap.get(DcMotorEx.class, "odoLeft");
//        odoBack = hardwareMap.get(DcMotorEx.class, "odoBack");

        claw = hardwareMap.get(Servo.class, "claw");
        lift = hardwareMap.get(Servo.class, "lift");
        swivel = hardwareMap.get(Servo.class, "swivel");
        rightSlide = hardwareMap.get(DcMotorEx.class, "RightSlide");
        leftSlide = hardwareMap.get(DcMotorEx.class, "LeftSlide");
        slides = new DcMotorEx[]{rightSlide, leftSlide};
        rightSlide.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.BRAKE);
        leftSlide.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.BRAKE);

        imu = hardwareMap.get(BNO055IMU.class, "imu");
        BNO055IMU.Parameters imuparams = new BNO055IMU.Parameters();
        imuparams.angleUnit = BNO055IMU.AngleUnit.DEGREES;
        imuparams.accelUnit = BNO055IMU.AccelUnit.METERS_PERSEC_PERSEC;
        imuparams.calibrationDataFile = "BNO055IMUCalibration.json"; // see the calibration sample opmode
        imuparams.loggingEnabled = true;
        imuparams.loggingTag = "IMU";
        imu.initialize(imuparams);

        drivepidfcontroller = new PIDFController(3, 5, 1, 0);
        drivepidfcontroller.setIntegrationBounds(-5, 5);
        drivepidfcontroller.setTolerance(1);
        slidepidfcontroller = new PIDFController(3, 0, 0, 0);
        slidepidfcontroller.setTolerance(10);

//        frontRightTelemetry = telemetry.addData("FR Motor", 0);
//        frontLeftTelemetry = telemetry.addData("FL Motor", 0);
//        backRightTelemetry = telemetry.addData("BR Motor", 0);
//        backLeftTelemetry = telemetry.addData("BL Motor", 0);
//        drivetrainTelemetry = new Telemetry.Item[]{frontRightTelemetry, frontLeftTelemetry, backRightTelemetry, backLeftTelemetry};
    }

    // ------------------------------------- MISC METHODS ------------------------------------------
    /**
     * kinda self explanatory bro
     */
    public void resetEncoders() {
        for (DcMotorEx motor : drivetrain) motor.setMode(DcMotorEx.RunMode.STOP_AND_RESET_ENCODER);

        //for (Telemetry.Item telemetryMotor : drivetrainTelemetry) telemetryMotor.setValue("NO DATA");
    }

    /**
     * For debugging; adds caption and message to telemetry and updates
     * @param caption
     * @param message
     */
    public void debugPrint(String caption, Object message) {
        telemetry.addData(caption, message);
        telemetry.update();
    }

    /**
     * Converts a distance (IN CENTIMETERS) on the field to a number of ticks for the motor to rotate about
     * @param distance (centimeters)
     */
    public int calculateTicks(double distance) {
        return (int) Math.round(distance / this.wheelCircumference * motorTicks);
    }

    /**
     * Pauses op mode and interactors while drivetrain motors are running using RUN_TO_POSITION
     */
    public void blockExecutionForRunToPosition() {
        while (opMode.opModeIsActive()) {
            if (!drivetrain[0].isBusy()) break;
        }
        resetEncoders();
    }

    // --------------------------------- MOVEMENT METHODS ------------------------------------------

    /**
     * Move the robot [distance] cm forwards using encoders
     * @param distance (centimeters)
     * @param speed (ticks per second)
     */
    public void forward(double distance, double speed) {
        resetEncoders();
        for (DcMotorEx motor : drivetrain) {
            motor.setTargetPosition(calculateTicks(distance));
            motor.setMode(DcMotorEx.RunMode.RUN_TO_POSITION);
            motor.setVelocity(speed);
        }
        blockExecutionForRunToPosition();
    }

    /**
     * Move the robot [distance] cm backwards using encoders
     * @param distance (centimeters)
     * @param speed (ticks per second)
     */
    public void backward(double distance, double speed) {
        resetEncoders();
        for (DcMotorEx motor : drivetrain) {
            motor.setTargetPosition(-calculateTicks(distance));
            motor.setMode(DcMotorEx.RunMode.RUN_TO_POSITION);
            motor.setVelocity(speed);
        }
        blockExecutionForRunToPosition();
    }

    /**
     * Move the robot [distance] cm to the right using encoders (may not be exact)
     * @param distance (centimeters)
     * @param speed (ticks per second)
     */
    public void right(double distance, double speed) {
        resetEncoders();
        int calculatedTicks = calculateTicks(distance);
        frontRight.setTargetPosition(-calculatedTicks);
        frontLeft.setTargetPosition(calculatedTicks);
        backRight.setTargetPosition(calculatedTicks);
        backLeft.setTargetPosition(-calculatedTicks);
        for (DcMotorEx motor : drivetrain) {
            motor.setMode(DcMotorEx.RunMode.RUN_TO_POSITION);
            motor.setVelocity(speed);
        }
        blockExecutionForRunToPosition();
    }

    /**
     * Move the robot [distance] cm to the left using encoders (may not be exact)
     * @param distance (centimeters)
     * @param speed (ticks per second)
     */
    public void left(double distance, double speed) {
        resetEncoders();
        int calculatedTicks = calculateTicks(distance);
        frontRight.setTargetPosition(calculatedTicks);
        frontLeft.setTargetPosition(-calculatedTicks);
        backRight.setTargetPosition(-calculatedTicks);
        backLeft.setTargetPosition(calculatedTicks);
        for (DcMotorEx motor : drivetrain) {
            motor.setMode(DcMotorEx.RunMode.RUN_TO_POSITION);
            motor.setVelocity(speed);
        }
        blockExecutionForRunToPosition();
    }

    /**
     * Rotates the robot [degrees] clockwise using PIDFController with imu as input
     * @param degrees angle to rotate to in degrees, SHOULD BE BETWEEN -180 AND 180
     */
    public void rotate(double degrees) {
        double motorSpeed = 900;
        if(degrees < 0) motorSpeed = -motorSpeed;
        float lastAngle = imu.getAngularOrientation().firstAngle;
        degrees += lastAngle;
        drivepidfcontroller.reset();
        drivepidfcontroller.setSetPoint(degrees);
        do {
            lastAngle = imu.getAngularOrientation().firstAngle;
            telemetry.addData("Motor Speed", motorSpeed);
            telemetry.addData("Last Angle", lastAngle);
            telemetry.update();

            motorSpeed = 18 * drivepidfcontroller.calculate(lastAngle);
            frontRight.setVelocity(-motorSpeed);
            frontLeft.setVelocity(motorSpeed);
            backRight.setVelocity(-motorSpeed);
            backLeft.setVelocity(motorSpeed);

        } while (!drivepidfcontroller.atSetPoint());
        //make sure motors stop
        for(DcMotorEx m : drivetrain) m.setVelocity(0);
        drivepidfcontroller.reset();
    }

    // ------------------------------------ INTERACTOR METHODS -------------------------------------

    /**
     * Extend/retract slides to desired height using PIDFController
     * @param height (ticks)
     */
    public void setSlidePosPID(double height) {
        // TODO: implement this bruh
        slidepidfcontroller.reset();
        slidepidfcontroller.setSetPoint(height);
        do {
            int lastTickPos = rightSlide.getCurrentPosition();
            double speed = slidepidfcontroller.calculate(lastTickPos);
            rightSlide.setVelocity(speed);
            leftSlide.setVelocity(speed);
        } while(!slidepidfcontroller.atSetPoint());
        rightSlide.setVelocity(0);
        leftSlide.setVelocity(0);
        slidepidfcontroller.reset();
    }

    /**
     * Extend/retract slides to desired height using encoders
     * @param height - (ticks)
     */
    public void setSlidePos(int height) {
        height = Math.max(0, Math.min(height, 1000));
        rightSlide.setTargetPosition(height);
        rightSlide.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        rightSlide.setVelocity(250);
        //leftSlide.setTargetPosition(-height);
//        for(DcMotorEx slide:slides) {
//            slide.setMode(DcMotorEx.RunMode.RUN_TO_POSITION);
//            slide.setVelocity(100);
//        }
    }

    /**
     * Set arm lifter to pos, where 0.1 < pos < 1.0
     * @param pos
     */
    public void setLift(double pos) {
        double position = Math.max(0, Math.min(pos, 0.95));
        lift.setPosition(position);
    }

    /**
     * Sets arm swivel to pos
     * @param pos 0.333 < pos < 1.000
     */
    public void setSwivel(double pos) {
        double position = Math.max(0.4, Math.min(pos, 0.9));
        swivel.setPosition(position);
    }

    /**
     * Sets claw to open position
     */
    public void clawOpen() {
        this.claw.setPosition(0);
    }

    /**
     * Sets claw to closed position
     */
    public void clawClose() { this.claw.setPosition(0.3); }

    public boolean isClawClose() {
        if (this.claw.getPosition() == 0.3){
            return true;
        }
        return false;
    }
}