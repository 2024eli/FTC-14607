package org.firstinspires.ftc.teamcode;

import androidx.annotation.NonNull;

import java.lang.Math;

import com.arcrobotics.ftclib.controller.PIDFController;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.robotcore.external.Telemetry;

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
//    public DcMotorEx odoRight;
//    public DcMotorEx odoLeft;
//    public DcMotorEx odoBack;
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
    // hardware properties TODO: record these values when robot is finished
    public final int motorTicks = 145; //gobilda 5202 1150 rpm
    public final double wheelDiameter = 9.6; // cm
    public final double wheelCircumference = Math.PI * wheelDiameter; // cm
    // other constants
    public final static int SLIDEBOTTOM = 0;
    public final static int SLIDETOP = 960;
    public final static int TALLPOLE = 960; //ticks
    public final static int MEDIUMPOLE = 640;
    public final static int SHORTPOLE = 320;
    public final static int GROUND = 0;

    /**
     * Instantiate all variables related to the robot and the opmode, initialize imu and pid w/ parameters.
     * Before calling constructor, make sure
     *      * slides are at the bottom
     * @param hardwareMap The opmode's hardwareMap
     * @param opModeInstance The opmode (pass using "this" keyword)
     * @param telemetryInstance The opmode's telemetry
     */
    public HardwareController(@NonNull HardwareMap hardwareMap, LinearOpMode opModeInstance, Telemetry telemetryInstance) {
        opMode = opModeInstance;
        telemetry = telemetryInstance;

        //drivetrain
        frontRight = hardwareMap.get(DcMotorEx.class, "FrontRight");
        frontLeft = hardwareMap.get(DcMotorEx.class, "FrontLeft");
        backRight = hardwareMap.get(DcMotorEx.class, "BackRight");
        backLeft = hardwareMap.get(DcMotorEx.class, "BackLeft");
        frontLeft.setDirection(DcMotorEx.Direction.REVERSE);
        backLeft.setDirection(DcMotorEx.Direction.REVERSE);
        drivetrain = new DcMotorEx[]{frontRight, frontLeft, backRight, backLeft};
        for (DcMotorEx motor : drivetrain) motor.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.BRAKE);

//        // odo
//        odoRight = hardwareMap.get(DcMotorEx.class, "odoRight");
//        odoLeft = hardwareMap.get(DcMotorEx.class, "odoLeft");
//        odoBack = hardwareMap.get(DcMotorEx.class, "odoBack");

        // arm
        claw = hardwareMap.get(Servo.class, "claw");
        lift = hardwareMap.get(Servo.class, "lift");
        swivel = hardwareMap.get(Servo.class, "swivel");

        //slides
        rightSlide = hardwareMap.get(DcMotorEx.class, "RightSlide");
        leftSlide = hardwareMap.get(DcMotorEx.class, "LeftSlide");
        slides = new DcMotorEx[]{rightSlide, leftSlide};
        for(DcMotorEx m: slides) {
            m.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.BRAKE);
            m.setVelocityPIDFCoefficients(15.0, 2.0, 0.0, 0);
            m.setPositionPIDFCoefficients(10.0);
        }
        resetSlideEncoders();

        //imu
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
        slidepidfcontroller = new PIDFController(50, 0.05, 0, 0);
        //slidepidfcontroller.setIntegrationBounds(-5, 5);
        slidepidfcontroller.setTolerance(3);
    }

    // ------------------------------------- MISC METHODS ------------------------------------------
    /**
     * Assigns the current position of every motor on the drivetrain to tick (position) 0
     */
    public void resetDriveTrainEncoders() {
        for (DcMotorEx motor : drivetrain) {
            motor.setMode(DcMotorEx.RunMode.STOP_AND_RESET_ENCODER);
        }
    }

    /**
     * Assigns the current position of both slide motors to tick (position) 0.
     * MAKE SURE THE SLIDES ARE AT THE BOTTOM WHEN DOING THIS.
     */
    public void resetSlideEncoders() {
        for (DcMotorEx motor : slides) motor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        // behavior may vary between motor models, so after resetting set all slides back to RUN_TO_POSITION
        for (DcMotorEx motor: slides) {
            motor.setTargetPosition(motor.getCurrentPosition());
            motor.setMode(DcMotorEx.RunMode.RUN_TO_POSITION);
        }
    }

    /**
     * Converts a distance (IN CENTIMETERS) on the field to a number of ticks for the motor to rotate about
     * @param distance (centimeters)
     * @return Ticks for the drivetrain motors to turn
     */
    public int calculateTicks(double distance, boolean strafe) {
        return (int) Math.round( (distance/this.wheelCircumference * motorTicks) * (strafe ? 1.2:1)  );
    }

    /**
     * Pauses op mode and interactors while drivetrain motors are running using RUN_TO_POSITION
     */
    public void blockExecutionForRunToPosition() {
        while (opMode.opModeIsActive()) {
            if (!drivetrain[0].isBusy()) break;
        }
        resetDriveTrainEncoders();
    }

    // --------------------------------- MOVEMENT METHODS ------------------------------------------

    /**
     * Move the robot [distance] cm forwards using encoders
     * @param distance (centimeters)
     * @param speed (ticks per second)
     */
    public void forward(double distance, double speed) {
        resetDriveTrainEncoders();
        int ticksToTravel = calculateTicks(distance, false);
        for (DcMotorEx motor : drivetrain) {
            motor.setTargetPosition(ticksToTravel);
            motor.setMode(DcMotorEx.RunMode.RUN_TO_POSITION);
            motor.setVelocity(speed);
        }
        telemetry.addData("moving ticks", ticksToTravel);
        //blockExecutionForRunToPosition();
    }

    /**
     * Move the robot [distance] cm backwards using encoders
     * @param distance (centimeters)
     * @param speed (ticks per second)
     */
    public void backward(double distance, double speed) {
        resetDriveTrainEncoders();
        for (DcMotorEx motor : drivetrain) {
            motor.setTargetPosition(-calculateTicks(distance, false));
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
        resetDriveTrainEncoders();
        int calculatedTicks = calculateTicks(distance, true);
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
        resetDriveTrainEncoders();
        int calculatedTicks = calculateTicks(distance, true);
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
     * Rotates the robot [degrees] clockwise using PIDFController with imu as input. Should not be used
     * in driver-controlled programs
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
     * Returns the slides current position
     * @return Left slides current position ~(0-960)
     */
    public int getSlidePos() { return leftSlide.getCurrentPosition(); }

    /**
     * Sets the velocity of the slides
     * @param velocity
     */
    public void setSlideVelo(double velocity){
        double currentPos = getSlidePos();
        // prevents setting velocity if it will exceed the limits
        if ( (currentPos>=955 && velocity>0) || (currentPos<=0 && velocity<0) ) velocity = 0;
        for (DcMotorEx slide : slides) slide.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rightSlide.setVelocity(-velocity);
        leftSlide.setVelocity(velocity);
    }

    /**
     * Extend/retract slides to desired height using encoders
     * @param height - (ticks)
     */
    public void setSlidePos(int height) {
        height = Range.clip(height, SLIDEBOTTOM, SLIDETOP);
        int change = height - getSlidePos();
        rightSlide.setTargetPosition(-height);
        leftSlide.setTargetPosition(height);
        for(DcMotorEx slide:slides) {
            slide.setMode(DcMotorEx.RunMode.RUN_TO_POSITION);
            slide.setVelocity( (change>0)? 900:550 );
        }
    }

    /**
     * Set arm lifter to pos, where 0 < pos < 1.0
     * @param pos
     */
    public void setLift(double pos) {
        double position = Range.clip(pos, 0, 1);
        lift.setPosition(position);
    }

    /**
     * Sets arm swivel to pos
     * @param pos 0.4 < pos < 0.9
     */
    public void setSwivel(double pos) {
        double position = Range.clip(pos, 0.4, 0.9);
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

    /**
     * Returns true if the claw is in the closed position
     * @return
     */
    public boolean isClawClose() { return this.claw.getPosition() == 0.3; }

}